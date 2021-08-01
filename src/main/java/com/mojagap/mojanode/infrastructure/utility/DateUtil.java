package com.mojagap.mojanode.infrastructure.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String DATE_FORMAT_BY_SLASH = "dd/MMM/yyyy";
    public static final String DATE_FORMAT_BY_MINUS = "dd-MMM-yyyy";

    public static SimpleDateFormat DefaultDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT_BY_SLASH);
    }

    public static Date now() {
        return Calendar.getInstance().getTime();
    }
}
