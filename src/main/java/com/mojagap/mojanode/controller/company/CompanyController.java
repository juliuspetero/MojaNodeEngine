package com.mojagap.mojanode.controller.company;


import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.controller.company.entity.CompanySummary;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.company.CompanyCommandService;
import com.mojagap.mojanode.service.company.CompanyQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class CompanyController extends BaseController {

    @Autowired
    private CompanyCommandService companyCommandService;

    @Autowired
    private CompanyQueryService companyQueryService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public CompanySummary createCompany(@RequestBody CompanySummary companySummary) {
        return logUserActivity(EntityTypeEnum.ORGANIZATION, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            CompanySummary response = companyCommandService.createOrganization(companySummary);
            log.setEntityId(response.getId());
            return response;
        });
    }
}
