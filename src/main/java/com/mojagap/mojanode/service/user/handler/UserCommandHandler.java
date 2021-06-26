package com.mojagap.mojanode.service.user.handler;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.model.http.ExternalUser;

public interface UserCommandHandler {

    ActionResponse createUser(AppUserDto appUserDto);

    ActionResponse updateUser(AppUserDto appUserDto);

    ActionResponse removeUser(Integer userId);

    ExternalUser createExternalUser(ExternalUser externalUser);
}
