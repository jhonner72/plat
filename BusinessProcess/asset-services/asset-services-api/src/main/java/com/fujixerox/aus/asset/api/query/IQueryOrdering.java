package com.fujixerox.aus.asset.api.query;

import com.fujixerox.aus.asset.api.mapping.IAttribute;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IQueryOrdering<T> extends Comparable<T> {

    IAttribute getAttribute();

    boolean isDescending();

}
