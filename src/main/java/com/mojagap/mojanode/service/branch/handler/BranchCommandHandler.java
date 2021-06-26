package com.mojagap.mojanode.service.branch.handler;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.branch.BranchDto;

public interface BranchCommandHandler {

    ActionResponse createBranch(BranchDto branchDto);

    ActionResponse updateBranch(BranchDto branchDto, Integer id);

    ActionResponse closeBranch(Integer id);
}
