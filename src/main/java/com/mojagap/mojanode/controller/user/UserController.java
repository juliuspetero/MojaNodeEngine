package com.mojagap.mojanode.controller.user;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.controller.user.contract.UserSummary;
import com.mojagap.mojanode.model.ActionTypeEnum;
import com.mojagap.mojanode.model.EntityTypeEnum;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.user.UserCommandService;
import com.mojagap.mojanode.service.user.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserQueryService userQueryService;

    @GetMapping
    public List<UserSummary> getUsers() throws Exception {
        return executeHttpGet(() -> userQueryService.getUsers());
    }

    @PostMapping
    public UserSummary createAppUser(@RequestBody UserSummary userSummary) {
        return logUserActivity(EntityTypeEnum.USER, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            UserSummary response = userCommandService.createUser(userSummary);
            log.setEntityId(response.getId());
            return response;
        });
    }

    @GetMapping("/external/{id}")
    public ExternalUser getExternalUser(@PathVariable Integer id) throws Exception {
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

}
