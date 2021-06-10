package com.mojagap.mojanode.controller.organization;


import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.controller.organization.entity.OrganizationSummary;
import com.mojagap.mojanode.model.ActionTypeEnum;
import com.mojagap.mojanode.model.EntityTypeEnum;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.organization.OrganizationCommandService;
import com.mojagap.mojanode.service.organization.OrganizationQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organization")
public class OrganizationController extends BaseController {

    @Autowired
    private OrganizationCommandService organizationCommandService;

    @Autowired
    private OrganizationQueryService organizationQueryService;

    @PostMapping("/create")
    public OrganizationSummary createOrganization(@RequestBody OrganizationSummary organizationSummary) {
        return logUserActivity(EntityTypeEnum.ORGANIZATION, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            OrganizationSummary response = organizationCommandService.createOrganization(organizationSummary);
            log.setEntityId(response.getId());
            return response;
        });
    }
}
