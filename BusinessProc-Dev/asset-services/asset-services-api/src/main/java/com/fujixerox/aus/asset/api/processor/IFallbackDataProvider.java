package com.fujixerox.aus.asset.api.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IFallbackDataProvider {

    InputStream getBlankImage() throws IOException;

    InputStream getNoResultImage() throws IOException;

    InputStream getFallBackImage() throws IOException;

    Map<String, String> getNoResultData(Set<String> attributes);

}
