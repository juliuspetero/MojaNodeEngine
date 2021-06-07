package com.mojagap.mojanode.helper;

import com.mojagap.mojanode.helper.utility.DateUtils;
import com.mojagap.mojanode.model.AuditEntity;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.user.AppUserRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext context;

    private static AppUser loggedInUser;

    public static final Map<String, Object> properties = new HashMap<>();

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        AppContext.context = context;
    }


    public static AppUser getLoggedInUser() {
        return loggedInUser = AppContext.getBean(AppUserRepository.class).getById(1);
    }

    public static void setLoggedInUser(AppUser loggedInUser) {
        AppContext.loggedInUser = loggedInUser;
    }

    public static <T extends AuditEntity> T stamp(T entity) {
        AppUser appUser = AppContext.getLoggedInUser();
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
