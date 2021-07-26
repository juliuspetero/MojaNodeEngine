package com.mojagap.mojanode.model.wallet;

import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.transaction.TransactionStatus;
import com.mojagap.mojanode.model.transaction.TransactionType;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Setter
@Entity(name = "wallet_transaction")
@NoArgsConstructor
public class WalletTransaction extends AuditEntity {
    private TransactionType transactionType;
    private BigDecimal amount;
    private Wallet wallet;
    private TransactionStatus transactionStatus;
}
