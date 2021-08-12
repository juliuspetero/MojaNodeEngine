package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.model.wallet.Wallet;
import com.mojagap.mojanode.repository.wallet.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletTransactionCommandService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public WalletTransactionCommandService(WalletTransactionRepository walletTransactionRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


}

