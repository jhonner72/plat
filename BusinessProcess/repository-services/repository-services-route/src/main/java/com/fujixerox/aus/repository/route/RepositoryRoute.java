package com.fujixerox.aus.repository.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
//import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.fujixerox.aus.lombard.outclearings.checkduplicatefile.CheckDuplicateFileRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersRequest;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import com.fujixerox.aus.repository.service.ErrorHandlingProcessor;
import com.fujixerox.aus.repository.util.exception.RetriableException;

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
       
        // Retriable Error Handling 21071, 24044
        onException(RetriableException.class).
	        log("${in.header[rabbitmq.CORRELATIONID]} -  Retriable Exception Start - lombard.dlx : system").
	        useOriginalMessage().
	        process(new ErrorHandlingProcessor("system")).
	        removeHeader(RabbitMQConstants.EXCHANGE_NAME).
	        handled(true).
	        marshal().json(JsonLibrary.Jackson).
	        log("${in.header[rabbitmq.CORRELATIONID]} -  Retriable Exception End - lombard.dlx : system").
        	toF("rabbitmq://%s:%s/lombard.dlx%s", hostname, port, toOptions);
        
        // Non-Retriable Error Handling 21071, 24044
        onException(Exception.class).
	        log("${in.header[rabbitmq.CORRELATIONID]} -  Non-Retriable Exception Start - lombard.dlx : service").
	        useOriginalMessage().
	        process(new ErrorHandlingProcessor("service")).
	        removeHeader(RabbitMQConstants.EXCHANGE_NAME).
	        handled(true).
	        marshal().json(JsonLibrary.Jackson).
	        log("${in.header[rabbitmq.CORRELATIONID]} -  Non-Retriable Exception End - lombard.dlx : service").
	        toF("rabbitmq://%s:%s/lombard.dlx%s", hostname, port, toOptions);

        // REPO01
        fromF("rabbitmq://%s:%s/lombard.service.repository.storevouchers.request?queue=lombard.service.repository.storevouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-storevouchers-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Store Vouchers - lombard.service.repository.storevouchers.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, StoreBatchVoucherRequest.class).
	        beanRef("repositoryService", "save").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.storevouchers.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Store Vouchers - lombard.service.repository.storevouchers.response : ${in.header[rabbitmq.ROUTING_KEY]}");

        // REPO02
        fromF("rabbitmq://%s:%s/lombard.service.repository.getvouchers.request?queue=lombard.service.repository.getvouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-getvouchers-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Get Vouchers - lombard.service.repository.getvouchers.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersRequest.class).
	        beanRef("repositoryService", "query").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.getvouchers.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Get Vouchers - lombard.service.repository.getvouchers.response : ${in.header[rabbitmq.ROUTING_KEY]}");
        
        // REPO03
        fromF("rabbitmq://%s:%s/lombard.service.repository.updatevouchers.request?queue=lombard.service.repository.updatevouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-updatevouchers-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Update Vouchers Status - lombard.service.repository.updatevouchers.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, UpdateVouchersStatusRequest.class).
	        beanRef("repositoryService", "update").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.updatevouchers.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Update Vouchers Status - lombard.service.repository.updatevouchers.response : ${in.header[rabbitmq.ROUTING_KEY]}");
        
        // REPO04
        fromF("rabbitmq://%s:%s/lombard.service.repository.repostvouchers.request?queue=lombard.service.repository.repostvouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-repostvouchers-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Repost Vouchers - lombard.service.repository.repostvouchers.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, RepostVouchersRequest.class).
	        beanRef("repositoryService", "repost").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.repostvouchers.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Repost Vouchers - lombard.service.repository.repostvouchers.response : ${in.header[rabbitmq.ROUTING_KEY]}");

        // REPO05
		fromF("rabbitmq://%s:%s/lombard.service.outclearings.storelisting.request?queue=lombard.service.outclearings.storelisting.request.queue%s", hostname, port, fromOptions).
			routeId("lombard-service-outclearings-storelisting-service").
			log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Store Listings - lombard.service.outclearings.storelisting.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
			setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
			unmarshal().json(JsonLibrary.Jackson, StoreListingRequest.class).
			beanRef("repositoryService", "saveListings").
			marshal().json(JsonLibrary.Jackson).
			setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
			removeHeader("rabbitmq.EXCHANGE_NAME").
			toF("rabbitmq://%s:%s/lombard.service.outclearings.storelisting.response%s", hostname, port, toOptions).
			log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Store Listings - lombard.service.outclearings.storelisting.response : ${in.header[rabbitmq.ROUTING_KEY]}");

		// REPO06
		fromF("rabbitmq://%s:%s/lombard.service.outclearings.triggerworkflow.request?queue=lombard.service.outclearings.triggerworkflow.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-triggerworkflow-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Trigger Workflow - lombard.service.outclearings.triggerworkflow.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, TriggerWorkflowRequest.class).
	        beanRef("repositoryService", "triggerWorkflow").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.triggerworkflow.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Trigger Workflow - lombard.service.outclearings.triggerworkflow.response : ${in.header[rabbitmq.ROUTING_KEY]}");

		// REPO07
		fromF("rabbitmq://%s:%s/lombard.service.reporting.storerepositoryreports.request?queue=lombard.service.reporting.storerepositoryreports.request.queue%s", hostname, port, fromOptions).
			routeId("lombard-service-reporting-storerepositoryreports-service").
			log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Store Reports - lombard.service.reporting.storerepositoryreports.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
			setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
			unmarshal().json(JsonLibrary.Jackson, StoreBatchRepositoryReportsRequest.class).
			beanRef("repositoryService", "saveReports").
			marshal().json(JsonLibrary.Jackson).
			setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
			removeHeader("rabbitmq.EXCHANGE_NAME").
			toF("rabbitmq://%s:%s/lombard.service.reporting.storerepositoryreports.response%s", hostname, port, toOptions).
			log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Store Reports - lombard.service.reporting.storerepositoryreports.response : ${in.header[rabbitmq.ROUTING_KEY]}");
		
		// REPO08
		fromF("rabbitmq://%s:%s/lombard.service.repository.getvouchersinformation.request?queue=lombard.service.repository.getvouchersinformation.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-getvouchersinformation-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Get Vouchers Information - lombard.service.repository.getvouchersinformation.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetVouchersInformationRequest.class).
	        beanRef("repositoryService", "queryVoucherInfo").
	        marshal().json(JsonLibrary.Jackson).
            setHeader("rabbitmq.TYPE", constant("Lombard.Adapters.DipsAdapter.Messages.GetVouchersInformationResponse:Lombard.Adapters.DipsAdapter")).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.getvouchersinformation.response%s", hostname, port, toOptions).
		log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Get Vouchers Information - lombard.service.repository.getvouchersinformation.response : ${in.header[rabbitmq.ROUTING_KEY]}");
		
		// REPO09
		fromF("rabbitmq://%s:%s/lombard.service.repository.updatevouchersinformation.request?queue=lombard.service.repository.updatevouchersinformation.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-updatevouchersinformation-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Update Vouchers Information - lombard.service.repository.updatevouchersinformation.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, UpdateVouchersInformationRequest.class).
	        beanRef("repositoryService", "updateVoucherInfo").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.updatevouchersinformation.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Update Vouchers Information - lombard.service.repository.updatevouchersinformation.response : ${in.header[rabbitmq.ROUTING_KEY]}");
		
		// REPO10
		fromF("rabbitmq://%s:%s/lombard.service.repository.storeadjustmentletter.request?queue=lombard.service.repository.storeadjustmentletter.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-storeadjustmentletter-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Store Adjustment Letters - lombard.service.repository.storeadjustmentletter.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, StoreBatchAdjustmentLettersRequest.class).
	        beanRef("repositoryService", "saveAdjustmentLetter").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.storeadjustmentletter.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Store Adjustment Letters - lombard.service.repository.storeadjustmentletter.response : ${in.header[rabbitmq.ROUTING_KEY]}");
		
		// REPO11
		fromF("rabbitmq://%s:%s/lombard.service.outclearings.associategeneratedvouchers.request?queue=lombard.service.outclearings.associategeneratedvouchers.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-outclearings-associategeneratedvouchers-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Associate Generated Voucher - lombard.service.outclearings.associategeneratedvouchers.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, AssociateVouchersRequest.class).
	        beanRef("repositoryService", "saveAndUpdateAssociateVoucher").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.outclearings.associategeneratedvouchers.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Associate Generated Voucher - lombard.service.outclearings.associategeneratedvouchers.response : ${in.header[rabbitmq.ROUTING_KEY]}");

		// REPO12
		fromF("rabbitmq://%s:%s/lombard.service.repository.getreceivedfiles.request?queue=lombard.service.repository.getreceivedfiles.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-getreceivedfiles-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Get Received Files - lombard.service.repository.getreceivedfiles.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, GetReceivedFilesRequest.class).
	        beanRef("repositoryService", "queryReceivedFiles").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.getreceivedfiles.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Get Received Files - lombard.service.repository.getreceivedfiles.response : ${in.header[rabbitmq.ROUTING_KEY]}");

		// REPO13
		fromF("rabbitmq://%s:%s/lombard.service.repository.checkduplicatefile.request?queue=lombard.service.repository.checkduplicatefile.request.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-repository-checkduplicatefile-service").
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Start: Check Duplicate File - lombard.service.repository.checkduplicatefile.request.queue : ${in.header[rabbitmq.ROUTING_KEY]}").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, CheckDuplicateFileRequest.class).
	        beanRef("repositoryService", "checkDuplicateFile").
	        marshal().json(JsonLibrary.Jackson).
	        setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
	        removeHeader("rabbitmq.EXCHANGE_NAME").
	        toF("rabbitmq://%s:%s/lombard.service.repository.checkduplicatefile.response%s", hostname, port, toOptions).
	        log("${in.header[rabbitmq.CORRELATIONID]} - Service Done: Check Duplicate File - lombard.service.repository.checkduplicatefile.response : ${in.header[rabbitmq.ROUTING_KEY]}");
	        
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
