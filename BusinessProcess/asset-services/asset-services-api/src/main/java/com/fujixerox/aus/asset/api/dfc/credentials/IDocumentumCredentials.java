package com.fujixerox.aus.asset.api.dfc.credentials;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IDocumentumCredentials {

    String getDocbaseName();

    String getUserName();

    String getPassword();

    String getDomain();
}
