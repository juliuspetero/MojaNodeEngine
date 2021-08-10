package com.mojagap.mojanode.controller.aws;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.dto.aws.S3DocumentDto;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.aws.handler.AwsDocumentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/v1/document")
public class AwsDocumentController extends BaseController {

    private final AwsDocumentHandler awsDocumentHandler;

    @Autowired
    public AwsDocumentController(AwsDocumentHandler awsDocumentHandler) {
        this.awsDocumentHandler = awsDocumentHandler;
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public RecordHolder<S3DocumentDto> uploadFilesToAws(@RequestParam("documents") MultipartFile[] multipartFiles, @RequestParam("uploadType") String uploadType) {
        return executeAndLogUserActivity(EntityTypeEnum.FILE, ActionTypeEnum.DOWNLOAD, (UserActivityLog log) -> awsDocumentHandler.uploadDocuments(multipartFiles, uploadType));
    }

    @RequestMapping(path = "/download/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) {
        return executeHttpGet(() -> awsDocumentHandler.downloadDocument(id));
    }

}
