package com.fujixerox.aus.asset.api.processor;

import java.util.Map;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IRequestProcessor<I, O> {

    O process(I request, Map headers);

    boolean canProcess(I request);

    Class<I> getRequestClass();

    Class<O> getResponseClass();

}
