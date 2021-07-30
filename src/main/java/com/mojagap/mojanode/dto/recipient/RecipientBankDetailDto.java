package com.mojagap.mojanode.dto.recipient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.utility.CommonUtil;
import com.mojagap.mojanode.model.recipient.RecipientBankDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientBankDetailDto {
    private Integer id;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String branchName;
    private String swiftCode;

    public void isValid() {
        PowerValidator.notNull(bankName, ErrorMessages.BANK_NAME_REQUIRED);
        PowerValidator.notNull(accountName, ErrorMessages.BANK_ACCOUNT_NAME_REQUIRED);
        PowerValidator.notNull(accountNumber, ErrorMessages.BANK_ACCOUNT_NUMBER_REQUIRED);
        PowerValidator.notNull(branchName, ErrorMessages.BANK_BRANCH_NAME_REQUIRED);
        PowerValidator.notNull(swiftCode, ErrorMessages.BANK_SWIFT_CODE_REQUIRED);
    }

    public RecipientBankDetail toRecipientBankDetailEntity() {
        return CommonUtil.copyProperties(this, new RecipientBankDetail());
    }
}
