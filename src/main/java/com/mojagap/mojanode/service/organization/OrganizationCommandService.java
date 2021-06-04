package com.mojagap.mojanode.service.organization;


import com.mojagap.mojanode.controller.organization.contract.OrganizationSummary;
import com.mojagap.mojanode.controller.user.contract.UserSummary;
import com.mojagap.mojanode.helper.ErrorMessages;
import com.mojagap.mojanode.helper.exception.BadRequestException;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.model.user.Organization;
import com.mojagap.mojanode.repository.user.OrganizationRepository;
import com.mojagap.mojanode.service.user.UserCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationCommandService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserCommandService userCommandService;

    public OrganizationSummary createOrganization(OrganizationSummary organizationSummary) {
        List<UserSummary> userSummaries = organizationSummary.getUserSummaries();
        if (CollectionUtils.isEmpty(userSummaries)) {
            throw new BadRequestException(ErrorMessages.USER_REQUIRED_WHEN_CREATING_ORGANIZATION);
        }
        final Organization organization = new Organization(organizationSummary);
        List<AppUser> appUsers = userSummaries.stream().map(userSummary -> new AppUser(userSummary, organization)).collect(Collectors.toList());
        appUsers.forEach(user -> {
            user.setCreatedBy(appUsers.get(0));
            user.setModifiedBy(appUsers.get(0));
        });
        organization.setCreatedBy(appUsers.get(0));
        organization.setModifiedBy(appUsers.get(0));
        organization.getAppUsers().addAll(appUsers);
        Organization response = organizationRepository.saveAndFlush(organization);
        organizationSummary.setId(response.getId());
        return organizationSummary;
    }

}
