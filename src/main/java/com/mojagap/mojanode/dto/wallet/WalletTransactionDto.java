package com.mojagap.mojanode.dto.wallet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
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
public class WalletTransactionDto {
    private Integer id;
    private WalletDto wallet;
    private String transactionType;
    private BigDecimal amount;
    private String transactionStatus;
    private String paymentMethodType;


    public void isValidTopUp() {
        PowerValidator.isTrue(wallet != null && wallet.getId() != null, String.format(ErrorMessages.ENTITY_REQUIRED, "Wallet ID"));
        PowerValidator.isGreaterThanZero(amount, ErrorMessages.TOP_UP_AMOUNT_LESS_THAN_ZERO);
    }
}
