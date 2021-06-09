package com.mojagap.mojanode.model.user;

import com.mojagap.mojanode.model.ActionTypeEnum;
import com.mojagap.mojanode.model.BaseEntity;
import com.mojagap.mojanode.model.EntityTypeEnum;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Setter
@Entity(name = "user_permission")
@NoArgsConstructor
public class UserPermission extends BaseEntity {
    private String name;
    private EntityTypeEnum entityType;
    private ActionTypeEnum actionType;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    public EntityTypeEnum getEntityType() {
        return entityType;
    }

    @Column(name = "action_type")
    @Enumerated(EnumType.STRING)
    public ActionTypeEnum getActionType() {
        return actionType;
    }
}
