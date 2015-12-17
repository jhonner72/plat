package com.fujixerox.aus.asset.api.util.cache.impl;

import org.junit.Assert;
import org.junit.Test;

import com.fujixerox.aus.asset.api.util.cache.impl.AtomicCyclicCounter;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class AtomicCyclicCounterTest {

    @Test
    public void testMaxValueIsIncludedBound() {
        AtomicCyclicCounter counter = new AtomicCyclicCounter(0, 1);
        Assert.assertEquals(1, counter.incrementAndGet());
        Assert.assertEquals(0, counter.incrementAndGet());
    }

    @Test
    public void testFirstValueIsOne() {
        AtomicCyclicCounter counter = new AtomicCyclicCounter(0, 1);
        Assert.assertEquals(1, counter.incrementAndGet());
    }

}
