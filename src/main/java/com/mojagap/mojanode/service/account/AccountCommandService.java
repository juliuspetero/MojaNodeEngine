package com.mojagap.mojanode.service.account;

import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.controller.account.entity.AccountSummary;
import com.mojagap.mojanode.service.account.interfaces.AccountCommand;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandService implements AccountCommand {

    @Override
    public AccountSummary createAccount(AccountSummary accountSummary) {
        return accountSummary;
    }

    @Override
    public ActionResponse updateAccount(AccountSummary accountSummary) {
        return new ActionResponse(23);
    }

    @Override
    public ActionResponse approveAccount(Integer accountId) {
        return new ActionResponse(accountId);
    }


}
