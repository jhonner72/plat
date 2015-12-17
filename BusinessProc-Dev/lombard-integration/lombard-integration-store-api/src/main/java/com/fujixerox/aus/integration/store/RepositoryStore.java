package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;

/**
 * Some objects within the document repository are exposed so that they can be retrieved directly
 * from the integration layer.
 * Created by warwick on 21/05/2015.
 */
public interface RepositoryStore {
    public boolean queryFileHasBeenReceivedBefore(String filename, DocumentExchangeEnum voucherInbound);
}
