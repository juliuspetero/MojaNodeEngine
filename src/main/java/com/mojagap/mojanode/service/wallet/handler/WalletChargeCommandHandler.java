package com.mojagap.mojanode.service.wallet.handler;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.wallet.WalletChargeDto;

public interface WalletChargeCommandHandler {

    ActionResponse createWalletCharge(WalletChargeDto walletChargeDto);

    ActionResponse updateWalletCharge(WalletChargeDto walletChargeDto, Integer id);

    ActionResponse activateWalletCharge(Integer id);

    ActionResponse deactivateWalletCharge(Integer id);
}
