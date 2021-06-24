package com.mojagap.mojanode.dto.account;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.i18n.phonenumbers.NumberParseException;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.account.CountryCode;
import com.mojagap.mojanode.model.company.CompanyType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class AccountDto {
    private Integer accountId;
    private String name;
    private String address;
    private String countryCode;
    private String email;
    private String contactPhoneNumber;
    private String accountType;
    private List<AppUserDto> users;
    private List<CompanyDto> companies;

    public AccountDto(Integer accountId, String accountType, String countryCode) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.countryCode = countryCode;
    }

    public void isValid() {
        PowerValidator.notEmpty(users, ErrorMessages.USER_REQUIRED_WHEN_CREATING_ACCOUNT);
        PowerValidator.ValidEnum(CountryCode.class, countryCode, ErrorMessages.VALID_COUNTRY_REQUIRED);
        PowerValidator.ValidEnum(AccountType.class, accountType, ErrorMessages.VALID_ACCOUNT_TYPE_REQUIRED);
    }

    @SneakyThrows(NumberParseException.class)
    public void isValidCompany() {
        PowerValidator.ValidEnum(CompanyType.class, accountType, ErrorMessages.VALID_COMPANY_TYPE);
        PowerValidator.validStringLength(name, 5, 255, ErrorMessages.INVALID_COMPANY_NAME);
        PowerValidator.validStringLength(address, 10, 255, ErrorMessages.INVALID_LOCATION_ADDRESS);
        PowerValidator.validEmail(email, ErrorMessages.INVALID_EMAIL_ADDRESS);
        PowerValidator.validPhoneNumber(contactPhoneNumber, ErrorMessages.INVALID_PHONE_NUMBER);
    }

}
