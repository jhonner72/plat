package com.fujixerox.aus.asset.api.beans.tuples;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
abstract class AbstractTuple<T extends AbstractTuple> implements Comparable<T> {

    AbstractTuple() {
        super();
    }

    protected abstract int getRank();

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    boolean isEquals(Object first, Object second) {
        return (first == second) || (first != null && first.equals(second));
    }

    @SuppressWarnings("unchecked")
    int compare(Comparable first, Comparable second) {
        if (first == null) {
            if (second == null) {
                return 0;
            }
            return -1;
        } else {
            if (second == null) {
                return +1;
            }
            return first.compareTo(second);
        }
    }

    protected abstract boolean doEquals(T other);

    @Override
    @SuppressWarnings("unchecked")
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() != getClass()) {
            return false;
        }
        return doEquals((T) other);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public int compareTo(T other) {
        return compare(getRank(), other.getRank());
    }

}
