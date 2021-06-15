package com.mojagap.mojanode.service.account.interfaces;

import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.controller.account.entity.AccountSummary;

public interface AccountCommand {

    AccountSummary createAccount(AccountSummary accountSummary);

    ActionResponse updateAccount(AccountSummary accountSummary);

    ActionResponse approveAccount(Integer accountId);
}
