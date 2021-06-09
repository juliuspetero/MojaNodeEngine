package com.mojagap.mojanode.controller.user.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.model.user.UserPermission;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class AppUserContract {
    private Integer id;
    private String authentication;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = ApplicationConstants.DD_MMM_YYY)
    private Date dateOfBirth;
    private String idNumber;
    private String idType;
    private String address;
    private String email;
    private String phoneNumber;
    private String password;
    private Boolean verified;
    private Integer organizationId;
    private String organizationName;
    private Integer roleId;
    private String roleName;
    private List<UserPermission> permissions;
}
