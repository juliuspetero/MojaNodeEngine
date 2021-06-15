package com.mojagap.mojanode.controller.user;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.controller.user.entity.AppUserSummary;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.user.UserCommandService;
import com.mojagap.mojanode.service.user.UserQueryService;
import com.mojagap.mojanode.service.user.entities.AppUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserQueryService userQueryService;

    @GetMapping
    public RecordHolder<AppUserDTO> getAppUsers(@RequestParam Map<String, String> queryParams) {
        return executeHttpGet(() -> userQueryService.getAppUsersByQueryParams(queryParams));
    }

    @PostMapping("/create")
    public AppUserSummary createAppUser(@RequestBody AppUserSummary appUserSummary) {
        return logUserActivity(EntityTypeEnum.USER, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            AppUserSummary response = userCommandService.createUser(appUserSummary);
            log.setEntityId(response.getId());
            return response;
        });
    }

    @GetMapping("/authenticate")
    public AppUserSummary authenticate(@RequestBody AppUserSummary appUserSummary) {
        return userCommandService.authenticateUser(appUserSummary);
    }

    @GetMapping("/external/{id}")
    public ExternalUser getExternalUser(@PathVariable Integer id) {
        return executeHttpGet(() -> userQueryService.getExternalUserById(id));
    }

    @PostMapping("/external")
    public ExternalUser createExternalUser(@RequestBody ExternalUser externalUser) {
        return logUserActivity(EntityTypeEnum.USER, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            ExternalUser response = userCommandService.createExternalUser(externalUser);
            log.setEntityId(response.getId());
            return response;
        });
    }

    @GetMapping("/external/all")
    public List<AppUser> getExternalUsers() {
        return executeHttpGet(() -> userQueryService.getExternalUsers());
    }

}
