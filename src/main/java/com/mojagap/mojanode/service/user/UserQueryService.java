package com.mojagap.mojanode.service.user;


import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.security.AppUserDetails;
import com.mojagap.mojanode.infrastructure.utility.DateUtils;
import com.mojagap.mojanode.model.RecordHolder;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.model.user.IdentificationEnum;
import com.mojagap.mojanode.repository.user.AppUserRepository;
import com.mojagap.mojanode.service.httpgateway.RestTemplateService;
import com.mojagap.mojanode.service.user.entities.AppUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserQueryService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public RecordHolder<AppUserDTO> getAppUsersByQueryParams(Map<String, String> queryParams) {
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Arrays.asList(AppUserQueryParams.values()).forEach(param -> queryParams.putIfAbsent(param.getValue(), null));
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(queryParams);
        if (loggedInUser.getOrganization() != null) {
            mapSqlParameterSource.addValue(AppUserQueryParams.ORGANIZATION_ID.getValue(), loggedInUser.getOrganization().getId());
        }
        if (queryParams.get(AppUserQueryParams.VERIFIED.getValue()) != null) {
            mapSqlParameterSource.addValue(AppUserQueryParams.VERIFIED.getValue(), Boolean.parseBoolean(queryParams.get(AppUserQueryParams.VERIFIED.getValue())), Types.BOOLEAN);
        }
        Integer limit = queryParams.get(AppUserQueryParams.LIMIT.getValue()) != null ? Integer.parseInt(queryParams.get(AppUserQueryParams.LIMIT.getValue())) : Integer.MAX_VALUE;
        mapSqlParameterSource.addValue(AppUserQueryParams.LIMIT.getValue(), limit, Types.INTEGER);
        Integer offset = queryParams.get(AppUserQueryParams.OFFSET.getValue()) != null ? Integer.parseInt(queryParams.get(AppUserQueryParams.OFFSET.getValue())) : 0;
        mapSqlParameterSource.addValue(AppUserQueryParams.OFFSET.getValue(), offset, Types.INTEGER);
        List<AppUserDTO> appUserDTOS = jdbcTemplate.query(appUserQuery(), mapSqlParameterSource, new AppUserMapper());
        return new RecordHolder<>(appUserDTOS.size(), appUserDTOS);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findOneByEmail(s);
        List<GrantedAuthority> authorities = new ArrayList<>();
        appUser.getRole().getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getName())).forEach(authorities::add);
        return new AppUserDetails(appUser, authorities);
    }

    public ExternalUser getExternalUserById(Integer id) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("name", "Peter");
        queryParams.set("age", "56");
        queryParams.set("sex", "MALE");
        return restTemplateService.doHttpGet(ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users/" + id, queryParams, ExternalUser.class);
    }

    public List<AppUser> getExternalUsers() {
        ExternalUser[] externalUsers = restTemplateService.doHttpGet(ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users", null, ExternalUser[].class);
        List<AppUser> appUsers = List.of(externalUsers).stream().map(x -> {
            AppUser appUser = new AppUser();
            appUser.setId(x.getId());
            appUser.setLastName(x.getName());
            appUser.setFirstName(x.getUsername());
            appUser.setPhoneNumber(x.getPhone());
            appUser.setEmail(x.getEmail());
            appUser.setAddress("XXXXXXXXX");
            appUser.setPassword("PASSWORD");
            appUser.setVerified(Boolean.FALSE);
            appUser.setDateOfBirth(DateUtils.now());
            appUser.setId_number(IdentificationEnum.NATIONAL_ID.name());
            AppContext.stamp(appUser);
            return appUser;
        }).collect(Collectors.toList());
        appUserRepository.saveAllAndFlush(appUsers);
        return appUsers;
    }


    private static final class AppUserMapper implements RowMapper<AppUserDTO> {

        @Override
        public AppUserDTO mapRow(ResultSet resultSet, int i) throws SQLException {
            AppUserDTO appUserDTO = new AppUserDTO();
            appUserDTO.setId(resultSet.getInt(AppUserQueryParams.ID.getValue()));
            appUserDTO.setFirstName(resultSet.getString(AppUserQueryParams.FIRST_NAME.getValue()));
            appUserDTO.setLastName(resultSet.getString(AppUserQueryParams.LAST_NAME.getValue()));
            appUserDTO.setDateOfBirth(resultSet.getDate(AppUserQueryParams.DATE_OF_BIRTH.getValue()));
            appUserDTO.setIdNumber(resultSet.getString(AppUserQueryParams.ID_NUMBER.getValue()));
            appUserDTO.setAddress(resultSet.getString(AppUserQueryParams.ADDRESS.getValue()));
            appUserDTO.setEmail(resultSet.getString(AppUserQueryParams.EMAIL.getValue()));
            appUserDTO.setPassword(resultSet.getString(AppUserQueryParams.PASSWORD.getValue()));
            appUserDTO.setVerified(resultSet.getBoolean(AppUserQueryParams.VERIFIED.getValue()));
            appUserDTO.setStatus(resultSet.getString(AppUserQueryParams.STATUS.getValue()));
            appUserDTO.setOrganizationId(resultSet.getInt(AppUserQueryParams.ORGANIZATION_ID.getValue()));
            appUserDTO.setOrganizationName(resultSet.getString(AppUserQueryParams.ORGANIZATION_NAME.getValue()));
            appUserDTO.setCreatedByFullName(resultSet.getString(AppUserQueryParams.CREATED_BY_FULL_NAME.getValue()));
            appUserDTO.setModifiedByFullName(resultSet.getString(AppUserQueryParams.MODIFIED_BY_FULL_NAME.getValue()));
            return appUserDTO;
        }
    }

    @AllArgsConstructor
    @Getter
    public enum AppUserQueryParams {
        LIMIT("limit"),
        OFFSET("offset"),
        ID("id"),
        LAST_NAME("lastName"),
        FIRST_NAME("firstName"),
        SORT_BY("sortBy"),
        ADDRESS("address"),
        EMAIL("email"),
        STATUS("status"),
        DATE_OF_BIRTH("dateOfBirth"),
        ID_NUMBER("idNumber"),
        PHONE_NUMBER("phoneNumber"),
        PASSWORD("password"),
        ORGANIZATION_NAME("organizationName"),
        ORGANIZATION_ID("organizationId"),
        VERIFIED("verified"),
        CREATED_BY_FULL_NAME("createdByFullName"),
        MODIFIED_BY_FULL_NAME("modifiedByFullName");
        private final String value;
    }

    private String appUserQuery() {
        return "SELECT appUser.id                                                AS id,\n" +
                "       appUser.first_name                                       AS firstName,\n" +
                "       appUser.last_name                                        AS lastName,\n" +
                "       appUser.address                                          AS address,\n" +
                "       appUser.email                                            AS email,\n" +
                "       appUser.record_status                                    AS status,\n" +
                "       appUser.date_of_birth                                    AS dateOfBirth,\n" +
                "       appUser.id_number                                        AS idNumber,\n" +
                "       appUser.phone_number                                     AS phoneNumber,\n" +
                "       appUser.is_verified                                      AS verified,\n" +
                "       appUser.password                                         AS password,\n" +
                "       org.id                                                   AS organizationId,\n" +
                "       org.name                                                 AS organizationName,\n" +
                "       CONCAT(createdBy.first_name, ' ', createdBy.last_name)   AS createdByFullName,\n" +
                "       CONCAT(modifiedBy.first_name, ' ', modifiedBy.last_name) AS modifiedByFullName\n" +
                "FROM app_user appUser\n" +
                "         LEFT OUTER JOIN organization org\n" +
                "                         ON org.id = appUser.org_id\n" +
                "         INNER JOIN app_user createdBy\n" +
                "                    ON createdBy.id = appUser.id\n" +
                "         INNER JOIN app_user modifiedBy\n" +
                "                    ON modifiedBy.id = appUser.id\n" +
                "WHERE (appUser.id = :id OR :id IS NULL)\n" +
                "  AND (LOWER(appUser.first_name) LIKE CONCAT('%', :firstName, '%') OR :firstName IS NULL)\n" +
                "  AND (LOWER(appUser.last_name) LIKE CONCAT('%', :lastName, '%') OR :lastName IS NULL)\n" +
                "  AND (LOWER(appUser.address) LIKE CONCAT('%', :address, '%') OR :address IS NULL)\n" +
                "  AND (LOWER(appUser.email) LIKE CONCAT('%', :email, '%') OR :email IS NULL)\n" +
                "  AND (appUser.record_status = :status OR :status IS NULL)\n" +
                "  AND (appUser.date_of_birth = DATE(:dateOfBirth) OR :dateOfBirth IS NULL)\n" +
                "  AND (LOWER(appUser.id_number) LIKE CONCAT('%', :idNumber, '%') OR :idNumber IS NULL)\n" +
                "  AND (LOWER(appUser.phone_number) LIKE CONCAT('%', :phoneNumber, '%') OR :phoneNumber IS NULL)\n" +
                "  AND (appUser.is_verified = :verified OR :verified IS NULL)\n" +
                "  AND (org.id = :organizationId OR :organizationId IS NULL)\n" +
                "  AND (LOWER(org.name) LIKE CONCAT('%', :organizationName, '%') OR :organizationName IS NULL)\n" +
                "  AND (LOWER(CONCAT(createdBy.first_name, ' ', createdBy.last_name)) LIKE CONCAT('%', :createdByFullName, '%') OR\n" +
                "       :createdByFullName IS NULL)\n" +
                "  AND (LOWER(CONCAT(modifiedBy.first_name, ' ', modifiedBy.last_name)) LIKE CONCAT('%', :modifiedByFullName, '%') OR\n" +
                "       :modifiedByFullName IS NULL)\n" +
                "ORDER BY :sortBy\n" +
                "LIMIT :limit OFFSET :offset\n";
    }
}
