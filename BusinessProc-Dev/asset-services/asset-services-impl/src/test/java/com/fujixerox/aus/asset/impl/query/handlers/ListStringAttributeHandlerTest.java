package com.fujixerox.aus.asset.impl.query.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ListStringAttributeHandlerTest {

    protected IAttributeHandler _handler;

    @Before
    public void setUp() throws Exception {
        _handler = new StringListAttributeHandler(false, Arrays.asList("1",
                "2", "3", "4"));
    }

    @Test
    public void testSingleLow() throws Exception {
        assertEquals("c = '1'", _handler.getRestriction(null, "c", "1", ""));
    }

    @Test
    public void testSingleHigh() throws Exception {
        assertEquals("c = '1'", _handler.getRestriction(null, "c", "", "1"));
    }

    @Test
    public void testNull() throws Exception {
        assertEquals("1<>1", _handler.getRestriction(null, "c", "", ""));
    }

    @Test
    public void testWildcard() throws Exception {
        assertEquals("1<>1", _handler.getRestriction(null, "c", "1*", "1*"));
    }

    @Test
    public void testRange() throws Exception {
        assertEquals("c IN ('1','2')", _handler.getRestriction(null, "c", "1",
                "2"));
    }

    @Test
    public void testEqualLowAndHigh() throws Exception {
        assertEquals("c = '1'", _handler.getRestriction(null, "c", "1", "1"));
    }

}
