package com.mojagap.mojanode.model.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType {
    TOP_UP(1),
    TOP_UP_CHARGE(1),
    DISBURSEMENT(2),
    RECEIVED_WALLET_TRANSFER(3),
    SENT_WALLET_TRANSFER(4),
    DISBURSEMENT_CHARGE(5),
    TRANSFER_CHARGE(6);
    private Integer index;
}
