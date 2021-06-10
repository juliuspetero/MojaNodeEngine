package com.mojagap.mojanode.model.user;

import com.mojagap.mojanode.controller.user.entity.AppUserSummary;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.model.AuditEntity;
import com.mojagap.mojanode.model.EntityCategory;
import com.mojagap.mojanode.model.role.UserRole;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.Date;

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
    private EntityCategory category;
    private Organization organization;
    private UserRole role;

    public AppUser(AppUserSummary appUserSummary, Organization organization) {
        BeanUtils.copyProperties(appUserSummary, this);
        this.organization = organization;
        AppContext.stamp(this);
    }

    public AppUser(AppUserSummary appUserSummary) {
        BeanUtils.copyProperties(appUserSummary, this);
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

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    public EntityCategory getCategory() {
        return category;
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
