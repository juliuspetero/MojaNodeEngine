package com.mojagap.mojanode.dto.aws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.dto.wallet.WalletTransactionDto;
import com.mojagap.mojanode.model.aws.S3Document;
import com.mojagap.mojanode.model.user.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class S3DocumentDto {
    private Integer id;
    private String name;
    private String mimeType;
    private String path;
    private String uploadType;
    private WalletTransactionDto walletTransaction;
    private AppUserDto createdBy;

    public S3DocumentDto(S3Document s3Document) {
        this.id = s3Document.getId();
        this.name = s3Document.getName();
        this.mimeType = s3Document.getMimeType();
        this.path = s3Document.getPath();
        this.uploadType = s3Document.getUploadType().name();
        this.walletTransaction = WalletTransactionDto.builder().id(s3Document.getWalletTransaction() != null ? s3Document.getWalletTransaction().getId() : null).build();
        AppUser appUser = s3Document.getCreatedBy();
        this.createdBy = AppUserDto.builder().id(appUser.getId()).firstName(appUser.getFirstName()).lastName(appUser.getLastName()).build();
    }
}
