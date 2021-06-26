package com.mojagap.mojanode.service.company;


import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.service.company.handler.CompanyQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CompanyQueryService implements CompanyQueryHandler {

    @Override
    public RecordHolder<CompanyDto> getCompanies(Map<String, String> queryParams) {
        return new RecordHolder<>(0, Collections.emptyList());
    }
}
