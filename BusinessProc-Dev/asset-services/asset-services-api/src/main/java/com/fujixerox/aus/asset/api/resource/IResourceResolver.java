package com.fujixerox.aus.asset.api.resource;

import java.io.InputStream;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IResourceResolver {

    InputStream getResourceAsStream(Class<?> clazz, String name);

}
