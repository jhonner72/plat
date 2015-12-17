package com.fujixerox.aus.asset.api.beans.tuples;

import java.io.Serializable;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class Sextet<T extends Sextet, F extends Comparable, S extends Comparable, M extends Comparable, N extends Comparable, O extends Comparable, P extends Comparable>
        extends Quintet<T, F, S, M, N, O> implements Serializable,
        Comparable<T> {

    private static final long serialVersionUID = 1L;

    private final P _sixth;

    protected Sextet(F first, S second, M third, N fourth, O fifth, P sixth) {
        super(first, second, third, fourth, fifth);
        _sixth = sixth;
    }

    P getSixth() {
        return _sixth;
    }

    @Override
    protected int getRank() {
        return 6;
    }

    @Override
    protected boolean doEquals(T other) {
        if (!super.doEquals(other)) {
            return false;
        }
        return isEquals(getSixth(), other.getSixth());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        if (_sixth == null) {
            result = 31 * result;
        } else {
            result = 31 * result + _sixth.hashCode();
        }
        return result;
    }

    @Override
    public int compareTo(T other) {
        int cmp = super.compareTo(other);
        if (cmp != 0) {
            return cmp;
        }
        return compare(getSixth(), other.getSixth());
    }

}
