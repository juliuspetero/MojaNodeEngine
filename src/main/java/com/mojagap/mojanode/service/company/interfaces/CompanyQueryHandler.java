package com.mojagap.mojanode.service.company.interfaces;

import com.mojagap.mojanode.dto.company.CompanyDto;

import java.util.List;
import java.util.Map;

public interface CompanyQueryHandler {

    List<CompanyDto> getCompanies(Map<String, String> queryParams);
}
