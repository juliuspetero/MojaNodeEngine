package com.mojagap.mojanode.infrastructure;

import com.mojagap.mojanode.infrastructure.exception.BadRequestException;

public class Validator {

    public static void notNull(Object object, String message, Object... args) {
        if (object == null) {
            throw new BadRequestException(String.format(message, args));
        }
    }
}
