package com.fujixerox.aus.incident.route;

/**
 * 
 * @author Henry.Niu 20/05/2015
 *
 */
public class RouteDefinitionHolder {
	
	private String methodName;
	private String routeDefinition;
	private String endPoint;
	private String endPointForNotTesting;
	
	public RouteDefinitionHolder(String methodName, String routeDefinition,	String endPoint, String endPointForNotTesting) {
		this.methodName = methodName;
		this.routeDefinition = routeDefinition;
		this.endPoint = endPoint;
		this.endPointForNotTesting = endPointForNotTesting;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getRouteDefinition() {
		return routeDefinition;
	}

	public String getEndPoint() {
		return endPoint;
	}	

	public String getEndPointForNotTesting() {
		return endPointForNotTesting;
	}

}
