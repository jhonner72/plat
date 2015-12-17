package com.fujixerox.aus.repository.route;

import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingResponse;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowResponse;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsResponse;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersResponse;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusResponse;
import com.fujixerox.aus.repository.api.RepositoryService;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by warwick on 27/03/2015.
 * Henry 19/05/2015 add the tests for universal save, query and update services
 */
public class RepositoryRouteTest {
	
	private static final String END_POINT_MOCKED = "direct:test-endpoint";
	
	@Test
    public void shouldInvokeSaveForIIE() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        StoreBatchVoucherResponse response = new StoreBatchVoucherResponse();
        when(repositoryService.save(Matchers.any(StoreBatchVoucherRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeSaveForIIE", repositoryService, "{\"jobIdentifier\":\"job-001-002\"}");
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<StoreBatchVoucherRequest> storeBatchVoucherRequestArgumentCaptor = ArgumentCaptor.forClass(StoreBatchVoucherRequest.class);
        verify(repositoryService).save(storeBatchVoucherRequestArgumentCaptor.capture());
        StoreBatchVoucherRequest storeBatchVoucherRequest = storeBatchVoucherRequestArgumentCaptor.getValue();
        assertThat(storeBatchVoucherRequest.getJobIdentifier(), is("job-001-002"));
    }
	
	@Test
    public void shouldInvokeSaveForSurplus() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        StoreBatchVoucherResponse response = new StoreBatchVoucherResponse();
        when(repositoryService.save(Matchers.any(StoreBatchVoucherRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeSaveForSurplus", repositoryService, "{\"jobIdentifier\":\"job-001-002\"}");
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<StoreBatchVoucherRequest> storeBatchVoucherRequestArgumentCaptor = ArgumentCaptor.forClass(StoreBatchVoucherRequest.class);
        verify(repositoryService).save(storeBatchVoucherRequestArgumentCaptor.capture());
        StoreBatchVoucherRequest storeBatchVoucherRequest = storeBatchVoucherRequestArgumentCaptor.getValue();
        assertThat(storeBatchVoucherRequest.getJobIdentifier(), is("job-001-002"));
    }
	
	@Test
    public void shouldInvokeSaveForOutclearing() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        StoreBatchVoucherResponse response = new StoreBatchVoucherResponse();
        when(repositoryService.save(Matchers.any(StoreBatchVoucherRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeSaveForOutclearing", repositoryService, "{\"jobIdentifier\":\"job-001-002\"}");
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<StoreBatchVoucherRequest> storeBatchVoucherRequestArgumentCaptor = ArgumentCaptor.forClass(StoreBatchVoucherRequest.class);
        verify(repositoryService).save(storeBatchVoucherRequestArgumentCaptor.capture());
        StoreBatchVoucherRequest storeBatchVoucherRequest = storeBatchVoucherRequestArgumentCaptor.getValue();
        assertThat(storeBatchVoucherRequest.getJobIdentifier(), is("job-001-002"));
    }

    @Test
    public void shouldInvokeQueryForIFV() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        GetVouchersResponse response = new GetVouchersResponse();
        when(repositoryService.query(Matchers.any(GetVouchersRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeQueryForIFV", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).query(ArgumentCaptor.forClass(GetVouchersRequest.class).capture());
    }
    
    @Test
    public void shouldInvokeQueryForIE() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        GetVouchersResponse response = new GetVouchersResponse();
        when(repositoryService.query(Matchers.any(GetVouchersRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeQueryForIE", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).query(ArgumentCaptor.forClass(GetVouchersRequest.class).capture());
    }
    
    @Test
    public void shouldInvokeQueryForPending() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        GetVouchersResponse response = new GetVouchersResponse();
        when(repositoryService.query(Matchers.any(GetVouchersRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeQueryForPending", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).query(ArgumentCaptor.forClass(GetVouchersRequest.class).capture());
    }
    
    @Test
    public void shouldInvokeQueryForVIF() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        GetVouchersResponse response = new GetVouchersResponse();
        when(repositoryService.query(Matchers.any(GetVouchersRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeQueryForVIF", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).query(ArgumentCaptor.forClass(GetVouchersRequest.class).capture());
    }

    @Test
    public void shouldInvokeUpdateVIF() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        UpdateVouchersStatusResponse response = new UpdateVouchersStatusResponse();
        when(repositoryService.update(Matchers.any(UpdateVouchersStatusRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeUpdateVIF", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).update(ArgumentCaptor.forClass(UpdateVouchersStatusRequest.class).capture());
    }
    
    @Test
    public void shouldInvokeUpdateVIFACK() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        UpdateVouchersStatusResponse response = new UpdateVouchersStatusResponse();
        when(repositoryService.update(Matchers.any(UpdateVouchersStatusRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeUpdateVIFACK", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).update(ArgumentCaptor.forClass(UpdateVouchersStatusRequest.class).capture());
    }
    
    @Test
    public void shouldInvokeUpdateIE() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        UpdateVouchersStatusResponse response = new UpdateVouchersStatusResponse();
        when(repositoryService.update(Matchers.any(UpdateVouchersStatusRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeUpdateIE", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).update(ArgumentCaptor.forClass(UpdateVouchersStatusRequest.class).capture());
    }

    @Test
    public void shouldInvokeRepost() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        RepostVouchersResponse response = new RepostVouchersResponse();
        when(repositoryService.repost(Matchers.any(RepostVouchersRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeRepost", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<RepostVouchersRequest> captor = ArgumentCaptor.forClass(RepostVouchersRequest.class);
        verify(repositoryService).repost(captor.capture());
    }

    @Test
    public void shouldInvokeSaveListing() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        StoreListingResponse response = new StoreListingResponse();
        when(repositoryService.saveListings(Matchers.any(StoreListingRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeSaveListing", repositoryService, "{\"jobIdentifier\":\"job-001-002\"}");
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<StoreListingRequest> storeListingRequestArgumentCaptor = ArgumentCaptor.forClass(StoreListingRequest.class);
        verify(repositoryService).saveListings(storeListingRequestArgumentCaptor.capture());
        StoreListingRequest storeListingRequest = storeListingRequestArgumentCaptor.getValue();
        assertThat(storeListingRequest.getJobIdentifier(), is("job-001-002"));
    }

    @Test
    public void shouldInvokeSaveReports() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        StoreBatchRepositoryReportsResponse response = new StoreBatchRepositoryReportsResponse();
        when(repositoryService.saveReports(Matchers.any(StoreBatchRepositoryReportsRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeSaveReports", repositoryService, "{\"jobIdentifier\":\"job-001-002\"}");
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<StoreBatchRepositoryReportsRequest> storeBatchRepositoryReportsRequestArgumentCaptor = ArgumentCaptor.forClass(StoreBatchRepositoryReportsRequest.class);
        verify(repositoryService).saveReports(storeBatchRepositoryReportsRequestArgumentCaptor.capture());
        StoreBatchRepositoryReportsRequest storeBatchRepositoryReportsRequest = storeBatchRepositoryReportsRequestArgumentCaptor.getValue();
        assertThat(storeBatchRepositoryReportsRequest.getJobIdentifier(), is("job-001-002"));
    }

    @Test
    public void shouldTriggerWorkflows() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        TriggerWorkflowResponse response = new TriggerWorkflowResponse();
        when(repositoryService.triggerWorkflow(Matchers.any(TriggerWorkflowRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldTriggerWorkflows", repositoryService, "{\"workflowName\":[\"WF_Surplus_Suspense_pool\"]}");
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<TriggerWorkflowRequest> triggerWorkflowRequestArgumentCaptor = ArgumentCaptor.forClass(TriggerWorkflowRequest.class);
        verify(repositoryService).triggerWorkflow(triggerWorkflowRequestArgumentCaptor.capture());
        TriggerWorkflowRequest triggerWorkflowRequest = triggerWorkflowRequestArgumentCaptor.getValue();
        assertThat(triggerWorkflowRequest.getWorkflowNames().get(0), is("WF_Surplus_Suspense_pool"));
    }
    
    @Test
    public void shouldInvokeQueryVoucherInfo() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        GetVouchersInformationResponse response = new GetVouchersInformationResponse();
        when(repositoryService.queryVoucherInfo(Matchers.any(GetVouchersInformationRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeQueryVoucherInfo", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).queryVoucherInfo(ArgumentCaptor.forClass(GetVouchersInformationRequest.class).capture());
    }
    
    @Test
    public void shouldInvokeQueryVoucherInfoForAdjustmentLetter() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        GetVouchersInformationResponse response = new GetVouchersInformationResponse();
        when(repositoryService.queryVoucherInfo(Matchers.any(GetVouchersInformationRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeQueryVoucherInfoForAdjustmentLetter", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).queryVoucherInfo(ArgumentCaptor.forClass(GetVouchersInformationRequest.class).capture());
    }
    
    @Test
    public void shouldInvokeUpdateVoucherInfo() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        UpdateVouchersInformationResponse response = new UpdateVouchersInformationResponse();
        when(repositoryService.updateVoucherInfo(Matchers.any(UpdateVouchersInformationRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeUpdateVoucherInfo", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).updateVoucherInfo(ArgumentCaptor.forClass(UpdateVouchersInformationRequest.class).capture());
    }
    
    @Test
    public void shouldInvokeSaveAdjustmentLetter() throws Exception {
        RepositoryService repositoryService = mock(RepositoryService.class);
        StoreBatchAdjustmentLettersResponse response = new StoreBatchAdjustmentLettersResponse();
        when(repositoryService.saveAdjustmentLetter(Matchers.any(StoreBatchAdjustmentLettersRequest.class))).thenReturn(response);

        List<Exchange> exchanges = invoke("shouldInvokeSaveAdjustmentLetter", repositoryService, "{}");
        assertThat(exchanges.size(), is(0));
        verify(repositoryService).saveAdjustmentLetter(ArgumentCaptor.forClass(StoreBatchAdjustmentLettersRequest.class).capture());
    }

    private List<Exchange> invoke(String methodName, RepositoryService repositoryService, String exchangeBody) throws Exception {

    	JndiContext jndiContext = new JndiContext();
        jndiContext.bind("repositoryService", repositoryService);
        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        RepositoryRoute repositoryRoute = new RepositoryRoute();
        repositoryRoute.setOptions("");
        repositoryRoute.setHostname("localhost");
        repositoryRoute.setPort("9576");

        camelContext.addRoutes(repositoryRoute);

        String endPointToTest = "";
        for (RouteDefinitionHolder holder : RouteDefinitionBuilder.getRouteDefinitionHolders()) {
        	String endPointMocked = holder.getEndPointForNotTesting();
        	if (holder.getMethodName().equals(methodName)) {
        		endPointMocked = END_POINT_MOCKED;
        		endPointToTest = holder.getEndPoint();
        	}
        	RouteDefinition rd = camelContext.getRouteDefinition(holder.getRouteDefinition());
            rd.adviceWith(camelContext, new AdviceWithRouteBuilderImpl(holder.getEndPoint(), endPointMocked));
        }

        MockEndpoint mockResponseQueue = MockEndpoint.resolve(camelContext, "mock:" + endPointToTest);

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint(END_POINT_MOCKED);
        Exchange exchange = endpoint.createExchange();
        exchange.getIn().setBody(exchangeBody);
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", "aaa-bbb-ccc");
        producerTemplate.send(endpoint, exchange);

        return mockResponseQueue.getExchanges();
    }

}