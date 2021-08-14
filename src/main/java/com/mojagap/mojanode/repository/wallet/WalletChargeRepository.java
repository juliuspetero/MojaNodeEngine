package com.mojagap.mojanode.repository.wallet;

import com.mojagap.mojanode.model.wallet.WalletCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletChargeRepository extends JpaRepository<WalletCharge, Integer> {

    List<WalletCharge> findAllByName(String name);

    Optional<WalletCharge> findByNameAndChargeTypeEnum(String name, String chargeTypeEnum);
}
