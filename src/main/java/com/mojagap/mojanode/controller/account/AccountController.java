package com.mojagap.mojanode.controller.account;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.account.handler.AccountCommandHandler;
import com.mojagap.mojanode.service.account.handler.AccountQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/v1/account")
public class AccountController extends BaseController {
    private final AccountCommandHandler accountCommandHandler;
    private final AccountQueryHandler accountQueryHandler;

    @Autowired
    public AccountController(AccountCommandHandler accountCommandHandler, AccountQueryHandler accountQueryHandler) {
        this.accountCommandHandler = accountCommandHandler;
        this.accountQueryHandler = accountQueryHandler;
    }

    @RequestMapping(method = RequestMethod.POST)
    public AppUserDto createAccount(@RequestBody AccountDto accountDto) {
        return executeAndLogUserActivity(EntityTypeEnum.ACCOUNT, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            AppUserDto response = accountCommandHandler.createAccount(accountDto);
            log.setEntityId(response.getAccount().getId());
            return response;
        });
    }

    @RequestMapping(path = "/authenticate", method = RequestMethod.GET)
    public AppUserDto authenticateUser() {
        return executeHttpGet(() -> accountCommandHandler.authenticateUser(new AppUserDto()));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ActionResponse updateAccount(@RequestBody AccountDto accountDto) {
        return executeAndLogUserActivity(EntityTypeEnum.ACCOUNT, ActionTypeEnum.UPDATE, (UserActivityLog log) -> {
            ActionResponse actionResponse = accountCommandHandler.updateAccount(accountDto);
            log.setEntityId(actionResponse.getResourceId());
            return actionResponse;
        });
    }

    @RequestMapping(path = "/activate/{id}", method = RequestMethod.POST)
    public ActionResponse activateAccount(@PathVariable("id") Integer accountId) {
        return executeAndLogUserActivity(EntityTypeEnum.ACCOUNT, ActionTypeEnum.APPROVE, (UserActivityLog log) -> {
            ActionResponse actionResponse = accountCommandHandler.activateAccount(accountId);
            log.setEntityId(actionResponse.getResourceId());
            return actionResponse;
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public RecordHolder<AccountDto> getAccounts(@RequestParam Map<String, String> queryParams) {
        return executeHttpGet(() -> accountQueryHandler.getAccounts(queryParams));
    }
}
