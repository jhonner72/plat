package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 25/04/15
 * Time: 10:44 PM
 * Implementation of ForValueSeedQuery interface
 */
public class JdbcForValueSeedQuery implements ForValueSeedQuery {

    private JdbcTemplate template;

    @Override
    public boolean findForValueEntry(String bsb, String accountNumber, DocumentTypeEnum documentType) {
        boolean found = false;
        List<String> list = template.queryForList("select acc from ref_for_value where bsb=? and acc=? and document_type=?", new Object[] { bsb, accountNumber, documentType.value() }, String.class);
        if (list.size() > 0) {
            found = true;
        }
        return found;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
}
