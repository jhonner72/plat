package com.fujixerox.aus.lombard.outclearings.endofday;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by warwick on 3/06/2015.
 */
public class EndOfDayProcessingJobAdapterTest {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Test
    public void shouldRollOverToTuesday_whenTodayIsMonday() throws ParseException {
        execute("2015/6/1", "2015/6/2", false, false);
    }

    @Test
    public void shouldRollOverToMonday_whenTodayIsFriday() throws ParseException {
        execute("2015/6/5", "2015/6/8", false, false);
    }

    @Test
    public void shouldBeLastDayOfWeek() throws ParseException {
        execute("2015/6/4", "2015/6/5", true, false);
    }

    @Test
    public void shouldBeEndOfMonth_whenTheDayIsTheLastDayOfTheMonth() throws ParseException {
        execute("2015/6/29", "2015/6/30", false, true);
    }

    @Test
    public void shouldBeEndOfMonth_whenLastDayOfMonthIsOnTheWeekend() throws ParseException {
        execute("2015/5/28", "2015/5/29", true, true);
    }

    @Test
    public void shouldRollOverToTuesday_whenTodayIsFridayAndMondayIsPublicHoliday() throws ParseException {
        execute("2015/6/5", "2015/6/9", false, false, "2015/1/1", "2015/6/8");
    }

    protected void execute(String beforeDay, String afterDay, boolean isEow, boolean isEom, String... closedDays) throws ParseException {
        EndOfDayProcessingJobAdapter endOfDayProcessingJobAdapter = new EndOfDayProcessingJobAdapter();

        MetadataStore mock = mock(MetadataStore.class);
        endOfDayProcessingJobAdapter.setMetadataStore(mock);
        
        SequenceNumberGenerator mockSeqNoGenerator = mock(SequenceNumberGenerator.class);
        endOfDayProcessingJobAdapter.setSequenceNumberGenerator(mockSeqNoGenerator);
        
        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setInEndOfDay(true);
        businessCalendar.setBusinessDay(dateFormat.parse(beforeDay));
        for (String closedDay : closedDays) {
            businessCalendar.getClosedDays().add(dateFormat.parse(closedDay));
        }
        when(mock.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);

        endOfDayProcessingJobAdapter.rollToNextBusinessDay();

        ArgumentCaptor<BusinessCalendar> businessCalendarArgumentCaptor = ArgumentCaptor.forClass(BusinessCalendar.class);
        verify(mock).storeMetadata(businessCalendarArgumentCaptor.capture());
        BusinessCalendar actualBusinessCalendar = businessCalendarArgumentCaptor.getValue();
        assertThat(actualBusinessCalendar.getBusinessDay(), is(dateFormat.parse(afterDay)));
        assertThat(businessCalendar.isInEndOfDay(), is(false));
        assertThat(businessCalendar.isIsEndOfMonth(), is(isEom));
        assertThat(businessCalendar.isIsEndOfWeek(), is(isEow));
    }
}