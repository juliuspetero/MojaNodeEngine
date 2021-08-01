package com.mojagap.mojanode.dto.recipient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import com.mojagap.mojanode.model.common.IdTypeEnum;
import com.opencsv.bean.CsvDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Date;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class RecipientCsvDto {
    private Integer id;
    private String firstName;
    private String lastName;
    @CsvDate(value = DateUtil.DATE_FORMAT_BY_MINUS)
    private Date dateOfBirth;
    private String idNumber;
    private String idTypeEnum;
    private String address;
    private String email;
    private String phoneNumber;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String branchName;
    private String swiftCode;

    @SneakyThrows
    public void isValid() {
        PowerValidator.validStringLength(firstName, 5, 255, ErrorMessages.INVALID_FIRST_NAME);
        PowerValidator.validStringLength(lastName, 5, 255, ErrorMessages.INVALID_LAST_NAME);
        PowerValidator.validStringLength(idNumber, 5, 255, ErrorMessages.INVALID_ID_NUMBER_PROVIDED);
        PowerValidator.ValidEnum(IdTypeEnum.class, idTypeEnum, ErrorMessages.INVALID_ID_TYPE);
        PowerValidator.notNull(dateOfBirth, ErrorMessages.DATE_OF_BIRTH_REQUIRED);
        PowerValidator.validStringLength(address, 10, 255, ErrorMessages.INVALID_LOCATION_ADDRESS);
        PowerValidator.validEmail(email, ErrorMessages.INVALID_EMAIL_ADDRESS);
        PowerValidator.validPhoneNumber(phoneNumber, ErrorMessages.INVALID_PHONE_NUMBER);
        PowerValidator.notNull(bankName, ErrorMessages.BANK_NAME_REQUIRED);
        PowerValidator.notNull(accountName, ErrorMessages.BANK_ACCOUNT_NAME_REQUIRED);
        PowerValidator.notNull(accountNumber, ErrorMessages.BANK_ACCOUNT_NUMBER_REQUIRED);
        PowerValidator.notNull(branchName, ErrorMessages.BANK_BRANCH_NAME_REQUIRED);
        PowerValidator.notNull(swiftCode, ErrorMessages.BANK_SWIFT_CODE_REQUIRED);
    }
}
