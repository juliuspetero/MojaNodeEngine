package com.mojagap.mojanode.service.aws.handler;

import com.mojagap.mojanode.dto.aws.S3DocumentDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AwsDocumentHandler {

    RecordHolder<S3DocumentDto> uploadDocuments(MultipartFile[] multipartFiles, String uploadType);

    ResponseEntity<Resource> downloadDocument(Integer id);
}
