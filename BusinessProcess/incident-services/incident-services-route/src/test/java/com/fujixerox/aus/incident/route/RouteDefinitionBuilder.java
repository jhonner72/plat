package com.fujixerox.aus.incident.route;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Henry.Niu 20/05/2015
 *
 */
public class RouteDefinitionBuilder {
	
	private static List<RouteDefinitionHolder> routeDefinitionHolders;
	
	static {
		routeDefinitionHolders = new ArrayList<RouteDefinitionHolder>();

		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeCreateForSLAIncident", 
				"lombard-service-support-incident",
				"rabbitmq://localhost:9576/lombard.service.support.incident",
				"direct:not-testing-createSLAIncident"));
		
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeCreateForError", 
				"lombard-service-support-error",
				"rabbitmq://localhost:9576/lombard.service.support.error",
				"direct:not-testing-createError"));

	}
	
	public static List<RouteDefinitionHolder> getRouteDefinitionHolders() {
		return routeDefinitionHolders;
	}

}
