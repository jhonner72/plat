package com.fujixerox.aus.asset.impl.processor.loginmanager;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.api.util.logger.Logger;
import com.fujixerox.aus.asset.impl.constants.ResponseConstants.LoginManagerCode.LoginCode;
import com.fujixerox.aus.asset.model.beans.generated.request.Login;
import com.fujixerox.aus.asset.model.beans.generated.request.Loginmanager;
import com.fujixerox.aus.asset.model.beans.generated.response.LoginResult;
import com.fujixerox.aus.asset.model.beans.generated.response.Loginmanagerresult;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class LoginProcessor extends AbstractLoginManagerProcessor {

    // never expired
    private static final int EXPIRE_DAYS = 24855;

    public LoginProcessor(ICredentialManager credentialManager) {
        super(credentialManager, false);
    }

    @Override
    protected boolean doCanProcess(Loginmanager request) {
        return request.getLogin() != null;
    }

    @Override
    protected boolean isAuthenticationRequired() {
        return false;
    }

    @Override
    protected Loginmanagerresult doProcess(Loginmanager request, String userName) {
        Loginmanagerresult result = createResponse(request);
        try {
            String sessionId = createNewSessionId(request);
            LoginResult loginResult = new LoginResult().withSectoken(sessionId)
                    .withSubcode(EXPIRE_DAYS);
            return result.withLoginresult(loginResult);
        } catch (AuthenticationException ex) {
            return result.withResponseCode(LoginCode.LOGIN_FAILED);
        }
    }

    private String createNewSessionId(Loginmanager request) {
        Login login = request.getLogin();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(login
                    .getUser(), login.getPassword());
            token.setRememberMe(true);
            Subject currentUser = new Subject.Builder().buildSubject();
            if (super.isAuthenticationRequired()) {
                currentUser.login(token);
                Logger.debug("User " + login.getUser()
                        + " successfully authenticated");
            }
            Session session = currentUser.getSession();
            return String.valueOf(session.getId());
        } catch (AuthenticationException ex) {
            Logger.error("Unable to authenticate user " + login.getUser(), ex);
            throw ex;
        }
    }

}
