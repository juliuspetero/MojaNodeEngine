package com.mojagap.mojanode.model.aws;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum S3UploadTypeEnum {
    BANK_CASH_DEPOSIT("bank_cash_deposits"),
    MOBILE_MONEY("mobile_money"),
    CHEQUE("cheque");
    private final String folder;
}
