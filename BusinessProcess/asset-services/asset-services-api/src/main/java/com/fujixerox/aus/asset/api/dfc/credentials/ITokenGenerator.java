package com.fujixerox.aus.asset.api.dfc.credentials;

import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface ITokenGenerator extends SessionIdGenerator {

    int getLength();

}
