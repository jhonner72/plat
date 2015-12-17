package com.fujixerox.aus.asset.impl.dfc.credentials;

import org.apache.commons.lang3.StringUtils;

import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class BeanCredentialManager extends AbstractCredentialManager {

    private final String _userName;

    private final String _password;

    private final String _domain;

    public BeanCredentialManager(String docbaseName, String userName) {
        this(docbaseName, userName, null);
    }

    public BeanCredentialManager(String docbaseName, String userName,
            String password) {
        this(docbaseName, userName, password, null);
    }

    public BeanCredentialManager(String docbaseName, String userName,
            String password, String domain) {
        super(docbaseName);
        if (StringUtils.isBlank(userName)) {
            throw new NullPointerException("Empty userName");
        }
        _userName = userName;
        _password = password;
        _domain = domain;
    }

    @Override
    public IDocumentumCredentials getCredentials(String userName,
            String password) {
        return doGetCredentials();
    }

    private IDocumentumCredentials doGetCredentials() {
        return new DocumentumCredentials(getDocbaseName(), _userName,
                _password, _domain);
    }

}
