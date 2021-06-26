package com.mojagap.mojanode.service.branch;


import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.service.branch.handler.BranchCommandHandler;
import org.springframework.stereotype.Service;

@Service
public class BranchCommandService implements BranchCommandHandler {
    @Override
    public ActionResponse createBranch(BranchDto branchDto) {
        return null;
    }

    @Override
    public ActionResponse updateBranch(BranchDto branchDto, Integer id) {
        return null;
    }

    @Override
    public ActionResponse closeBranch(Integer id) {
        return null;
    }
}
