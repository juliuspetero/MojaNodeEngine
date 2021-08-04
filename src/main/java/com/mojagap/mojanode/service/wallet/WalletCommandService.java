package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientTransactionDto;
import com.mojagap.mojanode.dto.wallet.ApplyWalletChargeDto;
import com.mojagap.mojanode.dto.wallet.WalletTransactionDto;
import com.mojagap.mojanode.dto.wallet.WalletTransferDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.wallet.handler.WalletCommandHandler;
import org.springframework.stereotype.Service;

@Service
public class WalletCommandService implements WalletCommandHandler {

    @Override
    public ActionResponse deactivateWallet(Integer id) {
        return null;
    }

    @Override
    public ActionResponse activateWallet(Integer id) {
        return null;
    }

    @Override
    public ActionResponse applyWalletCharge(ApplyWalletChargeDto applyWalletChargeDto) {
        return null;
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
