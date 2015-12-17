package com.fujixerox.aus.repository.util;

import java.lang.reflect.Method;

public class MethodHolder {
	
	private String sourceMethodName;
	private Method sourceMethod;
	private Method targetMethod;
	
	public MethodHolder(String sourceMethodName, Method sourceMethod, Method targetMethod) {
		super();
		this.sourceMethodName = sourceMethodName;
		this.sourceMethod = sourceMethod;
		this.targetMethod = targetMethod;
	}

	public String getSourceMethodName() {
		return sourceMethodName;
	}
	
	public Method getSourceMethod() {
		return sourceMethod;
	}
	
	public Method getTargetMethod() {
		return targetMethod;
	}	

}
