package com.mojagap.mojanode.service.branch;

import com.mojagap.mojanode.dto.branch.BranchDto;
import com.mojagap.mojanode.dto.branch.BranchSqlResultSet;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.utility.Util;
import com.mojagap.mojanode.model.branch.Branch;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.branch.handler.BranchQueryHandler;
import com.mojagap.mojanode.service.role.RoleQueryService;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
public class BranchQueryService implements BranchQueryHandler {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BranchQueryService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RecordHolder<BranchDto> getBranches(Map<String, String> queryParams) {
        Arrays.asList(BranchQueryParams.values()).forEach(param -> queryParams.putIfAbsent(param.getValue(), null));
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(queryParams);
        List<Integer> branches = AppContext.getBranchesOfLoggedInUser().stream().map(Branch::getId).collect(Collectors.toList());
        mapSqlParameterSource.addValue(BranchQueryParams.LOGGED_IN_USER_BRANCH_IDS.getValue(), branches);
        Integer limit = queryParams.get(BranchQueryParams.LIMIT.getValue()) != null ? Integer.parseInt(queryParams.get(RoleQueryService.PermissionQueryParams.LIMIT.getValue())) : Integer.MAX_VALUE;
        mapSqlParameterSource.addValue(BranchQueryParams.LIMIT.getValue(), limit, Types.INTEGER);
        Integer offset = queryParams.get(BranchQueryParams.OFFSET.getValue()) != null ? Integer.parseInt(queryParams.get(RoleQueryService.PermissionQueryParams.OFFSET.getValue())) : 0;
        mapSqlParameterSource.addValue(BranchQueryParams.OFFSET.getValue(), offset, Types.INTEGER);
        List<BranchSqlResultSet> sqlResultSets = jdbcTemplate.query(branchQuery(), mapSqlParameterSource, new BranchMapper());
        List<BranchDto> branchDtos = sqlResultSets.stream().map(this::toBranchDto)
                .collect(Collectors.toList());
        return new RecordHolder<>(branchDtos.size(), branchDtos);
    }

    private BranchDto toBranchDto(BranchSqlResultSet branchSqlResultSet) {
        BranchDto branchDto = Util.copyProperties(branchSqlResultSet, new BranchDto());
        branchDto.setParentBranch(new BranchDto(branchSqlResultSet.getParentBranchId(), branchSqlResultSet.getParentBranchName(), null, branchSqlResultSet.getParentBranchStatus()));
        branchDto.setCompany(new CompanyDto(branchSqlResultSet.getCompanyId(), branchSqlResultSet.getCompanyName(), null, branchSqlResultSet.getCompanyStatus()));
        branchDto.setCreatedBy(new AppUserDto(branchSqlResultSet.getCreatedById(), branchSqlResultSet.getCreatedByFirstName(), branchSqlResultSet.getCreatedByLastName()));
        return branchDto;
    }

    private static final class BranchMapper implements RowMapper<BranchSqlResultSet> {

        @Override
        public BranchSqlResultSet mapRow(ResultSet resultSet, int i) throws SQLException {
            BranchSqlResultSet branchSqlResultSet = new BranchSqlResultSet();
            branchSqlResultSet.setId(resultSet.getInt(BranchQueryParams.ID.getValue()));
            branchSqlResultSet.setName(resultSet.getString(BranchQueryParams.NAME.getValue()));
            branchSqlResultSet.setStatus(resultSet.getString(BranchQueryParams.STATUS.getValue()));
            branchSqlResultSet.setOpeningDate(resultSet.getDate(BranchQueryParams.OPENING_DATE.getValue()));
            branchSqlResultSet.setCompanyId(resultSet.getInt(BranchQueryParams.COMPANY_ID.getValue()));
            branchSqlResultSet.setCompanyName(resultSet.getString(BranchQueryParams.COMPANY_NAME.getValue()));
            branchSqlResultSet.setCompanyStatus(resultSet.getString(BranchQueryParams.COMPANY_STATUS.getValue()));
            branchSqlResultSet.setParentBranchId(resultSet.getInt(BranchQueryParams.PARENT_BRANCH_ID.getValue()));
            branchSqlResultSet.setParentBranchName(resultSet.getString(BranchQueryParams.PARENT_BRANCH_NAME.getValue()));
            branchSqlResultSet.setParentBranchStatus(resultSet.getString(BranchQueryParams.PARENT_BRANCH_STATUS.getValue()));
            branchSqlResultSet.setCreatedById(resultSet.getInt(BranchQueryParams.CREATED_BY_ID.getValue()));
            branchSqlResultSet.setCreatedByFirstName(resultSet.getString(BranchQueryParams.CREATED_BY_FIRST_NAME.getValue()));
            branchSqlResultSet.setCreatedByLastName(resultSet.getString(BranchQueryParams.CREATED_BY_LAST_NAME.getValue()));
            return branchSqlResultSet;
        }
    }


    private String branchQuery() {
        return "SELECT br.id                AS id,\n" +
                "       br.name              AS name,\n" +
                "       br.record_status     AS Status,\n" +
                "       br.created_on        AS openingDate,\n" +
                "       com.id               AS companyId,\n" +
                "       com.name             AS companyName,\n" +
                "       com.record_status    AS companyStatus,\n" +
                "       parent.id            AS parentBranchId,\n" +
                "       parent.name          AS parentBranchName,\n" +
                "       parent.record_status AS parentBranchStatus,\n" +
                "       createdBy.id         AS createdById,\n" +
                "       createdBy.first_name AS createdByFirstName,\n" +
                "       createdBy.last_name  AS createdByLastName\n" +
                "FROM branch br\n" +
                "         INNER JOIN company com\n" +
                "                    ON com.id = br.company_id\n" +
                "         INNER JOIN branch parent\n" +
                "                    ON parent.id = br.parent_branch_id\n" +
                "         INNER JOIN app_user createdBy\n" +
                "                    ON createdBy.id = br.created_by\n" +
                "WHERE (br.id = :id OR :id IS NULL)\n" +
                "  AND (br.name LIKE CONCAT('%', :name, '%') OR :name IS NULL)\n" +
                "  AND (br.company_id = :companyId OR :companyId IS NULL)\n" +
                "  AND (br.parent_branch_id = :parentBranchId OR :parentBranchId IS NULL)\n" +
                "  AND (br.id IN (:branchIds))";
    }

    @AllArgsConstructor
    @Getter
    public enum BranchQueryParams {
        LIMIT("limit"),
        OFFSET("offset"),
        ID("id"),
        NAME("name"),
        OPENING_DATE("openingDate"),
        STATUS("Status"),
        COMPANY_ID("companyId"),
        COMPANY_NAME("companyName"),
        COMPANY_STATUS("companyStatus"),
        PARENT_BRANCH_ID("parentBranchId"),
        PARENT_BRANCH_NAME("parentBranchName"),
        PARENT_BRANCH_STATUS("parentBranchId"),
        CREATED_BY_ID("createdById"),
        CREATED_BY_FIRST_NAME("createdByFirstName"),
        CREATED_BY_LAST_NAME("createdByLastName"),
        LOGGED_IN_USER_BRANCH_IDS("branchIds");
        private final String value;
    }
}
