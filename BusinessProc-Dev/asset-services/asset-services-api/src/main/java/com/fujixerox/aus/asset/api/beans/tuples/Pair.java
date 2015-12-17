package com.fujixerox.aus.asset.api.beans.tuples;

import java.io.Serializable;

public class Pair<T extends Pair, F extends Comparable, S extends Comparable>
        extends Unit<T, F> implements Serializable, Comparable<T> {

    private static final long serialVersionUID = 1L;

    private final S _second;

    protected Pair(F first, S second) {
        super(first);
        _second = second;
    }

    protected S getSecond() {
        return _second;
    }

    @Override
    protected int getRank() {
        return 2;
    }

    @Override
    protected boolean doEquals(T other) {
        if (!super.doEquals(other)) {
            return false;
        }
        return isEquals(getSecond(), other.getSecond());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        if (_second == null) {
            result = 31 * result;
        } else {
            result = 31 * result + _second.hashCode();
        }
        return result;
    }

    @Override
    public int compareTo(T other) {
        int cmp = super.compareTo(other);
        if (cmp == 0) {
            return compare(getSecond(), other.getSecond());
        }
        return cmp;
    }

}
