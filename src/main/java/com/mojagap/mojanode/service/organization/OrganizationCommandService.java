package com.mojagap.mojanode.service.organization;


import com.mojagap.mojanode.controller.organization.entity.OrganizationSummary;
import com.mojagap.mojanode.controller.user.entity.AppUserSummary;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.model.AuditEntity;
import com.mojagap.mojanode.model.EntityCategory;
import com.mojagap.mojanode.model.role.CommonPermissions;
import com.mojagap.mojanode.model.role.UserPermission;
import com.mojagap.mojanode.model.role.UserRole;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.model.user.Organization;
import com.mojagap.mojanode.repository.role.UserPermissionRepository;
import com.mojagap.mojanode.repository.role.UserRoleRepository;
import com.mojagap.mojanode.repository.user.OrganizationRepository;
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
public class OrganizationCommandService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional
    public OrganizationSummary createOrganization(OrganizationSummary organizationSummary) {
        List<AppUserSummary> userSummaries = organizationSummary.getUserSummaries();
        if (CollectionUtils.isEmpty(userSummaries)) {
            throw new BadRequestException(ErrorMessages.USER_REQUIRED_WHEN_CREATING_ORGANIZATION);
        }
        final Organization organization = new Organization(organizationSummary);
        UserPermission superPermission = userPermissionRepository.findOneByName(CommonPermissions.SUPER_PERMISSION.name());
        UserRole superUserRole = new UserRole("Super User", "This role provides all application permissions", AuditEntity.RecordStatus.ACTIVE,
                organization, Collections.singletonList(superPermission));
        userRoleRepository.save(superUserRole);
        List<AppUser> appUsers = userSummaries.stream().map(userSummary -> new AppUser(userSummary, organization)).collect(Collectors.toList());
        appUsers.forEach(user -> {
            user.setCreatedBy(appUsers.get(0));
            user.setModifiedBy(appUsers.get(0));
            user.setRole(superUserRole);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCategory(EntityCategory.ORGANIZATION);
        });

        organization.setCreatedBy(appUsers.get(0));
        organization.setModifiedBy(appUsers.get(0));
        organization.getAppUsers().addAll(appUsers);
        Organization response = organizationRepository.saveAndFlush(organization);
        organizationSummary.setId(response.getId());
        return organizationSummary;
    }

}
