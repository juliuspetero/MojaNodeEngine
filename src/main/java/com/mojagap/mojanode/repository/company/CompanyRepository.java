package com.mojagap.mojanode.repository.company;

import com.mojagap.mojanode.model.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    String topBottomQuery = "" +
            "WITH RECURSIVE companyCTE (id,\n" +
            "                           name,\n" +
            "                           registration_date,\n" +
            "                           registration_number,\n" +
            "                           company_type,\n" +
            "                           account_id,\n" +
            "                           address,\n" +
            "                           email,\n" +
            "                           phone_number,\n" +
            "                           parent_company_id,\n" +
            "                           created_on,\n" +
            "                           modified_on,\n" +
            "                           created_by,\n" +
            "                           modified_by,\n" +
            "                           record_status) AS\n" +
            "                   (\n" +
            "                       SELECT *\n" +
            "                       FROM company\n" +
            "                       WHERE parent_company_id = :parentId\n" +
            "                          OR id = :parentId\n" +
            "                       UNION\n" +
            "                       DISTINCT\n" +
            "                       SELECT com.*\n" +
            "                       FROM companyCTE AS cte\n" +
            "                                JOIN company AS com\n" +
            "                                     ON com.parent_company_id = cte.id\n" +
            "                   )\n" +
            "SELECT *\n" +
            "FROM companyCTE";

    String bottomUpQuery = "" +
            "WITH RECURSIVE companyCTE (id,\n" +
            "                           name,\n" +
            "                           registration_date,\n" +
            "                           registration_number,\n" +
            "                           company_type,\n" +
            "                           account_id,\n" +
            "                           address,\n" +
            "                           email,\n" +
            "                           phone_number,\n" +
            "                           parent_company_id,\n" +
            "                           created_on,\n" +
            "                           modified_on,\n" +
            "                           created_by,\n" +
            "                           modified_by,\n" +
            "                           record_status) AS\n" +
            "                   (\n" +
            "                       SELECT *\n" +
            "                       FROM company\n" +
            "                       WHERE parent_company_id = :childId\n" +
            "                          OR id = :childId\n" +
            "                       UNION\n" +
            "                       DISTINCT\n" +
            "                       SELECT com.*\n" +
            "                       FROM companyCTE AS cte\n" +
            "                                JOIN company AS com\n" +
            "                                     ON com.id = cte.parent_company_id\n" +
            "                   )\n" +
            "SELECT *\n" +
            "FROM companyCTE";

    @Query(value = topBottomQuery,
            nativeQuery = true)
    List<Company> topBottomHierarchy(Integer parentId);

    @Query(value = bottomUpQuery,
            nativeQuery = true)
    List<Company> bottomTopHierarchy(Integer childId);


    @Query(value = "SELECT * FROM company WHERE record_status = 'ACTIVE' AND id = :companyId",
            nativeQuery = true)
    Optional<Company> findCompanyById(Integer companyId);

    @Query(value = "SELECT * FROM company m WHERE m.record_status = 'ACTIVE' AND m.account_id = :accountId",
            nativeQuery = true)
    List<Company> findByAccountId(Integer accountId);
}
