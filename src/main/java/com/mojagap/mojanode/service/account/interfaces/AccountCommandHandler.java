package com.mojagap.mojanode.service.account.interfaces;

import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.user.AppUserDto;

public interface AccountCommandHandler {

    AppUserDto createAccount(AccountDto accountDto);

    AppUserDto authenticateUser(AppUserDto appUserDto);

    ActionResponse updateAccount(AccountDto accountDto);

    ActionResponse approveAccount(Integer accountId);


}
