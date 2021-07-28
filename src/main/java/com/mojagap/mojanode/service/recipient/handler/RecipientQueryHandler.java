package com.mojagap.mojanode.service.recipient.handler;

import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.model.common.RecordHolder;

import java.util.Map;

public interface RecipientQueryHandler {
    RecordHolder<BranchDto> getRecipients(Map<String, String> queryParams);
}
