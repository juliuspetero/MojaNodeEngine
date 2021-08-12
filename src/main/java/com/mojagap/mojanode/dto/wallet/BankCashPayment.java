package com.mojagap.mojanode.dto.wallet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BankCashPayment {
    private String receiverBankName;
    private String receiverBankBranch;
    private String receiverAccountNumber;
    private String receiverAccountName;
    private BigDecimal amount;
    @JsonFormat(pattern = DateUtil.DATE_FORMAT_BY_SLASH)
    private Date depositDate;
    private String depositorName;
    private String comment;
    private List<Integer> documents;
}
