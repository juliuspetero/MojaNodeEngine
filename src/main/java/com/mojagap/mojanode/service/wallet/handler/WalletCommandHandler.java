package com.mojagap.mojanode.service.wallet.handler;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.wallet.ApplyWalletChargeDto;
import com.mojagap.mojanode.model.wallet.Wallet;

public interface WalletCommandHandler {

    ActionResponse deactivateWallet(Integer id);

    ActionResponse activateWallet(Integer id);

    ActionResponse applyWalletCharge(ApplyWalletChargeDto applyWalletChargeDto);

    void recalculateWalletDerivedBalances(Wallet wallet);

}
