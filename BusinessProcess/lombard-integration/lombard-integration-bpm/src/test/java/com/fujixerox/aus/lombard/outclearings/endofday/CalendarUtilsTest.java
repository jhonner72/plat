package com.fujixerox.aus.lombard.outclearings.endofday;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by warwick on 4/06/2015.
 */
public class CalendarUtilsTest {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public static Calendar buildCalendar(String date) throws ParseException {
        Calendar instance = Calendar.getInstance();
        instance.setTime(dateFormat.parse(date));
        return instance;
    }

    public static List<Date> buildDates(String... dates) throws ParseException {
        ArrayList<Date> dateList = new ArrayList<>();
        for (String date : dates)
        {
            dateList.add(dateFormat.parse(date));
        }

        return dateList;
    }

    public static Date buildDate(String date) throws ParseException {
        return dateFormat.parse(date);
    }

    @Test
    public void shouldNotBeLastDayOfWeek_onMonday() throws Exception {
        assertThat(CalendarUtils.isLastDayOfWeek(buildCalendar("2015/06/01"), buildDates()), is(false));
        assertThat(CalendarUtils.isLastDayOfWeek(buildCalendar("2015/06/01"), buildDates("2015/01/01")), is(false));
        assertThat(CalendarUtils.isLastDayOfWeek(buildCalendar("2015/06/01"), buildDates("2015/01/01", "2015/12/24")), is(false));
    }

    @Test
    public void shouldBeLastDayOfWeek_onFriday() throws Exception {
        assertThat(CalendarUtils.isLastDayOfWeek(buildCalendar("2015/06/05"), buildDates()), is(true));
    }

    @Test
    public void shouldBeLastDayOfWeek_onThursday_whenFridayIsHoliday() throws Exception {
        assertThat(CalendarUtils.isLastDayOfWeek(buildCalendar("2015/06/04"), buildDates("2015/06/05")), is(true));
        assertThat(CalendarUtils.isLastDayOfWeek(buildCalendar("2015/06/04"), buildDates("2015/01/01", "2015/06/05")), is(true));
    }

    @Test
    public void shouldBeLastDayOfWeek_onWednesday_whenThursdayAndFridayAreHolidays() throws Exception {
        assertThat(CalendarUtils.isLastDayOfWeek(buildCalendar("2015/06/03"), buildDates("2015/06/04", "2015/06/05")), is(true));
    }

    @Test
    public void shouldBeLastDayOfMonth_whenTuesdayIsLastDayOfMonth() throws Exception {
        assertThat(CalendarUtils.isLastDayOfMonth(buildCalendar("2015/06/30"), buildDates()),is(true));
        assertThat(CalendarUtils.isLastDayOfMonth(buildCalendar("2015/06/30"), buildDates("2015/01/01")),is(true));
    }

    @Test
    public void shouldNotBeLastDayOfMonth_onFourthOfMonth() throws Exception {
        assertThat(CalendarUtils.isLastDayOfMonth(buildCalendar("2015/06/04"), buildDates()),is(false));
        assertThat(CalendarUtils.isLastDayOfMonth(buildCalendar("2015/06/04"), buildDates("2015/01/01", "2015/06/05")),is(false));
    }

    @Test
    public void shouldBeLastDayOfMonth_onFriday_whenSundayLastDayOfMonth() throws Exception {
        assertThat(CalendarUtils.isLastDayOfMonth(buildCalendar("2015/05/29"), buildDates()),is(true));
    }

    @Test
    public void shouldBeLastDayOfMonth_onFriday_whenSaturdayLastDayOfMonth() throws Exception {
        assertThat(CalendarUtils.isLastDayOfMonth(buildCalendar("2015/07/31"), buildDates()),is(true));
    }

    @Test
    public void shouldBeLastDayOfMonth_onThursday_whenFridayIsHoliday() throws Exception {
        assertThat(CalendarUtils.isLastDayOfMonth(buildCalendar("2015/06/29"), buildDates("2015/06/30")),is(true));
    }

    @Test
    public void shouldBeWorkingDay_onMonday() throws Exception {
        assertThat(CalendarUtils.isWorkingDay(buildCalendar("2015/06/01"), buildDates()),is(true));
        assertThat(CalendarUtils.isWorkingDay(buildCalendar("2015/06/01"), buildDates("2015/01/01")),is(true));
    }

    @Test
    public void shouldNotBeWorkingDay_onSaturday() throws Exception {
        assertThat(CalendarUtils.isWorkingDay(buildCalendar("2015/06/06"), buildDates()),is(false));
        assertThat(CalendarUtils.isWorkingDay(buildCalendar("2015/06/06"), buildDates("2015/01/01")),is(false));
    }

    @Test
    public void shouldNotBeWorkingDay_onSunday() throws Exception {
        assertThat(CalendarUtils.isWorkingDay(buildCalendar("2015/06/07"), buildDates()),is(false));
        assertThat(CalendarUtils.isWorkingDay(buildCalendar("2015/06/07"), buildDates("2015/01/01")),is(false));
    }

    @Test
    public void shouldNotBeWorkingDay_onHoliday() throws Exception {
        assertThat(CalendarUtils.isWorkingDay(buildCalendar("2015/06/01"), buildDates("2015/06/01")),is(false));
        assertThat(CalendarUtils.isWorkingDay(buildCalendar("2015/06/01"), buildDates("2015/01/01", "2015/06/01")),is(false));
    }
}