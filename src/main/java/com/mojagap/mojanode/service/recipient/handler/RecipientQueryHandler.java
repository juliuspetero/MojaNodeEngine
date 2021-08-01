package com.mojagap.mojanode.service.recipient.handler;

import com.mojagap.mojanode.dto.recipient.RecipientDto;
import com.mojagap.mojanode.model.common.RecordHolder;

import java.util.Map;

public interface RecipientQueryHandler {
    RecordHolder<RecipientDto> getRecipients(Map<String, String> queryParams);

    RecordHolder<RecipientDto> getRecipientTransactions(Map<String, String> queryParams);
}
