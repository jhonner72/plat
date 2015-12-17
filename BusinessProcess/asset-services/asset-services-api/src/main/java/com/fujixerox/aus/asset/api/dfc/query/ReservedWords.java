package com.fujixerox.aus.asset.api.dfc.query;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class ReservedWords {

    private static final Set<String> RESERVED = new HashSet<String>();

    static {
        fillKeywords();
    }

    private ReservedWords() {
        super();
    }

    public static boolean isReserved(String attrName) {
        if (StringUtils.isBlank(attrName)) {
            throw new IllegalArgumentException("attrname is blank");
        }
        return RESERVED.contains(attrName.toUpperCase());
    }

    public static String getProjection(Set<String> attrs) {
        if (attrs == null || attrs.isEmpty()) {
            throw new IllegalArgumentException("empty attributes");
        }
        StringBuilder result = new StringBuilder(attrs.size() * 15);
        for (Iterator<String> iter = attrs.iterator(); iter.hasNext();) {
            String attrName = iter.next();
            if (isReserved(attrName)) {
                result.append('\"').append(attrName).append('\"');
            } else {
                result.append(attrName);
            }
            if (iter.hasNext()) {
                result.append(',');
            }
        }
        return result.toString();
    }

    private static void fillKeywords() {
        InputStream inputStream = null;
        try {
            inputStream = ReservedWords.class
                    .getResourceAsStream("ReservedWords.txt");
            if (inputStream == null) {
                return;
            }
            for (String line : IOUtils.readLines(inputStream)) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                for (String word : line.split("\\s+")) {
                    RESERVED.add(StringUtils.trim(word));
                }
            }
        } catch (IOException ex) {
            Logger.error(ex.getMessage(), ex);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

}
