package com.fujixerox.aus.asset.api.dfc.query;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IRowHandler {

    void handleRow(IDfCollection row, int position) throws DfException;

}
