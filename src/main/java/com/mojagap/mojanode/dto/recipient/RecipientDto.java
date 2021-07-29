package com.mojagap.mojanode.dto.recipient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import com.mojagap.mojanode.model.common.IdTypeEnum;
import com.mojagap.mojanode.model.recipient.Recipient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class RecipientDto {
    private Integer id;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = DateUtil.DD_MMM_YYY)
    private Date dateOfBirth;
    private String idNumber;
    private String idTypeEnum;
    private String address;
    private String email;
    private String phoneNumber;
    private RecipientBankDetailDto recipientBankDetail;
    private CompanyDto company;
    private BranchDto branch;
    private AccountDto account;

    @SneakyThrows
    public void isValid() {
        PowerValidator.validStringLength(firstName, 5, 255, ErrorMessages.INVALID_FIRST_NAME);
        PowerValidator.notNull(recipientBankDetail, ErrorMessages.RECIPIENT_BANK_DETAIL_REQUIRED);
        PowerValidator.validStringLength(lastName, 5, 255, ErrorMessages.INVALID_LAST_NAME);
        PowerValidator.validStringLength(idNumber, 5, 255, ErrorMessages.INVALID_ID_NUMBER_PROVIDED);
        PowerValidator.ValidEnum(IdTypeEnum.class, idTypeEnum, ErrorMessages.INVALID_ID_TYPE);
        PowerValidator.notNull(dateOfBirth, ErrorMessages.DATE_OF_BIRTH_REQUIRED);
        PowerValidator.validStringLength(address, 10, 255, ErrorMessages.INVALID_LOCATION_ADDRESS);
        PowerValidator.validEmail(email, ErrorMessages.INVALID_EMAIL_ADDRESS);
        PowerValidator.validPhoneNumber(phoneNumber, ErrorMessages.INVALID_PHONE_NUMBER);
    }

    public Recipient toRecipientEntity() {
        Recipient recipient = new Recipient();
        recipient.setFirstName(firstName);
        recipient.setLastName(lastName);
        recipient.setDateOfBirth(dateOfBirth);
        recipient.setIdTypeEnum(IdTypeEnum.valueOf(idTypeEnum));
        recipient.setIdNumber(idNumber);
        recipient.setAddress(address);
        recipient.setEmail(email);
        recipient.setPhoneNumber(phoneNumber);
        return recipient;
    }
}
