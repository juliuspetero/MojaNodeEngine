package com.mojagap.mojanode.service.role;

import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.dto.role.PermissionDto;
import com.mojagap.mojanode.dto.role.RoleDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.role.Permission;
import com.mojagap.mojanode.model.role.Role;
import com.mojagap.mojanode.repository.role.PermissionRepository;
import com.mojagap.mojanode.repository.role.RoleRepository;
import com.mojagap.mojanode.service.role.handler.RoleCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RoleCommandService implements RoleCommandHandler {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;


    @Autowired
    public RoleCommandService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    @Transactional
    public ActionResponse createRole(RoleDto roleDto) {
        roleDto.isValid();
        Account account = AppContext.getLoggedInUser().getAccount();
        PowerValidator.iPermittedAccountType(account.getAccountType(), AccountType.COMPANY, AccountType.BACK_OFFICE);
        List<Integer> permissionIds = roleDto.getPermissions().stream().map(PermissionDto::getId).collect(Collectors.toList());
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        Role role = new Role(roleDto.getName(), roleDto.getDescription(), account, permissions);
        role = roleRepository.save(role);
        return new ActionResponse(role.getId());
    }

    @Override
    public ActionResponse updateRole(RoleDto roleDto, Integer roleId) {
        return null;
    }

    @Override
    public ActionResponse removeRole(Integer roleId) {
        return null;
    }
}
