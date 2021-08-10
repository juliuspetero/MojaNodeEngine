package com.mojagap.mojanode.service.company;


import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.dto.company.CompanySqlResultSet;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.utility.Util;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.service.company.handler.CompanyQueryHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompanyQueryService implements CompanyQueryHandler {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ModelMapper modelMapper;

    @Autowired
    public CompanyQueryService(NamedParameterJdbcTemplate jdbcTemplate, ModelMapper modelMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public RecordHolder<CompanyDto> getCompanies(Map<String, String> queryParams) {
        Arrays.asList(CompanyQueryParams.values()).forEach(param -> queryParams.putIfAbsent(param.getValue(), null));
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(queryParams);
        List<Integer> branches = AppContext.getCompaniesOfLoggedInUser().stream().map(Company::getId).collect(Collectors.toList());
        mapSqlParameterSource.addValue(CompanyQueryParams.LOGGED_IN_USER_COMPANY_IDS.getValue(), branches);
        Integer limit = queryParams.get(CompanyQueryParams.LIMIT.getValue()) != null ? Integer.parseInt(queryParams.get(CompanyQueryParams.LIMIT.getValue())) : Integer.MAX_VALUE;
        mapSqlParameterSource.addValue(CompanyQueryParams.LIMIT.getValue(), limit, Types.INTEGER);
        Integer offset = queryParams.get(CompanyQueryParams.OFFSET.getValue()) != null ? Integer.parseInt(queryParams.get(CompanyQueryParams.OFFSET.getValue())) : 0;
        mapSqlParameterSource.addValue(CompanyQueryParams.OFFSET.getValue(), offset, Types.INTEGER);
        List<CompanySqlResultSet> sqlResultSets = jdbcTemplate.query(companyQuery(), mapSqlParameterSource, new CompanyMapper());
        List<CompanyDto> companyDtos = sqlResultSets.stream().map(this::toCompanyDto).collect(Collectors.toList());
        return new RecordHolder<>(companyDtos.size(), companyDtos);
    }

    private CompanyDto toCompanyDto(CompanySqlResultSet companySqlResultSet) {
        CompanyDto companyDto = Util.copyProperties(companySqlResultSet, new CompanyDto());
        companyDto.setParentCompany(new CompanyDto(companySqlResultSet.getParentCompanyId(), companySqlResultSet.getParentCompanyName(), null, companySqlResultSet.getParentCompanyStatus()));
        companyDto.setCreatedByUser(new AppUserDto(companySqlResultSet.getCreatedById(), companySqlResultSet.getCreatedByFirstName(), companySqlResultSet.getCreatedByLastName()));
        companyDto.setAccount(new AccountDto(companySqlResultSet.getAccountId()));
        return companyDto;
    }

    private static final class CompanyMapper implements RowMapper<CompanySqlResultSet> {

        @Override
        public CompanySqlResultSet mapRow(ResultSet resultSet, int i) throws SQLException {
            CompanySqlResultSet companySqlResultSet = new CompanySqlResultSet();
            companySqlResultSet.setId(resultSet.getInt(CompanyQueryParams.ID.getValue()));
            companySqlResultSet.setName(resultSet.getString(CompanyQueryParams.NAME.getValue()));
            companySqlResultSet.setStatus(resultSet.getString(CompanyQueryParams.STATUS.getValue()));
            companySqlResultSet.setCompanyType(resultSet.getString(CompanyQueryParams.COMPANY_TYPE.getValue()));
            companySqlResultSet.setEmail(resultSet.getString(CompanyQueryParams.EMAIL.getValue()));
            companySqlResultSet.setPhoneNumber(resultSet.getString(CompanyQueryParams.PHONE_NUMBER.getValue()));
            companySqlResultSet.setAddress(resultSet.getString(CompanyQueryParams.ADDRESS.getValue()));
            companySqlResultSet.setOpeningDate(resultSet.getDate(CompanyQueryParams.OPENING_DATE.getValue()));
            companySqlResultSet.setRegistrationDate(resultSet.getDate(CompanyQueryParams.REGISTRATION_DATE.getValue()));
            companySqlResultSet.setRegistrationNumber(resultSet.getString(CompanyQueryParams.REGISTRATION_NUMBER.getValue()));
            companySqlResultSet.setAccountId(resultSet.getInt(CompanyQueryParams.ACCOUNT_ID.getValue()));
            companySqlResultSet.setParentCompanyId(resultSet.getInt(CompanyQueryParams.PARENT_COMPANY_ID.getValue()));
            companySqlResultSet.setParentCompanyName(resultSet.getString(CompanyQueryParams.PARENT_COMPANY_NAME.getValue()));
            companySqlResultSet.setParentCompanyStatus(resultSet.getString(CompanyQueryParams.PARENT_COMPANY_STATUS.getValue()));
            companySqlResultSet.setCreatedById(resultSet.getInt(CompanyQueryParams.CREATED_BY_ID.getValue()));
            companySqlResultSet.setCreatedByFirstName(resultSet.getString(CompanyQueryParams.CREATED_BY_FIRST_NAME.getValue()));
            companySqlResultSet.setCreatedByLastName(resultSet.getString(CompanyQueryParams.CREATED_BY_LAST_NAME.getValue()));
            return companySqlResultSet;
        }
    }


    private String companyQuery() {
        return "" +
                "SELECT com.id                  AS id,\n" +
                "       com.name                AS name,\n" +
                "       com.record_status       AS status,\n" +
                "       com.company_type        AS companyType,\n" +
                "       com.email               AS email,\n" +
                "       com.phone_number        AS phoneNumber,\n" +
                "       com.registration_date   AS registrationDate,\n" +
                "       com.registration_number AS registrationNumber,\n" +
                "       com.address             AS address,\n" +
                "       com.account_id          AS accountId,\n" +
                "       com.created_on          AS openingDate,\n" +
                "       parent.id               AS parentCompanyId,\n" +
                "       parent.name             AS parentCompanyName,\n" +
                "       parent.record_status    AS parentCompanyStatus,\n" +
                "       parent.company_type     AS parentCompanyType,\n" +
                "       createdBy.id            AS createdById,\n" +
                "       createdBy.first_name    AS createdByFirstName,\n" +
                "       createdBy.last_name     AS createdByLastName\n" +
                "FROM company com\n" +
                "         INNER JOIN company parent\n" +
                "                    ON parent.id = com.parent_company_id\n" +
                "         INNER JOIN app_user createdBy\n" +
                "                    ON createdBy.id = com.created_by\n" +
                "WHERE (com.id = :id OR :id IS NULL)\n" +
                "  AND (com.name LIKE CONCAT('%', :name, '%') OR :name IS NULL)\n" +
                "  AND (com.record_status LIKE CONCAT('%', :status, '%') OR :status IS NULL)\n" +
                "  AND (com.company_type LIKE CONCAT('%', :companyType, '%') OR :companyType IS NULL)\n" +
                "  AND (com.parent_company_id = :parentCompanyId OR :parentCompanyId IS NULL)\n" +
                "  AND (com.registration_date = DATE(:registrationDate) OR :registrationDate IS NULL)\n" +
                "  AND (com.registration_number LIKE CONCAT('%', :registrationNumber, '%') OR :registrationNumber IS NULL)\n" +
                "  AND (com.account_id = :accountId OR :accountId IS NULL)\n" +
                "  AND (com.email LIKE CONCAT('%', :email, '%') OR :email IS NULL)\n" +
                "  AND (com.phone_number LIKE CONCAT('%', :phoneNumber, '%') OR :phoneNumber IS NULL)\n" +
                "  AND (com.address LIKE CONCAT('%', :address, '%') OR :address IS NULL)\n" +
                "  AND (com.parent_company_id = :parentCompanyId OR :parentCompanyId IS NULL)\n" +
                "  AND (com.id IN (:companyIds))";
    }

    @AllArgsConstructor
    @Getter
    public enum CompanyQueryParams {
        LIMIT("limit"),
        OFFSET("offset"),
        ID("id"),
        NAME("name"),
        OPENING_DATE("openingDate"),
        STATUS("status"),
        COMPANY_TYPE("companyType"),
        EMAIL("email"),
        PHONE_NUMBER("phoneNumber"),
        REGISTRATION_DATE("registrationDate"),
        REGISTRATION_NUMBER("registrationNumber"),
        PARENT_COMPANY_ID("parentCompanyId"),
        PARENT_COMPANY_NAME("parentCompanyName"),
        PARENT_COMPANY_STATUS("parentCompanyStatus"),
        ADDRESS("address"),
        ACCOUNT_ID("accountId"),
        CREATED_BY_ID("createdById"),
        CREATED_BY_FIRST_NAME("createdByFirstName"),
        CREATED_BY_LAST_NAME("createdByLastName"),
        LOGGED_IN_USER_COMPANY_IDS("companyIds");
        private final String value;
    }


}
