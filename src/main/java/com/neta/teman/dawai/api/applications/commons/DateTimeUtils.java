package com.neta.teman.dawai.api.applications.commons;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateTimeUtils {

    public static Date startOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
//        return date;
    }

    public static Date endOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
//        return date;
    }

    public static boolean isBeforeAndEqualNow(Date startDate) {
        Calendar tmpA = Calendar.getInstance();
        Calendar tmpB = Calendar.getInstance();
        tmpA.setTime(startDate);
        tmpA.clear(Calendar.MILLISECOND);
        tmpB.clear(Calendar.MILLISECOND);
        return tmpA.getTime().before(tmpB.getTime()) || tmpA.equals(tmpB);
    }

    public static int excludeWeekendOnly(Date time, Date timeFinish) {
        Calendar start = Calendar.getInstance();
        Calendar finish = Calendar.getInstance();
        start.setTime(time);
        finish.setTime(timeFinish);
        start.clear(Calendar.MILLISECOND);
        finish.clear(Calendar.MILLISECOND);
        if (finish.before(start)) return 0;
        int days = 1;
        log.debug("last date {}", finish.getTime());
//        do {
//            int numDay = start.get(Calendar.DAY_OF_WEEK);
//            if (numDay != 1 && numDay != 7) {
//                ++days;
//                log.debug("num day {} last date {}, add", numDay, start.getTime());
//            } else log.debug("num day {} last date {}, skip", numDay, start.getTime());
//            start.add(Calendar.DATE, 1);
//        } while (start.before(finish));
        while (start.before(finish)) {
            int numDay = start.get(Calendar.DAY_OF_WEEK);
            if (numDay != 1 && numDay != 7) {
                ++days;
                log.debug("num day {} last date {}, add", numDay, start.getTime());
            } else log.debug("num day {} last date {}, skip", numDay, start.getTime());
            start.add(Calendar.DATE, 1);
        }
        return days;
    }

    public static Date endOfMonth(Calendar cal) {
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        return cal.getTime();
    }
}
