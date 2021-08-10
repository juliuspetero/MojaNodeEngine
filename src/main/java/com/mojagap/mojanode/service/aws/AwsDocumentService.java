package com.mojagap.mojanode.service.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.mojagap.mojanode.dto.aws.S3DocumentDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.infrastructure.utility.AwsUtil;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.aws.S3Document;
import com.mojagap.mojanode.model.aws.S3UploadTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.repository.aws.S3DocumentRepository;
import com.mojagap.mojanode.service.aws.handler.AwsDocumentHandler;
import liquibase.util.file.FilenameUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AwsDocumentService implements AwsDocumentHandler {
    private static final Logger LOG = Logger.getLogger(AwsDocumentService.class.getName());
    private final AmazonS3 amazonS3;
    private final S3DocumentRepository s3DocumentRepository;

    @Autowired
    public AwsDocumentService(AmazonS3 amazonS3, S3DocumentRepository s3DocumentRepository, HttpServletRequest httpServletRequest) {
        this.amazonS3 = amazonS3;
        this.s3DocumentRepository = s3DocumentRepository;
    }

    @Override
    @SneakyThrows
    public RecordHolder<S3DocumentDto> uploadDocuments(MultipartFile[] multipartFiles, String uploadType) {
        PowerValidator.isTrue(AwsUtil.isAwsEnabled(), ErrorMessages.AWS_IS_DISABLED);
        AppContext.isPermittedAccountTyp(AccountType.INDIVIDUAL, AccountType.COMPANY);
        Account account = AppContext.getLoggedInUser().getAccount();
        List<S3DocumentDto> s3DocumentDtos = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String mimeType = multipartFile.getContentType();
            S3UploadTypeEnum uploadTypeEnum = S3UploadTypeEnum.valueOf(uploadType);
            String name = RandomStringUtils.random(10, true, false) + "_" + multipartFile.getOriginalFilename();
            String path = AwsUtil.getS3BucketName() + "/" + account.getName().replaceAll("[^a-zA-Z]", "") + "/"
                    + uploadTypeEnum.getFolder();
            path = FilenameUtils.normalize(path);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.addUserMetadata(ApplicationConstants.CONTENT_TYPE, multipartFile.getContentType());
            objectMetadata.addUserMetadata(ApplicationConstants.CONTENT_LENGTH, String.valueOf(multipartFile.getSize()));
            LOG.log(Level.INFO, String.format("Starting uploading: %s/%s document to AWS", path, name));
            amazonS3.putObject(path, name, multipartFile.getInputStream(), objectMetadata);
            LOG.log(Level.INFO, String.format("Finished uploading: %s/%s document to AWS", path, name));
            S3Document s3Document = S3Document.builder()
                    .name(name)
                    .mimeType(mimeType)
                    .uploadType(uploadTypeEnum)
                    .path(path)
                    .build();
            AppContext.stamp(s3Document);
            s3DocumentRepository.save(s3Document);
            s3DocumentDtos.add(new S3DocumentDto(s3Document));
        }
        return new RecordHolder<>(s3DocumentDtos.size(), s3DocumentDtos);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<Resource> downloadDocument(Integer id) {
        PowerValidator.isTrue(AwsUtil.isAwsEnabled(), ErrorMessages.AWS_IS_DISABLED);
        S3Document s3Document = s3DocumentRepository.findById(id).orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Document", "ID")));
        LOG.log(Level.INFO, String.format("Starting downloading: %s/%s document from AWS", s3Document.getPath(), s3Document.getName()));
        S3Object object = amazonS3.getObject(s3Document.getPath(), s3Document.getName());
        LOG.log(Level.INFO, String.format("Finished downloading: %s/%s document from AWS", s3Document.getPath(), s3Document.getName()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(s3Document.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s3Document.getName() + "\"")
                .cacheControl(CacheControl.noCache())
                .body(new InputStreamResource(object.getObjectContent()));
    }
}
