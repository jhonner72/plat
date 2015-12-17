package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JdbcMetadataStoreTest
{
    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    JdbcMetadataStore jdbcMetadataStore;

    @Before
    public void setup()
    {
        jdbcMetadataStore = new JdbcMetadataStore();

        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder().
                setType(EmbeddedDatabaseType.H2).
                addScript("classpath:metadata-schema.sql").
                build();

        JdbcTemplate simpleJdbcTemplate = new JdbcTemplate(embeddedDatabase);

        jdbcMetadataStore.setTemplate(simpleJdbcTemplate);
    }

    @Test
    public void shouldHaveBusinessDayOfToday_whenReadForTheFirstTime() throws ParseException {
        BusinessCalendar businessCalendar = jdbcMetadataStore.getMetadata(BusinessCalendar.class);
        assertThat(businessCalendar.getBusinessDay(), is(format.parse(format.format(new Date()))));
    }

    @Test
    public void shouldStoreBusinessDay() throws ParseException {
        BusinessCalendar businessCalendar = jdbcMetadataStore.getMetadata(BusinessCalendar.class);
        businessCalendar.setBusinessDay(format.parse("1964/02/20"));
        jdbcMetadataStore.storeMetadata(businessCalendar);

        businessCalendar = jdbcMetadataStore.getMetadata(BusinessCalendar.class);
        assertThat(businessCalendar.getBusinessDay(), is(format.parse("1964/02/20")));
    }
}