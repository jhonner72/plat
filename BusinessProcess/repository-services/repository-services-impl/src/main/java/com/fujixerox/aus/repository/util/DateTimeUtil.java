package com.fujixerox.aus.repository.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/** 
 * Zaka Lei
 * 09/09/2015
 */
public class DateTimeUtil {
	
	public static Date substractFromUtcTime(Date date){
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //the amount of raw offset time in milliseconds to add to UTC
        calendar.add(Calendar.MILLISECOND, -(TimeZone.getDefault().getRawOffset()));
        return calendar.getTime();
	}
	
	public static Date removeTime(Date date){
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
	}
	
	public static Date getNextDate(Date date){
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.add(Calendar.DATE, 1);
	    return calendar.getTime();
	}
	
    public static Date convertToUTCTime(String dateString, String timeFormat) throws ParseException {
        TimeZone utcTimeZone = TimeZone.getTimeZone("Australia/Sydney");
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        sdf.setTimeZone(utcTimeZone);
        Date utcDate = sdf.parse(dateString);
        return utcDate;
    }

    public static String convertToUTCTimeString(String dateString, String timeFormat) throws ParseException {
        TimeZone utcTimeZone = TimeZone.getTimeZone("Etc/UTC");
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        sdf.setTimeZone(utcTimeZone);
        Date utcDate = sdf.parse(dateString);
        String utcDateStr = sdf.format(utcDate);
        return utcDateStr;
    }

}
