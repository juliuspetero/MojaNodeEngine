package com.mojagap.mojanode.dto.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.account.CountryCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class RoleDto {
    private Integer id;
    private String name;
    private String description;
    private String status;
    private List<PermissionDto> permissions;

    public void isValid() {
        PowerValidator.validStringLength(name, 5, 100, ErrorMessages.INVALID_ROLE_NAME);
        PowerValidator.validStringLength(description, 10, 255, ErrorMessages.INVALID_ROLE_DESCRIPTION);
        PowerValidator.notEmpty(permissions, ErrorMessages.PERMISSIONS_REQUIRED_FOR_ROLE);
    }
}
