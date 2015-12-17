package com.fujixerox.aus.repository.util;

import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;

/**
 * 
 * @author Henry.Niu
 * 08/09/2015
 * This class is used to generate documentum query by using the mapping file
 */
public class MappingEntryQueryProcessor {
	
	public String process(Criteria criteria, MappingEntry entry) throws Exception {	
		if (criteria.getValue() == null) {
			return null;
		}
		
		return entry.getKey() + " = " + manipulateValue(entry.getKeyType(), entry.getValueType(), criteria.getValue());
	}
		
	private String manipulateValue(String keyType, String valueType, String sourceValue) {		
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
}
