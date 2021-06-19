package com.mojagap.mojanode.infrastructure.utility;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String DD_MMM_YYY = "dd/MMM/yyyy";

    public static Date now() {
        return Calendar.getInstance().getTime();
    }
}
