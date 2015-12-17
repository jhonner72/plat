package com.fujixerox.aus.repository.route;

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

		// REPO01
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSave", 
				"lombard-service-repository-storevouchers-service",
				"rabbitmq://localhost:9576/lombard.service.repository.storevouchers.response",
				"direct:not-testing-save"));
		// REPO02
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQuery", 
				"lombard-service-repository-getvouchers-service",
				"rabbitmq://localhost:9576/lombard.service.repository.getvouchers.response",
				"direct:not-testing-query"));
		// REPO03
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeUpdate", 
				"lombard-service-repository-updatevouchers-service",
				"rabbitmq://localhost:9576/lombard.service.repository.updatevouchers.response",
				"direct:not-testing-update"));
		// REPO04
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeRepost", 
				"lombard-service-repository-repostvouchers-service",
				"rabbitmq://localhost:9576/lombard.service.repository.repostvouchers.response",
				"direct:not-testing-repost"));
		// REPO05
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveListing",
				"lombard-service-outclearings-storelisting-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.storelisting.response",
				"direct:not-testing-saveListing"));
		// REPO06
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveReports",
				"lombard-service-reporting-storerepositoryreports-service",
				"rabbitmq://localhost:9576/lombard.service.reporting.storerepositoryreports.response",
				"direct:not-testing-saveReports"));
		// REPO07
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldTriggerWorkflows",
				"lombard-service-outclearings-triggerworkflow-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.triggerworkflow.response",
				"direct:not-testing-triggerWorkflow"));
		// REPO08
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQueryVoucherInfo", 
				"lombard-service-repository-getvouchersinformation-service",
				"rabbitmq://localhost:9576/lombard.service.repository.getvouchersinformation.response",
				"direct:not-testing-search"));
		// REPO09
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeUpdateVoucherInfo", 
				"lombard-service-repository-updatevouchersinformation-service",
				"rabbitmq://localhost:9576/lombard.service.repository.updatevouchersinformation.response",
				"direct:not-testing-updateVoucherInfo"));
		// REPO10
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveAdjustmentLetter", 
				"lombard-service-repository-storeadjustmentletter-service",
				"rabbitmq://localhost:9576/lombard.service.repository.storeadjustmentletter.response",
				"direct:not-testing-storeadjustmentletter"));
		// REPO11
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeAssociateGeneratedVoucher", 
				"lombard-service-outclearings-associategeneratedvouchers-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.associategeneratedvouchers.response",
				"direct:not-testing-associategeneratedvoucher"));
		// REPO12
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQueryReceivedFiles", 
				"lombard-service-repository-getreceivedfiles-service",
				"rabbitmq://localhost:9576/lombard.service.repository.getreceivedfiles.response",
				"direct:not-testing-queryReceivedFiles"));
		// REPO13
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeCheckDuplicateFile", 
				"lombard-service-repository-checkduplicatefile-service",
				"rabbitmq://localhost:9576/lombard.service.repository.checkduplicatefile.response",
				"direct:not-testing-checkDuplicateFile"));
		
	}
	
	public static List<RouteDefinitionHolder> getRouteDefinitionHolders() {
		return routeDefinitionHolders;
	}

}
