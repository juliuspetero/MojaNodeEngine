package com.mojagap.mojanode.model.user;


import com.mojagap.mojanode.model.AuditEntity;
import com.mojagap.mojanode.model.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Setter
@Entity(name = "user_role")
@NoArgsConstructor
public class UserRole extends BaseEntity {
    private String name;
    private String description;
    private AuditEntity.RecordStatus status;
    private List<UserPermission> permissions;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "record_status")
    @Enumerated(EnumType.STRING)
    public AuditEntity.RecordStatus getStatus() {
        return status;
    }

    @JoinTable(name = "role_permission", joinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "permission_id", referencedColumnName = "id")})
    @OneToMany(targetEntity = UserPermission.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    public List<UserPermission> getPermissions() {
        return permissions;
    }
}
