package com.mojagap.mojanode.model.recipient;

import com.mojagap.mojanode.model.common.AuditEntity;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Setter
@Entity(name = "recipient_bank_detail")
@NoArgsConstructor
public class RecipientBankDetail extends AuditEntity {
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String branchName;
    private String swiftCode;

    @Column(name = "bank_name")
    public String getBankName() {
        return bankName;
    }

    @Column(name = "account_name")
    public String getAccountName() {
        return accountName;
    }

    @Column(name = "account_number")
    public String getAccountNumber() {
        return accountNumber;
    }

    @Column(name = "branch_name")
    public String getBranchName() {
        return branchName;
    }

    @Column(name = "swift_code")
    public String getSwiftCode() {
        return swiftCode;
    }
}
