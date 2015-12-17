package com.fujixerox.aus.asset.impl.dfc.credentials;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractCredentialManager implements ICredentialManager {

    private final String _docbaseName;

    AbstractCredentialManager(String docbaseName) {
        _docbaseName = docbaseName;
    }

    @Override
    public String getDocbaseName() {
        return _docbaseName;
    }

    @Override
    public final IDocumentumCredentials getCredentials(String sessionId) {
        Subject subject = new Subject.Builder().sessionId(sessionId)
                .buildSubject();

        if (!subject.isAuthenticated()) {
            Logger.error("Subject for session " + sessionId
                    + " is not authenticated");
            return null;
        }

        Session session = subject.getSession();
        if (session == null) {
            Logger.error("No session for sessionId: " + sessionId);
            return null;
        }

        IDocumentumCredentials stored = (IDocumentumCredentials) session
                .getAttribute(IDocumentumCredentials.class);

        if (stored == null) {
            Logger.error("Session with sessionId: " + sessionId
                    + " does not contain Documentum credentials");
            return null;
        }

        return getCredentials(stored.getUserName(), stored.getPassword());
    }

}
