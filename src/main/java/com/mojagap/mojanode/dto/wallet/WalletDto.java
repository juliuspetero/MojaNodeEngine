package com.mojagap.mojanode.dto.wallet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.dto.company.CompanyDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class WalletDto {
    private BigDecimal actualBalance;
    private BigDecimal availableBalance;
    private AccountDto account;
    private CompanyDto company;
    private BranchDto branch;
}
