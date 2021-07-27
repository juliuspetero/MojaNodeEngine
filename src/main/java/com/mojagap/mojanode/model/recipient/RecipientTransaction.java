package com.mojagap.mojanode.model.recipient;

import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.transaction.PaymentMethodType;
import com.mojagap.mojanode.model.transaction.TransactionStatus;
import com.mojagap.mojanode.model.transaction.TransactionType;
import com.mojagap.mojanode.model.wallet.WalletTransaction;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;


@Setter
@Entity(name = "recipient_transaction")
@NoArgsConstructor
public class RecipientTransaction extends AuditEntity {
    private Recipient recipient;
    private WalletTransaction walletTransaction;
    private BigDecimal amount;
    private String bankAccountNumber;
    private String phoneNumber;
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
    private PaymentMethodType paymentMethodType;
    private Integer platformType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "recipient_id")
    public Recipient getRecipient() {
        return recipient;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_transaction_id")
    public WalletTransaction getWalletTransaction() {
        return walletTransaction;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @Column(name = "bank_account_number")
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    public TransactionType getTransactionType() {
        return transactionType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method_type")
    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    @Column(name = "platform_type")
    public Integer getPlatformType() {
        return platformType;
    }
}
