package com.mojagap.mojanode.service.branch.handler;

import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.model.common.RecordHolder;

import java.util.Map;

public interface BranchQueryHandler {
    RecordHolder<BranchDto> getBranches(Map<String, String> queryParams);
}
