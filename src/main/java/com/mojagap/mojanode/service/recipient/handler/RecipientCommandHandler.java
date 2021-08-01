package com.mojagap.mojanode.service.recipient.handler;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientDto;
import org.springframework.web.multipart.MultipartFile;

public interface RecipientCommandHandler {

    ActionResponse createRecipient(RecipientDto recipientDto);

    ActionResponse createRecipientViaCsv(MultipartFile multipartFile);

    ActionResponse updateRecipient(RecipientDto recipientDto, Integer id);

    ActionResponse closeRecipient(Integer id);
}
