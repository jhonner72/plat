package com.fujixerox.aus.asset.impl.query.handlers;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class StringAttributeHandler extends AbstractAttributeHandler {

    private final boolean _wildCard;

    private final boolean _range;

    public StringAttributeHandler() {
        this(false, false);
    }

    public StringAttributeHandler(boolean wildCard, boolean range) {
        super();
        _wildCard = wildCard;
        _range = range;
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
        boolean containsWildcards = isContainWildcards(lowValue);
        if (containsWildcards) {
            if (isWildCard()) {
                Logger.debug("Request contains wildcards");
                containsWildcards = true;
            } else {
                Logger.debug("Request contains wildcards but wildcards are not supported");
                containsWildcards = false;
            }
        }

        if (isEqualBoundaries(lowValue, highValue) && !containsWildcards) {
            Logger.debug("Low ''{0}'' and high ''{1}'' are equal", lowValue, highValue);
            return equal(qualification, toQuotedString(highValue), matchCase);
        }
        if (containsWildcards) {
            return like(qualification, toQuotedString(replaceWildcards(lowValue)), matchCase);
        }
        if (!isRange()) {
            Logger.debug("Range is not supported");
            throw new IllegalArgumentException("Unable to build restriction for [" + lowValue + ";" + highValue + "]");
        }
        if (StringUtils.isEmpty(lowValue)) {
            Logger.debug("Low ''{0}'' is blank", lowValue);
            return lessThanOrEqual(qualification, toQuotedString(highValue), matchCase);
        }
        if (StringUtils.isEmpty(highValue)) {
            Logger.debug("High ''{0}'' is blank", highValue);
            return greaterThanOrEqual(qualification, toQuotedString(lowValue), matchCase);
        }
        return betweenOrEqual(qualification, toQuotedString(lowValue), toQuotedString(highValue), matchCase);
    }

    private boolean isContainWildcards(String value) {
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            if (current != QUESTION_MARK && current != ASTERISK) {
                continue;
            }
            // question mark or asterisk not preceded by backslashes
            // or preceded by even count of backslashes
            if (getBackwardEscapeParity(chars, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean getBackwardEscapeParity(char[] array, int position) {
        boolean parity = true;
        for (int j = position - 1; j >= 0 && array[j] == BACKSLASH; j--) {
            parity = !parity;
        }
        return parity;
    }

    private boolean getForwardEscapeParity(char[] array, int position) {
        boolean parity = true;
        for (int j = position + 1; j < array.length && array[j] == BACKSLASH; j++) {
            parity = !parity;
        }
        return parity;
    }

    private String replaceWildcards(String value) {
        StringBuilder stringBuilder = new StringBuilder(value.length());
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            if (current == BACKSLASH) {
                // possible escaping backslash
                // check whether it escapes anything
                if (getForwardEscapeParity(chars, i) && i != chars.length - 1) {
                    char next = chars[i + 1];
                    // backslash escapes wildcard - skipping
                    if (next == ASTERISK || next == QUESTION_MARK) {
                        continue;
                    }
                }
                // normal backslash - putting
                // we also put ESCAPE '\' into query,
                // so we need to escape this backslash
                stringBuilder.append(BACKSLASH);
                stringBuilder.append(current);
                continue;
            }
            // possible wildcard
            if (current == ASTERISK || current == QUESTION_MARK) {
                // wildcard is escaped
                if (!getBackwardEscapeParity(chars, i)) {
                    stringBuilder.append(current);
                    continue;
                }
                // wildcard is not escaped
                if (current == ASTERISK) {
                    current = PERCENT_SIGN;
                } else {
                    current = UNDERSCORE;
                }
                stringBuilder.append(current);
                continue;
            }
            // escape DQL wildcards
            if (current == UNDERSCORE || current == PERCENT_SIGN) {
                stringBuilder.append(BACKSLASH);
            }
            stringBuilder.append(current);
        }
        return stringBuilder.toString();
    }

    final boolean isRange() {
        return _range;
    }

    private boolean isWildCard() {
        return _wildCard;
    }

}
