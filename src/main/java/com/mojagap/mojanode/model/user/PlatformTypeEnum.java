package com.mojagap.mojanode.model.user;


import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlatformTypeEnum {
    WEB_APP(1),
    ANDROID_APP(2),
    IOS_APP(3),
    THIRD_PARTY_APP(3);

    private final Integer id;

    public static PlatformTypeEnum fromInt(Integer id) {
        PowerValidator.notNull(id, ErrorMessages.MISSING_APPLICATION_PLATFORM);
        switch (id) {
            case 1:
                return WEB_APP;
            case 2:
                return ANDROID_APP;
            case 3:
                return IOS_APP;
            case 4:
                return THIRD_PARTY_APP;
            default:
                throw new BadRequestException(ErrorMessages.INVALID_PLATFORM_ID);
        }
    }
}
