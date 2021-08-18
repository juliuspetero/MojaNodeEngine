package com.mojagap.mojanode.service.account;

import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.utility.Util;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.repository.account.AccountRepository;
import com.mojagap.mojanode.service.account.handler.AccountQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountQueryHandlerService implements AccountQueryHandler {

    final AccountRepository accountRepository;

    @Autowired
    public AccountQueryHandlerService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public RecordHolder<AccountDto> getAccounts(Map<String, String> queryParams) {
        AppContext.isPermittedAccountTypes(AccountType.BACK_OFFICE);
        List<Account> accounts = accountRepository.findAll();
        List<AccountDto> accountDtos = accounts.stream().map(this::toAccountDto).collect(Collectors.toList());
        return new RecordHolder<>(accountDtos.size(), accountDtos);
    }

    private AccountDto toAccountDto(Account account) {
        AccountDto accountDto = Util.copyProperties(account, new AccountDto());
        accountDto.setAccountType(account.getAccountType().name());
        accountDto.setCountryCode(account.getCountryCode().name());
        accountDto.setStatus(account.getRecordStatus().name());
        return accountDto;
    }
}
