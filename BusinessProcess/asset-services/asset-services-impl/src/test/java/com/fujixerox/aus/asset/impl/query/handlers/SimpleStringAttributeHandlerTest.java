package com.fujixerox.aus.asset.impl.query.handlers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SimpleStringAttributeHandlerTest {

    protected IAttributeHandler _handler;

    @Before
    public void setUp() throws Exception {
        _handler = new StringAttributeHandler(false, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSingleLow() throws Exception {
        assertEquals("c >= '1'", _handler.getRestriction(null, "c", "1", ""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSingleHigh() throws Exception {
        assertEquals("c <= '1'", _handler.getRestriction(null, "c", "", "1"));
    }

    @Test
    public void testNull() throws Exception {
        assertEquals("1<>1", _handler.getRestriction(null, "c", "", ""));
    }

    @Test
    public void testWildcard() throws Exception {
        assertEquals("c = '1*'", _handler.getRestriction(null, "c", "1*", "1*"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRange() throws Exception {
        assertEquals("c >= '1' AND c <= '2'", _handler.getRestriction(null,
                "c", "1", "2"));
    }

    @Test
    public void testEqualLowAndHigh() throws Exception {
        assertEquals("c = '1'", _handler.getRestriction(null, "c", "1", "1"));
    }

}
