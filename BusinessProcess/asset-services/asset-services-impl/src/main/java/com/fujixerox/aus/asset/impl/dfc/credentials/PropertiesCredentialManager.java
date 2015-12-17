package com.fujixerox.aus.asset.impl.dfc.credentials;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.impl.security.action.GetPropertyAction;
import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;
import com.fujixerox.aus.asset.api.util.CoreUtils;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class PropertiesCredentialManager extends AbstractCredentialManager {

    private static final String DOCBASE = "docbase";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String DOMAIN = "domain";

    private static final String PROPERTIES_FILE = "documentumcredentials.properties";

    private static final String PROPERTIES_FILE_LOCATION = "documentumcredentials.location";

    public PropertiesCredentialManager(String docbaseName) {
        super(docbaseName);
    }

    @Override
    public IDocumentumCredentials getCredentials(String userName,
            String password) {
        return doGetCredentials();
    }

    private IDocumentumCredentials doGetCredentials() {
        return getData(getProperties());
    }

    @Override
    public String getDocbaseName() {
        return doGetCredentials().getDocbaseName();
    }

    private IDocumentumCredentials getData(Properties properties) {
        if (properties == null) {
            throw new NullPointerException("Empty properties");
        }
        String docbase = properties.getProperty(DOCBASE);
        if (docbase == null) {
            throw new NullPointerException("Empty docbaseName");
        }
        String userName = properties.getProperty(USERNAME);
        if (StringUtils.isBlank(userName)) {
            throw new NullPointerException("Empty userName");
        }
        String password = properties.getProperty(PASSWORD);
        String domain = properties.getProperty(DOMAIN);
        return new DocumentumCredentials(docbase, userName, password, domain);
    }

    private Properties getProperties() {
        Properties properties = loadFromLocation();
        if (properties == null) {
            properties = loadFromClassPath();
        }
        return properties;
    }

    private Properties loadFromClassPath() {
        InputStream stream = getResourceAsStream(PROPERTIES_FILE);
        if (stream != null) {
            return load(stream);
        }
        return null;
    }

    private InputStream getResourceAsStream(String name) {
        return CoreUtils.getResourceAsStream(getClass(), name);
    }

    private Properties loadFromLocation() {
        String propertiesLocation = getPropertiesLocation();
        if (propertiesLocation == null) {
            return null;
        }
        try {
            URL location = new URL(propertiesLocation);
            Logger.debug("Properties loaded from " + location);
            return load(location.openStream());
        } catch (MalformedURLException ex) {
            Logger.error(ex.getMessage(), ex);
            return null;
        } catch (IOException ex) {
            Logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    private String getPropertiesLocation() {
        try {
            return AccessController.doPrivileged(new GetPropertyAction(
                    PROPERTIES_FILE_LOCATION));
        } catch (AccessControlException ex) {
            Logger.error("Failed to learn documentumcredentials.location", ex);
        }
        return null;
    }

    private Properties load(InputStream stream) {
        try {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException ex) {
            Logger.error(ex.getMessage(), ex);
            return null;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

}
