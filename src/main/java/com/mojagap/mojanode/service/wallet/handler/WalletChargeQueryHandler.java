package com.mojagap.mojanode.service.wallet.handler;

import com.mojagap.mojanode.dto.wallet.WalletChargeDto;
import com.mojagap.mojanode.model.common.RecordHolder;

import java.util.Map;

public interface WalletChargeQueryHandler {

    RecordHolder<WalletChargeDto> getWalletCharges(Map<String, String> queryParams);

    RecordHolder<WalletChargeDto> getDefaultWalletCharges();
}
