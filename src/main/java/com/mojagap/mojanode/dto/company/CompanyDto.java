package com.mojagap.mojanode.dto.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.i18n.phonenumbers.NumberParseException;
import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import com.mojagap.mojanode.model.company.CompanyType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Date;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class CompanyDto {
    private Integer companyId;
    private String name;
    private String companyType;
    @JsonFormat(pattern = DateUtil.DD_MMM_YYY)
    private Date registrationDate;
    private String registrationNumber;
    private String address;
    private String email;
    private String phoneNumber;
    private AccountDto account;
    private List<AppUserDto> appUsers;

    public CompanyDto(Integer companyId, String name, String companyType) {
        this.companyId = companyId;
        this.name = name;
        this.companyType = companyType;
    }

    @SneakyThrows(NumberParseException.class)
    public void isValid() {
        PowerValidator.ValidEnum(CompanyType.class, companyType, ErrorMessages.VALID_COMPANY_TYPE);
        PowerValidator.validStringLength(name, 5, 255, ErrorMessages.INVALID_COMPANY_NAME);
        PowerValidator.notNull(registrationDate, ErrorMessages.COMPANY_REGISTRATION_DATE_REQUIRED);
        PowerValidator.validStringLength(address, 10, 255, ErrorMessages.INVALID_LOCATION_ADDRESS);
        PowerValidator.validEmail(email, ErrorMessages.INVALID_EMAIL_ADDRESS);
        PowerValidator.validPhoneNumber(phoneNumber, ErrorMessages.INVALID_PHONE_NUMBER);
    }

}
