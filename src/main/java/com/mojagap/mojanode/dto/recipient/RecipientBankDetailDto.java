package com.mojagap.mojanode.dto.recipient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class RecipientBankDetailDto {
    private Integer id;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String branchName;
    private String swiftCode;
}
