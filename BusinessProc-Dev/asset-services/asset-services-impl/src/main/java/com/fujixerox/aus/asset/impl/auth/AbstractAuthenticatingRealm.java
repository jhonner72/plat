package com.fujixerox.aus.asset.impl.auth;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthenticatingRealm;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;
import com.fujixerox.aus.asset.api.dfc.util.SessionUtils;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
abstract class AbstractAuthenticatingRealm extends AuthenticatingRealm {

    private final ICredentialManager _credentialManager;

    AbstractAuthenticatingRealm(ICredentialManager credentialManager) {
        super();
        _credentialManager = credentialManager;
    }

    final void authenticate(AuthenticationToken token) throws DfException {
        IDocumentumCredentials credentials = _credentialManager.getCredentials(
                (String) token.getPrincipal(), new String((char[]) token
                        .getCredentials()));
        SessionUtils.newSessionManager(credentials.getDocbaseName(),
                credentials.getUserName(), credentials.getPassword())
                .authenticate(credentials.getDocbaseName());
    }

}
