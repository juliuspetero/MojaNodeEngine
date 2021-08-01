package com.mojagap.mojanode.dto.recipient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RecipientSqlResultSet {
    private Integer id;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = DateUtil.DATE_FORMAT_BY_SLASH)
    private Date dateOfBirth;
    @JsonFormat(pattern = DateUtil.DATE_FORMAT_BY_SLASH)
    private Date registrationDate;
    private String idNumber;
    private String idTypeEnum;
    private String address;
    private String email;
    private String phoneNumber;
    private String status;

    private Integer accountId;
    private String accountType;
    private String countryCode;
    private String accountStatus;

    private Integer companyId;
    private String companyName;
    private String companyType;
    @JsonFormat(pattern = DateUtil.DATE_FORMAT_BY_SLASH)
    private Date companyOpeningDate;
    private String companyStatus;

    private Integer branchId;
    private String branchName;
    @JsonFormat(pattern = DateUtil.DATE_FORMAT_BY_SLASH)
    private Date branchOpeningDate;
    private String branchStatus;

    private Integer bankDetailId;
    private String bankName;
    private String bankAccountName;
    private String bankAccountNumber;
    private String BankBranchName;
    private String bankSwiftCode;
}

