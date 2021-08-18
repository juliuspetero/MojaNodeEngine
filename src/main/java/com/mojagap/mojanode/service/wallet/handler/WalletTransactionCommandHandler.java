package com.mojagap.mojanode.service.wallet.handler;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientTransactionDto;
import com.mojagap.mojanode.dto.wallet.WalletTransactionRequestDto;
import com.mojagap.mojanode.dto.wallet.WalletTransferDto;
import com.mojagap.mojanode.model.common.RecordHolder;

public interface WalletTransactionCommandHandler {

    ActionResponse approveTransactionRequest(Integer id);

    ActionResponse topUpWallet(WalletTransactionRequestDto walletTransactionRequestDto, Integer walletId);

    ActionResponse sendMoney(RecordHolder<RecipientTransactionDto> records, Integer walletId);

    ActionResponse transferFund(WalletTransferDto walletTransferDto);

}
