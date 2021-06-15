package com.mojagap.mojanode.controller.company.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.controller.user.entity.AppUserSummary;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.model.company.CompanyType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class CompanySummary {
    private Integer id;
    private String name;
    @JsonFormat(pattern = ApplicationConstants.DD_MMM_YYY)
    private Date registrationDate;
    private CompanyType companyType;
    private String registrationNumber;
    private String address;
    private String email;
    private String phoneNumber;
    private List<AppUserSummary> userSummaries = new ArrayList<>();
}
