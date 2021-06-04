package com.mojagap.mojanode.controller.organization.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.controller.user.contract.UserSummary;
import com.mojagap.mojanode.model.user.Organization;
import com.mojagap.mojanode.helper.ApplicationConstants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class OrganizationSummary {
    private Integer id;
    private String name;
    @JsonFormat(pattern = ApplicationConstants.DD_MMM_YYY)
    private Date registrationDate;
    private Organization.OrganizationType organizationType;
    private String registrationNumber;
    private String address;
    private String email;
    private String phoneNumber;
    private List<UserSummary> userSummaries = new ArrayList<>();
}
