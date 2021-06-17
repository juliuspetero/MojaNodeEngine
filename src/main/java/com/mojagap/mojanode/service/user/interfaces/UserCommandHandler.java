package com.mojagap.mojanode.service.user.interfaces;

import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.model.http.ExternalUser;

public interface UserCommandHandler {

    AppUserDto createUser(AppUserDto appUserDto);

    AppUserDto updateUser(AppUserDto appUserDto);

    AppUserDto removeUser(Integer userId);

    ExternalUser createExternalUser(ExternalUser externalUser);
}
