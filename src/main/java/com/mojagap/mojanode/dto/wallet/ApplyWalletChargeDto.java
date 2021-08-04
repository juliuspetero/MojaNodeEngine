package com.mojagap.mojanode.dto.wallet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class ApplyWalletChargeDto {
    private List<Integer> walletIds;
    private List<Integer> walletChargeIds;
    private Boolean applyToAll;
}
