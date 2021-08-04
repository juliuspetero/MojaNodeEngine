package com.mojagap.mojanode.dto.wallet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class WalletChargeDto {
    private Integer id;
    private String name;
    private String description;
    private String feeTypeEnum;
    private BigDecimal amount;
    private String chargeTypeEnum;
    private String currencyCode;
}
