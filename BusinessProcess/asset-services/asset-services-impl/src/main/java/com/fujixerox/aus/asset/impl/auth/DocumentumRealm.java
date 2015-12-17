package com.fujixerox.aus.asset.impl.auth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class DocumentumRealm extends AbstractAuthenticatingRealm {

    public DocumentumRealm(ICredentialManager credentialManager) {
        super(credentialManager);
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
        setAuthenticationTokenClass(AuthenticationToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
        try {
            return queryForAuthenticationInfo(token);
        } catch (DfException e) {
            throw new AuthenticationException(e);
        }
    }

    private AuthenticationInfo queryForAuthenticationInfo(
            AuthenticationToken token) throws DfException {
        authenticate(token);
        return createAuthenticationInfo(token);
    }

    private AuthenticationInfo createAuthenticationInfo(
            AuthenticationToken token) {
        return new SimpleAuthenticationInfo(token.getPrincipal(), token
                .getPrincipal(), getName());
    }

}
