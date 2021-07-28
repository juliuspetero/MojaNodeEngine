package com.mojagap.mojanode.service.recipient;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientDto;
import com.mojagap.mojanode.repository.recipient.RecipientBankDetailRepository;
import com.mojagap.mojanode.repository.recipient.RecipientRepository;
import com.mojagap.mojanode.service.recipient.handler.RecipientCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipientCommandService implements RecipientCommandHandler {

    private final RecipientRepository recipientRepository;
    private final RecipientBankDetailRepository recipientBankDetailRepository;

    @Autowired
    public RecipientCommandService(RecipientRepository recipientRepository, RecipientBankDetailRepository recipientBankDetailRepository) {
        this.recipientRepository = recipientRepository;
        this.recipientBankDetailRepository = recipientBankDetailRepository;
    }

    @Override
    public ActionResponse createRecipient(RecipientDto recipientDto) {
        return null;
    }

    @Override
    public ActionResponse updateRecipient(RecipientDto recipientDto, Integer id) {
        return null;
    }

    @Override
    public ActionResponse closeRecipient(Integer id) {
        return null;
    }
}
