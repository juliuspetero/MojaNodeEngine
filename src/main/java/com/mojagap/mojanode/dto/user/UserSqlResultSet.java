package com.mojagap.mojanode.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
public class UserSqlResultSet {
    private Integer id;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String idNumber;
    private String idType;
    private String address;
    private String email;
    private String phoneNumber;
    private String status;
    private String password;
    private Boolean verified;
    private Integer companyId;
    private String companyName;
    private Integer accountId;
    private String accountType;
    private String modifiedByFullName;
    private String createdByFullName;
}
