package com.fujixerox.aus.asset.api.dfc.util;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.fujixerox.aus.asset.api.dfc.session.IDfcSessionInvoker;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class SessionUtils {

    private static final IDfClientX CLIENT_X = new DfClientX();

    private SessionUtils() {
        super();
    }

    private static IDfClientX getClientX() {
        return CLIENT_X;
    }

    private static IDfClient getClient() throws DfException {
        return getClientX().getLocalClient();
    }

    public static IDfSessionManager newSessionManager() throws DfException {
        return getClient().newSessionManager();
    }

    private static IDfLoginInfo newLoginInfo() {
        return getClientX().getLoginInfo();
    }

    public static IDfLoginInfo newLoginInfo(String userName, String password,
            String domain) {
        IDfLoginInfo loginInfo = newLoginInfo();
        loginInfo.setUser(userName);
        loginInfo.setPassword(password);
        loginInfo.setDomain(domain);
        return loginInfo;
    }

    private static IDfQuery getDfQuery() {
        return getClientX().getQuery();
    }

    public static IDfQuery getDfQuery(String query) {
        IDfQuery dfQuery = getDfQuery();
        dfQuery.setDQL(query);
        return dfQuery;
    }

    public static <T> T invokeInNewSession(IDfSessionManager sessionManager,
            String docbase, IDfcSessionInvoker<T> invoker) throws DfException {
        IDfSession session = null;
        try {
            session = sessionManager.newSession(docbase);
            return invoker.invoke(session);
        } finally {
            if (session != null) {
                sessionManager.release(session);
            }
        }
    }

    public static <T> T invokeInSession(IDfSessionManager sessionManager,
            String docbase, IDfcSessionInvoker<T> invoker) throws DfException {
        IDfSession session = null;
        try {
            session = sessionManager.getSession(docbase);
            return invoker.invoke(session);
        } finally {
            if (session != null) {
                sessionManager.release(session);
            }
        }
    }

    public static IDfSession newSession(String docbase, String userName,
            String password, String domain) throws DfException {
        return getClient().newSession(docbase,
                newLoginInfo(userName, password, domain));
    }

    public static IDfSessionManager newSessionManager(String docbase,
            String userName, String password) throws DfException {
        return newSessionManager(docbase, userName, password, null);
    }

    private static IDfSessionManager newSessionManager(String docbase,
            String userName, String password, String domain) throws DfException {
        IDfSessionManager sessionManager = getClient().newSessionManager();
        sessionManager.setIdentity(docbase, newLoginInfo(userName, password,
                domain));
        return sessionManager;
    }

}
