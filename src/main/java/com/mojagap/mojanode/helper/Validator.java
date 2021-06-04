package com.mojagap.mojanode.helper;

import com.mojagap.mojanode.helper.exception.BadRequestException;

public class Validator {

    public static void notNull(Object object, String message, Object... args) {
        if (object == null) {
            throw new BadRequestException(String.format(message, args));
        }
    }
}
