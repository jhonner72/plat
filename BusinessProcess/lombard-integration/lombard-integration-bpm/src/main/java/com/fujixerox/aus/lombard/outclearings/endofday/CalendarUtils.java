package com.fujixerox.aus.lombard.outclearings.endofday;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A collection of utilities for determining if the specified date meets certain business days
 * such as EndOfWeek, EndOfMonth, and is it a work day.
 *
 * Saturdays and Sundays are not working days. A list of closed days is also provided to
 * each question.
 *
 */
public final class CalendarUtils {

    private CalendarUtils()
    {
    }

    public static boolean isLastDayOfWeek(Calendar c, List<Date> closedDays) {

        if (isLastDayOfWeek(c))
        {
            return true;
        }
        return !isThereAWorkDayLeftInTheWeek(c, closedDays);
    }

    public static boolean isLastDayOfMonth(Calendar c, List<Date> closedDays)
    {
        if (isLastDayOfMonth(c))
        {
            return true;
        }
        return !isThereAWorkDayLeftInTheMonth(c, closedDays);
    }

    public static boolean isWorkingDay(Calendar c, List<Date> closedDays) {
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
        {
            return false;
        }
        for (Date closedDay : closedDays)
        {
            if (closedDay.equals(c.getTime()))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isThereAWorkDayLeftInTheMonth(Calendar c, List<Date> closedDays) {
        Calendar c2 = Calendar.getInstance();
        c2.setTime(c.getTime());
        while(!isLastDayOfMonth(c2)) {
            c2.add(Calendar.DAY_OF_MONTH, 1);
            if (isWorkingDay(c2, closedDays)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isThereAWorkDayLeftInTheWeek(Calendar c, List<Date> closedDays) {
        Calendar c2 = Calendar.getInstance();
        c2.setTime(c.getTime());
        while(!isLastDayOfWeek(c2)) {
            c2.add(Calendar.DAY_OF_MONTH, 1);
            if (isWorkingDay(c2, closedDays)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLastDayOfWeek(Calendar c)
    {
        return c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }

    private static boolean isLastDayOfMonth(Calendar c)
    {
        int lastDayOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return c.get(Calendar.DAY_OF_MONTH) == lastDayOfMonth;
    }

}
