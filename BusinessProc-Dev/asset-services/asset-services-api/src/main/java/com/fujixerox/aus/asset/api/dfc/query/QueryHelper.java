package com.fujixerox.aus.asset.api.dfc.query;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.util.SessionUtils;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class QueryHelper {

    private QueryHelper() {
        super();
    }

    public static boolean selectQuery(IDfSession session, CharSequence query,
            int limit, IRowHandler rowHandler) throws DfException {
        return selectQueryInternal(session, query, null, rowHandler,
                IDfQuery.DF_EXEC_QUERY, 0, limit);
    }

    public static boolean selectQuery(IDfSession session, CharSequence query,
            IRowHandler rowHandler) throws DfException {
        return selectQueryInternal(session, query, null, rowHandler,
                IDfQuery.DF_EXEC_QUERY, -1, -1);
    }

    private static boolean selectQueryInternal(IDfSession session,
            CharSequence query, IMetadataHandler metadataHandler,
            IRowHandler rowHandler, int queryType, int start, int count)
        throws DfException {
        if (session == null) {
            throw new NullPointerException("IDfSession is null");
        }
        if (query == null) {
            throw new NullPointerException("Query is null");
        }
        if (rowHandler == null) {
            throw new NullPointerException("Handler is null");
        }
        if (!(queryType == IDfQuery.DF_READ_QUERY
                || queryType == IDfQuery.DF_QUERY
                || queryType == IDfQuery.DF_CACHE_QUERY
                || queryType == IDfQuery.DF_EXECREAD_QUERY || queryType == IDfQuery.DF_EXEC_QUERY)) {
            throw new IllegalArgumentException("Wrong query type argument");
        }
        int end = -1;
        if (start >= 0 && count >= 0) {
            end = start + count;
        }
        try {
            IDfQuery dfQuery = getQuery(getDql(query));
            IDfCollection collection = null;
            try {
                collection = dfQuery.execute(session, queryType);
                int position = 0;
                if (metadataHandler == null) {
                    if (rowHandler instanceof IMetadataHandler) {
                        ((IMetadataHandler) rowHandler)
                                .handleMetadata(collection);
                    }
                } else {
                    metadataHandler.handleMetadata(collection);
                }
                while (collection.next() && (end < 0 || position < end)) {
                    if (position >= start) {
                        rowHandler.handleRow(collection, position);
                    }
                    position++;
                }
                return collection.next();
            } finally {
                close(collection);
            }
        } catch (DfException ex) {
            Logger.error("Exception in selectQueryInternal()", ex);
            throw ex;
        } catch (Exception ex) {
            Logger.error("Exception in selectQueryInternal()", ex);
            throw new DfException("Exception in selectQueryInternal()", ex);
        }
    }

    private static void close(IDfCollection coll) {
        try {
            if (coll == null) {
                return;
            }
            if (coll.getState() != IDfCollection.DF_CLOSED_STATE) {
                coll.close();
            }
        } catch (DfException ex) {
            Logger.error("Exception in close()", ex);
        }
    }

    private static String getDql(CharSequence query) {	
        if (query == null) {
        	return null;
        } else if (query instanceof String) {
        	return (String) query;
        } else {
        	return query.toString();
        }
    }

    private static IDfQuery getQuery(CharSequence query) {
        return SessionUtils.getDfQuery(getDql(query));
    }

}
