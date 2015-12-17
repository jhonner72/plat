package com.fujixerox.aus.asset.ingestion;

import java.io.PrintStream;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class TeeStream extends PrintStream {

    private PrintStream _out;

    public TeeStream(PrintStream out1, PrintStream out2) {
        super(out1);
        _out = out2;
    }

    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        _out.write(buf, off, len);
    }

    public void flush() {
        super.flush();
        _out.flush();
    }

}
