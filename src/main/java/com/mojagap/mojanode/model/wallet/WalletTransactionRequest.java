package com.mojagap.mojanode.model.wallet;

import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.transaction.TransactionStatus;
import com.mojagap.mojanode.model.transaction.TransactionType;
import com.mojagap.mojanode.model.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Entity(name = "wallet_transaction_request")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionRequest extends AuditEntity {
    private Wallet wallet;
    private TransactionType transactionType;
    private BigDecimal amount;
    private TransactionStatus transactionStatus;
    private AppUser approvedBy;
    private Date approvedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    public Wallet getWallet() {
        return wallet;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    public TransactionType getTransactionType() {
        return transactionType;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by")
    public AppUser getApprovedBy() {
        return approvedBy;
    }

    @Column(name = "approved_on")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getApprovedOn() {
        return approvedOn;
    }
}
