package com.fujixerox.aus.asset.model.beans.generated.response;

import javax.xml.bind.annotation.XmlTransient;

import com.fujixerox.aus.asset.api.constants.IResponseCode;
import com.fujixerox.aus.asset.model.beans.SessionInfo;
import com.fujixerox.aus.asset.model.beans.generated.ExtensionBase;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
@XmlTransient
public class ResponseBase extends ExtensionBase {

    @SuppressWarnings("unchecked")
    public <T> T withResponseCode(IResponseCode code) {
        setResponseCode(code);
        return (T) this;
    }

    public void setResponseCode(IResponseCode code) {
        setAttribute("code", code.getCode());
        setAttribute("subcode", code.getSubCode());
    }

    @SuppressWarnings("unchecked")
    public <T> T withSessionInfo(SessionInfo sessionInfo) {
        setSessionInfo(sessionInfo);
        return (T) this;
    }

    private void setSessionInfo(SessionInfo info) {
        setAttribute("sessionname", info.getSession());
    }

}
