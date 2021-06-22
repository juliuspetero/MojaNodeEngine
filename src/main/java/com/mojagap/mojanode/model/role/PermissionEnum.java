package com.mojagap.mojanode.model.role;

public enum PermissionEnum {
    SUPER_PERMISSION,
    AUTHENTICATED,

    READ_USER_ACCOUNTS,
    UPDATE_USER_ACCOUNTS,
    APPROVE_USER_ACCOUNTS,

    READ_APPLICATION_USERS,
    CREATE_APPLICATION_USERS,
    UPDATE_APPLICATION_USERS,

    READ_USER_ROLES,
    CREATE_USER_ROLES,
    UPDATE_USER_ROLES,
    READ_USER_PERMISSION;
}
