package com.mojagap.mojanode.service.role.handler;

import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.dto.role.RoleDto;

public interface RoleCommandHandler {

    ActionResponse createRole(RoleDto roleDto);

    ActionResponse updateRole(RoleDto roleDto, Integer roleId);

    ActionResponse removeRole(Integer roleId);

}
