package com.fujixerox.aus.asset.api.query;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IQueryCondition<T> extends Comparable<T> {

    String getLowValue();

    String getHighValue();

    boolean isMatchCase();
}
