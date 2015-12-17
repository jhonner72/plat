package com.fujixerox.aus.asset.impl.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fujixerox.aus.asset.api.processor.IFallbackDataProvider;
import com.fujixerox.aus.asset.api.resource.IResourceResolver;
import com.fujixerox.aus.asset.api.util.CoreUtils;
import com.fujixerox.aus.asset.api.util.cache.impl.ThreadLocalStorage;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class BeanFallbackDataProvider implements IFallbackDataProvider {

    private final String _blankLocation;

    private final String _noResultLocation;

    private final String _fallbackLocation;

    private final Map<String, String> _mapping;

    public BeanFallbackDataProvider(String blankLocation,
            String noResultLocation, String fallbackLocation,
            Map<String, String> mapping) {
        _blankLocation = blankLocation;
        _noResultLocation = noResultLocation;
        _fallbackLocation = fallbackLocation;
        _mapping = mapping;
    }

    @Override
    public InputStream getBlankImage() throws IOException {
        return getResourceAsStream(_blankLocation);
    }

    @Override
    public InputStream getNoResultImage() throws IOException {
        return getResourceAsStream(_noResultLocation);
    }

    @Override
    public InputStream getFallBackImage() throws IOException {
        return getResourceAsStream(_fallbackLocation);
    }

    @Override
    public Map<String, String> getNoResultData(Set<String> attributes) {
        if (_mapping == null) {
            return new HashMap<String, String>();
        }
        return new HashMap<String, String>(_mapping);
    }

    private InputStream getResourceAsStream(String name) throws IOException {
        if (StringUtils.isBlank(name)) {
            Logger.debug("Empty resource name");
            return null;
        }
        InputStream stream = null;

        IResourceResolver resourceResolver = ThreadLocalStorage
                .get(IResourceResolver.class);
        if (resourceResolver != null) {
            Logger.debug("Using resource resolver");
            stream = resourceResolver.getResourceAsStream(getClass(), name);
        }

        if (stream == null) {
            Logger.debug("Stream is null, trying classpath");
            stream = CoreUtils.getResourceAsStream(getClass(), name);
        }

        if (stream == null) {
            Logger.debug("Empty stream, returning null");
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(stream, baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

}
