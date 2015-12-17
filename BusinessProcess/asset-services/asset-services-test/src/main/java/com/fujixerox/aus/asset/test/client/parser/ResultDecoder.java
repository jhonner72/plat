package com.fujixerox.aus.asset.test.client.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fujixerox.aus.asset.test.client.exceptions.ImageMarkException;

class ResultDecoder {

    static final int NOT_FOUND = -1;

    static final int MAX_LOGGABLE_LENGTH = 3000;

    private static final char SINGLE_QUOTE = '\'';

    private static final char DOUBLE_QUOTE = '"';

    private static final int BUFFER_SIZE = 1024;

    private String _result;

    private int _pos;

    ResultDecoder(InputStream stream) throws ImageMarkException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int len = stream.read(buffer);
                if (!(len > 0)) {
                    break;
                }
                outputStream.write(buffer, 0, len);
            }
            _result = new String(outputStream.toByteArray());
            _pos = 0;
        } catch (IOException e) {
            throw new ImageMarkException("Error reading input stream", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    int findIdentifier(String elementName, boolean anchorPosition) {
        int len = _result.length();
        int elementLen = elementName.length();
        int thisPos = _pos;
        while (thisPos <= len) {
            thisPos = _result.indexOf(elementName, thisPos);
            if (thisPos == NOT_FOUND) {
                break;
            }
            if (identifierIsAtWordBoundary(len, elementLen, thisPos)) {
                thisPos += elementLen;
                if (anchorPosition) {
                    _pos = thisPos;
                }
                return thisPos;
            }
            thisPos++;
        }
        return NOT_FOUND;
    }

    private boolean identifierIsAtWordBoundary(int len, int elementLen,
            int thisPos) {
        return (thisPos > 0)
                && (thisPos + elementLen < len)
                && (!Character
                        .isJavaIdentifierPart(_result.charAt(thisPos - 1)))
                && (!Character.isJavaIdentifierPart(_result.charAt(thisPos
                        + elementLen)));
    }

    String getAttributeValue(String attributeName) {
        int thisPos = findIdentifier(attributeName, false);
        String value = null;
        if (thisPos > -1) {
            int startPos = findStartPosition(thisPos);

            int endPos = findEndPosition(startPos);

            int len = endPos - startPos;
            if (len > 0) {
                value = _result.substring(startPos, endPos);
            }
        }
        return value;
    }

    int getIntValue(String intStr) {
        int intValue = NOT_FOUND;
        try {
            intValue = Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            intValue = NOT_FOUND;
        }
        return intValue;
    }

    private int findStartPosition(int startingPos) {
        int len = _result.length();
        int thisPos = startingPos;
        while (thisPos < len) {
            char charAt = _result.charAt(thisPos++);
            if (charAt == SINGLE_QUOTE || charAt == DOUBLE_QUOTE) {
                break;
            }
        }
        return thisPos;
    }

    private int findEndPosition(int startPos) {
        int thisPos = startPos;
        int len = _result.length();
        while (thisPos < len) {
            char charAt = _result.charAt(thisPos);
            if (charAt == SINGLE_QUOTE || charAt == DOUBLE_QUOTE) {
                break;
            }
            thisPos++;
        }
        return thisPos;
    }

    boolean stringHasValue(String str) {
        return StringUtils.isNotBlank(str);
    }

    String getResult() {
        return _result;
    }

    protected void setResult(String result) {
        _result = result;
    }

    protected int getPos() {
        return _pos;
    }

    protected void setPos(int pos) {
        _pos = pos;
    }

}
