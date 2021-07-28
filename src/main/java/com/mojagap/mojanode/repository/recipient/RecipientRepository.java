package com.mojagap.mojanode.repository.recipient;

import com.mojagap.mojanode.model.branch.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipientRepository extends JpaRepository<Branch, Integer> {
}
