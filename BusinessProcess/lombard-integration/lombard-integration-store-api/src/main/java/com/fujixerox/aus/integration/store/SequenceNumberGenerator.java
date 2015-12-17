package com.fujixerox.aus.integration.store;

/**
 * Given a sequence name determine the next sequence value.
 * Created by warwick on 12/05/2015.
 */
public interface SequenceNumberGenerator {
    public int nextSequenceNumber(Class sequenceName);
    public void resetSequenceNumber(Class sequenceName);
}
