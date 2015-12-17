package com.fujixerox.aus.repository.route;

import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 * 
 */
public class RepositoryRoute extends RouteBuilder {

    private String hostname;
    private String port;
    private String options;

    @Override
    public void configure() throws Exception {
        String toOptions = options.length() > 0 ? "?" + options : "";
        String fromOptions = options.length() > 0 ? "&" + options : "";

        /*fromF("rabbitmq://%s:%s/lombard.service.repository.storevouchers.request?queue=lombard.service.repository.storevouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-storevouchers-service").
	        log("Service Start: Store Vouchers").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, StoreBatchVoucherRequest.class).
	        beanRef("repositoryService", "save").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.storevouchers.response%s", hostname, port, toOptions).
	        log("Service Done: Store Vouchers");*/
        
        fromF("rabbitmq://%s:%s/lombard.service.inclearings.storeinwardimageexchangedocumentum.request?queue=lombard.service.inclearings.storeinwardimageexchangedocumentum.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-inclearings-storeinwardimageexchangedocumentum-service").
	        log("Service Start: Store Vouchers for IIE").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, StoreBatchVoucherRequest.class).
	        beanRef("repositoryService", "save").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.inclearings.storeinwardimageexchangedocumentum.response%s", hostname, port, toOptions).
	        log("Service Done: Store Vouchers for IIE");
        
