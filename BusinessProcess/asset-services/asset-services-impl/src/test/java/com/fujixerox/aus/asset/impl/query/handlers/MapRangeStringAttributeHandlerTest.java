package com.fujixerox.aus.asset.impl.query.handlers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.documentum.fc.common.IDfValue;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class MapRangeStringAttributeHandlerTest {

    protected IAttributeHandler _handler;

    protected IDfValue _value;

    @Before
    public void setUp() throws Exception {
        _handler = new MapStringAttributeHandler(true, Arrays.asList("1", "2",
                "3", "4"), Arrays.asList("a", "b", "c", "d"));
        _value = mock(IDfValue.class);
    }

    @Test
    public void testSingleLow() throws Exception {
        assertEquals("c IN ('a','b','c','d')", _handler.getRestriction(null,
                "c", "1", ""));
    }

    @Test
    public void testSingleHigh() throws Exception {
        assertEquals("c = 'a'", _handler.getRestriction(null, "c", "", "1"));
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
        assertEquals("c IN ('a','b')", _handler.getRestriction(null, "c", "1",
                "2"));
    }

    @Test
    public void testEqualLowAndHigh() throws Exception {
        assertEquals("c = 'a'", _handler.getRestriction(null, "c", "1", "1"));
    }

    @Test
    public void testTransform() throws Exception {
        when(_value.asString()).thenReturn("a");
        assertEquals("1", _handler.getStringValue(_value));
        when(_value.asString()).thenReturn("b");
        assertEquals("2", _handler.getStringValue(_value));
        when(_value.asString()).thenReturn("c");
        assertEquals("3", _handler.getStringValue(_value));
        when(_value.asString()).thenReturn("d");
        assertEquals("4", _handler.getStringValue(_value));
        when(_value.asString()).thenReturn("e");
        assertEquals("", _handler.getStringValue(_value));
    }

}
