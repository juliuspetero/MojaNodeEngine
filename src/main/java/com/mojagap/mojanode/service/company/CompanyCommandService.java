package com.mojagap.mojanode.service.company;


import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.branch.Branch;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.model.company.CompanyType;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.company.CompanyRepository;
import com.mojagap.mojanode.service.company.handler.CompanyCommandHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyCommandService implements CompanyCommandHandler {
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CompanyCommandService(CompanyRepository companyRepository,
                                 ModelMapper modelMapper) {
        this.companyRepository = companyRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public ActionResponse createCompany(CompanyDto companyDto) {
        companyDto.isValid();
        Account account = AppContext.getLoggedInUser().getAccount();
        PowerValidator.iPermittedAccountType(account.getAccountType(), AccountType.COMPANY);
        List<Integer> loggedInUserCompanyIds = AppContext.getCompaniesOfLoggedInUser().stream().map(Company::getId).collect(Collectors.toList());
        if (companyDto.getParentCompany() == null || companyDto.getParentCompany().getId() == null) {
            PowerValidator.throwBadRequestException(String.format(ErrorMessages.ENTITY_REQUIRED, "Parent company ID"));
        }
        if (!loggedInUserCompanyIds.contains(companyDto.getParentCompany().getId())) {
            PowerValidator.throwBadRequestException(ErrorMessages.NOT_PERMITTED_TO_PERFORM_ACTION_ON_COMPANY);
        }
        Company company = modelMapper.map(companyDto, Company.class);
        company.setParentCompany(company);
        company.setAccount(account);
        Branch branch = new Branch(ApplicationConstants.DEFAULT_OFFICE_NAME, company);
        AppContext.stamp(branch);
        company.getBranches().add(branch);
        AppContext.stamp(company);
        companyRepository.save(company);
        return new ActionResponse(company.getId());
    }

    @Override
    public ActionResponse updateCompany(CompanyDto companyDto, Integer id) {
        companyDto.isValid();
        Account account = AppContext.getLoggedInUser().getAccount();
        PowerValidator.iPermittedAccountType(account.getAccountType(), AccountType.COMPANY);
        Company company = companyRepository.findCompanyById(companyDto.getId())
                .orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Company.class.getSimpleName(), "ID")));
        List<Integer> loggedInUserCompanyIds = AppContext.getCompaniesOfLoggedInUser().stream().map(Company::getId).collect(Collectors.toList());
        if (!loggedInUserCompanyIds.contains(id)) {
            PowerValidator.throwBadRequestException(ErrorMessages.NOT_PERMITTED_TO_PERFORM_ACTION_ON_COMPANY);
        }
        if (companyDto.getParentCompany() != null && companyDto.getParentCompany().getId() != null) {
            if (!loggedInUserCompanyIds.contains(companyDto.getParentCompany().getId())) {
                PowerValidator.throwBadRequestException(ErrorMessages.NOT_PERMITTED_TO_MIGRATE_COMPANY);
            }
            Company parentCompany = companyRepository.findCompanyById(companyDto.getId())
                    .orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Company.class.getSimpleName(), "ID")));
            company.setParentCompany(parentCompany);
        }
        company.setEmail(companyDto.getEmail());
        company.setPhoneNumber(companyDto.getPhoneNumber());
        company.setAddress(companyDto.getAddress());
        company.setCompanyType(CompanyType.valueOf(companyDto.getCompanyType()));
        company.setName(companyDto.getName());
        company.setRegistrationDate(companyDto.getRegistrationDate());
        companyRepository.save(company);
        return new ActionResponse(id);
    }

    @Override
    public ActionResponse closeCompany(Integer id) {
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = loggedInUser.getAccount();
        PowerValidator.iPermittedAccountType(account.getAccountType(), AccountType.COMPANY);
        Company company = companyRepository.findCompanyById(id)
                .orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Company.class.getSimpleName(), "ID")));
        List<Integer> loggedInUserCompanyIds = AppContext.getCompaniesOfLoggedInUser().stream().map(Company::getId).collect(Collectors.toList());
        if (!loggedInUserCompanyIds.contains(id) || loggedInUser.getCompany().getId().equals(id)) {
            PowerValidator.throwBadRequestException(ErrorMessages.NOT_PERMITTED_TO_PERFORM_ACTION_ON_COMPANY);
        }
        company.setRecordStatus(AuditEntity.RecordStatus.CLOSED);
        companyRepository.save(company);
        return new ActionResponse(id);
    }

}
