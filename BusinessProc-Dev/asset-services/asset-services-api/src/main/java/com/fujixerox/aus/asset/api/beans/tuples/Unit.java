package com.fujixerox.aus.asset.api.beans.tuples;

import java.io.Serializable;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class Unit<T extends Unit, F extends Comparable> extends
        AbstractTuple<T> implements Serializable, Comparable<T> {

    private static final long serialVersionUID = 1L;

    private final F _first;

    Unit(F first) {
        super();
        _first = first;
    }

    protected F getFirst() {
        return _first;
    }

    @Override
    protected int getRank() {
        return 1;
    }

    @Override
    protected boolean doEquals(T other) {
        return isEquals(getFirst(), other.getFirst());
    }

    @Override
    public int hashCode() {
        if (_first == null) {
            return 0;
        }
        return _first.hashCode();
    }

    @Override
    public int compareTo(T other) {
        int cmp = super.compareTo(other);
        if (cmp == 0) {
            return compare(getFirst(), other.getFirst());
        }
        return cmp;
    }

}
