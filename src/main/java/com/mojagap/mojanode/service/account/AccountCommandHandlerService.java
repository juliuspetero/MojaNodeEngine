package com.mojagap.mojanode.service.account;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.dto.role.PermissionDto;
import com.mojagap.mojanode.dto.role.RoleDto;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.infrastructure.security.AppUserDetails;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.account.CountryCode;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.model.role.Permission;
import com.mojagap.mojanode.model.role.PermissionEnum;
import com.mojagap.mojanode.model.role.Role;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.account.AccountRepository;
import com.mojagap.mojanode.repository.role.PermissionRepository;
import com.mojagap.mojanode.service.account.handler.AccountCommandHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountCommandHandlerService implements AccountCommandHandler {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    protected final HttpServletResponse httpServletResponse;

    @Autowired
    public AccountCommandHandlerService(PasswordEncoder passwordEncoder, AccountRepository accountRepository, PermissionRepository permissionRepository,
                                        ModelMapper modelMapper, AuthenticationManager authenticationManager, HttpServletResponse httpServletResponse) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.permissionRepository = permissionRepository;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    @Transactional
    public AppUserDto createAccount(AccountDto accountDto) {
        accountDto.isValid();
        PowerValidator.notEmpty(accountDto.getUsers(), ErrorMessages.USER_REQUIRED_WHEN_CREATING_ACCOUNT);
        AppUserDto appUserDto = accountDto.getUsers().get(0);
        appUserDto.isValid();
        AppUser appUser = new AppUser(appUserDto);
        AppContext.stamp(appUser);
        appUser.setModifiedBy(appUser);
        appUser.setCreatedBy(appUser);
        appUser.setVerified(Boolean.FALSE);
        String rawPassword = appUserDto.getPassword();
        appUser.setPassword(passwordEncoder.encode(appUserDto.getPassword()));

        Account account = new Account(accountDto);
        account.setRecordStatus(AuditEntity.RecordStatus.INACTIVE);
        AppContext.stamp(account);
        AppContext.stamp(account);
        account.setCreatedBy(appUser);
        account.setModifiedBy(appUser);
        account.getAppUsers().add(appUser);
        appUser.setAccount(account);

        AccountType accountType = account.getAccountType();
        switch (accountType) {
            case INDIVIDUAL -> {
                account.setAddress(appUserDto.getAddress());
                account.setEmail(appUserDto.getEmail());
                account.setContactPhoneNumber(appUserDto.getPhoneNumber());
                account.setName(appUserDto.getFirstName() + " " + appUserDto.getLastName());
            }
            case COMPANY -> {
                accountDto.isValidCompany();
                PowerValidator.notEmpty(accountDto.getCompanies(), ErrorMessages.COMPANY_DETAILS_REQUIRED);
                CompanyDto companyDto = accountDto.getCompanies().get(0);
                companyDto.isValid();
                account.setAddress(companyDto.getAddress());
                account.setEmail(companyDto.getEmail());
                account.setContactPhoneNumber(companyDto.getPhoneNumber());
                account.setName(companyDto.getName());
                appUser.setRole(createSuperUserRole(account));
                Company company = modelMapper.map(companyDto, Company.class);
                company.setAccount(account);
                AppContext.stamp(company);
                appUser.setCompany(company);
                company.setCreatedBy(appUser);
                company.setModifiedBy(appUser);
                account.getCompanies().add(company);
            }
            case PARTNER, BACK_OFFICE -> throw new UnsupportedOperationException("You cannot create a backoffice or partner account at the moment");
            default -> throw new UnsupportedOperationException("The account type provided is not accepted here");
        }
        accountRepository.save(account);
        appUserDto.setPassword(rawPassword);
        return authenticateUser(appUserDto);
    }

    @Override
    public AppUserDto authenticateUser(AppUserDto appUserDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(appUserDto.getEmail(), appUserDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUser appUser = ((AppUserDetails) authentication.getPrincipal()).getAppUser();
        String authenticationToken = generateAuthenticationToken(appUser);
        appUserDto.setAuthentication(authenticationToken);
        BeanUtils.copyProperties(appUser, appUserDto);
        appUserDto.setPassword(null);

        if (EnumSet.of(AccountType.BACK_OFFICE, AccountType.COMPANY).contains(appUser.getAccount().getAccountType())) {
            RoleDto roleDto = modelMapper.map(appUser.getRole(), RoleDto.class);
            List<PermissionDto> permissionDtoList = appUser.getRole().getPermissions()
                    .stream()
                    .map(permission -> modelMapper.map(permission, PermissionDto.class))
                    .collect(Collectors.toList());
            roleDto.setPermissions(permissionDtoList);
            appUserDto.setRole(roleDto);
        }
        Company company = appUser.getCompany();
        if (company != null) {
            appUserDto.setCompany(new CompanyDto(company.getId(), company.getName(), company.getCompanyType().name()));
        }
        Account account = appUser.getAccount();
        appUserDto.setAccount(new AccountDto(account.getId(), account.getAccountType().name(), account.getCountryCode().name()));
        httpServletResponse.setHeader(ApplicationConstants.AUTHENTICATION_HEADER_NAME, authenticationToken);
        return appUserDto;
    }

    public static String generateAuthenticationToken(AppUser appUser) {
        String expirationTime = ApplicationConstants.JWT_EXPIRATION_TIME_IN_MINUTES;
        Date expiryDate = new Date(System.currentTimeMillis() + Long.parseLong(expirationTime) * 60 * 1000);
        String secretKey = Base64.getEncoder().encodeToString(ApplicationConstants.JWT_SECRET_KEY.getBytes());
        Claims claims = Jwts.claims().setSubject(appUser.getEmail());
        claims.put(ApplicationConstants.APP_USER_ID, appUser.getId());
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secretKey).setExpiration(expiryDate).compact();
    }

    @Override
    public ActionResponse updateAccount(AccountDto accountDto) {
        accountDto.isValid();
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = loggedInUser.getAccount();
        account.setAccountType(AccountType.valueOf(accountDto.getAccountType()));
        account.setCountryCode(CountryCode.valueOf(accountDto.getCountryCode()));
        AccountType accountType = account.getAccountType();
        switch (accountType) {
            case INDIVIDUAL -> {
                if (accountDto.getAccountType().equals(AccountType.COMPANY.name())) {
                    setAccountProperties(account, accountDto);
                    PowerValidator.notEmpty(accountDto.getCompanies(), ErrorMessages.COMPANY_DETAILS_REQUIRED);
                    CompanyDto companyDto = accountDto.getCompanies().get(0);
                    companyDto.isValid();
                    Company company = modelMapper.map(companyDto, Company.class);
                    company.setAccount(account);
                    loggedInUser.setRole(createSuperUserRole(account));
                    AppContext.stamp(loggedInUser);
                    AppContext.stamp(company);
                    loggedInUser.setCompany(company);
                    account.getCompanies().add(company);
                }
            }
            case COMPANY -> setAccountProperties(account, accountDto);
            default -> throw new UnsupportedOperationException(ErrorMessages.ACCOUNT_TYPE_NOT_PERMITTED);
        }
        AppContext.stamp(account);
        accountRepository.save(account);
        return new ActionResponse(account.getId());
    }

    @Override
    public ActionResponse approveAccount(Integer accountId) {
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Account.class.getSimpleName(), "ID")));
        PowerValidator.isFalse(AuditEntity.RecordStatus.ACTIVE.equals(account.getRecordStatus()), "Account is already active");
        account.setRecordStatus(AuditEntity.RecordStatus.ACTIVE);
        AppContext.stamp(account);
        account.setApprovedBy(loggedInUser);
        account.setApprovedOn(DateUtil.now());
        accountRepository.saveAndFlush(account);
        return new ActionResponse(accountId);
    }

    private void setAccountProperties(Account account, AccountDto accountDto) {
        accountDto.isValidCompany();
        account.setName(accountDto.getName());
        account.setAddress(accountDto.getAddress());
        account.setEmail(accountDto.getEmail());
        account.setContactPhoneNumber(accountDto.getContactPhoneNumber());
    }

    private Role createSuperUserRole(Account account) {
        Permission superPermission = permissionRepository.findOneByName(PermissionEnum.SUPER_PERMISSION.name());
        return new Role(ApplicationConstants.DEFAULT_ROLE_NAME, ApplicationConstants.DEFAULT_ROLE_DESCRIPTION, account, Collections.singletonList(superPermission));
    }
}
