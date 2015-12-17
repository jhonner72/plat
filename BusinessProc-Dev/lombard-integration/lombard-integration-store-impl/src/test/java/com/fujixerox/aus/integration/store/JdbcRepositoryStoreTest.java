package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JdbcRepositoryStoreTest
{
    JdbcRepositoryStore jdbcRepositoryStore;

    @Before
    public void setup()
    {
        jdbcRepositoryStore = new JdbcRepositoryStore();

        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder().
                setType(EmbeddedDatabaseType.H2).
                addScript("classpath:repository-schema.sql").
                build();

        JdbcTemplate simpleJdbcTemplate = new JdbcTemplate(embeddedDatabase);

        simpleJdbcTemplate.update("insert into fxa_file_receipt (filename, exchange) values('file1', 'VOUCHER_INBOUND')");
        jdbcRepositoryStore.setTemplate(simpleJdbcTemplate);
    }

    @Test
    public void shouldFindFile()
    {
        assertThat(jdbcRepositoryStore.queryFileHasBeenReceivedBefore("file1", DocumentExchangeEnum.VOUCHER_INBOUND), is(true));
    }

    @Test
    public void shouldNotFindFile()
    {
        assertThat(jdbcRepositoryStore.queryFileHasBeenReceivedBefore("file2", DocumentExchangeEnum.VOUCHER_INBOUND), is(false));
    }

}