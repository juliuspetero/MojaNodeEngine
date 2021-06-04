package com.mojagap.mojanode.helper.utility;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date now() {
        return Calendar.getInstance().getTime();
    }
}
