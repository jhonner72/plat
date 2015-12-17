package com.fujixerox.aus.repository.util;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.StringTokenizer;

import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.ForValueTypeEnum;

/**
 * 
 * @author Henry.Niu
 * 23/07/2015
 * This class is used to populate VoucherInformation object from documentum table by using the mapping file
 */
public class MappingEntryObjectProcessor {
	
	public void process(Object sourceObject, Object targetObject, MappingEntry entry) throws Exception {		
		
		Method sourceMethod = sourceObject.getClass().getMethod(manipulateSourceMethodName(entry.getKeyType()), String.class);
		Object sourceValue = sourceMethod.invoke(sourceObject, entry.getKey());
		if (sourceValue == null || (sourceValue.equals("") && skipEnumEntry(entry.getValueType()))) {
			return;
		}
				
		StringTokenizer st = new StringTokenizer(entry.getValue(), ".");
		Object subObject = manipulateTargetObject(st.nextToken(), targetObject);
		
		String targetMethodName = "set" + capInitial(st.nextToken());
		Method targetMethod = subObject.getClass().getMethod(targetMethodName, getObjectClass(entry.getValueType()));
		targetMethod.invoke(subObject, manipulateSourceValue(entry.getKeyType(), entry.getValueType(), sourceValue));
	}		

	private boolean skipEnumEntry(String valueType) {
		if (valueType == null) {  
			return false;
		}
		if (valueType.endsWith("Enum")) {
			return true;
		}
		return false;
	}

	private String manipulateSourceMethodName(String type) {
		return "get" + capInitial(type);
	}
	
	private Object manipulateTargetObject(String subObjectName, Object targetObject) throws Exception {
		String subObjectMethodName = "get" + capInitial(subObjectName);
		Method method = targetObject.getClass().getMethod(subObjectMethodName, null);
		return method.invoke(targetObject, null);
	}
		
	private Class getObjectClass(String type) throws ClassNotFoundException {
		switch (type) {
			case "Boolean":
				return Boolean.class;
			case "boolean":
				return boolean.class;
			case "int":
				return int.class;
			case "date":
				return Date.class;
			case "time":
				return IDfTime.class;
			case "DocumentTypeEnum":
				return com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum.class;
			case "WorkTypeEnum":
				return com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum.class;
			case "StateEnum":
				return com.fujixerox.aus.lombard.common.voucher.StateEnum.class;
			case "ForValueTypeEnum":
				return com.fujixerox.aus.lombard.common.voucher.ForValueTypeEnum.class;
			default:
				return String.class; 
		}
	}
	
	private Object manipulateSourceValue(String keyType, String valueType, Object sourceValue) {
		if (keyType.equals(valueType)) {
			return sourceValue;
		}
		
		switch (valueType) {
			case "DocumentTypeEnum":
				return DocumentTypeEnum.fromValue(sourceValue.toString());
			case "WorkTypeEnum":
				return WorkTypeEnum.fromValue(sourceValue.toString());
			case "StateEnum":
				return StateEnum.fromValue(sourceValue.toString());
			case "ForValueTypeEnum":
				return ForValueTypeEnum.fromValue(sourceValue.toString());
			case "int":
				if (keyType.equals("boolean") && (boolean)sourceValue) {
					return 1;
				} else {
					return 0;
				}
			case "date":
				return ((IDfTime)sourceValue).getDate();
			case "Boolean":
				if (keyType.equals("boolean") && (boolean)sourceValue) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			default:
				return sourceValue;
		}
	}
	
	private String capInitial(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
