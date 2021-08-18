package com.mojagap.mojanode.dto.wallet;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class WalletChargeSqlResultSet {
    private Integer id;
    private String name;
    private String description;
    private String feeTypeEnum;
    private BigDecimal amount;
    private String chargeTypeEnum;
    private String currencyCode;
    private Integer createdById;
    private String createdByFirstName;
    private String createdByLastName;
    private Date creationDate;
}
