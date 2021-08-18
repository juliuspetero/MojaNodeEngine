package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.dto.wallet.WalletChargeDto;
import com.mojagap.mojanode.dto.wallet.WalletChargeSqlResultSet;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.wallet.Wallet;
import com.mojagap.mojanode.model.wallet.WalletCharge;
import com.mojagap.mojanode.repository.wallet.WalletRepository;
import com.mojagap.mojanode.service.wallet.handler.WalletChargeQueryHandler;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WalletChargeQueryService implements WalletChargeQueryHandler {

    private final WalletRepository walletRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public WalletChargeQueryService(WalletRepository walletRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.walletRepository = walletRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RecordHolder<WalletChargeDto> getDefaultWalletCharges() {
        AppContext.isPermittedAccountTypes(AccountType.BACK_OFFICE);
        Wallet wallet = walletRepository.findDefaultWallet().orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Default Wallet", "ID")));
        Set<WalletCharge> walletCharges = wallet.getWalletCharges();
        List<WalletChargeDto> walletChargeDtos = walletCharges.stream().map(WalletCharge::toWalletChargeDto).collect(Collectors.toList());
        return new RecordHolder<>(walletChargeDtos.size(), walletChargeDtos);
    }


    @Override
    @SneakyThrows
    public RecordHolder<WalletChargeDto> getWalletCharges(Map<String, String> queryParams) {
        AppContext.isPermittedAccountTypes(AccountType.BACK_OFFICE);
        Arrays.asList(WalletChargeQueryParams.values()).forEach(param -> queryParams.putIfAbsent(param.getValue(), null));
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(queryParams);
        String walletChargeQuery = getWalletChargeQuery();
        if (queryParams.get(WalletChargeQueryParams.WALLET_ID.getValue()) != null) {
            mapSqlParameterSource.addValue(WalletChargeQueryParams.WALLET_IDS.getValue(), List.of(queryParams.get(WalletChargeQueryParams.WALLET_ID.getValue())));
        } else {
            walletChargeQuery = getWalletChargeQueryWithoutWalletIds();
        }

        if (queryParams.get(WalletChargeQueryParams.CREATION_DATE.getValue()) != null) {
            Date creationDate = DateUtil.DefaultDateFormat().parse(queryParams.get(WalletChargeQueryParams.CREATION_DATE.getValue()));
            mapSqlParameterSource.addValue(WalletChargeQueryParams.CREATION_DATE.getValue(), creationDate, Types.DATE);
        }
        Integer limit = queryParams.get(WalletChargeQueryParams.LIMIT.getValue()) != null ? Integer.parseInt(queryParams.get(WalletChargeQueryParams.LIMIT.getValue())) : Integer.MAX_VALUE;
        mapSqlParameterSource.addValue(WalletChargeQueryParams.LIMIT.getValue(), limit, Types.INTEGER);
        Integer offset = queryParams.get(WalletChargeQueryParams.OFFSET.getValue()) != null ? Integer.parseInt(queryParams.get(WalletChargeQueryParams.OFFSET.getValue())) : 0;
        mapSqlParameterSource.addValue(WalletChargeQueryParams.OFFSET.getValue(), offset, Types.INTEGER);
        List<WalletChargeSqlResultSet> sqlResultSets = jdbcTemplate.query(walletChargeQuery, mapSqlParameterSource, new WalletChargeMapper());
        List<WalletChargeDto> walletDtos = sqlResultSets.stream().map(this::fromSqlResultSet).collect(Collectors.toList());
        return new RecordHolder<>(walletDtos.size(), walletDtos);
    }

    private WalletChargeDto fromSqlResultSet(WalletChargeSqlResultSet resultSet) {
        WalletChargeDto walletChargeDto = new WalletChargeDto();
        BeanUtils.copyProperties(resultSet, walletChargeDto);
        walletChargeDto.setCreatedBy(new AppUserDto(resultSet.getCreatedById(), resultSet.getCreatedByFirstName(), resultSet.getCreatedByLastName()));
        return walletChargeDto;
    }

    private static final class WalletChargeMapper implements RowMapper<WalletChargeSqlResultSet> {

        @Override
        public WalletChargeSqlResultSet mapRow(ResultSet resultSet, int i) throws SQLException {
            WalletChargeSqlResultSet walletChargeSqlResultSet = new WalletChargeSqlResultSet();
            walletChargeSqlResultSet.setId(resultSet.getInt(WalletChargeQueryParams.ID.getValue()));
            walletChargeSqlResultSet.setCreationDate(resultSet.getDate(WalletChargeQueryParams.CREATION_DATE.getValue()));
            walletChargeSqlResultSet.setName(resultSet.getString(WalletChargeQueryParams.NAME.getValue()));
            walletChargeSqlResultSet.setDescription(resultSet.getString(WalletChargeQueryParams.DESCRIPTION.getValue()));
            walletChargeSqlResultSet.setFeeTypeEnum(resultSet.getString(WalletChargeQueryParams.FEE_TYPE_ENUM.getValue()));
            walletChargeSqlResultSet.setChargeTypeEnum(resultSet.getString(WalletChargeQueryParams.CHARGE_TYPE_ENUM.getValue()));
            walletChargeSqlResultSet.setCurrencyCode(resultSet.getString(WalletChargeQueryParams.CURRENCY_CODE.getValue()));
            walletChargeSqlResultSet.setAmount(resultSet.getBigDecimal(WalletChargeQueryParams.AMOUNT.getValue()));
            walletChargeSqlResultSet.setCreatedById(resultSet.getInt(WalletChargeQueryParams.CREATED_BY_ID.getValue()));
            walletChargeSqlResultSet.setCreatedByFirstName(resultSet.getString(WalletChargeQueryParams.CREATED_BY_FIRST_NAME.getValue()));
            walletChargeSqlResultSet.setCreatedByLastName(resultSet.getString(WalletChargeQueryParams.CREATED_BY_LAST_NAME.getValue()));
            return walletChargeSqlResultSet;
        }
    }

    private final String BASE_WALLET_CHARGE_QUERY = "" +
            "SELECT wc.id                AS id,\n" +
            "       wc.name              AS name,\n" +
            "       wc.description       AS description,\n" +
            "       wc.fee_type_enum     AS feeTypeEnum,\n" +
            "       wc.amount            AS amount,\n" +
            "       wc.charge_type_enum  AS chargeTypeEnum,\n" +
            "       wc.currency_code     AS currencyCode,\n" +
            "       createdBy.id         AS createdById,\n" +
            "       createdBy.first_name AS createdByFirstName,\n" +
            "       createdBy.last_name  AS createdByLastName\n" +
            "FROM wallet_charge wc\n" +
            "         LEFT OUTER JOIN app_user createdBy\n" +
            "                         ON createdBy.id = wc.created_by\n" +
            "         LEFT OUTER JOIN wallet_wallet_charge wwc\n" +
            "                         ON wwc.wallet_charge_id = wc.id\n" +
            "         LEFT OUTER JOIN wallet wl\n" +
            "                         ON wl.id = wwc.wallet_id\n" +
            "WHERE (wc.id = :id OR :id IS NULL)\n" +
            "  AND (wc.name LIKE CONCAT('%', :name, '%') OR :name IS NULL)\n" +
            "  AND (wc.amount LIKE CONCAT('%', :amount, '%') OR :amount IS NULL)\n" +
            "  AND (wc.fee_type_enum LIKE CONCAT('%', :feeTypeEnum, '%') OR :feeTypeEnum IS NULL)\n" +
            "  AND (wc.charge_type_enum LIKE CONCAT('%', :chargeTypeEnum, '%') OR :chargeTypeEnum IS NULL)\n" +
            "  AND (wc.currency_code LIKE CONCAT('%', :currencyCode, '%') OR :currencyCode IS NULL)\n" +
            "  AND (CONCAT(createdBy.first_name, '', createdBy.last_name) LIKE\n" +
            "       CONCAT('%', REPLACE(:createdByFullName, ' ', ''), '%') OR\n" +
            "       :createdByFullName IS NULL)\n";


    private String getWalletChargeQuery() {
        return BASE_WALLET_CHARGE_QUERY +
                "  AND (wl.id IN (:walletIds))\n" +
                "LIMIT :limit OFFSET :offset";
    }

    private String getWalletChargeQueryWithoutWalletIds() {
        return BASE_WALLET_CHARGE_QUERY +
                "LIMIT :limit OFFSET :offset";
    }

    @AllArgsConstructor
    @Getter
    public enum WalletChargeQueryParams {
        LIMIT("limit"),
        OFFSET("offset"),
        ID("id"),
        NAME("name"),
        DESCRIPTION("description"),
        FEE_TYPE_ENUM("onHoldBalance"),
        AMOUNT("amount"),
        CHARGE_TYPE_ENUM("chargeTypeEnum"),
        CURRENCY_CODE("currencyCode"),
        CREATED_BY_ID("createdById"),
        CREATED_BY_FIRST_NAME("createdByFirstName"),
        CREATED_BY_LAST_NAME("createdByLastName"),
        CREATION_DATE("creationDate"),
        WALLET_ID("walletId"),
        WALLET_IDS("walletIds");
        private final String value;
    }


}
