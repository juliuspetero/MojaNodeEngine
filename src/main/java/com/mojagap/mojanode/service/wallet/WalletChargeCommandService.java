package com.mojagap.mojanode.service.wallet;


import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.wallet.ApplyWalletChargeDto;
import com.mojagap.mojanode.dto.wallet.WalletChargeDto;
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
import com.mojagap.mojanode.service.wallet.handler.WalletChargeCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class WalletChargeCommandService implements WalletChargeCommandHandler {

    private final WalletChargeRepository walletChargeRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public WalletChargeCommandService(WalletChargeRepository walletChargeRepository, WalletRepository walletRepository) {
        this.walletChargeRepository = walletChargeRepository;
        this.walletRepository = walletRepository;
    }


    @Override
    public ActionResponse createWalletCharge(WalletChargeDto walletChargeDto) {
        walletChargeDto.isValid();
        AppContext.isPermittedAccountTyp(AccountType.BACK_OFFICE);
        List<WalletCharge> walletCharges = walletChargeRepository.findAllByName(walletChargeDto.getName());
        PowerValidator.isEmpty(walletCharges, String.format(ErrorMessages.ENTITY_ALREADY_EXISTS, WalletCharge.class.getSimpleName(), "name"));
        WalletCharge walletCharge = walletChargeDto.toWalletChargeEntity(new WalletCharge());
        AppContext.stamp(walletCharge);
        walletChargeRepository.save(walletCharge);
        return new ActionResponse(walletCharge.getId());

    }

    @Override
    public ActionResponse updateWalletCharge(WalletChargeDto walletChargeDto, Integer id) {
        walletChargeDto.isValid();
        AppContext.isPermittedAccountTyp(AccountType.BACK_OFFICE);
        WalletCharge walletCharge = walletChargeRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, WalletCharge.class.getSimpleName(), "id")));
        if (!walletChargeDto.getName().equals(walletCharge.getName())) {
            List<WalletCharge> walletCharges = walletChargeRepository.findAllByName(walletChargeDto.getName());
            PowerValidator.isEmpty(walletCharges, String.format(ErrorMessages.ENTITY_ALREADY_EXISTS, WalletCharge.class.getSimpleName(), "name"));
        }
        walletCharge = walletChargeDto.toWalletChargeEntity(walletCharge);
        AppContext.stamp(walletCharge);
        walletChargeRepository.save(walletCharge);
        return new ActionResponse(walletCharge.getId());
    }

    @Override
    public ActionResponse activateWalletCharge(Integer id) {
        AppContext.isPermittedAccountTyp(AccountType.BACK_OFFICE);
        WalletCharge walletCharge = walletChargeRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, WalletCharge.class.getSimpleName(), "id")));
        PowerValidator.isFalse(AuditEntity.RecordStatus.ACTIVE.equals(walletCharge.getRecordStatus()), "Wallet charge is already active");
        walletCharge.setRecordStatus(AuditEntity.RecordStatus.ACTIVE);
        AppContext.stamp(walletCharge);
        walletChargeRepository.save(walletCharge);
        return new ActionResponse(id);
    }

    @Override
    public ActionResponse deactivateWalletCharge(Integer id) {
        AppContext.isPermittedAccountTyp(AccountType.BACK_OFFICE);
        WalletCharge walletCharge = walletChargeRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, WalletCharge.class.getSimpleName(), "id")));
        PowerValidator.isFalse(AuditEntity.RecordStatus.INACTIVE.equals(walletCharge.getRecordStatus()), "Wallet charge is already active");
        walletCharge.setRecordStatus(AuditEntity.RecordStatus.INACTIVE);
        AppContext.stamp(walletCharge);
        walletChargeRepository.save(walletCharge);
        return new ActionResponse(id);
    }

    @Override
    public ActionResponse updateDefaultWalletCharge(ApplyWalletChargeDto applyWalletChargeDto) {
        AppContext.isPermittedAccountTyp(AccountType.BACK_OFFICE);
        Wallet wallet = walletRepository.findDefaultWallet().orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Default Wallet", "ID")));
        List<WalletCharge> walletCharges = walletChargeRepository.findAllById(applyWalletChargeDto.getWalletChargeIds());
        wallet.setWalletCharges(new HashSet<>(walletCharges));
        AppContext.stamp(wallet);
        walletRepository.save(wallet);
        return new ActionResponse(wallet.getId());
    }
}
