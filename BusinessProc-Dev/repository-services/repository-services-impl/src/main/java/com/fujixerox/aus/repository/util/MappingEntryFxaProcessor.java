package com.fujixerox.aus.repository.util;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.StringTokenizer;

import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;

/**
 * 
 * @author Henry.Niu
 * 23/07/2015
 * This class is used to populate documentum object from VoucherInformation object by using the mapping file
 */
public class MappingEntryFxaProcessor {
	
	public void process(Object sourceObject, Object targetObject, MappingEntry entry) throws Exception {				
		Object thisSourceObject = sourceObject;
		
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
				return;
			}			
			if (thisSourceObject instanceof Boolean && !((Boolean) thisSourceObject).booleanValue() ) {
				return;
			}
			i++;
		}
		
		Method targetObjectMethod = targetObject.getClass().getMethod(manipulateTargetMethodName(entry.getKeyType()), String.class, 
				getObjectClass(entry.getKeyType()));
		targetObjectMethod.invoke(targetObject, entry.getKey(), 
				getObjectValue(thisSourceObject, entry.getValueType(), entry.getKeyType()));
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
	
	private String manipulateTargetMethodName(String type) {		
		switch (type) {
			case "boolean":
				return "setBoolean";
			case "int":
				return "setInt";
			case "time":
				return "setTime";
			default:
				return "setString";
		}
	}
	
	private Class getObjectClass(String type) {
		switch (type) {
			case "boolean":
				return boolean.class;
			case "int":
				return int.class;
			case "date":
				return Date.class;
			case "time":
				return IDfTime.class;
			default:
				return String.class;
		}
	}
	
	private Object getObjectValue(Object object, String fromType, String toType) throws Exception {
		if (fromType.equalsIgnoreCase(toType)) {		
			return object;
		}
		
		if (object instanceof Enum) {
			Method method = object.getClass().getMethod("value", null);
			return method.invoke(object, null);
		}
		
		switch (toType) {
			case "boolean":
				if (object.toString().equals("1")) {
					return true;
				} else {
					return false;
				}				
			case "time":				
				IDfTime processTime = new DfTime((Date)object);
	            String dateFormat = processTime.getDay() + "/" + processTime.getMonth() + "/" + processTime.getYear() + " 12:00:00";
	            return new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14);
			default: 
				return object.toString();
		}
		
		
	}
	
	private String capInitial(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