        fromF("rabbitmq://%s:%s/lombard.service.outclearings.surplus.storebatchvoucher.request?queue=lombard.service.outclearings.surplus.storebatchvoucher.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-surplus-storebatchvoucher-service").
	        log("Service Start: Store Vouchers for Surplus").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, StoreBatchVoucherRequest.class).
	        beanRef("repositoryService", "save").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.surplus.storebatchvoucher.response%s", hostname, port, toOptions).
	        log("Service Done: Store Vouchers for Surplus");
        
        fromF("rabbitmq://%s:%s/lombard.service.outclearings.storebatchvoucher.request?queue=lombard.service.outclearings.storebatchvoucher.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-storebatchvoucher-service").
	        log("Service Start: Store Vouchers for Outclearing").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, StoreBatchVoucherRequest.class).
	        beanRef("repositoryService", "save").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.storebatchvoucher.response%s", hostname, port, toOptions).
	        log("Service Done: Store Vouchers for Outclearing");
        
        /*fromF("rabbitmq://%s:%s/lombard.service.repository.getvouchers.request?queue=lombard.service.repository.getvouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-getvouchers-service").
	        log("Service Start: Get Vouchers").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersRequest.class).
	        beanRef("repositoryService", "query").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.getvouchers.response%s", hostname, port, toOptions).
	        log("Service Done: Get Vouchers");*/
        
        fromF("rabbitmq://%s:%s/lombard.service.inclearings.getinwardforvaluevouchers.request?queue=lombard.service.inclearings.getinwardforvaluevouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-inclearings-getinwardforvaluevouchers-service").
	        log("Service Start: Get Vouchers for IFV").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersRequest.class).
	        beanRef("repositoryService", "query").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.inclearings.getinwardforvaluevouchers.response%s", hostname, port, toOptions).
	        log("Service Done: Get Vouchers for IFV");
        
        fromF("rabbitmq://%s:%s/lombard.service.outclearings.getvouchersforimageexchange.request?queue=lombard.service.outclearings.getvouchersforimageexchange.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-getvouchersforimageexchange-service").
	        log("Service Start: Get Vouchers for IE").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersRequest.class).
	        beanRef("repositoryService", "query").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.getvouchersforimageexchange.response%s", hostname, port, toOptions).
	        log("Service Done: Get Vouchers fo IE");
        
        fromF("rabbitmq://%s:%s/lombard.service.outclearings.updatependingvouchers.request?queue=lombard.service.outclearings.updatependingvouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-updatependingvouchers-service").
	        log("Service Start: Get Vouchers for Pending").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersRequest.class).
	        beanRef("repositoryService", "query").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.updatependingvouchers.response%s", hostname, port, toOptions).
	        log("Service Done: Get Vouchers for Pending");
        
        fromF("rabbitmq://%s:%s/lombard.service.outclearings.getvouchersforvalueinstructionfile.request?queue=lombard.service.outclearings.getvouchersforvalueinstructionfile.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-getvouchersforvalueinstructionfile-service").
	        log("Service Start: Get Vouchers for VIF").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersRequest.class).
	        beanRef("repositoryService", "query").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.getvouchersforvalueinstructionfile.response%s", hostname, port, toOptions).
	        log("Service Done: Get Vouchers for VIF");
        
        /*fromF("rabbitmq://%s:%s/lombard.service.repository.updatevouchers.request?queue=lombard.service.repository.updatevouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-updatevouchers-service").
	        log("Service Start: Update Vouchers Status").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, UpdateVouchersStatusRequest.class).
	        beanRef("repositoryService", "update").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.updatevouchers.response%s", hostname, port, toOptions).
	        log("Service Done: Update Vouchers Status");*/
        
        fromF("rabbitmq://%s:%s/lombard.service.outclearings.updatevalueinstructionfilevouchersstatus.request?queue=lombard.service.outclearings.updatevalueinstructionfilevouchersstatus.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-updatevalueinstructionfilevouchersstatus-service").
	        log("Service Start: Update Vouchers Status for VIF").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, UpdateVouchersStatusRequest.class).
	        beanRef("repositoryService", "update").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.updatevalueinstructionfilevouchersstatus.response%s", hostname, port, toOptions).
	        log("Service Done: Update Vouchers Status for VIF");
        
        fromF("rabbitmq://%s:%s/lombard.service.outclearings.updatevalueinstructionfilevouchersackstatus.request?queue=lombard.service.outclearings.updatevalueinstructionfilevouchersackstatus.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-updatevalueinstructionfilevouchersackstatus-service").
	        log("Service Start: Update Vouchers Status for VIF ACK").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, UpdateVouchersStatusRequest.class).
	        beanRef("repositoryService", "update").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.updatevalueinstructionfilevouchersackstatus.response%s", hostname, port, toOptions).
	        log("Service Done: Update Vouchers Status for VIF ACK");
        
        fromF("rabbitmq://%s:%s/lombard.service.outclearings.updateimageexchangevouchersstatus.request?queue=lombard.service.outclearings.updateimageexchangevouchersstatus.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-updateimageexchangevouchersstatus-service").
	        log("Service Start: Update Vouchers Status for IE").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, UpdateVouchersStatusRequest.class).
	        beanRef("repositoryService", "update").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.updateimageexchangevouchersstatus.response%s", hostname, port, toOptions).
	        log("Service Done: Update Vouchers Status for IE");
        
        fromF("rabbitmq://%s:%s/lombard.service.repository.repostvouchers.request?queue=lombard.service.repository.repostvouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-repostvouchers-service").
	        log("Service Start: Repost Vouchers").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, RepostVouchersRequest.class).
	        beanRef("repositoryService", "repost").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.repostvouchers.response%s", hostname, port, toOptions).
	        log("Service Done: Repost Vouchers");

		fromF("rabbitmq://%s:%s/lombard.service.outclearings.storelisting.request?queue=lombard.service.outclearings.storelisting.request.queue%s", hostname, port, fromOptions).
			routeId("lombard-service-outclearings-storelisting-service").
			log("Service Start: Store Listings").
			setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
			unmarshal().json(JsonLibrary.Jackson, StoreListingRequest.class).
			beanRef("repositoryService", "saveListings").
			marshal().json(JsonLibrary.Jackson).
			setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
			removeHeader("rabbitmq.EXCHANGE_NAME").
			toF("rabbitmq://%s:%s/lombard.service.outclearings.storelisting.response%s", hostname, port, toOptions).
			log("Service Done: Store Listings");

		fromF("rabbitmq://%s:%s/lombard.service.outclearings.triggerworkflow.request?queue=lombard.service.outclearings.triggerworkflow.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-triggerworkflow-service").
	        log("Service Start: Trigger Workflow").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, TriggerWorkflowRequest.class).
	        beanRef("repositoryService", "triggerWorkflow").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.triggerworkflow.response%s", hostname, port, toOptions).
	        log("Service Done: Trigger Workflow");


		fromF("rabbitmq://%s:%s/lombard.service.reporting.storerepositoryreports.request?queue=lombard.service.reporting.storerepositoryreports.request.queue%s", hostname, port, fromOptions).
			routeId("lombard-service-reporting-storerepositoryreports-service").
			log("Service Start: Store Reports").
			setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
			unmarshal().json(JsonLibrary.Jackson, StoreBatchRepositoryReportsRequest.class).
			beanRef("repositoryService", "saveReports").
			marshal().json(JsonLibrary.Jackson).
			setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
			removeHeader("rabbitmq.EXCHANGE_NAME").
			toF("rabbitmq://%s:%s/lombard.service.reporting.storerepositoryreports.response%s", hostname, port, toOptions).
			log("Service Done: Store Reports");
		
		fromF("rabbitmq://%s:%s/lombard.service.repository.getvouchersinformation.request?queue=lombard.service.repository.getvouchersinformation.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-getvouchersinformation-service").
	        log("Service Start: Get Vouchers Information").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersInformationRequest.class).
	        beanRef("repositoryService", "queryVoucherInfo").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.getvouchersinformation.response%s", hostname, port, toOptions).
	        log("Service Done: Get Vouchers Information");
		
		fromF("rabbitmq://%s:%s/lombard.service.repository.getvouchersinformation.foradjletter.request?queue=lombard.service.repository.getvouchersinformation.foradjletter.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-getvouchersinformation-foradjletter-service").
	        log("Service Start: Get Vouchers Information For Adjustment Letter").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersInformationRequest.class).
	        beanRef("repositoryService", "queryVoucherInfo").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.getvouchersinformation.foradjletter.response%s", hostname, port, toOptions).
	        log("Service Done: Get Vouchers Information For Adjustment Letter");
		
		fromF("rabbitmq://%s:%s/lombard.service.repository.updatevouchersinformation.request?queue=lombard.service.repository.updatevouchersinformation.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-updatevouchersinformation-service").
	        log("Service Start: Update Vouchers Information").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, UpdateVouchersInformationRequest.class).
	        beanRef("repositoryService", "updateVoucherInfo").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.updatevouchersinformation.response%s", hostname, port, toOptions).
	        log("Service Done: Update Vouchers Information");
		
		fromF("rabbitmq://%s:%s/lombard.service.repository.storeadjustmentletter.request?queue=lombard.service.repository.storeadjustmentletter.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-storeadjustmentletter-service").
	        log("Service Start: Store Adjustment Letters").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, StoreBatchAdjustmentLettersRequest.class).
	        beanRef("repositoryService", "saveAdjustmentLetter").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.storeadjustmentletter.response%s", hostname, port, toOptions).
	        log("Service Done: Store Adjustment Letters");
                       
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
