package com.mojagap.mojanode.model.branch;

import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.company.Company;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "branch")
@Setter
@NoArgsConstructor
public class Branch extends AuditEntity {
    private String name;
    private Branch parentBranch;
    private Account account;
    private Company company;

    public Branch(String name, Company company, Account account) {
        this.name = name;
        this.company = company;
        this.account = account;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_branch_id")
    public Branch getParentBranch() {
        return parentBranch;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    public Account getAccount() {
        return account;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    public Company getCompany() {
        return company;
    }
}
