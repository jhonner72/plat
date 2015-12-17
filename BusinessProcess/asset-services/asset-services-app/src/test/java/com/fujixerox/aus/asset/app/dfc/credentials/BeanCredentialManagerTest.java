package com.fujixerox.aus.asset.app.dfc.credentials;

import java.io.Serializable;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Test;

import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.impl.dfc.credentials.BeanCredentialManager;
import com.fujixerox.aus.asset.test.junit.RegexMatcher;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class BeanCredentialManagerTest extends AbstractCredentialManagerTest {

    @Override
    public ICredentialManager getCredentialManager() {
        return new BeanCredentialManager(getDocbaseName(), getUserName(),
                getPassword());
    }

    @Test
    public void anyUserMayAuthenticate() {
        UsernamePasswordToken token = new UsernamePasswordToken("", "");
        Subject currentUser = getSubject();
        currentUser.login(token);
        Assert.assertTrue(currentUser.isAuthenticated());
    }

    @Test
    public void validSessionId() {
        UsernamePasswordToken token = new UsernamePasswordToken("", "");
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
