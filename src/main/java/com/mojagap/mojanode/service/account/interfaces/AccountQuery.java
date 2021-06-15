package com.mojagap.mojanode.service.account.interfaces;

import com.mojagap.mojanode.controller.account.entity.AccountSummary;
import com.mojagap.mojanode.model.common.RecordHolder;

import java.util.Map;

public interface AccountQuery {

    RecordHolder<AccountSummary> getAccounts(Map<String, String> queryParams);

}
