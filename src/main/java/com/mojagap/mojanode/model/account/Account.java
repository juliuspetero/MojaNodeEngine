package com.mojagap.mojanode.model.account;


import com.mojagap.mojanode.model.common.AuditEntity;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Entity(name = "account")
@NoArgsConstructor
public class Account extends AuditEntity {
    private String name;
    private String address;
    private CountryCode countryCode;
    private String email;
    private String contactPhoneNumber;
    private AccountType accountType;

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
    @Size(min = 3, max = 3, message = "Please provide a valid country code")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    @NotBlank(message = "Account Type cannot be empty")
    public AccountType getAccountType() {
        return accountType;
    }
}

