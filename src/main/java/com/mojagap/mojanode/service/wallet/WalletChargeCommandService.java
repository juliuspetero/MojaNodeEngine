package com.mojagap.mojanode.service.wallet;


import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.wallet.WalletChargeDto;
import com.mojagap.mojanode.service.wallet.handler.WalletChargeCommandHandler;
import org.springframework.stereotype.Service;

@Service
public class WalletChargeCommandService implements WalletChargeCommandHandler {
    @Override
    public ActionResponse createWalletCharge(WalletChargeDto walletChargeDto) {
        return null;
    }

    @Override
    public ActionResponse updateWalletCharge(WalletChargeDto walletChargeDto, Integer id) {
        return null;
    }

    @Override
    public ActionResponse activateWalletCharge(Integer id) {
        return null;
    }

    @Override
    public ActionResponse deactivateWalletCharge(Integer id) {
        return null;
    }
}
