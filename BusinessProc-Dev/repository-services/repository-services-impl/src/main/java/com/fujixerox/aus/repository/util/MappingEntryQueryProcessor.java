package com.fujixerox.aus.repository.util;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.ForValueTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;

/**
 * 
 * @author Henry.Niu
 * 06/08/2015
 * This class is used to generate documentum query from VoucherInformation object by using the mapping file
 */
public class MappingEntryQueryProcessor {

	public String process(VoucherInformation voucherInformation, MappingEntry entry) throws Exception {
		Object thisSourceObject = voucherInformation;
		
		StringTokenizer st = new StringTokenizer(entry.getValue(), ".");
		int count = st.countTokens();
		int i = 1;
		
		while (st.hasMoreTokens()) {
			boolean isLastToken = false;
			if (i == count) {
				isLastToken = true;
			}
			String sourceMethodName = st.nextToken();
			Method sourceMethod = thisSourceObject.getClass().getMethod(
					manipulateSourceMethodName(sourceMethodName, entry.getValueType(), isLastToken), null);
			thisSourceObject = sourceMethod.invoke(thisSourceObject, null);
			if (thisSourceObject == null) {
				return "";
			}
			if (entry.getKeyType().equals("boolean")) {
				if (entry.getValueType().equals("boolean") && thisSourceObject.toString().equals("false")) {
					return "";
				}
				if (entry.getValueType().equals("int") && thisSourceObject.toString().equals("0")) {
					return "";
				}
			}
			if (entry.getKeyType().equals("string")) {
				if (entry.getValueType().equals("int") && thisSourceObject.toString().equals("0")) {
					return "";
				}
			}
			i++;
		}
		
		return entry.getKey() + " = " + manipulateSourceObject(entry.getKeyType(), entry.getValueType(), thisSourceObject) + " \nAND ";
	}

	private String manipulateSourceMethodName(String name, String type, boolean lastToken) {		
		if (!lastToken) {
			return "get" + capInitial(name);
		}
		
		switch (type) {
			case "boolean":
			case "Boolean":	
				return "is" + capInitial(name);
			default:
				return "get" + capInitial(name);
		}
	}
		
	private String manipulateSourceObject(String keyType, String valueType, Object sourceObject) {
		switch (keyType) {
			case "time":
				String procegssingDateString = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(sourceObject);
				return "date('" + procegssingDateString + "', '" +  Constant.DOCUMENTUM_DATE_FORMAT + "')";
			case "boolean":
				if (valueType.equals("int")) {
					if (sourceObject.toString().equals("1")) {
						return "true";
					} else {
						return "false";
					}
				}
				return sourceObject.toString();
			case "DocumentTypeEnum":
				return "'" + ((DocumentTypeEnum)sourceObject).value() + "'";
			case "WorkTypeEnum":
				return "'" + ((WorkTypeEnum)sourceObject).value() + "'";
			case "StateEnum":
				return "'" + ((StateEnum)sourceObject).value() + "'";
			case "ForValueTypeEnum":
				return "'" + ((ForValueTypeEnum)sourceObject).value() + "'";
			case "int":
				return sourceObject.toString();
			default:
				return "'" + sourceObject.toString() + "'";
		}
	}
	
	private String capInitial(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

}
