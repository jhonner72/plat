package com.fujixerox.aus.asset.model.beans.generated.request;

import javax.xml.bind.annotation.XmlTransient;

import com.fujixerox.aus.asset.model.beans.SessionInfo;
import com.fujixerox.aus.asset.model.beans.generated.ExtensionBase;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
@XmlTransient
public class RequestBase extends ExtensionBase {

    public SessionInfo getSessionInfo() {
        return new SessionInfo((String) getAttribute("sessionname"),
                (String) getAttribute("applname"));
    }

    public String getSecurityToken() {
        return (String) getAttribute("sectoken");
    }

}
