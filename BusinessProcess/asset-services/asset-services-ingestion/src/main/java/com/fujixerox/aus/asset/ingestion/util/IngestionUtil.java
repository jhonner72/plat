package com.fujixerox.aus.asset.ingestion.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fujixerox.aus.asset.ingestion.Ingester;
import com.fujixerox.aus.asset.ingestion.TeeStream;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class IngestionUtil {

    private IngestionUtil() {
        super();
    }

    public static void redirectOut() throws IOException {
        String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File logFile = new File(Ingester.class.getSimpleName() + "-"
                + dateString + ".log");
        PrintStream logOut = new PrintStream(
                new FileOutputStream(logFile, true));
        PrintStream teeStdOut = new TeeStream(System.out, logOut);
        PrintStream teeStdErr = new TeeStream(System.err, logOut);
        System.setOut(teeStdOut);
        System.setErr(teeStdErr);
    }

    public static String asString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public static void log(String message) {
        log(message, System.out);
    }

    public static void err(String message) {
        log(message, System.err);
    }

    public static void log(String message, PrintStream ps) {
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date());
        ps.println(dateString + " [" + Thread.currentThread().getName() + "]: "
                + message);
    }

    public static String trimOrEmpty(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

}
