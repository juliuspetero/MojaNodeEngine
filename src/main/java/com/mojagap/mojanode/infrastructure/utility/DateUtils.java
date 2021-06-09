package com.mojagap.mojanode.infrastructure.utility;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date now() {
        return Calendar.getInstance().getTime();
    }
}
