package com.mojagap.mojanode.service.user.handler;

import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.AppUser;
import lombok.SneakyThrows;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface UserQueryHandler {

    RecordHolder<AppUserDto> getAppUsersByQueryParams(Map<String, String> queryParams);

    ExternalUser getExternalUserById(Integer id);

    List<AppUser> getExternalUsers();
}
