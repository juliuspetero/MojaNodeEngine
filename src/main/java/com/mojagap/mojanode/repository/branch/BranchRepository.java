package com.mojagap.mojanode.repository.branch;

import com.mojagap.mojanode.model.branch.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    String topBottomQuery = "" +
            "WITH RECURSIVE branchCTE (id, name, parent_branch_id, company_id, created_on, created_by, modified_on, modified_by,\n" +
            "                          record_status) AS\n" +
            "                   (\n" +
            "                       SELECT id,\n" +
            "                              name,\n" +
            "                              parent_branch_id,\n" +
            "                              company_id,\n" +
            "                              created_on,\n" +
            "                              created_by,\n" +
            "                              modified_on,\n" +
            "                              modified_by,\n" +
            "                              record_status\n" +
            "                       FROM branch\n" +
            "                       WHERE parent_branch_id = :parentId\n" +
            "                          OR id = :parentId\n" +
            "                       UNION\n" +
            "                       DISTINCT\n" +
            "                       SELECT br.id,\n" +
            "                              br.name,\n" +
            "                              br.parent_branch_id,\n" +
            "                              br.company_id,\n" +
            "                              br.created_on,\n" +
            "                              br.created_by,\n" +
            "                              br.modified_on,\n" +
            "                              br.modified_by,\n" +
            "                              br.record_status\n" +
            "                       FROM branchCTE AS cte\n" +
            "                                JOIN branch AS br\n" +
            "                                     ON br.parent_branch_id = cte.id\n" +
            "                   )\n" +
            "SELECT id,\n" +
            "       name,\n" +
            "       parent_branch_id,\n" +
            "       company_id,\n" +
            "       created_on,\n" +
            "       created_by,\n" +
            "       modified_on,\n" +
            "       modified_by,\n" +
            "       record_status\n" +
            "FROM branchCTE";

    String bottomUpQuery = "" +
            "WITH RECURSIVE branchCTE (id, name, parent_branch_id, company_id, created_on, created_by, modified_on, modified_by,\n" +
            "                          record_status) AS\n" +
            "                   (\n" +
            "                       SELECT *\n" +
            "                       FROM branch\n" +
            "                       WHERE parent_branch_id = :childId\n" +
            "                          OR id = :childId\n" +
            "                       UNION\n" +
            "                       DISTINCT\n" +
            "                       SELECT br.*\n" +
            "                       FROM branchCTE AS cte\n" +
            "                                JOIN branch AS br\n" +
            "                                     ON br.id = cte.parent_branch_id\n" +
            "                   )\n" +
            "SELECT *\n" +
            "FROM branchCTE";

    @Query(value = topBottomQuery,
            nativeQuery = true)
    List<Branch> topBottomHierarchy(Integer parentId);

    @Query(value = bottomUpQuery,
            nativeQuery = true)
    List<Branch> bottomTopHierarchy(Integer childId);

    @Override
    @Query(value = "SELECT * FROM branch WHERE record_status = 'ACTIVE' AND id = :branchId",
            nativeQuery = true)
    Optional<Branch> findById(Integer branchId);
}
