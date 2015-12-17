package com.fujixerox.aus.asset.impl.dfc.credentials;

import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SessionCredentialManager extends AbstractCredentialManager {

    public SessionCredentialManager(String docbaseName) {
        super(docbaseName);
    }

    @Override
    public IDocumentumCredentials getCredentials(String userName,
            String password) {
        return new DocumentumCredentials(getDocbaseName(), userName, password,
                null);
    }

}
