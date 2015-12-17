package com.fujixerox.aus.asset.impl.query.handlers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class DateAttributeHandler extends AbstractAttributeHandler {

    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";

    private final String _dateFormat;

    public DateAttributeHandler() {
        this(DEFAULT_DATE_FORMAT);
    }

    public DateAttributeHandler(String dateFormat) {
        super();
        if (StringUtils.isNotBlank(dateFormat)) {
            Logger.debug("Using {0} as date format", dateFormat);
            _dateFormat = dateFormat;
        } else {
            Logger.debug("Using default date format {0}", DEFAULT_DATE_FORMAT);
            _dateFormat = DEFAULT_DATE_FORMAT;
        }
    }

    @Override
    public String getStringValue(IDfValue value) {
        return convertToDate(value.asTime());
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
        if (StringUtils.isBlank(highValue)) {
            Logger.debug("High ''{0}'' is blank", highValue);
            return greaterThanOrEqual(qualification, getDateLow(lowValue), true);
        }
        if (StringUtils.isBlank(lowValue)) {
            Logger.debug("Low ''{0}'' is blank", lowValue);
            return lessThanOrEqual(qualification, getDateHigh(highValue), true);
        }
        return betweenOrEqual(qualification, getDateLow(lowValue),
                getDateHigh(highValue));
    }

    private String getDateLow(String date) {
        return "DATE('"
                + new SimpleDateFormat("yyyy/MM/dd").format(extractDate(date))
                + " 00:00:00', 'yyyy/mm/dd hh:mi:ss')";
    }

    private String getDateHigh(String date) {
        return "DATE('"
                + new SimpleDateFormat("yyyy/MM/dd").format(extractDate(date))
                + " 23:59:59', 'yyyy/mm/dd hh:mi:ss')";
    }

    private Date extractDate(String date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(_dateFormat);
            Date result = dateFormat.parse(date);
            Logger.debug("String ''{0}'' parsed to date ''{1}''", date, result);
            return result;
        } catch (ParseException ex) {
            Logger.error("Unable to parse date ''{0}''", ex, date);
            throw new IllegalArgumentException(ex);
        }
    }

    private String convertToDate(IDfTime time) {
        if (time == null || !time.isValid()) {
            Logger
                    .debug("Time is either null or invalid, returning empty string");
            return "";
        }
        Date date = time.getDate();
        if (date == null) {
            Logger.debug("Date is null, returning empty string");
            return "";
        }
        String result = new SimpleDateFormat(_dateFormat).format(date);
        Logger.debug("Date ''{0}'' converted to string ''{1}''", date, result);
        return result;
    }

}
