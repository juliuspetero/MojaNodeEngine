package com.mojagap.mojanode.model.wallet;

import com.mojagap.mojanode.model.common.AuditEntity;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Setter
@Entity(name = "wallet_charge")
@NoArgsConstructor
public class WalletCharge extends AuditEntity {
    private String name;
    private String description;
    private FeeTypeEnum feeTypeEnum;
    private BigDecimal amount;
    private ChargeTypeEnum chargeTypeEnum;
    private CurrencyCode currencyCode;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type_enum")
    public FeeTypeEnum getFeeTypeEnum() {
        return feeTypeEnum;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type_enum")
    public ChargeTypeEnum getChargeTypeEnum() {
        return chargeTypeEnum;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code")
    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }
}
