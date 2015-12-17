package com.fujixerox.aus.asset.impl.auth;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;
import com.fujixerox.aus.asset.impl.dfc.credentials.DocumentumCredentials;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class DocumentumSecurityManager extends DefaultSecurityManager {

    public DocumentumSecurityManager() {
        super();
    }

    public DocumentumSecurityManager(Realm singleRealm) {
        super(singleRealm);
    }

    public DocumentumSecurityManager(Collection<Realm> realms) {
        super(realms);
    }

    @Override
    protected void onSuccessfulLogin(AuthenticationToken token,
            AuthenticationInfo info, Subject subject) {
        super.onSuccessfulLogin(token, info, subject);
        Session session = subject.getSession();
        session.setAttribute(IDocumentumCredentials.class,
                new DocumentumCredentials(null, (String) token.getPrincipal(),
                        new String((char[]) token.getCredentials()), null));
    }

}
