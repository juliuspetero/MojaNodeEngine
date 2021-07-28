package com.mojagap.mojanode.service.recipient;


import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.recipient.handler.RecipientQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RecipientQueryService implements RecipientQueryHandler {
    @Override
    public RecordHolder<BranchDto> getRecipients(Map<String, String> queryParams) {
        return null;
    }
}
