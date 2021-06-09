package com.mojagap.mojanode.controller.user.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.helper.ApplicationConstants;
import com.mojagap.mojanode.model.user.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class AppUserContract {
    private Integer id;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = ApplicationConstants.DD_MMM_YYY)
    private Date dateOfBirth;
    private String id_number;
    private String address;
    private String email;
    private String phoneNumber;
    private String password;
    private Boolean verified;
    private Integer organizationId;
    private String organizationName;
    private Integer roleId;
    private String roleName;

    public AppUserContract(AppUser appUser) {
        BeanUtils.copyProperties(appUser, this);
        this.organizationId = appUser.getOrganization() != null ? appUser.getOrganization().getId() : null;
    }

}
