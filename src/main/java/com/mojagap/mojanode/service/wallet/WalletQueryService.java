package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.wallet.WalletDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.wallet.handler.WalletQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WalletQueryService implements WalletQueryHandler {

    @Override
    public RecordHolder<WalletDto> getWallets(Map<String, String> queryParams) {
        return null;
    }
}
