package com.mojagap.mojanode.infrastructure;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.infrastructure.utility.CommonUtil;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PowerValidator {

    private static final Logger LOG = Logger.getLogger(PowerValidator.class.getName());

    public static void notNull(Object object, String message, Object... args) {
        if (object == null) {
            throw new BadRequestException(String.format(message, args));
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BadRequestException(message);
        }
    }

    public static <T extends Enum<T>> void ValidEnum(Class<T> clazz, String enumName, String message) {
        if (!EnumUtils.isValidEnum(clazz, enumName)) {
            throw new BadRequestException(message);
        }
    }

    public static void validStringLength(String str, Integer minLength, Integer maxLength, String message) {
        if (str == null || (str.length() < minLength && str.length() > maxLength)) {
            throw new BadRequestException(message);
        }
    }

    public static void validEmail(String email, String message) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || !pattern.matcher(email).matches()) {
            throw new BadRequestException(message);
        }
    }

    public static void validPassword(String password, String message) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        if (password == null || !pattern.matcher(password).matches()) {
            throw new BadRequestException(message);
        }
    }

    public static void validPhoneNumber(String phoneNumber, String message) throws NumberParseException {
        PhoneNumberUtil phoneNumberUtil = CommonUtil.PHONE_NUMBER_UTIL;
        try {
            if (phoneNumber == null || phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(phoneNumber, Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name()))) {
                throw new BadRequestException(message);
            }
        } catch (NumberParseException ex) {
            LOG.log(Level.INFO, "Failed to parse the provided phone number = " + phoneNumber, ex);
            throw new BadRequestException(message);
        }
    }
}
