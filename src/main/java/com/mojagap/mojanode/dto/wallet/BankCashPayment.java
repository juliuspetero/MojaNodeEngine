package com.mojagap.mojanode.dto.wallet;

import java.math.BigDecimal;
import java.util.Date;

public class BankCashPayment {
    private String receiverBankName;
    private String receiverBankBranch;
    private String receiverAccountNumber;
    private String receiverAccountName;
    private BigDecimal amount;
    private Date depositDate;
    private String depositorName;
    private String comment;
}
