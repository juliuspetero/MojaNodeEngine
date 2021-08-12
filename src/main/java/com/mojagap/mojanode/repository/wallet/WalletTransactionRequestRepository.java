package com.mojagap.mojanode.repository.wallet;

import com.mojagap.mojanode.model.wallet.WalletTransactionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRequestRepository extends JpaRepository<WalletTransactionRequest, Integer> {
}
