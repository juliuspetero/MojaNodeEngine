package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.wallet.WalletChargeDto;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.wallet.Wallet;
import com.mojagap.mojanode.model.wallet.WalletCharge;
import com.mojagap.mojanode.repository.wallet.WalletRepository;
import com.mojagap.mojanode.service.wallet.handler.WalletChargeQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WalletChargeQueryService implements WalletChargeQueryHandler {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletChargeQueryService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public RecordHolder<WalletChargeDto> getWalletCharges(Map<String, String> queryParams) {
        return null;
    }

    @Override
    public RecordHolder<WalletChargeDto> getDefaultWalletCharges() {
        Wallet wallet = walletRepository.findDefaultWallet().orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Default Wallet", "ID")));
        Set<WalletCharge> walletCharges = wallet.getWalletCharges();
        List<WalletChargeDto> walletChargeDtos = walletCharges.stream().map(WalletCharge::toWalletChargeDto).collect(Collectors.toList());
        return new RecordHolder<>(walletChargeDtos.size(), walletChargeDtos);
    }
}
