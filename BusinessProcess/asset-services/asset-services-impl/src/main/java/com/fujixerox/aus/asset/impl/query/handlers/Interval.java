package com.fujixerox.aus.asset.impl.query.handlers;

import com.fujixerox.aus.asset.api.beans.tuples.Triplet;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class Interval extends Triplet<Interval, Integer, Integer, Boolean> {

    public Interval(int low, int high, boolean interval) {
        super(low, high, interval);
    }

    public int getLow() {
        return getFirst();
    }

    public int getHigh() {
        return getSecond();
    }

    public boolean include() {
        return getThird();
    }

}
