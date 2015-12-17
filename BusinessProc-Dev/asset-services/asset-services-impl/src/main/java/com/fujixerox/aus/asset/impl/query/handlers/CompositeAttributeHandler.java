package com.fujixerox.aus.asset.impl.query.handlers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.fujixerox.aus.asset.api.util.logger.Logger;
import com.fujixerox.aus.asset.impl.util.StringProcessor;

/**
 * @author Henry Niu
 */
public class CompositeAttributeHandler extends AbstractAttributeHandler {

    private final boolean _range;
    private String requestFields;
    private String dctmFxaVoucherFields;
    private String types;
    
    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";

    private final String _dateFormat;

    public CompositeAttributeHandler() {
        this(false, "date", "fxa_processing_date", "date", DEFAULT_DATE_FORMAT);
    }

    public CompositeAttributeHandler(boolean range, String requestFields, String dctmFxaVoucherFields, String types) {
    	this(false, requestFields, dctmFxaVoucherFields, types, DEFAULT_DATE_FORMAT);
    }
    
    public CompositeAttributeHandler(boolean range, String requestFields, String dctmFxaVoucherFields, String types, String dateFormat) {
        super();
        _range = range;
        if (StringUtils.isNotBlank(dateFormat)) {
            Logger.debug("Using {0} as date format", dateFormat);
            _dateFormat = dateFormat;
        } else {
            Logger.debug("Using default date format {0}", DEFAULT_DATE_FORMAT);
            _dateFormat = DEFAULT_DATE_FORMAT;
        }
        
        this.requestFields = requestFields;
        this.dctmFxaVoucherFields = dctmFxaVoucherFields;
        this.types = types;
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
    	
    	String result = "";
    	
    	 String[] requestFieldArray = StringProcessor.convert(requestFields, ",");
    	 String[] dctmFxaVoucherFieldArray = StringProcessor.convert(dctmFxaVoucherFields, ",");
    	 String[] typeArray = StringProcessor.convert(types, ",");
    	 
    	 for (int i = 0; i < requestFieldArray.length; i++) {
    		 String temp = doGetRestriction(session, requestFieldArray[i], dctmFxaVoucherFieldArray[i], typeArray[i], lowValue, highValue);
    		 if (result.equals("")) {
    			 result += temp;
    		 } else {
    			 result += " AND " + temp;
    		 }
    	 }
        
    	 return result;
    }
    
    private String doGetRestriction(IDfSession session, String requestField,
            String dctmFxaVoucherField, String type, String lowValue, String highValue) {
    	
    	String low = getValue(lowValue, requestField);
    	String high = getValue(highValue, requestField);
    	
    	if (type.equalsIgnoreCase("date")) {    	
	    	if (StringUtils.isBlank(high)) {
	            Logger.debug("High ''{0}'' is blank", highValue);
	            return greaterThanOrEqual(dctmFxaVoucherField, getDateLow(low), true);
	        }
	        if (StringUtils.isBlank(low)) {
	            Logger.debug("Low ''{0}'' is blank", lowValue);
	            return lessThanOrEqual(dctmFxaVoucherField, getDateHigh(high), true);
	        }
	        return betweenOrEqual(dctmFxaVoucherField, getDateLow(low), getDateHigh(high));    
    	}
    	
    	return equal(dctmFxaVoucherField, toQuotedString(low));        
    }
        
    private String getDateLow(String date) {
        return "DATE('" + new SimpleDateFormat("yyyy/MM/dd").format(extractDate(date))
                + " 00:00:00', 'yyyy/mm/dd hh:mi:ss')";
    }

    private String getDateHigh(String date) {
        return "DATE('" + new SimpleDateFormat("yyyy/MM/dd").format(extractDate(date))
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
    
    private String getValue(String input, String attributeName) {
    	StringTokenizer st = new StringTokenizer(input, " ");
    	while (st.hasMoreTokens()) {
    		String token = st.nextToken();
    		int index = token.indexOf("=");
    		String thisAttributeName = token.substring(0, index);
    		String value = token.substring(index + 2, token.length() - 1);
    		if (thisAttributeName.equalsIgnoreCase(attributeName)) {
    			return value;
    		}
    	}
    	return "";
    }

    final boolean isRange() {
        return _range;
    }

}
