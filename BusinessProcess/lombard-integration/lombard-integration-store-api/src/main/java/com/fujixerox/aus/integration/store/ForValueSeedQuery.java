package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 25/04/15
 * Time: 10:41 PM
 * To check if a voucher is For Value
 */
public interface ForValueSeedQuery {

    public boolean findForValueEntry(String bsb, String accountNumber, DocumentTypeEnum documentType);
}
