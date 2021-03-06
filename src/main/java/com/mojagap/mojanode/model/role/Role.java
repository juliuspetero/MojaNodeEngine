package com.mojagap.mojanode.model.role;


import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.common.BaseEntity;
import com.mojagap.mojanode.model.account.Account;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Setter
@Entity(name = "role")
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    private String name;
    private String description;
    private AuditEntity.RecordStatus status = AuditEntity.RecordStatus.ACTIVE;
    private Account account;
    private List<Permission> permissions;

    public Role(String name, String description, Account account, AuditEntity.RecordStatus status, List<Permission> permissions) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.permissions = permissions;
        this.account = account;
    }

    @Column(name = "name")
    @NotBlank(message = "Role name is required")
    public String getName() {
        return name;
    }

    @Column(name = "description")
    @NotBlank(message = "Role description is needed")
    public String getDescription() {
        return description;
    }

    @Column(name = "record_status")
    @Enumerated(EnumType.STRING)
    public AuditEntity.RecordStatus getStatus() {
        return status;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    public Account getAccount() {
        return account;
    }

    @JoinTable(name = "role_permission", joinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "permission_id", referencedColumnName = "id")})
    @OneToMany(targetEntity = Permission.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<Permission> getPermissions() {
        return permissions;
    }
}
