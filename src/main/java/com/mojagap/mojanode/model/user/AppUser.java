package com.mojagap.mojanode.model.user;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mojagap.mojanode.controller.user.contract.AppUserContract;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.model.AuditEntity;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Entity(name = "app_user")
@NoArgsConstructor
public class AppUser extends AuditEntity {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String idNumber;
    private String address;
    private String email;
    private String phoneNumber;
    private String password;
    private Boolean verified = Boolean.FALSE;
    private Organization organization;
    private UserRole role;

    public AppUser(AppUserContract appUserContract, Organization organization) {
        BeanUtils.copyProperties(appUserContract, this);
        this.organization = organization;
        AppContext.stamp(this);
    }

    public AppUser(AppUserContract appUserContract) {
        BeanUtils.copyProperties(appUserContract, this);
        AppContext.stamp(this);
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    @Column(name = "id_number")
    public String getIdNumber() {
        return idNumber;
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

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    @Column(name = "is_verified")
    public Boolean getVerified() {
        return verified;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    public Organization getOrganization() {
        return organization;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    public UserRole getRole() {
        return role;
    }
}
