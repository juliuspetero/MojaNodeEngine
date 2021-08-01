package com.mojagap.mojanode.controller.recipient;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientDto;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.recipient.handler.RecipientCommandHandler;
import com.mojagap.mojanode.service.recipient.handler.RecipientQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/v1/recipient")
public class RecipientController extends BaseController {
    private final RecipientCommandHandler recipientCommandHandler;
    private final RecipientQueryHandler recipientQueryHandler;

    @Autowired
    public RecipientController(RecipientCommandHandler recipientCommandHandler, RecipientQueryHandler recipientQueryHandler) {
        this.recipientCommandHandler = recipientCommandHandler;
        this.recipientQueryHandler = recipientQueryHandler;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ActionResponse createRecipient(@RequestBody RecipientDto recipientDto) {
        return executeAndLogUserActivity(EntityTypeEnum.RECIPIENT, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            ActionResponse response = recipientCommandHandler.createRecipient(recipientDto);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/csv", method = RequestMethod.POST)
    public ActionResponse createRecipientViaCsv(@RequestParam("csvFile") MultipartFile multipartFile) {
        return executeAndLogUserActivity(EntityTypeEnum.RECIPIENT, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            ActionResponse response = recipientCommandHandler.createRecipientViaCsv(multipartFile);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ActionResponse updateRecipient(@RequestBody RecipientDto recipientDto, @PathVariable("id") Integer id) {
        return executeAndLogUserActivity(EntityTypeEnum.RECIPIENT, ActionTypeEnum.UPDATE, (UserActivityLog log) -> {
            ActionResponse response = recipientCommandHandler.updateRecipient(recipientDto, id);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ActionResponse closedRecipient(@PathVariable("id") Integer id) {
        return executeAndLogUserActivity(EntityTypeEnum.RECIPIENT, ActionTypeEnum.CLOSE, (UserActivityLog log) -> {
            ActionResponse response = recipientCommandHandler.closeRecipient(id);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public RecordHolder<RecipientDto> getRecipients(@RequestParam Map<String, String> queryParams) {
        return executeHttpGet(() -> recipientQueryHandler.getRecipients(queryParams));
    }

}
