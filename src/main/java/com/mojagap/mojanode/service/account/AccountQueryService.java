package com.mojagap.mojanode.service.account;

import com.mojagap.mojanode.controller.account.entity.AccountSummary;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.account.interfaces.AccountQuery;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountQueryService implements AccountQuery {

    @Override
    public RecordHolder<AccountSummary> getAccounts(Map<String, String> queryParams) {
        return null;
    }
}
