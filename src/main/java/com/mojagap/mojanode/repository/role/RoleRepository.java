package com.mojagap.mojanode.repository.role;

import com.mojagap.mojanode.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query(value = "" +
            "SELECT *\n" +
            "FROM role r\n" +
            "WHERE r.record_status = 'ACTIVE'\n" +
            "  AND (r.id = :id OR :id IS NULL)\n" +
            "  AND (r.account_id = :accountId OR :accountId IS NULL)\n" +
            "  AND (r.name LIKE CONCAT('%', :name, '%') OR :name IS NULL)\n" +
            "  AND (r.description LIKE CONCAT('%', :description, '%') OR :description IS NULL)",
            nativeQuery = true)
    List<Role> findByQueryParams(Integer id, Integer accountId, String name, String description);

    default List<Role> findByAccountIdAndName(String name, Integer accountId) {
        return findByQueryParams(null, accountId, name, null);
    }

    Optional<Role> findByIdAndAccountId(Integer Id, Integer accountId);
}
