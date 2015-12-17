package com.fujixerox.aus.asset.api.dfc.credentials;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface ICredentialManager {

    String getDocbaseName();

    IDocumentumCredentials getCredentials(String sessionId);

    IDocumentumCredentials getCredentials(String userName, String password);

}
