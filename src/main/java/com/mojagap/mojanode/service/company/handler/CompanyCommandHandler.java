package com.mojagap.mojanode.service.company.handler;

import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.dto.company.CompanyDto;

public interface CompanyCommandHandler {

    ActionResponse createCompany(CompanyDto companyDto);
}
