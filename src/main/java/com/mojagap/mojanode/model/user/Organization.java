package com.mojagap.mojanode.model.user;

import com.mojagap.mojanode.controller.organization.contract.OrganizationSummary;
import com.mojagap.mojanode.model.AuditEntity;
import com.mojagap.mojanode.helper.AppContext;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity(name = "organization")
@Setter
@NoArgsConstructor
public class Organization extends AuditEntity {
    private String name;
    private Date registrationDate;
    private OrganizationType OrganizationType;
    private String registrationNumber;
    private String address;
    private String email;
    private String phoneNumber;
    private List<AppUser> appUsers = new ArrayList<>();

    public Organization(OrganizationSummary organizationSummary) {
        BeanUtils.copyProperties(organizationSummary, this);
        AppContext.stamp(this);
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "registration_date")
    public Date getRegistrationDate() {
        return registrationDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "org_type")
    public Organization.OrganizationType getOrganizationType() {
        return OrganizationType;
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.LAZY)
    public List<AppUser> getAppUsers() {
        return appUsers;
    }

    public enum OrganizationType {
        SCHOOL,
        RELIGIOUS,
        BUSINESS,
        NGO,
        COMPANY,
        OTHER
    }
}
