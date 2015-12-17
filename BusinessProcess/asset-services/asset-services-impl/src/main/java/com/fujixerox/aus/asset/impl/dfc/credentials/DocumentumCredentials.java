package com.fujixerox.aus.asset.impl.dfc.credentials;

import com.fujixerox.aus.asset.api.beans.tuples.Quartet;
import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class DocumentumCredentials extends
        Quartet<DocumentumCredentials, String, String, String, String>
        implements IDocumentumCredentials {

    public DocumentumCredentials(String docbaseName, String userName,
            String password, String domain) {
        super(docbaseName, userName, password, domain);
    }

    @Override
    public String getDocbaseName() {
        return getFirst();
    }

    @Override
    public String getUserName() {
        return getSecond();
    }

    @Override
    public String getPassword() {
        return getThird();
    }

    @Override
    public String getDomain() {
        return getFourth();
    }

}
