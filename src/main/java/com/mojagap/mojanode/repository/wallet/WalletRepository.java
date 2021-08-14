package com.mojagap.mojanode.repository.wallet;


import com.mojagap.mojanode.model.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    default String getWalletBalanceQuery() {
        return "SELECT IFNULL(SUM(\n" +
                "                      IF(wt.transaction_type IN ('TOP_UP', 'RECEIVED_WALLET_TRANSFER'), IFNULL(wt.amount, 0),\n" +
                "                         -IFNULL(wt.amount, 0))\n" +
                "                  ), 0)      AS actualBalance,\n" +
                "       IFNULL(SUM(\n" +
                "                      CASE\n" +
                "                          WHEN (wt.transaction_type IN ('TOP_UP', 'RECEIVED_WALLET_TRANSFER') AND\n" +
                "                                wt.transaction_status IN ('APPROVED', 'SUCCESS'))\n" +
                "                              THEN IFNULL(wt.amount, 0)\n" +
                "                          WHEN wt.transaction_status IN ('PENDING')\n" +
                "                              THEN 0\n" +
                "                          ELSE\n" +
                "                              -IFNULL(wt.amount, 0)\n" +
                "                          END\n" +
                "                  ), 0)      AS availableBalance,\n" +
                "       IFNULL(SUM(\n" +
                "                      IF((wt.transaction_type IN\n" +
                "                          ('SENT_WALLET_TRANSFER', 'DISBURSEMENT', 'DISBURSEMENT_CHARGE', 'TRANSFER_CHARGE') AND\n" +
                "                          wt.transaction_status = 'PENDING'), IFNULL(wt.amount, 0),\n" +
                "                         0)\n" +
                "                  ), 0)      AS onHoldBalance,\n" +
                "       COUNT(DISTINCT wt.id) AS numberOfTransactions\n" +
                "FROM wallet_transaction wt\n" +
                "WHERE wt.wallet_id = :walletId\n" +
                "  AND wt.transaction_status NOT IN ('FAILED', 'REJECTED')";
    }

    @Query(value = "" +
            "SELECT w.* FROM wallet w " +
            "INNER JOIN account acc " +
            "   ON acc.id = w.account_id " +
            "WHERE w.record_status = 'ACTIVE' " +
            "   AND acc.account_type = 'BACK_OFFICE' " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Wallet> findDefaultWallet();
}
