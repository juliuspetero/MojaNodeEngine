package com.mojagap.mojanode.dto.wallet;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class WalletSqlResultSet {
    private Integer id;
    private BigDecimal actualBalance;
    private BigDecimal availableBalance;
    private BigDecimal onHoldBalance;
    private Integer numberOfTransactions;
    private Date creationDate;

    private Integer accountId;
    private String countryCode;
    private String accountType;
    private String accountStatus;

    private Integer companyId;
    private String companyName;
    private String companyType;
    private String companyStatus;
    private Date companyOpeningDate;

    private Integer branchId;
    private String branchName;
    private Date branchOpeningDate;
    private String branchStatus;
}
