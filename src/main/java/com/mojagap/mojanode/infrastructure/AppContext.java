package com.mojagap.mojanode.infrastructure;

import com.mojagap.mojanode.infrastructure.utility.DateUtils;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.user.AppUser;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext context;
    private static final ThreadLocal<AppUser> APP_USER = new ThreadLocal<>();

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> T getBean(String name) {
        return (T) context.getBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        AppContext.context = context;
    }

    public static AppUser getLoggedInUser() {
        return APP_USER.get();
    }

    public static void setLoggedInUser(AppUser loggedInUser) {
        APP_USER.set(loggedInUser);
    }

    public static <T extends AuditEntity> void stamp(T entity) {
        AppUser appUser = AppContext.getLoggedInUser();
        Date now = DateUtils.now();
        entity.setModifiedBy(appUser);
        entity.setModifiedOn(now);
        if (entity.getCreatedBy() == null) {
            entity.setCreatedBy(appUser);
        }
        if (entity.getCreatedOn() == null) {
            entity.setCreatedOn(now);
        }
    }
}
