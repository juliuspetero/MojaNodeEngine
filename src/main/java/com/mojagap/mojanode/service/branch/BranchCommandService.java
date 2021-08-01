package com.mojagap.mojanode.service.branch;


import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.branch.Branch;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.model.wallet.Wallet;
import com.mojagap.mojanode.repository.branch.BranchRepository;
import com.mojagap.mojanode.service.branch.handler.BranchCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchCommandService implements BranchCommandHandler {
    private final BranchRepository branchRepository;

    @Autowired
    public BranchCommandService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }


    @Override
    @Transactional
    public ActionResponse createBranch(BranchDto branchDto) {
        branchDto.isValid();
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = loggedInUser.getAccount();
        PowerValidator.isPermittedAccountType(account.getAccountType(), AccountType.COMPANY);
        Branch parentBranch = getParentBranch(branchDto);
        Company company = loggedInUser.getCompany();
        Branch branch = new Branch(branchDto.getName(), company, account);
        branch.setParentBranch(parentBranch);
        AppContext.stamp(branch);
        company.getBranches().add(branch);
        Wallet wallet = new Wallet();
        wallet.setAvailableBalance(BigDecimal.ZERO);
        wallet.setActualBalance(BigDecimal.ZERO);
        wallet.setAccount(account);
        wallet.setBranch(branch);
        wallet.setCompany(company);
        AppContext.stamp(wallet);
        branch.getWallets().add(wallet);
        branchRepository.save(branch);
        return new ActionResponse(branch.getId());
    }

    @Override
    @Transactional
    public ActionResponse updateBranch(BranchDto branchDto, Integer id) {
        Account account = AppContext.getLoggedInUser().getAccount();
        PowerValidator.isPermittedAccountType(account.getAccountType(), AccountType.COMPANY);
        branchDto.isValid();
        PowerValidator.notNull(id, String.format(ErrorMessages.ENTITY_REQUIRED, "Branch ID"));
        Branch branch = branchRepository.findById(id).orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Branch", "ID")));
        Branch parentBranch = getParentBranch(branchDto);
        branch.setParentBranch(parentBranch);
        branch.setName(branchDto.getName());
        AppContext.stamp(branch);
        branchRepository.save(branch);
        return new ActionResponse(id);
    }

    private Branch getParentBranch(BranchDto branchDto) {
        List<Integer> loggedInUserBranchIds = AppContext.getBranchesOfLoggedInUser().stream().map(Branch::getId).collect(Collectors.toList());
        Branch parentBranch = branchRepository.findById(branchDto.getParentBranch().getId()).orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Branch", "ID")));
        if (!loggedInUserBranchIds.contains(branchDto.getParentBranch().getId())) {
            PowerValidator.throwBadRequestException(ErrorMessages.NOT_PERMITTED_TO_CREATE_BRANCH_UNDER_PARENT);
        }
        return parentBranch;
    }

    @Override
    @Transactional
    public ActionResponse closeBranch(Integer id) {
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = loggedInUser.getAccount();
        PowerValidator.isPermittedAccountType(account.getAccountType(), AccountType.COMPANY);
        Branch branch = branchRepository.findById(id).orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Branch", "ID")));
        List<Integer> loggedInUserBranchIds = AppContext.getBranchesOfLoggedInUser().stream().map(Branch::getId).collect(Collectors.toList());
        if (!loggedInUserBranchIds.contains(id) || loggedInUser.getBranch().getId().equals(id)) {
            PowerValidator.throwBadRequestException(ErrorMessages.NOT_PERMITTED_TO_CLOSE_COMPANY);
        }
        branch.setRecordStatus(AuditEntity.RecordStatus.CLOSED);
        branchRepository.save(branch);
        return new ActionResponse(id);
    }
}
