package com.fujixerox.aus.asset.app.component;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class ContextBuilder {

    private final Map<String, Object> _parameters;

    public ContextBuilder(Map<String, Object> parameters) {
        super();
        _parameters = new HashMap<>(parameters);
    }

    public void buildContext() throws NamingException {
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        for (Map.Entry<String, Object> e : _parameters.entrySet()) {
            builder.bind(e.getKey(), e.getValue());
        }
        builder.activate();
    }

}
