package com.mojagap.mojanode.service.company.handler;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.company.CompanyDto;

public interface CompanyCommandHandler {

    ActionResponse createCompany(CompanyDto companyDto);
}
