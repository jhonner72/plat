package com.fujixerox.aus.repository.util;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.ForValueTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.ReleaseStatusEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;

public class MappingEntry {
	
	private String key;
	private String value;
	private String keyType;
	private String valueType;
	
	/*private static ExpressionParser parser = new SpelExpressionParser();
	private static Map<String,Expression> spelExpressions = new HashMap<String,Expression>();
	private static Map<String,Map<String,Method>> classMethodMapping = new HashMap<String,Map<String,Method>>();*/
	
	public MappingEntry(String key, String value, String keyType, String valueType) {
		super();
		this.key = key;
		this.value = value;
		this.keyType = keyType;
		this.valueType = valueType;
		
		if (keyType != null && valueType == null) {
			this.valueType = keyType;
		}		
		if (keyType == null) {
			this.keyType = "string";
		}
		if (this.valueType == null) {
			this.valueType = "string";
		}
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
		
	public String getKeyType() {
		return keyType;
	}
	
	public String getValueType() {
		return valueType;
	}	
	
	/*public void setFxaValue(VoucherInformation voucherInfo, IDfSysObject fxaVoucher) throws Exception {
		Expression exp = lookupExpression(value);
		Object obj = null;
		try {
			obj = exp.getValue(voucherInfo);
		} catch (Exception e) {
			return;
		}		
		if (obj == null) {
			return;
		}
		
		String fxaSetMethodName = manipulateFxaSetMethodName(keyType);
		Method fxaSetMethod = lookupMethod(fxaVoucher.getClass(), fxaSetMethodName, 
				new Class[]{String.class, getFxaFieldClass(keyType)});
		fxaSetMethod.invoke(fxaVoucher, new Object[]{key, getVoucherInfoFieldValue(obj, valueType, keyType)});
	}
	
	public void setFxaValue(Criteria criteria, IDfSysObject fxaVoucher) throws Exception {	
		if (criteria.getValue() == null) {
			return;
		}
		
		Method fxaSetMethod = fxaVoucher.getClass().getMethod(manipulateFxaSetMethodName(keyType), 
				String.class, getFxaFieldClass(keyType));
		fxaSetMethod.invoke(fxaVoucher, key, getVoucherInfoFieldValue(criteria.getValue(), valueType, keyType));
	}
	
	public void setVoucherInfoValue(IDfSysObject fxaVoucher, VoucherInformation voucherInfo) throws Exception {		
		
		Method fxaGetMethod = fxaVoucher.getClass().getMethod(manipulateFxaGetMethodName(keyType), String.class);
		Object fxaFieldValue = fxaGetMethod.invoke(fxaVoucher, key);
		if (fxaFieldValue == null || (fxaFieldValue.equals("") && skipEnumEntry(valueType))) {
			return;
		}
				
		StringTokenizer st = new StringTokenizer(value, ".");
		Object subObject = manipulateVoucherInfoObject(st.nextToken(), voucherInfo);
		
		String voucherInfoFieldSetMethodName = "set" + capInitial(st.nextToken());
		Method voucherInfoFieldSetMethod = subObject.getClass().getMethod(voucherInfoFieldSetMethodName, getVoucherInfoFieldClass(valueType));
		voucherInfoFieldSetMethod.invoke(subObject, manipulateFxaFieldValue(keyType, valueType, fxaFieldValue));
	}
	
	public String buildFxaQuery(Criteria criteria) throws Exception {	
		if (criteria.getValue() == null) {
			return null;
		}		
		return key + " = " + manipulateQueryValue(keyType, valueType, criteria.getValue());
	}
	
	private Expression lookupExpression(String spelExpression) {
		Expression exp = spelExpressions.get(spelExpression);
		if (exp == null) {
			exp = parser.parseExpression(spelExpression);
			spelExpressions.put(spelExpression, exp);
		}
		return exp;
	}
	
	private Method lookupMethod(String clazzName, String methodName, Class[] methodParams) 
			throws ClassNotFoundException, NoSuchMethodException, SecurityException	{
		Map<String,Method> methodMappings = classMethodMapping.get(clazzName);
		if (methodMappings == null) {
			methodMappings = new HashMap<String,Method>();
			classMethodMapping.put(clazzName, methodMappings);
		}
		Method method = methodMappings.get(methodName);
		if (method == null)	{
			Class clazz = this.getClass().getClassLoader().loadClass(clazzName);
			//Assume we always lookup getters
			method = clazz.getMethod(methodName, methodParams);
			methodMappings.put(methodName, method);
		}
		return method;
	}
	
	private Method lookupMethod(Class clazz, String methodName, Class[] methodParams) 
			throws ClassNotFoundException, NoSuchMethodException, SecurityException	{
		String clazzName = clazz.getName();
		Map<String,Method> methodMappings = classMethodMapping.get(clazzName);
		if (methodMappings == null) {
			methodMappings = new HashMap<String,Method>();
			classMethodMapping.put(clazzName, methodMappings);
		}
		Method method = methodMappings.get(methodName);
		if (method == null)	{
			//Assume we always lookup getters
			method = clazz.getMethod(methodName, methodParams);
			methodMappings.put(methodName, method);
		}
		return method;
	}
	
	private String manipulateFxaSetMethodName(String keyType) {		
		switch (keyType) {
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
	
	private Class getFxaFieldClass(String type) {
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
	
	private Object getVoucherInfoFieldValue(Object object, String fromType, String toType) throws Exception {
		Object inputObject = object;
		if (object instanceof String) {
			inputObject = manipulateFxaFieldValue((String)object, fromType);
		}
		
		if (fromType.equalsIgnoreCase(toType)) {
			return inputObject;
		}
		
		if (inputObject instanceof Enum) {			
			Method method = inputObject.getClass().getMethod("value", null);
			return method.invoke(inputObject, null);
		}
		
		switch (toType) {
			case "boolean":
				if (object.toString().equals("1")) {
					return true;
				} else {
					return false;
				}				
			case "time":
				IDfTime processTime = new DfTime((Date)inputObject);
				String dateFormat = processTime.getDay() + "/" + processTime.getMonth() + "/" + processTime.getYear() + " 12:00:00";
	            return new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14);
			default: 
				return object.toString();
		}
	}
	
	private Object manipulateFxaFieldValue(String object, String fromType) throws ParseException {
		switch (fromType) {
			case "boolean":
				return new Boolean(object);
			case "int":
				return new Integer(object);
			case "DocumentTypeEnum":
				return DocumentTypeEnum.fromValue(object);
			case "StateEnum":
				return StateEnum.fromValue(object);
			case "WorkTypeEnum":
				return WorkTypeEnum.fromValue(object);
			case "ForValueTypeEnum":
				return ForValueTypeEnum.fromValue(object);
			case "ReleaseStatusEnum":
				return ReleaseStatusEnum.fromValue(object);
			case "date":
				return new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).parse(object);
			default:
				return object;
		}
	}
	
	private String manipulateFxaGetMethodName(String type) {
		return "get" + capInitial(type);
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
	
	private Object manipulateVoucherInfoObject(String subObjectName, Object targetObject) throws Exception {
		String subObjectMethodName = "get" + capInitial(subObjectName);
		Method method = targetObject.getClass().getMethod(subObjectMethodName, null);
		return method.invoke(targetObject, null);
	}
	
	private Class getVoucherInfoFieldClass(String type) throws ClassNotFoundException {
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
			default:
				return String.class; 
		}
	}
	
	private Object manipulateFxaFieldValue(String keyType, String valueType, Object sourceValue) {
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
			default:
				return sourceValue;
		}
	}
	
	private String manipulateQueryValue(String keyType, String valueType, String sourceValue) {		
		switch (keyType) {
			case "time":
				return "date('" + sourceValue + "', '" +  Constant.DOCUMENTUM_DATE_FORMAT + "')";
			case "boolean":
				if (valueType.equals("int")) {
					if (sourceValue.equals("1")) {
						return "true";
					} else {
						return "false";
					}
				}
				return sourceValue;
			case "int":
				return sourceValue;
			default:
				return "'" + sourceValue + "'";
		}
	}
	
	private String capInitial(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}*/

}
