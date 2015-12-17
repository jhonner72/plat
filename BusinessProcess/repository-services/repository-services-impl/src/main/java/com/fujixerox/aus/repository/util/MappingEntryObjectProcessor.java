package com.fujixerox.aus.repository.util;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.*;

/**
 * 
 * @author Henry.Niu
 * 23/07/2015
 * This class is used to populate VoucherInformation object from documentum table by using the mapping file
 */
public class MappingEntryObjectProcessor {
	
	private static Map<String, MethodHolder> methodMap = new HashMap<String, MethodHolder>();
	
	public void process(Object sourceObject, Object targetObject, MappingEntry entry) throws Exception {		
		
		Method sourceMethod = null;
		Object sourceValue = null;
		String sourceMethodName = manipulateSourceMethodName(entry.getKeyType());
		try {
			sourceMethod = sourceObject.getClass().getMethod(sourceMethodName, String.class);
			sourceValue = sourceMethod.invoke(sourceObject, entry.getKey());
		} catch (Exception e) {
	        LogUtil.log("Error in invoking source method " + sourceMethodName + " for key: " + entry.getKey() + ". Message: " + e.getMessage(), LogUtil.ERROR, e);
	        throw e;
	    }  
		
		if (sourceValue == null || ("".equals(sourceValue) && skipEnumEntry(entry.getValueType()))) {
			return;
		}
				
		StringTokenizer st = new StringTokenizer(entry.getValue(), ".");
		Object subObject = manipulateTargetObject(st.nextToken(), targetObject);
		
		String targetMethodName = "set" + capInitial(st.nextToken());
		Method targetMethod = null;
		try {
			targetMethod = subObject.getClass().getMethod(targetMethodName, getObjectClass(entry.getValueType()));
			targetMethod.invoke(subObject, manipulateSourceValue(entry.getKeyType(), entry.getValueType(), sourceValue));
		} catch (Exception e) {
	        LogUtil.log("Error in invoking target method " + targetMethodName + " for key: " + entry.getKey() + ". Message: " + e.getMessage(), LogUtil.ERROR, e);
	        throw e;
	    }
	}		
	
	/*private MethodHolder lookupMethod(String sourceMethodName, Object sourceObject, Object targetObject, MappingEntry entry) 
			throws NoSuchMethodException, SecurityException {
		
		MethodHolder methodHolder = methodMap.get(sourceMethodName);
		
		if (methodHolder == null) {
			Method sourceMethod = sourceObject.getClass().getMethod(
					manipulateSourceMethodName(sourceMethodName, entry.getValueType()), null);
			Method targetMethod = targetObject.getClass().getMethod(manipulateTargetMethodName(entry.getKeyType()), String.class, 
					getObjectClass(entry.getKeyType()));
			
			methodHolder = new MethodHolder(sourceMethodName, sourceMethod, targetMethod);
			methodMap.put(sourceMethodName, methodHolder);
		}
		
		return methodHolder;
	}*/

	private boolean skipEnumEntry(String valueType) {

		if (valueType != null && valueType.endsWith("Enum")) {
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
			case "ReleaseStatusEnum":
				return com.fujixerox.aus.lombard.common.voucher.ReleaseStatusEnum.class;
			case "APPresentmentTypeEnum":
				return com.fujixerox.aus.lombard.common.voucher.APPresentmentTypeEnum.class;
            case "InsertedCreditTypeEnum":
                return com.fujixerox.aus.lombard.common.voucher.InsertedCreditTypeEnum.class;
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
			case "ReleaseStatusEnum":
				return ReleaseStatusEnum.fromValue(sourceValue.toString());
			case "APPresentmentTypeEnum":
				return APPresentmentTypeEnum.fromValue(sourceValue.toString());
            case "InsertedCreditTypeEnum":
                return InsertedCreditTypeEnum.fromValue(sourceValue.toString());
			case "int":
				if (keyType.equals("boolean") && (boolean)sourceValue) {
					return 1;
				} else if (keyType.equals("string") && sourceValue != null) {
					return new Integer(sourceValue.toString()).intValue();
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
			case "string":
				if (keyType.equals("boolean")) {
					if ((boolean)sourceValue) {
						return "1";
					} else {
						return "0";
					}
				} else {
					return sourceValue.toString();
				}
			default:
				return sourceValue;
		}
	}
	
	private String capInitial(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
