package com.fujixerox.aus.asset.api.beans.tuples;

import java.io.Serializable;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class Quintet<T extends Quintet, F extends Comparable, S extends Comparable, M extends Comparable, N extends Comparable, O extends Comparable>
        extends Quartet<T, F, S, M, N> implements Serializable, Comparable<T> {

    private static final long serialVersionUID = 1L;

    private final O _fifth;

    Quintet(F first, S second, M third, N fourth, O fifth) {
        super(first, second, third, fourth);
        _fifth = fifth;
    }

    O getFifth() {
        return _fifth;
    }

    @Override
    protected int getRank() {
        return 5;
    }

    @Override
    protected boolean doEquals(T other) {
        if (!super.doEquals(other)) {
            return false;
        }
        return isEquals(getFirst(), other.getFifth());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        if (_fifth == null) {
            result = 31 * result;
        } else {
            result = 31 * result + _fifth.hashCode();
        }
        return result;
    }

    @Override
    public int compareTo(T other) {
        int cmp = super.compareTo(other);
        if (cmp != 0) {
            return cmp;
        }
        return compare(getFirst(), other.getFifth());
    }

}
