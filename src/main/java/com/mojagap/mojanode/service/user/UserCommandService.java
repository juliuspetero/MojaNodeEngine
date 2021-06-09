package com.mojagap.mojanode.service.user;

import com.mojagap.mojanode.controller.user.contract.AppUserContract;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.user.AppUserRepository;
import com.mojagap.mojanode.repository.user.OrganizationRepository;
import com.mojagap.mojanode.service.httpgateway.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCommandService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public AppUserContract createUser(AppUserContract appUserContract) {
        AppUser loggedInUser = AppContext.getLoggedInUser();
        AppUser appUser = new AppUser(appUserContract);
        if (loggedInUser != null) {
            appUser.setOrganization(loggedInUser.getOrganization());
        } else {
            appUser.setCreatedBy(appUser);
            appUser.setModifiedBy(appUser);
        }
        appUser.setPassword(passwordEncoder.encode(appUserContract.getPassword()));
        appUser = appUserRepository.saveAndFlush(appUser);
        appUserContract.setId(appUser.getId());
        return appUserContract;
    }

    public AppUserContract updateUser(AppUserContract appUserContract) {
        return appUserContract;
    }

    public AppUserContract removeUser(Integer userId) {
        return new AppUserContract();
    }

    public ExternalUser createExternalUser(ExternalUser externalUser) {
        return restTemplateService.doHttpPost(ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users", externalUser, ExternalUser.class);

    }
}
