package com.mojagap.mojanode.controller.wallet;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.wallet.ApplyWalletChargeDto;
import com.mojagap.mojanode.dto.wallet.WalletChargeDto;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.wallet.handler.WalletChargeCommandHandler;
import com.mojagap.mojanode.service.wallet.handler.WalletChargeQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/walletCharge")
public class WalletChargeController extends BaseController {
    private final WalletChargeCommandHandler walletChargeCommandHandler;
    private final WalletChargeQueryHandler walletChargeQueryHandler;

    @Autowired
    public WalletChargeController(WalletChargeCommandHandler walletChargeCommandHandler, WalletChargeQueryHandler walletChargeQueryHandler) {
        this.walletChargeCommandHandler = walletChargeCommandHandler;
        this.walletChargeQueryHandler = walletChargeQueryHandler;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ActionResponse createWalletCharge(@RequestBody WalletChargeDto walletChargeDto) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET_CHARGE, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            ActionResponse response = walletChargeCommandHandler.createWalletCharge(walletChargeDto);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ActionResponse updateWalletCharge(@RequestBody WalletChargeDto walletChargeDto, @PathVariable("id") Integer id) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET_CHARGE, ActionTypeEnum.UPDATE, (UserActivityLog log) -> {
            ActionResponse response = walletChargeCommandHandler.updateWalletCharge(walletChargeDto, id);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/default", method = RequestMethod.PUT)
    public ActionResponse updateDefaultWalletCharge(@RequestBody ApplyWalletChargeDto applyWalletChargeDto) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET_CHARGE, ActionTypeEnum.UPDATE, (UserActivityLog log) -> {
            ActionResponse response = walletChargeCommandHandler.updateDefaultWalletCharge(applyWalletChargeDto);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/default", method = RequestMethod.GET)
    public RecordHolder<WalletChargeDto> getDefaultWalletCharges() {
        return executeHttpGet(walletChargeQueryHandler::getDefaultWalletCharges);
    }

    @RequestMapping(path = "/activate/{id}", method = RequestMethod.DELETE)
    public ActionResponse activateWalletCharge(@PathVariable("id") Integer id) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET_CHARGE, ActionTypeEnum.ACTIVATE, (UserActivityLog log) -> {
            ActionResponse response = walletChargeCommandHandler.activateWalletCharge(id);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/deactivate/{id}", method = RequestMethod.DELETE)
    public ActionResponse deactivateWalletCharge(@PathVariable("id") Integer id) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET_CHARGE, ActionTypeEnum.DEACTIVATE, (UserActivityLog log) -> {
            ActionResponse response = walletChargeCommandHandler.deactivateWalletCharge(id);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public RecordHolder<WalletChargeDto> getWalletCharges(@RequestParam Map<String, String> queryParams) {
        return executeHttpGet(() -> walletChargeQueryHandler.getWalletCharges(queryParams));
    }

}
