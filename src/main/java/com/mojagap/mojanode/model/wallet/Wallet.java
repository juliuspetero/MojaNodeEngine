package com.mojagap.mojanode.model.wallet;

import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.branch.Branch;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.company.Company;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Entity(name = "wallet")
@NoArgsConstructor
public class Wallet extends AuditEntity {
    private BigDecimal actualBalance;
    private BigDecimal availableBalance;
    private BigDecimal onHoldBalance;
    private Integer numberOfTransactions;
    private Account account;
    private Company company;
    private Branch branch;
    private Set<WalletCharge> walletCharges = new HashSet<>();

    @Column(name = "actual_balance")
    public BigDecimal getActualBalance() {
        return actualBalance;
    }

    @Column(name = "available_balance")
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    @Column(name = "on_hold_balance")
    public BigDecimal getOnHoldBalance() {
        return onHoldBalance;
    }

    @Column(name = "number_of_transactions")
    public Integer getNumberOfTransactions() {
        return numberOfTransactions;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    public Account getAccount() {
        return account;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    public Company getCompany() {
        return company;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "branch_id")
    public Branch getBranch() {
        return branch;
    }

    @JoinTable(name = "wallet_wallet_charge", joinColumns = {
            @JoinColumn(name = "wallet_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "wallet_charge_id", referencedColumnName = "id")})
    @OneToMany(targetEntity = WalletCharge.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<WalletCharge> getWalletCharges() {
        return walletCharges;
    }
}
