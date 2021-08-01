package com.mojagap.mojanode.service.wallet.handler;

import com.mojagap.mojanode.dto.wallet.WalletDto;
import com.mojagap.mojanode.model.common.RecordHolder;

import java.util.Map;

public interface WalletQueryHandler {

    RecordHolder<WalletDto> getWallets(Map<String, String> queryParams);
}
