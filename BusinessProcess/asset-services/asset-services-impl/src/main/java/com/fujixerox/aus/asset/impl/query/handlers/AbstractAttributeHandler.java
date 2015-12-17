package com.fujixerox.aus.asset.impl.query.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfValue;
import com.fujixerox.aus.asset.api.query.IQueryRestriction;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractAttributeHandler implements IAttributeHandler {

    static final String EMPTY_CONDITION = "1<>1";

    static final char QUESTION_MARK = '?';

    static final char ASTERISK = '*';

    static final char BACKSLASH = '\\';

    static final char PERCENT_SIGN = '%';

    static final char UNDERSCORE = '_';

    private static final char APOSTROPHE = '\'';

    @Override
    public String getRestriction(IDfSession session, String qualification,
            IQueryRestriction restriction) throws DfException {
        return getRestriction(session, qualification,
                restriction.isMatchCase(), restriction.getLowValue(),
                restriction.getHighValue());
    }

    @Override
    public final String getRestriction(IDfSession session,
            String qualification, String low, String high) throws DfException {
        return getRestriction(session, qualification, true, low, high);
    }

    @Override
    public final String getRestriction(IDfSession session,
            String qualification, boolean matchCase, String low, String high)
        throws DfException {
        Logger
                .debug(
                        "Building restriction for low=''{0}'', high=''{1}'' and match case=''{2}''",
                        low, high, matchCase);
        if (StringUtils.isBlank(high) && StringUtils.isBlank(low)) {
            Logger
                    .debug(
                            "Both low ''{0}'' and high ''{1}'' are blank, returning negative restriction",
                            low, high);
            return EMPTY_CONDITION;
        }
        String restriction = doGetRestriction(session, qualification,
                matchCase, low, high);
        Logger.debug("Result is: ''{0}''", restriction);
        return restriction;
    }

    @Override
    public String getStringValue(IDfValue value) {
        return value.asString();
    }

    @Override
    public String getDefaultValue() {
        return null;
    }

    protected abstract String doGetRestriction(IDfSession session,
            String qualification, boolean matchCase, String lowValue,
            String highValue) throws DfException;

    private String getQualification(String qualification, boolean matchCase) {
        return getValue(qualification, matchCase);
    }

    private String getValue(String value, boolean matchCase) {
        if (matchCase) {
            return value;
        }
        return "LOWER(" + value + ")";
    }

    private String getSimple(String qualification, String value,
            boolean matchCase, String operator) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getQualification(qualification, matchCase));
        stringBuilder.append(" ").append(operator).append(" ");
        stringBuilder.append(getValue(value, matchCase));
        return stringBuilder.toString();
    }

    protected String greaterThan(String qualification, String value) {
        return greaterThan(qualification, value, true);
    }

    private String greaterThan(String qualification, String value,
            boolean matchCase) {
        return getSimple(qualification, value, matchCase, ">");
    }

    String greaterThanOrEqual(String qualification, String value) {
        return greaterThanOrEqual(qualification, value, true);
    }

    String greaterThanOrEqual(String qualification, String value,
            boolean matchCase) {
        return getSimple(qualification, value, matchCase, ">=");
    }

    protected String lessThan(String qualification, String value) {
        return lessThan(qualification, value, true);
    }

    private String lessThan(String qualification, String value,
            boolean matchCase) {
        return getSimple(qualification, value, matchCase, "<");
    }

    String lessThanOrEqual(String qualification, String value) {
        return lessThanOrEqual(qualification, value, true);
    }

    String lessThanOrEqual(String qualification, String value, boolean matchCase) {
        return getSimple(qualification, value, matchCase, "<=");
    }

    String equal(String qualification, String value) {
        return equal(qualification, value, true);
    }

    String equal(String qualification, String value, boolean matchCase) {
        return getSimple(qualification, value, matchCase, "=");
    }

    protected String notEqual(String qualification, String value,
            boolean matchCase) {
        return getSimple(qualification, value, matchCase, "<>");
    }

    protected String like(String qualification, String value) {
        return like(qualification, value, true);
    }

    String like(String qualification, String value, boolean matchCase) {
        return getQualification(qualification, matchCase) + " LIKE "
                + getValue(value, matchCase) + " ESCAPE '" + BACKSLASH + "'";
    }

    protected String notLike(String qualification, String value) {
        return notLike(qualification, value, true);
    }

    private String notLike(String qualification, String value, boolean matchCase) {
        return getQualification(qualification, matchCase) + " NOT LIKE "
                + getValue(value, matchCase) + " ESCAPE '" + BACKSLASH + "'";
    }

    protected String between(String qualification, String low, String high) {
        return between(qualification, low, high, true);
    }

    private String between(String qualification, String low, String high,
            boolean matchCase) {
        return greaterThan(qualification, low, matchCase) + " AND "
                + lessThan(qualification, high, matchCase);
    }

    String betweenOrEqual(String qualification, String low, String high) {
        return betweenOrEqual(qualification, low, high, true);
    }

    String betweenOrEqual(String qualification, String low, String high,
            boolean matchCase) {
        return greaterThanOrEqual(qualification, low, matchCase) + " AND "
                + lessThanOrEqual(qualification, high, matchCase);
    }

    @SuppressWarnings("PMD.ShortMethodName")
    String in(String qualification, boolean matchCase, String... values) {
        return in(qualification, matchCase, Arrays.asList(values));
    }

    @SuppressWarnings("PMD.ShortMethodName")
    String in(String qualification, boolean matchCase, List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        boolean hasEmptyStrings = false;
        List<String> list = new ArrayList<String>();
        for (String value : values) {
            if (StringUtils.isEmpty(value)) {
                hasEmptyStrings = true;
                continue;
            }
            list.add(value);
        }

        if (list.isEmpty()) {
            if (hasEmptyStrings) {
                return getQualification(qualification, matchCase)
                        + " IS NULL OR "
                        + getQualification(qualification, matchCase)
                        + " IS NULLSTRING";
            }
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getQualification(qualification, matchCase));
        stringBuilder.append(" IN (");
        for (int i = 0, n = list.size(); i < n; i++) {
            stringBuilder.append(getValue(toQuotedString(list.get(i)),
                    matchCase));
            if (i > 0 && i % 250 == 0) {
                stringBuilder.append(") OR ").append(
                        getQualification(qualification, matchCase)).append(
                        " IN (");
            } else if (i < n - 1) {
                stringBuilder.append(',');
            }
        }
        stringBuilder.append(")");
        if (hasEmptyStrings) {
            stringBuilder.append(" OR ").append(qualification).append(
                    " IS NULL OR ");
            stringBuilder.append(getQualification(qualification, matchCase))
                    .append(" IS NULLSTRING");
        }
        return stringBuilder.toString();
    }

    String toQuotedString(String value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(APOSTROPHE);
        stringBuilder.append(escapeQuotes(value));
        stringBuilder.append(APOSTROPHE);
        return stringBuilder.toString();
    }

    private String escapeQuotes(String value) {
        StringBuilder stringBuilder = new StringBuilder(value.length());
        char[] chars = value.toCharArray();
        for (char current : chars) {
            if (current != APOSTROPHE) {
                stringBuilder.append(current);
                continue;
            }
            stringBuilder.append(APOSTROPHE);
            stringBuilder.append(current);
        }
        return stringBuilder.toString();
    }

    boolean isEqualBoundaries(String low, String high) {
        return (low == null && high == null)
                || ((high != null) && high.equals(low));
    }

    @Override
    public void dispose() {
        // do nothing
    }

}
