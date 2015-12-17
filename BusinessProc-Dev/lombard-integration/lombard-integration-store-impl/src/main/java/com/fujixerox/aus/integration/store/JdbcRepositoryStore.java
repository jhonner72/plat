package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by warwick on 21/05/2015.
 */
public class JdbcRepositoryStore implements RepositoryStore {

    private JdbcTemplate template;

    @Override
    public boolean queryFileHasBeenReceivedBefore(String filename, DocumentExchangeEnum voucherInbound) {
        int count = template.queryForObject("select count(filename) from fxa_file_receipt where filename = ? and exchange = ?", Integer.class, filename, voucherInbound.value());
        return count > 0;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
}
