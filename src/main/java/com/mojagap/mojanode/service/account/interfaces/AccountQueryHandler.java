package com.mojagap.mojanode.service.account.interfaces;

import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.model.common.RecordHolder;

import java.util.Map;

public interface AccountQueryHandler {

    RecordHolder<AccountDto> getAccounts(Map<String, String> queryParams);

}
