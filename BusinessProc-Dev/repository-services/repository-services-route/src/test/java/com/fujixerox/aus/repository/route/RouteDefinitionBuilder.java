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

		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveForIIE", 
				"lombard-service-inclearings-storeinwardimageexchangedocumentum-service",
				"rabbitmq://localhost:9576/lombard.service.inclearings.storeinwardimageexchangedocumentum.response",
				"direct:not-testing-save-iie"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveForSurplus", 
				"lombard-service-outclearings-surplus-storebatchvoucher-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.surplus.storebatchvoucher.response",
				"direct:not-testing-save-surplus"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveForOutclearing", 
				"lombard-service-outclearings-storebatchvoucher-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.storebatchvoucher.response",
				"direct:not-testing-save-outclearing"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQueryForIFV", 
				"lombard-service-inclearings-getinwardforvaluevouchers-service",
				"rabbitmq://localhost:9576/lombard.service.inclearings.getinwardforvaluevouchers.response",
				"direct:not-testing-query-ifv"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQueryForIE", 
				"lombard-service-outclearings-getvouchersforimageexchange-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.getvouchersforimageexchange.response",
				"direct:not-testing-query-ie"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQueryForPending", 
				"lombard-service-outclearings-updatependingvouchers-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.updatependingvouchers.response",
				"direct:not-testing-query-pending"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQueryForVIF", 
				"lombard-service-outclearings-getvouchersforvalueinstructionfile-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.getvouchersforvalueinstructionfile.response",
				"direct:not-testing-query-vif"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeUpdateVIF", 
				"lombard-service-outclearings-updatevalueinstructionfilevouchersstatus-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.updatevalueinstructionfilevouchersstatus.response",
				"direct:not-testing-update-vif"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeUpdateVIFACK", 
				"lombard-service-outclearings-updatevalueinstructionfilevouchersackstatus-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.updatevalueinstructionfilevouchersackstatus.response",
				"direct:not-testing-update-vifack"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeUpdateIE", 
				"lombard-service-outclearings-updateimageexchangevouchersstatus-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.updateimageexchangevouchersstatus.response",
				"direct:not-testing-update-ie"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeRepost", 
				"lombard-service-repository-repostvouchers-service",
				"rabbitmq://localhost:9576/lombard.service.repository.repostvouchers.response",
				"direct:not-testing-repost"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveListing",
				"lombard-service-outclearings-storelisting-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.storelisting.response",
				"direct:not-testing-saveListing"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveReports",
				"lombard-service-reporting-storerepositoryreports-service",
				"rabbitmq://localhost:9576/lombard.service.reporting.storerepositoryreports.response",
				"direct:not-testing-saveReports"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldTriggerWorkflows",
				"lombard-service-outclearings-triggerworkflow-service",
				"rabbitmq://localhost:9576/lombard.service.outclearings.triggerworkflow.response",
				"direct:not-testing-triggerWorkflow"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQueryVoucherInfo", 
				"lombard-service-repository-getvouchersinformation-foradjletter-service",
				"rabbitmq://localhost:9576/lombard.service.repository.getvouchersinformation.foradjletter.response",
				"direct:not-testing-search"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeQueryVoucherInfoForAdjustmentLetter", 
				"lombard-service-repository-getvouchersinformation-service",
				"rabbitmq://localhost:9576/lombard.service.repository.getvouchersinformation.response",
				"direct:not-testing-search-foradjletter"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeUpdateVoucherInfo", 
				"lombard-service-repository-updatevouchersinformation-service",
				"rabbitmq://localhost:9576/lombard.service.repository.updatevouchersinformation.response",
				"direct:not-testing-updateVoucherInfo"));
		routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeSaveAdjustmentLetter", 
				"lombard-service-repository-storeadjustmentletter-service",
				"rabbitmq://localhost:9576/lombard.service.repository.storeadjustmentletter.response",
				"direct:not-testing-storeadjustmentletter"));
		/*routeDefinitionHolders.add(new RouteDefinitionHolder("shouldInvokeDirect", 
				"direct",
				"rabbitmq://localhost:9576/direct",
				"direct:not-testing-direct"));*/



	}
	
	public static List<RouteDefinitionHolder> getRouteDefinitionHolders() {
		return routeDefinitionHolders;
	}

}
