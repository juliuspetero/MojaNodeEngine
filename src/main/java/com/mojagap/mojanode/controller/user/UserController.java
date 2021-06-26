package com.mojagap.mojanode.controller.user;

import com.mojagap.mojanode.controller.BaseController;
import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.user.AppUserDto;
import com.mojagap.mojanode.model.common.ActionTypeEnum;
import com.mojagap.mojanode.model.common.EntityTypeEnum;
import com.mojagap.mojanode.model.common.RecordHolder;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.service.user.handler.UserCommandHandler;
import com.mojagap.mojanode.service.user.handler.UserQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserController extends BaseController {

    @Autowired
    private UserCommandHandler userCommandHandler;

    @Autowired
    private UserQueryHandler userQueryHandler;

    @GetMapping
    public RecordHolder<AppUserDto> getAppUsers(@RequestParam Map<String, String> queryParams) {
        return executeHttpGet(() -> userQueryHandler.getAppUsersByQueryParams(queryParams));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ActionResponse createAppUser(@RequestBody AppUserDto appUserDto) {
        return executeAndLogUserActivity(EntityTypeEnum.USER, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            ActionResponse response = userCommandHandler.createUser(appUserDto);
            log.setEntityId(response.resourceId());
            return response;
        });
    }


    @RequestMapping(path = "/external/{id}", method = RequestMethod.GET)
    public ExternalUser getExternalUser(@PathVariable Integer id) {
        return executeHttpGet(() -> userQueryHandler.getExternalUserById(id));
    }

    @RequestMapping(path = "/external", method = RequestMethod.GET)
    public ExternalUser createExternalUser(@RequestBody ExternalUser externalUser) {
        return executeAndLogUserActivity(EntityTypeEnum.USER, ActionTypeEnum.CREATE, (UserActivityLog log) -> {
            ExternalUser response = userCommandHandler.createExternalUser(externalUser);
            log.setEntityId(response.getId());
            return response;
        });
    }

    @GetMapping("/external/all")
    public List<AppUser> getExternalUsers() {
        return executeHttpGet(() -> userQueryHandler.getExternalUsers());
    }

}
