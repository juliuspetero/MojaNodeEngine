package com.mojagap.mojanode.repository.role;

import com.mojagap.mojanode.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query(value = """
            SELECT *
            FROM role r
            WHERE r.record_status = 'ACTIVE'
              AND (r.id = :id OR :id IS NULL)
              AND (r.account_id = :accountId OR :accountId IS NULL)
              AND (r.name LIKE CONCAT('%', :name, '%') OR :name IS NULL)
              AND (r.description LIKE CONCAT('%', :description, '%') OR :description IS NULL)
            """,
            nativeQuery = true)
    List<Role> findByQueryParams(Integer id, Integer accountId, String name, String description);
}
