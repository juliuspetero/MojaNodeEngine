package com.mojagap.mojanode.model.user;


import com.mojagap.mojanode.helper.ErrorMessages;
import com.mojagap.mojanode.helper.Validator;
import com.mojagap.mojanode.helper.exception.BadRequestException;
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
        Validator.notNull(id, ErrorMessages.MISSING_APPLICATION_PLATFORM);
        return switch (id) {
            case 1 -> WEB_APP;
            case 2 -> ANDROID_APP;
            case 3 -> IOS_APP;
            case 4 -> THIRD_PARTY_APP;
            default -> throw new BadRequestException(ErrorMessages.INVALID_PLATFORM_ID);
        };
    }
}
