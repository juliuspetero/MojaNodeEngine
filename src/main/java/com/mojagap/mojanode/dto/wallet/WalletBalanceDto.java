package com.mojagap.mojanode.dto.wallet;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WalletBalanceDto {
    private BigDecimal actualBalance;
    private BigDecimal availableBalance;
    private BigDecimal onHoldBalance;
    private Integer numberOfTransactions;
}
