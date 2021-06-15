package com.mojagap.mojanode.model.company;

import com.mojagap.mojanode.controller.company.entity.CompanySummary;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.user.AppUser;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity(name = "company")
@Setter
@NoArgsConstructor
public class Company extends AuditEntity {
    private String name;
    private Date registrationDate;
    private CompanyType companyType;
    private String registrationNumber;
    private String address;
    private String email;
    private String phoneNumber;
    private List<AppUser> appUsers = new ArrayList<>();
    private Account account;

    public Company(CompanySummary companySummary) {
        BeanUtils.copyProperties(companySummary, this);
        AppContext.stamp(this);
    }

    @Column(name = "name")
    @Size(min = 6, max = 100)
    public String getName() {
        return name;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "registration_date")
    public Date getRegistrationDate() {
        return registrationDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    public CompanyType getCompanyType() {
        return companyType;
    }

    @Column(name = "registration_number")
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    public Account getAccount() {
        return account;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", fetch = FetchType.LAZY)
    public List<AppUser> getAppUsers() {
        return appUsers;
    }
}
