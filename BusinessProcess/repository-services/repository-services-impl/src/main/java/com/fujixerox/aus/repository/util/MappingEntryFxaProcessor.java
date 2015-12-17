package com.fujixerox.aus.repository.util;

 import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

 import com.fujixerox.aus.lombard.common.voucher.*;
 import org.apache.log4j.Level;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

 import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;

/**
 *
 * @author Henry.Niu
 * 23/07/2015
 * This class is used to populate documentum object from VoucherInformation object by using the mapping file
 */
 public class MappingEntryFxaProcessor {
     private static Map<String,Map<String,Method>> classMethodMapping = new ConcurrentHashMap<String,Map<String,Method>>();
     private static Map<String,Expression> spelExpressions = new ConcurrentHashMap<String,Expression>();
     private static ExpressionParser parser = new SpelExpressionParser();
     
	private Expression lookupExpression(String spelExpression) {
		Expression exp = spelExpressions.get(spelExpression);
		if (exp == null) {
			exp = parser.parseExpression(spelExpression);
			spelExpressions.put(spelExpression, exp);
		}
		return exp;
	}
     
    public void process(Object sourceObject, Object targetObject, MappingEntry entry) throws Exception {
		Expression exp = lookupExpression(entry.getValue());
		
		Object obj = null;
		try {
			obj = exp.getValue(sourceObject);
		} catch (Exception e) {
			// LogUtil.log("Caught exception for: "+entry.getValue(),LogUtil.DEBUG,e);
			return;
		}

		if (obj == null) {
			return;
		}

		String targetMethodName = manipulateTargetMethodName(entry.getKeyType());
		Method targetObjectMethod = lookupMethod(targetObject.getClass(), targetMethodName, new Class[] { String.class,
				getObjectClass(entry.getKeyType()) });
        try {
            targetObjectMethod.invoke(targetObject,
                    new Object[] { entry.getKey(), getObjectValue(obj, entry.getValueType(), entry.getKeyType()) });
        } catch (Exception e) {
            LogUtil.log("Error in invoking target method for key: " + entry.getKey() + ". Message: " + e.getMessage(), LogUtil.ERROR, e);
            throw new Exception(e);
        }
     }
     
     private Method lookupMethod(Class clazz,String methodName, Class[] methodParams) throws ClassNotFoundException, NoSuchMethodException, SecurityException
     {
         String clazzName = clazz.getName();
         Map<String,Method> methodMappings = classMethodMapping.get(clazzName);
         if(methodMappings == null)
         {
             methodMappings = new HashMap<String,Method>();
             classMethodMapping.put(clazzName, methodMappings);
         }
         Method method = methodMappings.get(methodName);
         if(method == null)
         {
             //Assume we always lookup getters
             method = clazz.getMethod(methodName, methodParams);
             methodMappings.put(methodName, method);
         }
         return method;
     }
     
     public void process(Criteria criteria, Object targetObject, MappingEntry entry) throws Exception {    
         if (criteria.getValue() == null) {
             return;
         }
         
         Method targetObjectMethod = targetObject.getClass().getMethod(manipulateTargetMethodName(entry.getKeyType()), String.class, 
                 getObjectClass(entry.getKeyType()));
         targetObjectMethod.invoke(targetObject, entry.getKey(), 
                 getObjectValue(criteria.getValue(), entry.getValueType(), entry.getKeyType()));
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
         Object inputObject = object;
         if (object instanceof String) {
             inputObject = manipulateSourceValue((String)object, fromType);
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
             case "int":
                 return new Integer(object.toString());
             default: 
                 return object.toString();
         }
     }
     
     private Object manipulateSourceValue(String object, String fromType) throws ParseException {
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
             case "APPresentmentTypeEnum":
                 return APPresentmentTypeEnum.fromValue(object);
             case "InsertedCreditTypeEnum":
                 return InsertedCreditTypeEnum.fromValue(object);
             case "date":
                 return new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).parse(object);
             default:
                 return object;
         }
     }
 }