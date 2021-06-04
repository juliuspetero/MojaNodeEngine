package com.mojagap.mojanode.service.user;


import com.mojagap.mojanode.controller.user.contract.UserSummary;
import com.mojagap.mojanode.helper.AppContext;
import com.mojagap.mojanode.helper.ApplicationConstants;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.user.AppUserRepository;
import com.mojagap.mojanode.service.httpgateway.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
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
        return restTemplateService.doHttpGet(ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users/" + id, queryParams, ExternalUser.class);
    }

}
