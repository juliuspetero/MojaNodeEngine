package com.mojagap.mojanode.dto.wallet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.model.wallet.ChargeTypeEnum;
import com.mojagap.mojanode.model.wallet.CurrencyCode;
import com.mojagap.mojanode.model.wallet.FeeTypeEnum;
import com.mojagap.mojanode.model.wallet.WalletCharge;
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
public class WalletChargeDto {
    private Integer id;
    private String name;
    private String description;
    private String feeTypeEnum;
    private BigDecimal amount;
    private String chargeTypeEnum;
    private String currencyCode;
    private AppUserDto createdBy;

    public void isValid() {
        PowerValidator.validStringLength(name, 5, 100, ErrorMessages.INVALID_WALLET_CHARGE_NAME);
        PowerValidator.validStringLength(description, 5, 255, ErrorMessages.INVALID_WALLET_CHARGE_DESCRIPTION);
        PowerValidator.ValidEnum(FeeTypeEnum.class, feeTypeEnum, ErrorMessages.INVALID_FEE_TYPE);
        PowerValidator.ValidEnum(ChargeTypeEnum.class, chargeTypeEnum, ErrorMessages.INVALID_WALLET_CHARGE_TYPE);
        PowerValidator.ValidEnum(CurrencyCode.class, currencyCode, ErrorMessages.VALID_CURRENCY_CODE_REQUIRED);
        PowerValidator.isGreaterThanZero(amount, ErrorMessages.WALLET_CHARGE_AMOUNT_LESS_THAN_ZERO);
    }

    public WalletCharge toWalletChargeEntity(WalletCharge walletCharge) {
        walletCharge.setName(name);
        walletCharge.setDescription(description);
        walletCharge.setFeeTypeEnum(FeeTypeEnum.valueOf(feeTypeEnum));
        walletCharge.setChargeTypeEnum(ChargeTypeEnum.valueOf(chargeTypeEnum));
        walletCharge.setAmount(amount);
        walletCharge.setCurrencyCode(CurrencyCode.valueOf(currencyCode));
        return walletCharge;
    }
}
