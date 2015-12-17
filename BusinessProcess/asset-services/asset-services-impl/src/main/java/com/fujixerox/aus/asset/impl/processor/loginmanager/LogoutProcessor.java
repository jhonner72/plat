package com.fujixerox.aus.asset.impl.processor.loginmanager;

import org.apache.shiro.subject.Subject;

import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.model.beans.generated.request.Loginmanager;
import com.fujixerox.aus.asset.model.beans.generated.response.Loginmanagerresult;
import com.fujixerox.aus.asset.model.beans.generated.response.LogoutResult;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class LogoutProcessor extends AbstractLoginManagerProcessor {

    public LogoutProcessor(ICredentialManager credentialManager,
            boolean authenticationRequired) {
        super(credentialManager, authenticationRequired);
    }

    @Override
    protected boolean doCanProcess(Loginmanager request) {
        return request.getLogout() != null;
    }

    @Override
    protected Loginmanagerresult doProcess(Loginmanager request, String userName) {
        if (isAuthenticationRequired()) {
            Subject subject = getSubject(request);
            subject.logout();
        }
        return createResponse(request).withLogoutresult(new LogoutResult());
    }

}
