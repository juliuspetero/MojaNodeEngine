package com.mojagap.mojanode.service.user;

import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.company.CompanyRepository;
import com.mojagap.mojanode.repository.user.AppUserRepository;
import com.mojagap.mojanode.service.httpgateway.RestTemplateService;
import com.mojagap.mojanode.service.user.handler.UserCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class UserCommandService implements UserCommandHandler {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    protected HttpServletResponse httpServletResponse;


    @Override
    public AppUserDto createUser(AppUserDto appUserDto) {
        AppUser appUser = new AppUser(appUserDto);
        AppContext.stamp(appUser);
        appUser.setPassword(passwordEncoder.encode(appUserDto.getPassword()));
        appUser = appUserRepository.saveAndFlush(appUser);
        appUserDto.setId(appUser.getId());
        return appUserDto;
    }

    @Override
    public AppUserDto updateUser(AppUserDto appUserDto) {
        return appUserDto;
    }

    @Override
    public AppUserDto removeUser(Integer userId) {
        return new AppUserDto();
    }

    @Override
    public ExternalUser createExternalUser(ExternalUser externalUser) {
        return restTemplateService.doHttpPost(ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users", externalUser, ExternalUser.class);

    }
}
