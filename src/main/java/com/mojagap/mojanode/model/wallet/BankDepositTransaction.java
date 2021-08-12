package com.mojagap.mojanode.model.wallet;

import com.mojagap.mojanode.model.common.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Entity(name = "bank_deposit_transaction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDepositTransaction extends AuditEntity {
    private String receiverBankName;
    private String receiverBankBranch;
    private String receiverAccountNumber;
    private String receiverAccountName;
    private BigDecimal amount;
    private Date depositDate;
    private String depositorName;
    private String comment;

    @Column(name = "receiver_bank_name")
    public String getReceiverBankName() {
        return receiverBankName;
    }

    @Column(name = "receiver_bank_branch")
    public String getReceiverBankBranch() {
        return receiverBankBranch;
    }

    @Column(name = "receiver_account_number")
    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    @Column(name = "receiver_account_name")
    public String getReceiverAccountName() {
        return receiverAccountName;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @Column(name = "deposit_date")
    public Date getDepositDate() {
        return depositDate;
    }

    @Column(name = "depositor_name")
    public String getDepositorName() {
        return depositorName;
    }

    @Column(name = "comment")
    public String getComment() {
        return comment;
    }
}
