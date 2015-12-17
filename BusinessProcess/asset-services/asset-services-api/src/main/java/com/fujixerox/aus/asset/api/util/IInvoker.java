package com.fujixerox.aus.asset.api.util;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IInvoker<T, S, E extends Throwable> {

    T invoke(S session) throws E;

}
