package com.fujixerox.aus.asset.impl.auth;

import java.io.Serializable;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;
import org.apache.shiro.session.Session;

import com.fujixerox.aus.asset.api.dfc.credentials.ITokenGenerator;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class TokenGenerator implements ITokenGenerator {

    private final int _length;

    private final SecureRandom _random;

    public TokenGenerator(int bytes) {
        _length = bytes;
        _random = new SecureRandom(new SecureRandom().generateSeed(_length));
    }

    @Override
    public int getLength() {
        return _length;
    }

    @Override
    public Serializable generateId(Session session) {
        byte[] data = new byte[_length];
        _random.nextBytes(data);
        long timestamp = System.currentTimeMillis();
        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (timestamp & 0xFF);
            timestamp >>= 8;
        }
        return new String(Hex.encodeHex(data));
    }

}
