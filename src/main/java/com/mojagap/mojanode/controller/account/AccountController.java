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
            log.setEntityId(response.getAccount().getAccountId());
            return response;
        });
    }

    @RequestMapping(path = "/authenticate", method = RequestMethod.GET)
    public AppUserDto authenticateUser(@RequestBody AppUserDto appUserDto) {
        return executeHttpGet(() -> accountCommandHandler.authenticateUser(appUserDto));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ActionResponse updateAccount(@RequestBody AccountDto accountDto, @PathVariable("id") Integer accountId) {
        return executeAndLogUserActivity(EntityTypeEnum.ACCOUNT, ActionTypeEnum.UPDATE, (UserActivityLog log) -> {
            ActionResponse actionResponse = accountCommandHandler.updateAccount(accountDto, accountId);
            log.setEntityId(actionResponse.resourceId());
            return actionResponse;
        });
    }

    @RequestMapping(path = "/approve/{id}", method = RequestMethod.POST)
    public ActionResponse approveAccount(@PathVariable("id") Integer accountId) {
        return executeAndLogUserActivity(EntityTypeEnum.ACCOUNT, ActionTypeEnum.APPROVE, (UserActivityLog log) -> {
            ActionResponse actionResponse = accountCommandHandler.approveAccount(accountId);
            log.setEntityId(actionResponse.resourceId());
            return actionResponse;
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public RecordHolder<AccountDto> getAccounts(@RequestParam Map<String, String> queryParams) {
        return executeHttpGet(() -> accountQueryHandler.getAccounts(queryParams));
    }
}
