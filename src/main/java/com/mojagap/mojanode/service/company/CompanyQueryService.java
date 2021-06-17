package com.mojagap.mojanode.service.company;


import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.service.company.interfaces.CompanyQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CompanyQueryService implements CompanyQueryHandler {

    @Override
    public List<CompanyDto> getCompanies(Map<String, String> queryParams) {
        return Collections.emptyList();
    }
}
