package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.account.AccountDto;
import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.dto.wallet.WalletDto;
import com.mojagap.mojanode.dto.wallet.WalletSqlResultSet;
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
import com.mojagap.mojanode.service.wallet.handler.WalletQueryHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WalletQueryService implements WalletQueryHandler {
    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final AccountRepository accountRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public WalletQueryService(BranchRepository branchRepository, CompanyRepository companyRepository, AccountRepository accountRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.branchRepository = branchRepository;
        this.companyRepository = companyRepository;
        this.accountRepository = accountRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @SneakyThrows(ParseException.class)
    public RecordHolder<WalletDto> getWallets(Map<String, String> queryParams) {
        Account account = AppContext.getLoggedInUser().getAccount();
        Arrays.asList(WalletQueryParams.values()).forEach(param -> queryParams.putIfAbsent(param.getValue(), null));
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(queryParams);
        AccountType accountType = account.getAccountType();
        String walletQuery = getWalletQuery();
        switch (accountType) {
            case INDIVIDUAL:
                mapSqlParameterSource.addValue(WalletQueryParams.ACCOUNT_IDS.getValue(), List.of(account.getId()));
                break;
            case COMPANY:
                walletQuery = getCompanyWalletQuery();
                List<Integer> branches = AppContext.getBranchesOfLoggedInUser().stream().map(Branch::getId).collect(Collectors.toList());
                List<Integer> companies = AppContext.getCompaniesOfLoggedInUser().stream().map(Company::getId).collect(Collectors.toList());
                mapSqlParameterSource.addValue(WalletQueryParams.BRANCH_IDS.getValue(), branches);
                mapSqlParameterSource.addValue(WalletQueryParams.COMPANY_IDS.getValue(), companies);
                mapSqlParameterSource.addValue(WalletQueryParams.ACCOUNT_IDS.getValue(), List.of(account.getId()));
                break;
            case BACK_OFFICE:
                if (queryParams.get(WalletQueryParams.ACCOUNT_ID.getValue()) != null) {
                    Integer accountId = Integer.parseInt(queryParams.get(WalletQueryParams.ACCOUNT_ID.getValue()));
                    branches = branchRepository.findByAccountId(accountId).stream().map(Branch::getId).collect(Collectors.toList());
                    companies = companyRepository.findByAccountId(accountId).stream().map(Company::getId).collect(Collectors.toList());
                    mapSqlParameterSource.addValue(WalletQueryParams.BRANCH_IDS.getValue(), branches);
                    mapSqlParameterSource.addValue(WalletQueryParams.COMPANY_IDS.getValue(), companies);
                    mapSqlParameterSource.addValue(WalletQueryParams.ACCOUNT_IDS.getValue(), List.of(accountId));
                } else {
                    branches = branchRepository.findAll().stream().map(Branch::getId).collect(Collectors.toList());
                    companies = companyRepository.findAll().stream().map(Company::getId).collect(Collectors.toList());
                    mapSqlParameterSource.addValue(WalletQueryParams.BRANCH_IDS.getValue(), branches);
                    mapSqlParameterSource.addValue(WalletQueryParams.COMPANY_IDS.getValue(), companies);
                    List<Integer> accountIds = accountRepository.findAll().stream().map(Account::getId).collect(Collectors.toList());
                    mapSqlParameterSource.addValue(WalletQueryParams.ACCOUNT_IDS.getValue(), accountIds);
                }
                break;
            default:
                throw new BadRequestException(String.format(ErrorMessages.ACCOUNT_TYPE_NOT_PERMITTED, accountType.name()));
        }

        if (queryParams.get(WalletQueryParams.CREATION_DATE.getValue()) != null) {
            Date creationDate = DateUtil.DefaultDateFormat().parse(queryParams.get(WalletQueryParams.CREATION_DATE.getValue()));
            mapSqlParameterSource.addValue(WalletQueryParams.CREATION_DATE.getValue(), creationDate, Types.DATE);
        }
        Integer limit = queryParams.get(WalletQueryParams.LIMIT.getValue()) != null ? Integer.parseInt(queryParams.get(WalletQueryParams.LIMIT.getValue())) : Integer.MAX_VALUE;
        mapSqlParameterSource.addValue(WalletQueryParams.LIMIT.getValue(), limit, Types.INTEGER);
        Integer offset = queryParams.get(WalletQueryParams.OFFSET.getValue()) != null ? Integer.parseInt(queryParams.get(WalletQueryParams.OFFSET.getValue())) : 0;
        mapSqlParameterSource.addValue(WalletQueryParams.OFFSET.getValue(), offset, Types.INTEGER);
        List<WalletSqlResultSet> sqlResultSets = jdbcTemplate.query(walletQuery, mapSqlParameterSource, new WalletMapper());
        List<WalletDto> walletDtos = sqlResultSets.stream().map(this::fromSqlResultSet).collect(Collectors.toList());
        return new RecordHolder<>(walletDtos.size(), walletDtos);
    }

    private WalletDto fromSqlResultSet(WalletSqlResultSet resultSet) {
        WalletDto walletDto = new WalletDto();
        BeanUtils.copyProperties(resultSet, walletDto);
        walletDto.setAccount(new AccountDto(resultSet.getAccountId(), resultSet.getAccountType(), resultSet.getCountryCode(), resultSet.getAccountStatus()));
        if (resultSet.getCompanyId() != 0 && resultSet.getCompanyId() != null) {
            walletDto.setCompany(new CompanyDto(resultSet.getCompanyId(), resultSet.getCompanyName(), resultSet.getCompanyType(), resultSet.getCompanyOpeningDate(), resultSet.getCompanyStatus()));
        }
        if (resultSet.getBranchId() != 0 && resultSet.getBranchId() != null) {
            walletDto.setBranch(new BranchDto(resultSet.getBranchId(), resultSet.getBranchName(), resultSet.getBranchOpeningDate(), resultSet.getBranchStatus()));
        }
        return walletDto;
    }

    private static final class WalletMapper implements RowMapper<WalletSqlResultSet> {

        @Override
        public WalletSqlResultSet mapRow(ResultSet resultSet, int i) throws SQLException {
            WalletSqlResultSet walletSqlResultSet = new WalletSqlResultSet();
            walletSqlResultSet.setId(resultSet.getInt(WalletQueryParams.ID.getValue()));
            walletSqlResultSet.setActualBalance(resultSet.getBigDecimal(WalletQueryParams.ACTUAL_BALANCE.getValue()));
            walletSqlResultSet.setAvailableBalance(resultSet.getBigDecimal(WalletQueryParams.AVAILABLE_BALANCE.getValue()));
            walletSqlResultSet.setOnHoldBalance(resultSet.getBigDecimal(WalletQueryParams.ON_HOLD_BALANCE.getValue()));
            walletSqlResultSet.setNumberOfTransactions(resultSet.getInt(WalletQueryParams.NUMBER_OF_TRANSACTIONS.getValue()));
            walletSqlResultSet.setCreationDate(resultSet.getDate(WalletQueryParams.CREATION_DATE.getValue()));

            walletSqlResultSet.setAccountId(resultSet.getInt(WalletQueryParams.ACCOUNT_ID.getValue()));
            walletSqlResultSet.setAccountType(resultSet.getString(WalletQueryParams.ACCOUNT_TYPE.getValue()));
            walletSqlResultSet.setCountryCode(resultSet.getString(WalletQueryParams.COUNTRY_CODE.getValue()));
            walletSqlResultSet.setAccountStatus(resultSet.getString(WalletQueryParams.ACCOUNT_STATUS.getValue()));

            walletSqlResultSet.setCompanyId(resultSet.getInt(WalletQueryParams.COMPANY_ID.getValue()));
            walletSqlResultSet.setCompanyName(resultSet.getString(WalletQueryParams.COMPANY_NAME.getValue()));
            walletSqlResultSet.setCompanyType(resultSet.getString(WalletQueryParams.COMPANY_TYPE.getValue()));
            walletSqlResultSet.setCompanyOpeningDate(resultSet.getDate(WalletQueryParams.COMPANY_OPENING_DATE.getValue()));
            walletSqlResultSet.setCompanyStatus(resultSet.getString(WalletQueryParams.COMPANY_STATUS.getValue()));

            walletSqlResultSet.setBranchId(resultSet.getInt(WalletQueryParams.BRANCH_ID.getValue()));
            walletSqlResultSet.setBranchName(resultSet.getString(WalletQueryParams.BRANCH_NAME.getValue()));
            walletSqlResultSet.setBranchOpeningDate(resultSet.getDate(WalletQueryParams.BRANCH_OPENING_DATE.getValue()));
            walletSqlResultSet.setBranchStatus(resultSet.getString(WalletQueryParams.BRANCH_STATUS.getValue()));
            return walletSqlResultSet;
        }
    }

    private final String BASE_WALLET_QUERY = "" +
            "SELECT wl.id                     AS id,\n" +
            "       wl.actual_balance         AS actualBalance,\n" +
            "       wl.available_balance      AS availableBalance,\n" +
            "       wl.on_hold_balance        AS onHoldBalance,\n" +
            "       wl.number_of_transactions AS numberOfTransactions,\n" +
            "       acc.id                    AS accountId,\n" +
            "       acc.country_code          AS countryCode,\n" +
            "       acc.account_type          AS accountType,\n" +
            "       acc.record_status         AS accountStatus,\n" +
            "       com.id                    AS companyId,\n" +
            "       com.name                  AS companyName,\n" +
            "       com.company_type          AS companyType,\n" +
            "       com.record_status         AS companyStatus,\n" +
            "       br.id                     AS branchId,\n" +
            "       br.name                   AS branchName,\n" +
            "       br.created_on             AS branchOpeningDate,\n" +
            "       br.record_status          AS branchStatus\n" +
            "FROM wallet wl\n" +
            "\n" +
            "         LEFT OUTER JOIN app_user createdBy\n" +
            "                         ON createdBY.id = wl.created_by\n" +
            "         LEFT OUTER JOIN account acc\n" +
            "                         ON acc.id = wl.account_id\n" +
            "         LEFT OUTER JOIN company com\n" +
            "                         ON com.id = wl.company_id\n" +
            "         LEFT OUTER JOIN branch br\n" +
            "                         ON br.id = wl.branch_id\n" +
            "WHERE (wl.id = :id OR :id IS NULL)\n" +
            "  AND (wl.actual_balance LIKE CONCAT('%', :actualBalance, '%') OR :actualBalance IS NULL)\n" +
            "  AND (wl.available_balance LIKE CONCAT('%', :availableBalance, '%') OR :availableBalance IS NULL)\n" +
            "  AND (wl.on_hold_balance LIKE CONCAT('%', :onHoldBalance, '%') OR :onHoldBalance IS NULL)\n" +
            "  AND (wl.number_of_transactions = :numberOfTransactions OR :numberOfTransactions IS NULL)\n" +
            "  AND (CONCAT(createdBy.first_name, '', createdBy.last_name) LIKE\n" +
            "       CONCAT('%', REPLACE(:createdByFullName, ' ', ''), '%') OR\n" +
            "       :createdByFullName IS NULL)\n" +
            "  AND (com.name LIKE CONCAT('%', :companyName, '%') OR :companyName IS NULL)\n" +
            "  AND (br.name LIKE CONCAT('%', :branchName, '%') OR :branchName IS NULL)\n" +
            "  AND (wl.account_id IN (:accountIds))";

    private String getCompanyWalletQuery() {
        return BASE_WALLET_QUERY + "" +
                "  AND (wl.company_id IN (:companyIds))\n" +
                "  AND (wl.branch_id IN (:branchIds))\n" +
                "LIMIT :limit OFFSET :offset";
    }

    private String getWalletQuery() {
        return BASE_WALLET_QUERY + "" +
                "LIMIT :limit OFFSET :offset";
    }

    @AllArgsConstructor
    @Getter
    public enum WalletQueryParams {
        LIMIT("limit"),
        OFFSET("offset"),
        ID("id"),
        ACTUAL_BALANCE("actualBalance"),
        AVAILABLE_BALANCE("availableBalance"),
        ON_HOLD_BALANCE("onHoldBalance"),
        NUMBER_OF_TRANSACTIONS("numberOfTransactions"),
        CREATION_DATE("creationDate"),

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
        BRANCH_STATUS("branchStatus");
        private final String value;
    }
}
