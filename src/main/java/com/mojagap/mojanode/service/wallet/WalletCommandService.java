package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientTransactionDto;
import com.mojagap.mojanode.dto.wallet.ApplyWalletChargeDto;
import com.mojagap.mojanode.dto.wallet.WalletTransactionDto;
import com.mojagap.mojanode.dto.wallet.WalletTransferDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.wallet.Wallet;
import com.mojagap.mojanode.model.wallet.WalletCharge;
import com.mojagap.mojanode.repository.wallet.WalletChargeRepository;
import com.mojagap.mojanode.repository.wallet.WalletRepository;
import com.mojagap.mojanode.service.wallet.handler.WalletCommandHandler;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class WalletCommandService implements WalletCommandHandler {

    private final WalletRepository walletRepository;
    private final WalletChargeRepository walletChargeRepository;

    @Autowired
    public WalletCommandService(WalletRepository walletRepository, WalletChargeRepository walletChargeRepository) {
        this.walletRepository = walletRepository;
        this.walletChargeRepository = walletChargeRepository;
    }

    @Override
    public ActionResponse deactivateWallet(Integer id) {
        AppContext.isPermittedAccountTyp(AccountType.BACK_OFFICE);
        Wallet wallet = walletRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Wallet.class.getSimpleName(), "id")));
        PowerValidator.isFalse(AuditEntity.RecordStatus.INACTIVE.equals(wallet.getRecordStatus()), "Wallet charge is already active");
        wallet.setRecordStatus(AuditEntity.RecordStatus.INACTIVE);
        AppContext.stamp(wallet);
        walletRepository.save(wallet);
        return new ActionResponse(id);
    }

    @Override
    public ActionResponse activateWallet(Integer id) {
        AppContext.isPermittedAccountTyp(AccountType.BACK_OFFICE);
        Wallet wallet = walletRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Wallet.class.getSimpleName(), "id")));
        PowerValidator.isFalse(AuditEntity.RecordStatus.ACTIVE.equals(wallet.getRecordStatus()), "Wallet charge is already active");
        wallet.setRecordStatus(AuditEntity.RecordStatus.ACTIVE);
        AppContext.stamp(wallet);
        walletRepository.save(wallet);
        return new ActionResponse(id);
    }

    @Override
    public ActionResponse applyWalletCharge(ApplyWalletChargeDto applyWalletChargeDto) {
        AppContext.isPermittedAccountTyp(AccountType.BACK_OFFICE);
        if (CollectionUtils.isNotEmpty(applyWalletChargeDto.getWalletIds()) && CollectionUtils.isNotEmpty(applyWalletChargeDto.getWalletChargeIds())) {
            List<WalletCharge> walletCharges = walletChargeRepository.findAllById(applyWalletChargeDto.getWalletChargeIds());
            List<Wallet> wallets = applyWalletChargeDto.getApplyToAll() ? walletRepository.findAll() : walletRepository.findAllById(applyWalletChargeDto.getWalletIds());
            for (Wallet wallet : wallets) {
                wallet.setWalletCharges(new HashSet<>(walletCharges));
                AppContext.stamp(wallet);
            }
            walletRepository.saveAll(wallets);
        }
        return new ActionResponse("Charges successfully applied to wallets");
    }

    @Override
    public ActionResponse topUpWallet(WalletTransactionDto walletTransactionDto, Integer walletId) {

        return null;
    }

    @Override
    public ActionResponse sendMoney(RecordHolder<RecipientTransactionDto> records, Integer walletId) {
        return null;
    }

    @Override
    public ActionResponse transferFund(WalletTransferDto walletTransferDto) {
        return null;
    }
}
