package com.mojagap.mojanode.service.company;


import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.model.role.CommonPermissions;
import com.mojagap.mojanode.model.role.Permission;
import com.mojagap.mojanode.model.role.Role;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.company.CompanyRepository;
import com.mojagap.mojanode.repository.role.PermissionRepository;
import com.mojagap.mojanode.repository.role.RoleRepository;
import com.mojagap.mojanode.service.company.interfaces.CompanyCommandHandler;
import com.mojagap.mojanode.service.user.UserCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyCommandService implements CompanyCommandHandler {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public ActionResponse createCompany(CompanyDto companyDto) {
        AppUser loggedInUser = AppContext.getLoggedInUser();
        List<AppUserDto> userSummaries = companyDto.getAppUsers();
        if (CollectionUtils.isEmpty(userSummaries)) {
            throw new BadRequestException(ErrorMessages.USER_REQUIRED_WHEN_CREATING_ACCOUNT);
        }
        Company company = new Company(companyDto);
        Account account = loggedInUser.getAccount();
        Permission superPermission = permissionRepository.findOneByName(CommonPermissions.SUPER_PERMISSION.name());
        Role superAdminRole = new Role(ApplicationConstants.DEFAULT_ROLE_NAME, ApplicationConstants.DEFAULT_ROLE_DESCRIPTION, account, AuditEntity.RecordStatus.ACTIVE, Collections.singletonList(superPermission));
        roleRepository.save(superAdminRole);
        List<AppUser> appUsers = userSummaries.stream().map(AppUser::new).collect(Collectors.toList());
        appUsers.forEach(user -> {
            user.setCreatedBy(appUsers.get(0));
            user.setModifiedBy(appUsers.get(0));
            user.setRole(superAdminRole);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        });
        company.setCreatedBy(appUsers.get(0));
        company.setModifiedBy(appUsers.get(0));
        companyRepository.saveAndFlush(company);
        return new ActionResponse(company.getId());
    }

}
