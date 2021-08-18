package com.mojagap.mojanode.service.wallet;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.wallet.ApplyWalletChargeDto;
import com.mojagap.mojanode.dto.wallet.WalletBalanceDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.wallet.Wallet;
import com.mojagap.mojanode.model.wallet.WalletCharge;
import com.mojagap.mojanode.repository.wallet.WalletChargeRepository;
import com.mojagap.mojanode.repository.wallet.WalletRepository;
import com.mojagap.mojanode.service.wallet.handler.WalletCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

@Service
public class WalletCommandService implements WalletCommandHandler {
    private final WalletRepository walletRepository;
    private final WalletChargeRepository walletChargeRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public WalletCommandService(WalletRepository walletRepository, WalletChargeRepository walletChargeRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.walletRepository = walletRepository;
        this.walletChargeRepository = walletChargeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public ActionResponse deactivateWallet(Integer id) {
        AppContext.isPermittedAccountTypes(AccountType.BACK_OFFICE);
        Wallet wallet = walletRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Wallet.class.getSimpleName(), "id")));
        PowerValidator.isFalse(AuditEntity.RecordStatus.INACTIVE.equals(wallet.getRecordStatus()), "Wallet charge is already active");
        wallet.setRecordStatus(AuditEntity.RecordStatus.INACTIVE);
        AppContext.stamp(wallet);
        walletRepository.save(wallet);
        return new ActionResponse(id);
    }

    @Override
    @Transactional
    public ActionResponse activateWallet(Integer id) {
        AppContext.isPermittedAccountTypes(AccountType.BACK_OFFICE);
        Wallet wallet = walletRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Wallet.class.getSimpleName(), "id")));
        PowerValidator.isFalse(AuditEntity.RecordStatus.ACTIVE.equals(wallet.getRecordStatus()), "Wallet charge is already active");
        wallet.setRecordStatus(AuditEntity.RecordStatus.ACTIVE);
        AppContext.stamp(wallet);
        walletRepository.save(wallet);
        return new ActionResponse(id);
    }

    @Override
    @Transactional
    public ActionResponse applyWalletCharge(ApplyWalletChargeDto applyWalletChargeDto) {
        AppContext.isPermittedAccountTypes(AccountType.BACK_OFFICE);
        List<WalletCharge> walletCharges = walletChargeRepository.findAllById(applyWalletChargeDto.getWalletChargeIds());
        List<Wallet> wallets = applyWalletChargeDto.getApplyToAll() ? walletRepository.findAll() : walletRepository.findAllById(applyWalletChargeDto.getWalletIds());
        for (Wallet wallet : wallets) {
            wallet.setWalletCharges(new HashSet<>(walletCharges));
            AppContext.stamp(wallet);
        }
        walletRepository.saveAll(wallets);
        return new ActionResponse("Charges successfully applied to wallets");
    }

    @Override
    public void recalculateWalletDerivedBalances(Wallet wallet) {
        String walletBalanceQuery = walletRepository.getWalletBalanceQuery();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("walletId", wallet.getId());
        WalletBalanceDto walletBalanceDto = jdbcTemplate.queryForObject(walletBalanceQuery, mapSqlParameterSource, (rs, rowNum) ->
                new WalletBalanceDto(
                        rs.getBigDecimal("actualBalance"),
                        rs.getBigDecimal("availableBalance"),
                        rs.getBigDecimal("onHoldBalance"),
                        rs.getInt("numberOfTransactions")
                ));
        wallet.setActualBalance(walletBalanceDto.getActualBalance());
        wallet.setAvailableBalance(walletBalanceDto.getAvailableBalance());
        wallet.setOnHoldBalance(walletBalanceDto.getOnHoldBalance());
        wallet.setNumberOfTransactions(walletBalanceDto.getNumberOfTransactions());
        walletRepository.save(wallet);
    }

}
