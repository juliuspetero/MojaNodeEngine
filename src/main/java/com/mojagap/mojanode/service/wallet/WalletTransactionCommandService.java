package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientTransactionDto;
import com.mojagap.mojanode.dto.wallet.BankCashPayment;
import com.mojagap.mojanode.dto.wallet.WalletTransactionRequestDto;
import com.mojagap.mojanode.dto.wallet.WalletTransferDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.infrastructure.utility.Util;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.aws.S3Document;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.transaction.PaymentMethodType;
import com.mojagap.mojanode.model.transaction.TransactionStatus;
import com.mojagap.mojanode.model.transaction.TransactionType;
import com.mojagap.mojanode.model.wallet.*;
import com.mojagap.mojanode.repository.aws.S3DocumentRepository;
import com.mojagap.mojanode.repository.wallet.WalletRepository;
import com.mojagap.mojanode.repository.wallet.WalletTransactionRepository;
import com.mojagap.mojanode.repository.wallet.WalletTransactionRequestRepository;
import com.mojagap.mojanode.service.wallet.handler.WalletCommandHandler;
import com.mojagap.mojanode.service.wallet.handler.WalletTransactionCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class WalletTransactionCommandService implements WalletTransactionCommandHandler {

    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRequestRepository walletTransactionRequestRepository;
    private final WalletCommandHandler walletCommandHandler;
    private final S3DocumentRepository s3DocumentRepository;

    @Autowired
    public WalletTransactionCommandService(WalletTransactionRepository walletTransactionRepository, WalletRepository walletRepository, WalletTransactionRequestRepository walletTransactionRequestRepository, WalletCommandHandler walletCommandHandler, S3DocumentRepository s3DocumentRepository) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRequestRepository = walletTransactionRequestRepository;
        this.walletCommandHandler = walletCommandHandler;
        this.s3DocumentRepository = s3DocumentRepository;
    }

    @Override
    @Transactional
    public ActionResponse approveTransactionRequest(Integer id) {
        WalletTransactionRequest walletTransactionRequest = walletTransactionRequestRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, WalletTransactionRequest.class.getSimpleName(), "id")));
        PowerValidator.isFalse(TransactionStatus.APPROVED.equals(walletTransactionRequest.getTransactionStatus()), "Transaction request already approved");
        Set<WalletTransaction> walletTransactions = walletTransactionRequest.getWalletTransactions();
        walletTransactions.forEach(t -> t.setTransactionStatus(TransactionStatus.APPROVED));
        walletCommandHandler.recalculateWalletDerivedBalances(walletTransactionRequest.getWallet());
        walletTransactionRequestRepository.saveAndFlush(walletTransactionRequest);
        return new ActionResponse(id);
    }

    @Override
    @Transactional
    public ActionResponse topUpWallet(WalletTransactionRequestDto walletTransactionRequestDto, Integer walletId) {
        AppContext.isPermittedAccountTypes(AccountType.INDIVIDUAL, AccountType.COMPANY);
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Wallet.class.getSimpleName(), "id")));
        walletTransactionRequestDto.isValidBankCashPayment();
        WalletTransactionRequest walletTransactionRequest = WalletTransactionRequest.builder()
                .wallet(wallet)
                .transactionType(TransactionType.TOP_UP)
                .transactionStatus(TransactionStatus.PENDING)
                .amount(walletTransactionRequestDto.getAmount())
                .build();
        AppContext.stamp(walletTransactionRequest);
        walletTransactionRequestRepository.saveAndFlush(walletTransactionRequest);
        List<WalletTransaction> walletTransactions = new ArrayList<>();
        BigDecimal totalChargeAmount = BigDecimal.ZERO;
        for (WalletCharge walletCharge : wallet.getWalletCharges()) {
            if (ChargeTypeEnum.TOP_UP_CHARGE.equals(walletCharge.getChargeTypeEnum())) {
                BigDecimal chargeAmount = computeChargeAmount(walletCharge, walletTransactionRequestDto.getBankCashPayment().getAmount());
                totalChargeAmount = totalChargeAmount.add(chargeAmount);
                WalletTransaction chargeTransaction = WalletTransaction.builder()
                        .transactionStatus(TransactionStatus.PENDING)
                        .transactionType(TransactionType.TOP_UP_CHARGE)
                        .amount(chargeAmount)
                        .wallet(wallet)
                        .walletTransactionRequest(walletTransactionRequest)
                        .build();
                AppContext.stamp(chargeTransaction);
                walletTransactions.add(chargeTransaction);
            }
        }
        PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(walletTransactionRequestDto.getPaymentMethodType());
        switch (paymentMethodType) {
            case BANK_CASH_DEPOSIT:
                if (totalChargeAmount.compareTo(walletTransactionRequestDto.getBankCashPayment().getAmount()) > 0) {
                    PowerValidator.throwBadRequestException(ErrorMessages.TOP_UP_CHARGES_GREATER_AMOUNT);
                }
                WalletTransaction topUpTransaction = topUpViaBankCashDeposit(wallet, walletTransactionRequestDto, walletTransactionRequest);
                walletTransactions.add(topUpTransaction);
                break;
            case MOBILE_MONEY:
                throw new BadRequestException(String.format(ErrorMessages.PAYMENT_METHOD_TYPE_NOT_PERMITTED, PaymentMethodType.MOBILE_MONEY.name()));
            case CHEQUE:
                throw new BadRequestException(String.format(ErrorMessages.PAYMENT_METHOD_TYPE_NOT_PERMITTED, PaymentMethodType.CHEQUE.name()));
            default:
                throw new BadRequestException(String.format(ErrorMessages.PAYMENT_METHOD_TYPE_NOT_PERMITTED, paymentMethodType.name()));
        }
        walletTransactionRepository.saveAll(walletTransactions);
        walletCommandHandler.recalculateWalletDerivedBalances(wallet);
        return new ActionResponse(walletTransactionRequest.getId());
    }

    private BigDecimal computeChargeAmount(WalletCharge walletCharge, BigDecimal transactionAmount) {
        BigDecimal amount = walletCharge.getAmount();
        if (FeeTypeEnum.PERCENTAGE.equals(walletCharge.getFeeTypeEnum())) {
            amount = transactionAmount.multiply(amount).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
        }
        return amount;
    }

    private WalletTransaction topUpViaBankCashDeposit(Wallet wallet, WalletTransactionRequestDto walletTransactionRequestDto, WalletTransactionRequest walletTransactionRequest) {
        WalletTransaction topUpTransaction = WalletTransaction.builder()
                .transactionStatus(TransactionStatus.PENDING)
                .amount(walletTransactionRequestDto.getBankCashPayment().getAmount())
                .transactionType(TransactionType.TOP_UP)
                .walletTransactionRequest(walletTransactionRequest)
                .wallet(wallet)
                .build();
        AppContext.stamp(topUpTransaction);
        BankCashPayment bankCashPayment = walletTransactionRequestDto.getBankCashPayment();
        BankDepositTransaction bankDepositTransaction = Util.copyProperties(bankCashPayment, new BankDepositTransaction());
        AppContext.stamp(bankDepositTransaction);
        topUpTransaction.setBankDepositTransaction(bankDepositTransaction);
        for (Integer document : bankCashPayment.getDocuments()) {
            S3Document s3Document = s3DocumentRepository.findById(document).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, S3Document.class.getSimpleName(), "id")));
            s3Document.setWalletTransactionRequest(walletTransactionRequest);
            s3DocumentRepository.save(s3Document);
        }
        return topUpTransaction;
    }

    @Override
    @Transactional
    public ActionResponse sendMoney(RecordHolder<RecipientTransactionDto> records, Integer walletId) {
        return null;
    }

    @Override
    @Transactional
    public ActionResponse transferFund(WalletTransferDto walletTransferDto) {
        return null;
    }


}

