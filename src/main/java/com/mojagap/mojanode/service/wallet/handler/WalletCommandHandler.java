package com.mojagap.mojanode.service.wallet.handler;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientTransactionDto;
import com.mojagap.mojanode.dto.wallet.WalletTransactionDto;
import com.mojagap.mojanode.model.common.RecordHolder;

public interface WalletCommandHandler {

    ActionResponse deactivateWallet(Integer id);

    ActionResponse activateWallet(Integer id);

    ActionResponse topUpWallet(WalletTransactionDto walletTransactionDto, Integer walletId);

    ActionResponse sendMoney(RecordHolder<RecipientTransactionDto> records, Integer walletId);

}
