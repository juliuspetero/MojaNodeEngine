package com.mojagap.mojanode.controller.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.account.CountryCode;
import com.mojagap.mojanode.model.company.Company;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class AccountSummary {
    private String authentication;
    private String name;
    private String address;
    private CountryCode countryCode;
    private String email;
    private String contactPhoneNumber;
    private AccountType accountType;
    private List<Company> companies = new ArrayList<>();
}
