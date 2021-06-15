package com.mojagap.mojanode.service.company;


import com.mojagap.mojanode.controller.company.entity.CompanySummary;
import com.mojagap.mojanode.controller.user.entity.AppUserSummary;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.role.CommonPermissions;
import com.mojagap.mojanode.model.role.Permission;
import com.mojagap.mojanode.model.role.Role;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.repository.role.UserPermissionRepository;
import com.mojagap.mojanode.repository.role.UserRoleRepository;
import com.mojagap.mojanode.repository.company.CompanyRepository;
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
public class CompanyCommandService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional
    public CompanySummary createOrganization(CompanySummary companySummary) {
        List<AppUserSummary> userSummaries = companySummary.getUserSummaries();
        if (CollectionUtils.isEmpty(userSummaries)) {
            throw new BadRequestException(ErrorMessages.USER_REQUIRED_WHEN_CREATING_ORGANIZATION);
        }
        final Company organization = new Company(companySummary);
        final Account account = new Account();
        Permission superPermission = userPermissionRepository.findOneByName(CommonPermissions.SUPER_PERMISSION.name());
        Role superAdminRole = new Role(ApplicationConstants.DEFAULT_ROLE_NAME, ApplicationConstants.DEFAULT_ROLE_DESCRIPTION, account, AuditEntity.RecordStatus.ACTIVE, Collections.singletonList(superPermission));
        userRoleRepository.save(superAdminRole);
        List<AppUser> appUsers = userSummaries.stream().map(userSummary -> new AppUser(userSummary, organization)).collect(Collectors.toList());
        appUsers.forEach(user -> {
            user.setCreatedBy(appUsers.get(0));
            user.setModifiedBy(appUsers.get(0));
            user.setRole(superAdminRole);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        });

        organization.setCreatedBy(appUsers.get(0));
        organization.setModifiedBy(appUsers.get(0));
        organization.getAppUsers().addAll(appUsers);
        Company response = companyRepository.saveAndFlush(organization);
        companySummary.setId(response.getId());
        return companySummary;
    }

}
