package com.fujixerox.aus.asset.api.beans.tuples;

import java.io.Serializable;

public class Triplet<T extends Triplet, F extends Comparable, S extends Comparable, M extends Comparable>
        extends Pair<T, F, S> implements Serializable, Comparable<T> {

    private static final long serialVersionUID = 1L;

    private final M _third;

    protected Triplet(F first, S second, M third) {
        super(first, second);
        _third = third;
    }

    protected M getThird() {
        return _third;
    }

    @Override
    protected int getRank() {
        return 3;
    }

    @Override
    protected boolean doEquals(T other) {
        if (!super.doEquals(other)) {
            return false;
        }
        return isEquals(getThird(), other.getThird());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        if (_third == null) {
            result = 31 * result;
        } else {
            result = 31 * result + _third.hashCode();
        }
        return result;
    }

    @Override
    public int compareTo(T other) {
        int cmp = super.compareTo(other);
        if (cmp != 0) {
            return cmp;
        }
        return compare(getThird(), other.getThird());
    }

}
