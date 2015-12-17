package com.fujixerox.aus.asset.app.dfc.credentials;

import java.io.Serializable;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Test;

import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.impl.dfc.credentials.SessionCredentialManager;
import com.fujixerox.aus.asset.test.junit.RegexMatcher;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SessionCredentialManagerTest extends AbstractCredentialManagerTest {

    @Override
    public ICredentialManager getCredentialManager() {
        return new SessionCredentialManager(getDocbaseName());
    }

    @Test
    public void validUserMayAuthenticate() {
        UsernamePasswordToken token = new UsernamePasswordToken(getUserName(),
                getPassword());
        Subject currentUser = getSubject();
        currentUser.login(token);
        Assert.assertTrue(currentUser.isAuthenticated());
    }

    @Test
    public void invalidUserMayNotAuthenticate() {
        UsernamePasswordToken token = new UsernamePasswordToken(getUserName(),
                "");
        Subject currentUser = getSubject();
        Assert.assertFalse(currentUser.isAuthenticated());
        try {
            currentUser.login(token);
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof AuthenticationException);
            Assert.assertFalse(currentUser.isAuthenticated());
        }
        Assert.assertFalse(currentUser.isAuthenticated());
    }

    @Test
    public void validSessionId() {
        UsernamePasswordToken token = new UsernamePasswordToken(getUserName(),
                getPassword());
        Subject currentUser = getSubject();
        currentUser.login(token);
        Session session = currentUser.getSession();
        Assert.assertTrue(currentUser.isAuthenticated());
        Serializable sessionId = session.getId();
        Assert.assertNotNull(sessionId);
        Assert.assertEquals(String.class, sessionId.getClass());
        Assert.assertEquals(LENGTH * 2, ((String) sessionId).length());
        Assert.assertThat((String) sessionId, RegexMatcher
                .matchesRegex(TOKEN_PATTERN));
    }

}
