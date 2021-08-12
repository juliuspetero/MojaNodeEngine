package com.mojagap.mojanode.model.aws;

import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.wallet.WalletTransactionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "s3_document")
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class S3Document extends AuditEntity {
    private String name;
    private String mimeType;
    private String path;
    private S3UploadTypeEnum uploadType;
    private WalletTransactionRequest walletTransactionRequest;

    @Column(name = "name")
    public String getName() {
        return name;
    }


    @Column(name = "mime_type")
    public String getMimeType() {
        return mimeType;
    }

    @Column(name = "path")
    public String getPath() {
        return path;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_type_enum")
    public S3UploadTypeEnum getUploadType() {
        return uploadType;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_transaction_request_id")
    public WalletTransactionRequest getWalletTransactionRequest() {
        return walletTransactionRequest;
    }
}
