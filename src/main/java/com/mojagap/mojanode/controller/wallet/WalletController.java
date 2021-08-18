package com.mojagap.mojanode.controller.wallet;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientTransactionDto;
import com.mojagap.mojanode.dto.wallet.ApplyWalletChargeDto;
import com.mojagap.mojanode.dto.wallet.WalletDto;
import com.mojagap.mojanode.dto.wallet.WalletTransactionRequestDto;
import com.mojagap.mojanode.dto.wallet.WalletTransferDto;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.wallet.handler.WalletCommandHandler;
import com.mojagap.mojanode.service.wallet.handler.WalletQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/wallet")
public class WalletController extends BaseController {
    private final WalletCommandHandler walletCommandHandler;
    private final WalletQueryHandler walletQueryHandler;

    @Autowired
    public WalletController(WalletCommandHandler walletCommandHandler, WalletQueryHandler walletQueryHandler) {
        this.walletCommandHandler = walletCommandHandler;
        this.walletQueryHandler = walletQueryHandler;
    }

    @RequestMapping(path = "/activate", method = RequestMethod.POST)
    public ActionResponse activateWallet(Integer id) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET, ActionTypeEnum.ACTIVATE, (UserActivityLog log) -> {
            ActionResponse response = walletCommandHandler.activateWallet(id);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/deactivate", method = RequestMethod.POST)
    public ActionResponse deactivateWallet(Integer id) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET, ActionTypeEnum.DEACTIVATE, (UserActivityLog log) -> {
            ActionResponse response = walletCommandHandler.deactivateWallet(id);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/applyCharge", method = RequestMethod.POST)
    public ActionResponse applyWalletCharge(@RequestBody ApplyWalletChargeDto applyWalletChargeDto) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET, ActionTypeEnum.APPLY_CHARGE, (UserActivityLog log) -> {
            ActionResponse response = walletCommandHandler.applyWalletCharge(applyWalletChargeDto);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }


    @RequestMapping(method = RequestMethod.GET)
    public RecordHolder<WalletDto> getWallets(@RequestParam Map<String, String> queryParams) {
        return executeHttpGet(() -> walletQueryHandler.getWallets(queryParams));
    }
}
