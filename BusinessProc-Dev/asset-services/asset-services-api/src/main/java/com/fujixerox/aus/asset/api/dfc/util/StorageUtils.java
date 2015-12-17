package com.fujixerox.aus.asset.api.dfc.util;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.content.IDfStore;
import com.documentum.fc.client.content.impl.Store;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfUtil;
import com.fujixerox.aus.asset.api.util.cache.ThreadLocalCache;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class StorageUtils {

    private StorageUtils() {
        super();
    }

    @ThreadLocalCache
    public static boolean isStoreAvailable(IDfSession session, String storeName)
        throws DfException {
        if (StringUtils.isEmpty(storeName)) {
            return false;
        }
        IDfStore store = (IDfStore) session
                .getObjectByQualification("dm_store where name='"
                        + DfUtil.escapeQuotedString(storeName) + "'");
        if (store == null) {
            return false;
        }
        return store.getStatus() == Store.ON_LINE;
    }

}
