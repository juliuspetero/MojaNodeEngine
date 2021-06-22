package com.mojagap.mojanode.service.role;

import com.mojagap.mojanode.dto.role.PermissionDto;
import com.mojagap.mojanode.dto.role.RoleDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.role.handler.RoleQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class RoleQueryService implements RoleQueryHandler {
    @Override
    public RecordHolder<RoleDto> getRoles(Map<String, String> queryParams) {
        return null;
    }

    @Override
    public RecordHolder<PermissionDto> getPermissions(Map<String, String> queryParams) {
        return null;
    }
}
