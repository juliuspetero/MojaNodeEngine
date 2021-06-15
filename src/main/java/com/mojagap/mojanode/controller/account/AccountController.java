package com.mojagap.mojanode.controller.account;

import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.controller.account.entity.AccountSummary;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/account")
public class AccountController {

    @RequestMapping(method = RequestMethod.POST)
    public AccountSummary createCompany(AccountSummary accountSummary) {
        return accountSummary;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ActionResponse updateAccount(AccountSummary accountSummary) {
        return new ActionResponse(10);
    }

}
