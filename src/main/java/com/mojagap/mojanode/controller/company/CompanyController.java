package com.mojagap.mojanode.controller.company;


import com.mojagap.mojanode.controller.ActionResponse;
import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.company.CompanyCommandService;
import com.mojagap.mojanode.service.company.CompanyQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/company")
public class CompanyController extends BaseController {

    @Autowired
    private CompanyCommandService companyCommandService;

    @Autowired
    private CompanyQueryService companyQueryService;


    @RequestMapping(method = RequestMethod.POST)
    public ActionResponse createCompany(@RequestBody CompanyDto companyDto) {
        return executeAndLogUserActivity(EntityTypeEnum.COMPANY, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            ActionResponse response = companyCommandService.createCompany(companyDto);
            log.setEntityId(response.getResourceId());
            return response;
        });
    }
}
