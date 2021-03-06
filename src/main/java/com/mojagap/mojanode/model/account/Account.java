package com.mojagap.mojanode.model.account;


import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.model.user.AppUser;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Setter
@Entity(name = "account")
@NoArgsConstructor
public class Account extends AuditEntity {
    private String name;
    private String address;
    private CountryCode countryCode;
    private String email;
    private String contactPhoneNumber;
    private AppUser approvedBy;
    private AccountType accountType;
    private Set<AppUser> appUsers = new HashSet<>();
    private Set<Company> companies = new HashSet<>();

    public Account(AccountDto accountDto) {
        this.countryCode = CountryCode.getByCode(accountDto.getCountryCode());
        this.accountType = AccountType.valueOf(accountDto.getAccountType());
    }

    @NotNull(message = "Account name cannot be empty")
    public String getName() {
        return name;
    }

    @Length(min = 10, max = 1000, message = "Provide a valid address")
    public String getAddress() {
        return address;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "country_code")
    @NotNull(message = "Please provide a valid country code")
    public CountryCode getCountryCode() {
        return countryCode;
    }

    @NotBlank(message = "Contact Phone Number is required")
    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    @Column(name = "email")
    @Email(message = "Please provide a valid email address")
    public String getEmail() {
        return email;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by")
    public AppUser getApprovedBy() {
        return approvedBy;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    @NotNull(message = "Account Type cannot be empty")
    public AccountType getAccountType() {
        return accountType;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.LAZY)
    public Set<AppUser> getAppUsers() {
        return appUsers;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.LAZY)
    public Set<Company> getCompanies() {
        return companies;
    }
}

