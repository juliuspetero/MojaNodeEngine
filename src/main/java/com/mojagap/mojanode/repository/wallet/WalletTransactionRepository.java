package com.mojagap.mojanode.repository.wallet;

import com.mojagap.mojanode.model.wallet.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {
}
