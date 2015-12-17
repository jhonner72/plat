package com.fujixerox.aus.asset.app.component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadState;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.api.dfc.credentials.ITokenGenerator;
import com.fujixerox.aus.asset.api.dfc.preferences.PreferencesLoader;
import com.fujixerox.aus.asset.api.util.logger.LogLevel;
import com.fujixerox.aus.asset.api.util.logger.Logger;
import com.fujixerox.aus.asset.impl.auth.DocumentumRealm;
import com.fujixerox.aus.asset.impl.auth.DocumentumSecurityManager;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public aspect ComponentTestAspect {

    public interface IComponentTest {

        ITokenGenerator getSessionIdGenerator();

        ICredentialManager getCredentialManager();

        Subject getSubject();

        String getDocbaseName();

        String getUserName();

        String getPassword();

    }

    private ThreadState IComponentTest._subjectThreadState;

    private ICredentialManager IComponentTest._internalCredentialManager;

    before(): @annotation(org.junit.Test) && target(IComponentTest) {
        IComponentTest target = (IComponentTest) getTarget(thisJoinPoint);
        setupJNDIContext(target);
        loadDFCPreferences(target);
        setupCredentialManager(target);
        setupAuthentication(target);
    }

    after(): @annotation(org.junit.Test) && target(IComponentTest) {
        IComponentTest target = (IComponentTest) getTarget(thisJoinPoint);
        tearDown(target);
    }

    static Object getTarget(final JoinPoint point) {
        Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
        if (Modifier.isStatic(method.getModifiers())) {
            return method.getDeclaringClass();
        }
        return point.getTarget();
    }

    public final ContextBuilder setupJNDIContext(IComponentTest target) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring/test-jndi.xml");
        return (ContextBuilder) ctx.getBean("jndiLoader");
    }

    public final void loadDFCPreferences(IComponentTest target) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring/preferences/dfc-preferences.xml");
        PreferencesLoader loader = (PreferencesLoader) ctx.getBean("preferencesLoader");
    }

    public final void setupCredentialManager(IComponentTest target) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring/auth/shiro-preferences.xml");
        target._internalCredentialManager = (ICredentialManager) ctx.getBean("credentialManager");
    }

    public final void setupAuthentication(final IComponentTest target) {
        MemorySessionDAO sessionDAO = new MemorySessionDAO();
        sessionDAO.setSessionIdGenerator(target.getSessionIdGenerator());
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        SessionsSecurityManager securityManager = new DocumentumSecurityManager(new DocumentumRealm(target.getCredentialManager()));
        securityManager.setSessionManager(sessionManager);
        setSecurityManager(securityManager);
    }

    public final ITokenGenerator IComponentTest.getSessionIdGenerator() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring/auth/shiro-preferences.xml");
        return (ITokenGenerator) ctx.getBean("sessionIdGenerator");
    }

    public final void IComponentTest.setSubject(Subject subject) {
        clearSubject();
        _subjectThreadState = createThreadState(subject);
        _subjectThreadState.bind();
    }

    public final Subject IComponentTest.getSubject() {
        return new Subject.Builder(getSecurityManager()).buildSubject();
    }

    public ThreadState IComponentTest.createThreadState(Subject subject) {
        return new SubjectThreadState(subject);
    }

    public final void IComponentTest.clearSubject() {
        doClearSubject();
    }

    public final void IComponentTest.doClearSubject() {
        synchronized (this) {
            if (_subjectThreadState != null) {
                _subjectThreadState.clear();
                _subjectThreadState = null;
            }
        }
    }

    protected static void setSecurityManager(SecurityManager securityManager) {
        SecurityUtils.setSecurityManager(securityManager);
    }

    protected static SecurityManager getSecurityManager() {
        return SecurityUtils.getSecurityManager();
    }

    public static void tearDownShiro(IComponentTest taget) {
        try {
            SecurityManager securityManager = getSecurityManager();
            LifecycleUtils.destroy(securityManager);
        } catch (UnavailableSecurityManagerException e) {
            Logger.log(LogLevel.DEBUG, taget, e.getMessage(), e);
        }
        setSecurityManager(null);
    }

    public void tearDown(IComponentTest target) {
        try {
            target.clearSubject();
        } catch (Throwable t) {
            //ignore
        }
    }

    public final String IComponentTest.getDocbaseName() {
        return _internalCredentialManager.getCredentials(null, null).getDocbaseName();
    }

    public final String IComponentTest.getUserName() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring/test-jndi.xml");
        ContextBuilder builder = (ContextBuilder) ctx.getBean("jndiLoader");
        return _internalCredentialManager.getCredentials((String) builder.get("java:comp/env/documentum/username"), (String) builder.get("java:comp/env/documentum/password")).getUserName();
    }

    public final String IComponentTest.getPassword() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring/test-jndi.xml");
        ContextBuilder builder = (ContextBuilder) ctx.getBean("jndiLoader");
        return _internalCredentialManager.getCredentials((String) builder.get("java:comp/env/documentum/username"), (String) builder.get("java:comp/env/documentum/password")).getPassword();
    }

}