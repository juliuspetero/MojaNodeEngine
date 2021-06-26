package com.mojagap.mojanode.service.company.handler;

import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.model.common.RecordHolder;

import java.util.Map;

public interface CompanyQueryHandler {

    RecordHolder<CompanyDto> getCompanies(Map<String, String> queryParams);
}
