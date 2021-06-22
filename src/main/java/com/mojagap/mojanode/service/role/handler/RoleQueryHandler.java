package com.mojagap.mojanode.service.role.handler;

import com.mojagap.mojanode.dto.role.PermissionDto;
import com.mojagap.mojanode.dto.role.RoleDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.role.Permission;

import java.util.Map;

public interface RoleQueryHandler {

    RecordHolder<RoleDto> getRoles(Map<String, String> queryParams);

    RecordHolder<PermissionDto> getPermissions(Map<String, String> queryParams);
}
