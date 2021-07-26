package com.mojagap.mojanode.model.recipient;

import com.mojagap.mojanode.model.branch.Branch;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.common.IdTypeEnum;
import com.mojagap.mojanode.model.company.Company;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Entity(name = "recipient")
@NoArgsConstructor
public class Recipient extends AuditEntity {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String idNumber;
    private IdTypeEnum idTypeEnum;
    private String address;
    private String email;
    private String phoneNumber;
    private RecipientBankDetail recipientBankDetail;
    private Company company;
    private Branch branch;

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    @Column(name = "date_of_birth")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    @Column(name = "id_number")
    public String getIdNumber() {
        return idNumber;
    }

    @Column(name = "id_type_enum")
    public IdTypeEnum getIdTypeEnum() {
        return idTypeEnum;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "recipient_bank_detail_id")
    public RecipientBankDetail getRecipientBankDetail() {
        return recipientBankDetail;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id")
    public Company getCompany() {
        return company;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "branch_id")
    public Branch getBranch() {
        return branch;
    }
}
