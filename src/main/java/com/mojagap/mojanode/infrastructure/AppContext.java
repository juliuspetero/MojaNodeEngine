package com.mojagap.mojanode.infrastructure;

import com.mojagap.mojanode.infrastructure.security.JwtAuthorizationFilter;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import com.mojagap.mojanode.model.branch.Branch;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.branch.BranchRepository;
import com.mojagap.mojanode.repository.company.CompanyRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext context;
    private static final ThreadLocal<AppUser> APP_USER = new ThreadLocal<>();

    @Getter
    @Setter
    private static List<JwtAuthorizationFilter.RequestSecurity> requestSecurities;

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

    public static List<Company> getCompaniesOfLoggedInUser() {
        List<Company> companies = Collections.emptyList();
        if (getLoggedInUser() != null) {
            companies = getBean(CompanyRepository.class).topBottomHierarchy(getLoggedInUser().getId());
        }
        return companies;
    }

    public static List<Branch> getBranchesOfLoggedInUser() {
        List<Branch> branches = Collections.emptyList();
        if (getLoggedInUser() != null) {
            branches = getBean(BranchRepository.class).topBottomHierarchy(getLoggedInUser().getId());
        }
        return branches;
    }

    public static <T extends AuditEntity> void stamp(T entity) {
        AppUser appUser = AppContext.getLoggedInUser();
        Date now = DateUtil.now();
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
