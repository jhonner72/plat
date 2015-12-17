package com.fujixerox.aus.asset.impl.query.handlers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class WildcardRangeStringAttributeHandlerTest {

    protected IAttributeHandler _handler;

    @Before
    public void setUp() throws Exception {
        _handler = new StringAttributeHandler(true, true);
    }

    @Test
    public void testSingleLow() throws Exception {
        assertEquals("c >= '1'", _handler.getRestriction(null, "c", "1", ""));
    }

    @Test
    public void testSingleHigh() throws Exception {
        assertEquals("c <= '1'", _handler.getRestriction(null, "c", "", "1"));
    }

    @Test
    public void testNull() throws Exception {
        assertEquals("1<>1", _handler.getRestriction(null, "c", "", ""));
    }

    @Test
    public void testWildcard() throws Exception {
        assertEquals("c LIKE '1%' ESCAPE '\\'", _handler.getRestriction(null,
                "c", "1*", "1*"));
    }

    @Test
    public void testRange() throws Exception {
        assertEquals("c >= '1' AND c <= '2'", _handler.getRestriction(null,
                "c", "1", "2"));
    }

    @Test
    public void testEqualLowAndHigh() throws Exception {
        assertEquals("c = '1'", _handler.getRestriction(null, "c", "1", "1"));
    }

}
