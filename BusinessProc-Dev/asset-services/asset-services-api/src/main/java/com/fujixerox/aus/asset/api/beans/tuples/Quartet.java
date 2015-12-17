package com.fujixerox.aus.asset.api.beans.tuples;

import java.io.Serializable;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class Quartet<T extends Quartet, F extends Comparable, S extends Comparable, M extends Comparable, N extends Comparable>
        extends Triplet<T, F, S, M> implements Serializable, Comparable<T> {

    private static final long serialVersionUID = 1L;

    private final N _fourth;

    protected Quartet(F first, S second, M third, N fourth) {
        super(first, second, third);
        _fourth = fourth;
    }

    protected N getFourth() {
        return _fourth;
    }

    @Override
    protected int getRank() {
        return 4;
    }

    @Override
    protected boolean doEquals(T other) {
        if (!super.doEquals(other)) {
            return false;
        }
        return isEquals(getFourth(), other.getFourth());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        if (_fourth == null) {
            result = 31 * result;
        } else {
            result = 31 * result + _fourth.hashCode();
        }
        return result;
    }

    @Override
    public int compareTo(T other) {
        int cmp = super.compareTo(other);
        if (cmp != 0) {
            return cmp;
        }
        return compare(getFourth(), other.getFourth());
    }

}
