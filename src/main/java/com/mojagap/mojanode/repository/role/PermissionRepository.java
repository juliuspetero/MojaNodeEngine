package com.mojagap.mojanode.repository.role;


import com.mojagap.mojanode.model.role.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    Permission findOneByName(String name);
}
