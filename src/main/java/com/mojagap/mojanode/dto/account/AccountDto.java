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
    private Integer id;
    private String name;
    private String address;
    private String status;
    private String countryCode;
    private String email;
    private String contactPhoneNumber;
    private String accountType;
    private List<AppUserDto> users;
    private List<CompanyDto> companies;

    public AccountDto(Integer id) {
        this.id = id;
    }

    public AccountDto(Integer id, String accountType, String countryCode) {
        this.id = id;
        this.accountType = accountType;
        this.countryCode = countryCode;
    }

    public AccountDto(Integer id, String accountType, String countryCode, String status) {
        this.id = id;
        this.accountType = accountType;
        this.countryCode = countryCode;
        this.status = status;
    }

    public void isValid() {
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
