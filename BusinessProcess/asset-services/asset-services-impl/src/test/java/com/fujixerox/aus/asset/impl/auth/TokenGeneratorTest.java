package com.fujixerox.aus.asset.impl.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.asset.api.dfc.credentials.ITokenGenerator;
import com.fujixerox.aus.asset.test.junit.RegexMatcher;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class TokenGeneratorTest {

    public static final int LENGTH = 20;

    public static final String TOKEN_PATTERN = "([0-9a-f]{2}){" + LENGTH + "}";

    private ITokenGenerator _tokenGenerator;

    @Before
    public void setUp() {
        _tokenGenerator = new TokenGenerator(LENGTH);
    }

    @Test
    public void testLength() {
        Serializable id = _tokenGenerator.generateId(null);
        assertNotNull(id);
        assertEquals(String.class, id.getClass());
        assertEquals(LENGTH * 2, ((String) id).length());
        assertThat((String) id, RegexMatcher.matchesRegex(TOKEN_PATTERN));
    }

    @Test
    public void testRandomToken() {
        Assert.assertNotEquals(_tokenGenerator.generateId(null),
                _tokenGenerator.generateId(null));
    }

}
