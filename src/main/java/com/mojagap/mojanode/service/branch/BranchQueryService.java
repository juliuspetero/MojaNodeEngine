package com.mojagap.mojanode.service.branch;

import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.branch.handler.BranchQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BranchQueryService implements BranchQueryHandler {

    @Override
    public RecordHolder<BranchDto> getBranches(Map<String, String> queryParams) {
        return null;
    }
}
