package com.fujixerox.aus.asset.web.resource;

import java.io.InputStream;

import javax.servlet.ServletContext;

import com.fujixerox.aus.asset.api.resource.IResourceResolver;
import com.fujixerox.aus.asset.api.util.CoreUtils;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ContextResourceResolver implements IResourceResolver {

    private final ServletContext _servletContext;

    public ContextResourceResolver(ServletContext servletContext) {
        super();
        _servletContext = servletContext;
    }

    @Override
    public InputStream getResourceAsStream(Class<?> clazz, String name) {
        InputStream stream = null;
        if (_servletContext != null) {
            try {
                if (name.charAt(0) == '/') {
                    stream = _servletContext.getResourceAsStream(name);
                } else {
                    stream = _servletContext.getResourceAsStream("/" + name);
                }
            } catch (IllegalArgumentException ex) {
                Logger.error(ex.getMessage(), ex);
            }
        } else {
            Logger.error("Context is null");
        }
        if (stream != null) {
            return stream;
        }
        return CoreUtils.getResourceAsStream(clazz, name);
    }

}
