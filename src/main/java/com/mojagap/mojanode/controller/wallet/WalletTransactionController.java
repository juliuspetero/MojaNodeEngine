package com.mojagap.mojanode.controller.wallet;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientTransactionDto;
import com.mojagap.mojanode.dto.wallet.WalletTransactionRequestDto;
import com.mojagap.mojanode.dto.wallet.WalletTransferDto;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.wallet.handler.WalletTransactionCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/walletTransaction")
public class WalletTransactionController extends BaseController {
    private final WalletTransactionCommandHandler walletTransactionCommandHandler;

    @Autowired
    public WalletTransactionController(WalletTransactionCommandHandler walletTransactionCommandHandler) {
        this.walletTransactionCommandHandler = walletTransactionCommandHandler;
    }


    @RequestMapping(path = "/topUp/{walletId}", method = RequestMethod.POST)
    public ActionResponse topUpWallet(@RequestBody WalletTransactionRequestDto walletTransactionRequestDto, @PathVariable("walletId") Integer walletId) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET, ActionTypeEnum.TOP_UP, (UserActivityLog log) -> {
            ActionResponse response = walletTransactionCommandHandler.topUpWallet(walletTransactionRequestDto, walletId);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/approve/{id}", method = RequestMethod.POST)
    public ActionResponse approveTransactionRequest(@PathVariable("id") Integer id) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET, ActionTypeEnum.APPROVE_REQUEST, (UserActivityLog log) -> {
            ActionResponse response = walletTransactionCommandHandler.approveTransactionRequest(id);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public ActionResponse transferFund(@RequestBody WalletTransferDto walletTransferDto) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET, ActionTypeEnum.TRANSFER, (UserActivityLog log) -> {
            ActionResponse response = walletTransactionCommandHandler.transferFund(walletTransferDto);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/sendMoney/{walletId}", method = RequestMethod.POST)
    public ActionResponse sendMoney(@RequestBody RecordHolder<RecipientTransactionDto> records, @PathVariable("walletId") Integer walletId) {
        return executeAndLogUserActivity(EntityTypeEnum.WALLET, ActionTypeEnum.TOP_UP, (UserActivityLog log) -> {
            ActionResponse response = walletTransactionCommandHandler.sendMoney(records, walletId);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }
}
