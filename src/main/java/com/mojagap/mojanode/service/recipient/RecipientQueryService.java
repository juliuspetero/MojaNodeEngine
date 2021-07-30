package com.mojagap.mojanode.service.recipient;

import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.dto.recipient.RecipientBankDetailDto;
import com.mojagap.mojanode.dto.recipient.RecipientDto;
import com.mojagap.mojanode.dto.recipient.RecipientSqlResultSet;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.branch.Branch;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.repository.account.AccountRepository;
import com.mojagap.mojanode.repository.branch.BranchRepository;
import com.mojagap.mojanode.repository.company.CompanyRepository;
import com.mojagap.mojanode.service.recipient.handler.RecipientQueryHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecipientQueryService implements RecipientQueryHandler {
    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final AccountRepository accountRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RecipientQueryService(BranchRepository branchRepository, CompanyRepository companyRepository, AccountRepository accountRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.branchRepository = branchRepository;
        this.companyRepository = companyRepository;
        this.accountRepository = accountRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @SneakyThrows(ParseException.class)
    public RecordHolder<RecipientDto> getRecipients(Map<String, String> queryParams) {
        Account account = AppContext.getLoggedInUser().getAccount();
        Arrays.asList(RecipientQueryParams.values()).forEach(param -> queryParams.putIfAbsent(param.getValue(), null));
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(queryParams);
        AccountType accountType = account.getAccountType();
        String recipientQuery = getRecipientQuery();
        switch (accountType) {
            case INDIVIDUAL:
                mapSqlParameterSource.addValue(RecipientQueryParams.ACCOUNT_IDS.getValue(), List.of(account.getId()));
                break;
            case COMPANY:
                recipientQuery = getCompanyRecipientQuery();
                List<Integer> branches = AppContext.getBranchesOfLoggedInUser().stream().map(Branch::getId).collect(Collectors.toList());
                List<Integer> companies = AppContext.getCompaniesOfLoggedInUser().stream().map(Company::getId).collect(Collectors.toList());
                mapSqlParameterSource.addValue(RecipientQueryParams.BRANCH_IDS.getValue(), branches);
                mapSqlParameterSource.addValue(RecipientQueryParams.COMPANY_IDS.getValue(), companies);
                mapSqlParameterSource.addValue(RecipientQueryParams.ACCOUNT_IDS.getValue(), List.of(account.getId()));
                break;
            case BACK_OFFICE:
                if (queryParams.get(RecipientQueryParams.ACCOUNT_ID.getValue()) != null) {
                    Integer accountId = Integer.parseInt(queryParams.get(RecipientQueryParams.ACCOUNT_ID.getValue()));
                    branches = branchRepository.findByAccountId(accountId).stream().map(Branch::getId).collect(Collectors.toList());
                    companies = companyRepository.findByAccountId(accountId).stream().map(Company::getId).collect(Collectors.toList());
                    mapSqlParameterSource.addValue(RecipientQueryParams.BRANCH_IDS.getValue(), branches);
                    mapSqlParameterSource.addValue(RecipientQueryParams.COMPANY_IDS.getValue(), companies);
                    mapSqlParameterSource.addValue(RecipientQueryParams.ACCOUNT_IDS.getValue(), List.of(accountId));
                } else {
                    branches = branchRepository.findAll().stream().map(Branch::getId).collect(Collectors.toList());
                    companies = companyRepository.findAll().stream().map(Company::getId).collect(Collectors.toList());
                    mapSqlParameterSource.addValue(RecipientQueryParams.BRANCH_IDS.getValue(), branches);
                    mapSqlParameterSource.addValue(RecipientQueryParams.COMPANY_IDS.getValue(), companies);
                    List<Integer> accountIds = accountRepository.findAll().stream().map(Account::getId).collect(Collectors.toList());
                    mapSqlParameterSource.addValue(RecipientQueryParams.ACCOUNT_IDS.getValue(), accountIds);
                }
                break;
            default:
                throw new BadRequestException(String.format(ErrorMessages.ACCOUNT_TYPE_NOT_PERMITTED, accountType.name()));
        }

        if (queryParams.get(RecipientQueryParams.DATE_OF_BIRTH.getValue()) != null) {
            Date dateOfBirth = DateUtil.DefaultDateFormat().parse(queryParams.get(RecipientQueryParams.DATE_OF_BIRTH.getValue()));
            mapSqlParameterSource.addValue(RecipientQueryParams.DATE_OF_BIRTH.getValue(), dateOfBirth, Types.DATE);
        }
        if (queryParams.get(RecipientQueryParams.REGISTRATION_DATE.getValue()) != null) {
            Date registrationDate = DateUtil.DefaultDateFormat().parse(queryParams.get(RecipientQueryParams.REGISTRATION_DATE.getValue()));
            mapSqlParameterSource.addValue(RecipientQueryParams.REGISTRATION_DATE.getValue(), registrationDate, Types.DATE);
        }
        Integer limit = queryParams.get(RecipientQueryParams.LIMIT.getValue()) != null ? Integer.parseInt(queryParams.get(RecipientQueryParams.LIMIT.getValue())) : Integer.MAX_VALUE;
        mapSqlParameterSource.addValue(RecipientQueryParams.LIMIT.getValue(), limit, Types.INTEGER);
        Integer offset = queryParams.get(RecipientQueryParams.OFFSET.getValue()) != null ? Integer.parseInt(queryParams.get(RecipientQueryParams.OFFSET.getValue())) : 0;
        mapSqlParameterSource.addValue(RecipientQueryParams.OFFSET.getValue(), offset, Types.INTEGER);
        List<RecipientSqlResultSet> sqlResultSets = jdbcTemplate.query(recipientQuery, mapSqlParameterSource, new RecipientMapper());
        List<RecipientDto> recipientDtos = sqlResultSets.stream().map(this::fromSqlResultSet).collect(Collectors.toList());
        return new RecordHolder<>(recipientDtos.size(), recipientDtos);
    }

    private RecipientDto fromSqlResultSet(RecipientSqlResultSet resultSet) {
        RecipientDto recipientDto = new RecipientDto();
        BeanUtils.copyProperties(resultSet, recipientDto);
        recipientDto.setAccount(new AccountDto(resultSet.getAccountId(), resultSet.getAccountType(), resultSet.getCountryCode(), resultSet.getAccountStatus()));
        if (resultSet.getCompanyId() != 0 && resultSet.getCompanyId() != null) {
            recipientDto.setCompany(new CompanyDto(resultSet.getCompanyId(), resultSet.getCompanyName(), resultSet.getCompanyType(), resultSet.getCompanyOpeningDate(), resultSet.getCompanyStatus()));
        }
        if (resultSet.getBranchId() != 0 && resultSet.getBranchId() != null) {
            recipientDto.setBranch(new BranchDto(resultSet.getBranchId(), resultSet.getBranchName(), resultSet.getBranchOpeningDate(), resultSet.getBranchStatus()));
        }
        recipientDto.setRecipientBankDetail(new RecipientBankDetailDto(resultSet.getBankDetailId(), resultSet.getBankName(),
                resultSet.getBankAccountName(), resultSet.getBankAccountNumber(), resultSet.getBankBranchName(), resultSet.getBankSwiftCode()));
        return recipientDto;
    }

    private static final class RecipientMapper implements RowMapper<RecipientSqlResultSet> {

        @Override
        public RecipientSqlResultSet mapRow(ResultSet resultSet, int i) throws SQLException {
            RecipientSqlResultSet recipientSqlResultSet = new RecipientSqlResultSet();
            recipientSqlResultSet.setId(resultSet.getInt(RecipientQueryParams.ID.getValue()));
            recipientSqlResultSet.setLastName(resultSet.getString(RecipientQueryParams.LAST_NAME.getValue()));
            recipientSqlResultSet.setFirstName(resultSet.getString(RecipientQueryParams.FIRST_NAME.getValue()));
            recipientSqlResultSet.setDateOfBirth(resultSet.getDate(RecipientQueryParams.DATE_OF_BIRTH.getValue()));
            recipientSqlResultSet.setDateOfBirth(resultSet.getDate(RecipientQueryParams.REGISTRATION_DATE.getValue()));
            recipientSqlResultSet.setIdTypeEnum(resultSet.getString(RecipientQueryParams.ID_TYPE_ENUM.getValue()));
            recipientSqlResultSet.setIdNumber(resultSet.getString(RecipientQueryParams.ID_NUMBER.getValue()));
            recipientSqlResultSet.setAddress(resultSet.getString(RecipientQueryParams.ADDRESS.getValue()));
            recipientSqlResultSet.setEmail(resultSet.getString(RecipientQueryParams.EMAIL.getValue()));
            recipientSqlResultSet.setStatus(resultSet.getString(RecipientQueryParams.STATUS.getValue()));
            recipientSqlResultSet.setPhoneNumber(resultSet.getString(RecipientQueryParams.PHONE_NUMBER.getValue()));

            recipientSqlResultSet.setAccountId(resultSet.getInt(RecipientQueryParams.ACCOUNT_ID.getValue()));
            recipientSqlResultSet.setAccountType(resultSet.getString(RecipientQueryParams.ACCOUNT_TYPE.getValue()));
            recipientSqlResultSet.setCountryCode(resultSet.getString(RecipientQueryParams.COUNTRY_CODE.getValue()));
            recipientSqlResultSet.setAccountStatus(resultSet.getString(RecipientQueryParams.ACCOUNT_STATUS.getValue()));

            recipientSqlResultSet.setCompanyId(resultSet.getInt(RecipientQueryParams.COMPANY_ID.getValue()));
            recipientSqlResultSet.setCompanyName(resultSet.getString(RecipientQueryParams.COMPANY_NAME.getValue()));
            recipientSqlResultSet.setCompanyType(resultSet.getString(RecipientQueryParams.COMPANY_TYPE.getValue()));
            recipientSqlResultSet.setCompanyOpeningDate(resultSet.getDate(RecipientQueryParams.COMPANY_OPENING_DATE.getValue()));
            recipientSqlResultSet.setCompanyStatus(resultSet.getString(RecipientQueryParams.COMPANY_STATUS.getValue()));

            recipientSqlResultSet.setBranchId(resultSet.getInt(RecipientQueryParams.BRANCH_ID.getValue()));
            recipientSqlResultSet.setBranchName(resultSet.getString(RecipientQueryParams.BRANCH_NAME.getValue()));
            recipientSqlResultSet.setBranchOpeningDate(resultSet.getDate(RecipientQueryParams.BRANCH_OPENING_DATE.getValue()));
            recipientSqlResultSet.setBranchStatus(resultSet.getString(RecipientQueryParams.BRANCH_STATUS.getValue()));

            recipientSqlResultSet.setBankName(resultSet.getString(RecipientQueryParams.BANK_NAME.getValue()));
            recipientSqlResultSet.setBankAccountName(resultSet.getString(RecipientQueryParams.BANK_ACCOUNT_NAME.getValue()));
            recipientSqlResultSet.setBankAccountNumber(resultSet.getString(RecipientQueryParams.BANK_ACCOUNT_NUMBER.getValue()));
            recipientSqlResultSet.setBankBranchName(resultSet.getString(RecipientQueryParams.BANK_BRANCH_NAME.getValue()));
            recipientSqlResultSet.setBankSwiftCode(resultSet.getString(RecipientQueryParams.BANK_SWIFT_CODE.getValue()));
            return recipientSqlResultSet;
        }
    }

    private final String BASE_RECIPIENT_QUERY = "" +
            "SELECT rcp.id               AS id,\n" +
            "       rcp.last_name        AS lastName,\n" +
            "       rcp.first_name       AS firstName,\n" +
            "       rcp.date_of_birth    AS dateOfBirth,\n" +
            "       rcp.id_number        AS idNumber,\n" +
            "       rcp.id_type_enum     AS idTypeEnum,\n" +
            "       rcp.address          AS address,\n" +
            "       rcp.email            AS email,\n" +
            "       rcp.phone_number     AS phoneNumber,\n" +
            "       rcp.created_on       AS registrationDate,\n" +
            "       rcp.record_status    AS status,\n" +
            "       createdBy.id         AS createdByUserId,\n" +
            "       createdBy.first_name AS createdByUserFirstName,\n" +
            "       createdBy.last_name  AS createdByUserLastName,\n" +
            "       acc.id               AS accountId,\n" +
            "       acc.account_type     AS accountType,\n" +
            "       acc.country_code     AS countryCode,\n" +
            "       acc.record_status    AS accountStatus,\n" +
            "       com.id               AS companyId,\n" +
            "       com.name             AS companyName,\n" +
            "       com.created_on       AS companyOpeningDate,\n" +
            "       com.company_type     AS companyType,\n" +
            "       com.record_status    AS companyStatus,\n" +
            "       br.id                AS branchId,\n" +
            "       br.name              AS branchName,\n" +
            "       br.created_on        AS branchOpeningDate,\n" +
            "       br.record_status     AS branchStatus,\n" +
            "       rbd.id               AS bankDetailId,\n" +
            "       rbd.bank_name        AS bankName,\n" +
            "       rbd.account_name     AS bankAccountName,\n" +
            "       rbd.account_name     AS bankAccountNumber,\n" +
            "       rbd.branch_name      AS bankBranchName,\n" +
            "       rbd.swift_code       AS bankSwiftCode\n" +
            "FROM recipient rcp\n" +
            "         INNER JOIN recipient_bank_detail rbd\n" +
            "                    ON rbd.id = rcp.recipient_bank_detail_id\n" +
            "         INNER JOIN account acc\n" +
            "                    ON acc.id = rcp.account_id\n" +
            "         INNER JOIN app_user createdBy\n" +
            "                    ON createdBy.id = rcp.created_by\n" +
            "         INNER JOIN app_user modifiedBy\n" +
            "                    ON modifiedBy.id = rcp.modified_by\n" +
            "         LEFT OUTER JOIN company com\n" +
            "                         ON com.id = rcp.company_id\n" +
            "         LEFT OUTER JOIN branch br\n" +
            "                         ON br.id = rcp.branch_id\n" +
            "WHERE (rcp.id = :id OR :id IS NULL)\n" +
            "  AND (CONCAT(rcp.first_name, '', rcp.last_name) LIKE\n" +
            "       CONCAT('%', REPLACE(:fullName, ' ', ''), '%') OR\n" +
            "       :fullName IS NULL)\n" +
            "  AND (rcp.date_of_birth = DATE(:dateOfBirth) OR :dateOfBirth IS NULL)\n" +
            "  AND (rcp.id_number LIKE CONCAT('%', :idNumber, '%') OR :idNumber IS NULL)\n" +
            "  AND (rcp.id_type_enum LIKE CONCAT('%', :idTypeEnum, '%') OR :idTypeEnum IS NULL)\n" +
            "  AND (rcp.address LIKE CONCAT('%', :address, '%') OR :address IS NULL)\n" +
            "  AND (rcp.email LIKE CONCAT('%', :email, '%') OR :email IS NULL)\n" +
            "  AND (rcp.phone_number LIKE CONCAT('%', :phoneNumber, '%') OR :phoneNumber IS NULL)\n" +
            "  AND (rcp.created_on = DATE(:registrationDate) OR :registrationDate IS NULL)\n" +
            "  AND (rcp.record_status = :status OR :status IS NULL)\n" +
            "  AND (CONCAT(createdBy.first_name, '', createdBy.last_name) LIKE\n" +
            "       CONCAT('%', REPLACE(:createdByFullName, ' ', ''), '%') OR\n" +
            "       :createdByFullName IS NULL)\n" +
            "  AND (CONCAT(modifiedBy.first_name, '', modifiedBy.last_name) LIKE\n" +
            "       CONCAT('%', REPLACE(:modifiedByFullName, ' ', ''), '%') OR\n" +
            "       :modifiedByFullName IS NULL)\n" +
            "  AND (rcp.account_id IN (:accountIds))\n" +
            "  AND (com.name LIKE CONCAT('%', :companyName, '%') OR :companyName IS NULL)\n" +
            "  AND (br.name LIKE CONCAT('%', :branchName, '%') OR :branchName IS NULL)";

    private String getCompanyRecipientQuery() {
        return BASE_RECIPIENT_QUERY + "" +
                "  AND (rcp.company_id IN (:companyIds))\n" +
                "  AND (rcp.branch_id IN (:branchIds))\n" +
                "LIMIT :limit OFFSET :offset";
    }

    private String getRecipientQuery() {
        return BASE_RECIPIENT_QUERY + "" +
                "LIMIT :limit OFFSET :offset";
    }

    @AllArgsConstructor
    @Getter
    public enum RecipientQueryParams {
        LIMIT("limit"),
        OFFSET("offset"),
        ID("id"),
        FULL_NAME("fullName"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        ADDRESS("address"),
        EMAIL("email"),
        STATUS("status"),
        DATE_OF_BIRTH("dateOfBirth"),
        REGISTRATION_DATE("registrationDate"),
        ID_NUMBER("idNumber"),
        ID_TYPE_ENUM("idTypeEnum"),
        PHONE_NUMBER("phoneNumber"),

        ACCOUNT_IDS("accountIds"),
        ACCOUNT_ID("accountId"),
        ACCOUNT_TYPE("accountType"),
        COUNTRY_CODE("countryCode"),
        ACCOUNT_STATUS("accountStatus"),

        COMPANY_ID("companyId"),
        COMPANY_IDS("companyIds"),
        COMPANY_NAME("companyName"),
        COMPANY_TYPE("companyType"),
        COMPANY_OPENING_DATE("companyOpeningDate"),
        COMPANY_STATUS("companyStatus"),

        BRANCH_ID("branchId"),
        BRANCH_IDS("branchIds"),
        BRANCH_NAME("branchName"),
        BRANCH_OPENING_DATE("branchOpeningDate"),
        BRANCH_STATUS("branchStatus"),

        BANK_NAME("bankName"),
        BANK_ACCOUNT_NAME("bankAccountName"),
        BANK_ACCOUNT_NUMBER("bankAccountNumber"),
        BANK_BRANCH_NAME("BankBranchName"),
        BANK_SWIFT_CODE("bankSwiftCode"),
        CREATED_BY_FULL_NAME("createdByFullName"),
        MODIFIED_BY_FULL_NAME("modifiedByFullName");
        private final String value;
    }
}
