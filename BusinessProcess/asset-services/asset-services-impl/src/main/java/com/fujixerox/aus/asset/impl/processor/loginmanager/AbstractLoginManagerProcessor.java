package com.fujixerox.aus.asset.impl.processor.loginmanager;

import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.impl.processor.AbstractRequestProcessor;
import com.fujixerox.aus.asset.model.beans.generated.request.Loginmanager;
import com.fujixerox.aus.asset.model.beans.generated.response.Loginmanagerresult;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractLoginManagerProcessor extends
        AbstractRequestProcessor<Loginmanager, Loginmanagerresult> {

    AbstractLoginManagerProcessor(ICredentialManager credentialManager,
            boolean authenticationRequired) {
        super(credentialManager, authenticationRequired);
    }

    @Override
    public final Class<Loginmanager> getRequestClass() {
        return Loginmanager.class;
    }

    @Override
    public final Class<Loginmanagerresult> getResponseClass() {
        return Loginmanagerresult.class;
    }

}
