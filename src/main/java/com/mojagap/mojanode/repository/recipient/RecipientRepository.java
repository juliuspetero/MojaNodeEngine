package com.mojagap.mojanode.repository.recipient;

import com.mojagap.mojanode.model.recipient.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Integer> {

    @Query(value = "SELECT * FROM recipient r " +
            "WHERE r.record_status = 'ACTIVE' AND r.id = :id AND r.account_id = :accountId",
            nativeQuery = true)
    Optional<Recipient> findByIdAndAccountId(Integer id, Integer accountId);
}
