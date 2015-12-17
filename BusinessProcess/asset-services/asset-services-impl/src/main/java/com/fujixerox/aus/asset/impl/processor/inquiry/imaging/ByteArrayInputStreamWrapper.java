package com.fujixerox.aus.asset.impl.processor.inquiry.imaging;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
class ByteArrayInputStreamWrapper extends FilterInputStream {

    public ByteArrayInputStreamWrapper(ByteArrayInputStream wrapped) {
        super(wrapped);
    }

    public ByteArrayInputStreamWrapper(byte[] buffer) {
        this(new ByteArrayInputStream(buffer));
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (markSupported()) {
            reset();
        }
    }

}
