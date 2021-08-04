package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.wallet.WalletChargeDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.wallet.handler.WalletChargeQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WalletChargeQueryService implements WalletChargeQueryHandler {
    @Override
    public RecordHolder<WalletChargeDto> getWalletCharges(Map<String, String> queryParams) {
        return null;
    }
}
