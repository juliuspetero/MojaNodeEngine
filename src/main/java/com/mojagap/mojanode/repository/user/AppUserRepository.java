package com.mojagap.mojanode.repository.user;

import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    AppUser findOneByEmailAndRecordStatus(String email, AuditEntity.RecordStatus recordStatus);

    AppUser findOneByIdAndRecordStatus(Integer id, AuditEntity.RecordStatus recordStatus);

    @Query(value = "SELECT * FROM app_user r WHERE r.record_status='ACTIVE' AND r.role_id =:roleId",
            nativeQuery = true)
    List<AppUser> findByRoleId(Integer roleId);

    @Query(value = "SELECT * FROM app_user r WHERE r.record_status='ACTIVE' AND r.id =:id AND r.account_id = :accountId",
            nativeQuery = true)
    Optional<AppUser> findByIdAndAccountId(Integer id, Integer accountId);
}
