package com.mojagap.mojanode.service.user;


import com.mojagap.mojanode.controller.user.contract.UserSummary;
import com.mojagap.mojanode.helper.AppContext;
import com.mojagap.mojanode.helper.ApplicationConstants;
import com.mojagap.mojanode.helper.utility.DateUtils;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.model.user.IdentificationEnum;
import com.mojagap.mojanode.repository.user.AppUserRepository;
import com.mojagap.mojanode.service.httpgateway.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserQueryService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RestTemplateService restTemplateService;

    public List<UserSummary> getUsers() {
        AppContext.getLoggedInUser();
        List<AppUser> appUsers = appUserRepository.findAll();
        return appUsers.stream().map(UserSummary::new).collect(Collectors.toList());
    }

    public ExternalUser getExternalUserById(Integer id) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("name", "Peter");
        queryParams.set("age", "56");
        queryParams.set("sex", "MALE");
        return restTemplateService.doHttpGet(ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users/" + id, queryParams, ExternalUser.class);
    }

    public List<AppUser> getExternalUsers() {
        ExternalUser[] externalUsers = restTemplateService.doHttpGet(ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users", null, ExternalUser[].class);
        List<AppUser> appUsers = List.of(externalUsers).stream().map(x -> {
            AppUser appUser = new AppUser();
            appUser.setId(x.getId());
            appUser.setLastName(x.getName());
            appUser.setFirstName(x.getUsername());
            appUser.setPhoneNumber(x.getPhone());
            appUser.setEmail(x.getEmail());
            appUser.setAddress("XXXXXXXXX");
            appUser.setPassword("PASSWORD");
            appUser.setVerified(Boolean.FALSE);
            appUser.setDateOfBirth(DateUtils.now());
            appUser.setId_number(IdentificationEnum.NATIONAL_ID.name());
            AppContext.stamp(appUser);
            return appUser;
        }).collect(Collectors.toList());
        appUserRepository.saveAllAndFlush(appUsers);
        return appUsers;
    }

}
