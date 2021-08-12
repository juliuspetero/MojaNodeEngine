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
public class WalletTransactionRequestDto {
    private Integer id;
    private WalletDto wallet;
    private String transactionType;
    private BigDecimal amount;
    private String transactionStatus;
    private String paymentMethodType;
    private BankCashPayment bankCashPayment;

    public void isValidBankCashPayment() {
        PowerValidator.isTrue(wallet != null && wallet.getId() != null, String.format(ErrorMessages.ENTITY_REQUIRED, "Wallet ID"));
        PowerValidator.isTrue(bankCashPayment != null, String.format(ErrorMessages.ENTITY_REQUIRED, "Bank cash payment detail"));
        PowerValidator.notNull(bankCashPayment.getReceiverBankName(), ErrorMessages.BANK_NAME_REQUIRED);
        PowerValidator.notNull(bankCashPayment.getReceiverAccountName(), ErrorMessages.BANK_ACCOUNT_NAME_REQUIRED);
        PowerValidator.notNull(bankCashPayment.getReceiverAccountNumber(), ErrorMessages.BANK_ACCOUNT_NUMBER_REQUIRED);
        PowerValidator.notNull(bankCashPayment.getReceiverBankBranch(), ErrorMessages.BANK_BRANCH_NAME_REQUIRED);
        PowerValidator.isGreaterThanZero(bankCashPayment.getAmount(), ErrorMessages.TOP_UP_AMOUNT_LESS_THAN_ZERO);
        PowerValidator.notNull(bankCashPayment.getDepositDate(), String.format(ErrorMessages.ENTITY_REQUIRED, "Deposit date"));
        PowerValidator.notNull(bankCashPayment.getDepositorName(), String.format(ErrorMessages.ENTITY_REQUIRED, "Depositor name"));
    }
}
