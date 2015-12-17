package com.fujixerox.aus.repository.util;

public class MappingEntry {
	
	private String key;
	private String value;
	private String keyType;
	private String valueType;
	
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
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
		
	public String getKeyType() {
		return keyType;
	}
	
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
	
	public String getValueType() {
		return valueType;
	}
	
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}	

}
