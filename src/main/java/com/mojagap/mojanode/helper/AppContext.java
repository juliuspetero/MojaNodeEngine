package com.mojagap.mojanode.helper;

import com.mojagap.mojanode.helper.exception.BadRequestException;
import com.mojagap.mojanode.helper.exception.RecordNotFoundException;
import com.mojagap.mojanode.helper.utility.DateUtils;
import com.mojagap.mojanode.model.AuditEntity;
import com.mojagap.mojanode.model.user.AppUser;

import java.util.Date;
import java.util.Objects;

public class AppContext {

    public static AppUser getLoggedInUser() {
        return null;
//        throw new RecordNotFoundException("The DB is empty!!!!");
    }

    public static <T extends AuditEntity> T stamp(T entity) {
        AppUser appUser = getLoggedInUser();
        Date now = DateUtils.now();
        entity.setModifiedBy(appUser);
        entity.setModifiedOn(now);
        if (Objects.isNull(entity.getCreatedBy())) {
            entity.setCreatedBy(appUser);
        }
        if (Objects.isNull(entity.getCreatedOn())) {
            entity.setCreatedOn(now);
        }
        return entity;
    }
}
