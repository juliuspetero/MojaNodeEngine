package com.mojagap.mojanode.model.role;

import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.BaseEntity;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Setter
@Entity(name = "permission")
@NoArgsConstructor
public class Permission extends BaseEntity {
    private String name;
    private EntityTypeEnum entityType;
    private ActionTypeEnum actionType;
    private AccountType accountType;

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

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    public AccountType getAccountType() {
        return accountType;
    }
}
