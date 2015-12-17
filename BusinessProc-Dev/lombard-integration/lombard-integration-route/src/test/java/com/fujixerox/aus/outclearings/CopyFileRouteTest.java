package com.fujixerox.aus.outclearings;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;
import com.fujixerox.aus.lombard.common.job.Job;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 5/05/15
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyFileRouteTest {

    JobStore jobStore;
    Transformer requestTransformer;
    public static final String SERVICE_NAME = "lombard.service.copyfile";
    public static final String CORRELATION_ID = "22042015-3EEB-4069-A2DD-SSS987654321";

    @Before
    public void init() {
    	jobStore = mock(JobStore.class);
    	requestTransformer = mock(Transformer.class);
    }
    
    @Test
    public void shouldCopyIeFile() throws Exception {
        String bitLocker = "src/test/resources/data";
        File jobFolder = new File(bitLocker, CORRELATION_ID);
        jobFolder.mkdirs();
        String sourceDropPath = "src/test/resources/data/dropzone/inbound/VIFACK";
        new File(sourceDropPath).mkdirs();
        String sourceFileName = "VIF.ACK";

        File sourceFile = new File(sourceDropPath, sourceFileName);
        if (!sourceFile.exists()) sourceFile.createNewFile();
        File targetFile = new File(jobFolder, sourceFileName);

        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);
        jndiContext.bind("transform." + SERVICE_NAME + ".request", requestTransformer);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        when(jobStore.findJob(anyString())).thenReturn(job);

        CopyFileRequest copyFileRequest = new CopyFileRequest();
        copyFileRequest.setSourceFilename(sourceFile.getAbsolutePath());
        copyFileRequest.setTargetFilename(targetFile.getAbsolutePath());
        when(requestTransformer.transform(any(Job.class))).thenReturn(copyFileRequest);

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        CopyFileRoute copyFileRoute = new CopyFileRoute();
        copyFileRoute.setServiceName(SERVICE_NAME);
        camelContext.addRoutes(copyFileRoute);

        RouteDefinition rd = camelContext.getRouteDefinition(SERVICE_NAME+".request");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                replaceFromWith("direct:test-endpoint");
                mockEndpointsAndSkip("direct:lombard-service-copyfile-request");
            }
        });

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody(String.format("{\"jobIdentifier\":\"%s\"}", CORRELATION_ID));
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", CORRELATION_ID);
        Exchange ex = producerTemplate.send(endpoint, exchange);

        assertThat(targetFile.exists(), is(true));
        
        // Check number of method invocation
        verify(jobStore, times(1)).addRequest(any(Job.class), anyObject(), anyString(), anyString(), anyString());
        verify(jobStore, times(1)).addResponse(any(Job.class), anyObject(), anyString(), anyString());

        targetFile.delete();
        sourceFile.delete();
        sourceFile.getParentFile().delete();
        sourceFile.getParentFile().getParentFile().delete();
        sourceFile.getParentFile().getParentFile().getParentFile().delete();
    }

//    @Test
    public void shouldCopyFolderAndFiles() throws Exception {
        String bitLocker = "src/test/resources/data";
        File jobFolder = new File(bitLocker, CORRELATION_ID);
        jobFolder.mkdirs();
        String sourceDropPath = "src/test/resources/data/dropzone/DipsTransfer";

        File targetFile = new File(sourceDropPath, CORRELATION_ID);
        targetFile.mkdirs();

        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);
        jndiContext.bind("transform." + SERVICE_NAME + ".request", requestTransformer);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        when(jobStore.findJob(anyString())).thenReturn(job);

        CopyFileRequest copyFileRequest = new CopyFileRequest();
        copyFileRequest.setSourceFilename(jobFolder.getAbsolutePath());
        copyFileRequest.setTargetFilename(targetFile.getAbsolutePath());
        when(requestTransformer.transform(any(Job.class))).thenReturn(copyFileRequest);

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        CopyFileRoute copyFileRoute = new CopyFileRoute();
        copyFileRoute.setServiceName(SERVICE_NAME);
        camelContext.addRoutes(copyFileRoute);

        RouteDefinition rd = camelContext.getRouteDefinition(SERVICE_NAME+".request");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                replaceFromWith("direct:test-endpoint");
                mockEndpointsAndSkip("direct:lombard-service-copyfile-request");
            }
        });

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody(String.format("{\"jobIdentifier\":\"%s\"}", CORRELATION_ID));
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", CORRELATION_ID);
        Exchange ex = producerTemplate.send(endpoint, exchange);

        assertThat(targetFile.exists(), is(true));
        assertThat(targetFile.listFiles().length, is(1));

        targetFile.listFiles()[0].delete();
        targetFile.delete();
        targetFile.getParentFile().delete();
        targetFile.getParentFile().getParentFile().delete();
    }
    
    
    @Test
    public void shouldSkipJobStoreRequest_WhenCopyFile() throws Exception {
        String bitLocker = "src/test/resources/data";
        File jobFolder = new File(bitLocker, CORRELATION_ID);
        jobFolder.mkdirs();
        String sourceDropPath = "src/test/resources/data/dropzone/inbound/VIFACK";
        new File(sourceDropPath).mkdirs();
        String sourceFileName = "VIF.ACK";

        File sourceFile = new File(sourceDropPath, sourceFileName);
        if (!sourceFile.exists()) sourceFile.createNewFile();
        File targetFile = new File(jobFolder, sourceFileName);

        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);
        jndiContext.bind("transform." + SERVICE_NAME + ".request", requestTransformer);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        when(jobStore.findJob(anyString())).thenReturn(job);

        CopyFileRequest copyFileRequest = new CopyFileRequest();
        copyFileRequest.setSourceFilename(sourceFile.getAbsolutePath());
        copyFileRequest.setTargetFilename(targetFile.getAbsolutePath());
        when(requestTransformer.transform(any(Job.class))).thenReturn(copyFileRequest);

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        // Skip Store Job Request
        CopyFileRoute copyFileRoute = new CopyFileRoute();
        copyFileRoute.setServiceName(SERVICE_NAME);
        copyFileRoute.setSkipStoreJobRequest(true);
        copyFileRoute.setSkipStoreJobResponse(false);
        camelContext.addRoutes(copyFileRoute);

        RouteDefinition rd = camelContext.getRouteDefinition(SERVICE_NAME+".request");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                replaceFromWith("direct:test-endpoint");
                mockEndpointsAndSkip("direct:lombard-service-copyfile-request");
            }
        });

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody(String.format("{\"jobIdentifier\":\"%s\"}", CORRELATION_ID));
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", CORRELATION_ID);
        Exchange ex = producerTemplate.send(endpoint, exchange);

        assertThat(targetFile.exists(), is(true));
        
        // Check number of method invocation
        verify(jobStore, times(0)).addRequest(any(Job.class), anyObject(), anyString(), anyString(), anyString());
        verify(jobStore, times(1)).addRequestOnce(any(Job.class), anyObject(), anyString(), anyString(), anyString());
        verify(jobStore, times(1)).addResponse(any(Job.class), anyObject(), anyString(), anyString());

        targetFile.delete();
        sourceFile.delete();
        sourceFile.getParentFile().delete();
        sourceFile.getParentFile().getParentFile().delete();
        sourceFile.getParentFile().getParentFile().getParentFile().delete();
    }
    
    @Test
    public void shouldSkipJobStoreResponse_WhenCopyFile() throws Exception {
        String bitLocker = "src/test/resources/data";
        File jobFolder = new File(bitLocker, CORRELATION_ID);
        jobFolder.mkdirs();
        String sourceDropPath = "src/test/resources/data/dropzone/inbound/VIFACK";
        new File(sourceDropPath).mkdirs();
        String sourceFileName = "VIF.ACK";

        File sourceFile = new File(sourceDropPath, sourceFileName);
        if (!sourceFile.exists()) sourceFile.createNewFile();
        File targetFile = new File(jobFolder, sourceFileName);

        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);
        jndiContext.bind("transform." + SERVICE_NAME + ".request", requestTransformer);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        when(jobStore.findJob(anyString())).thenReturn(job);

        CopyFileRequest copyFileRequest = new CopyFileRequest();
        copyFileRequest.setSourceFilename(sourceFile.getAbsolutePath());
        copyFileRequest.setTargetFilename(targetFile.getAbsolutePath());
        when(requestTransformer.transform(any(Job.class))).thenReturn(copyFileRequest);

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        // Skip Store Job Response
        CopyFileRoute copyFileRoute = new CopyFileRoute();
        copyFileRoute.setServiceName(SERVICE_NAME);
        copyFileRoute.setSkipStoreJobRequest(false);
        copyFileRoute.setSkipStoreJobResponse(true);
        camelContext.addRoutes(copyFileRoute);

        RouteDefinition rd = camelContext.getRouteDefinition(SERVICE_NAME+".request");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                replaceFromWith("direct:test-endpoint");
                mockEndpointsAndSkip("direct:lombard-service-copyfile-request");
            }
        });

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody(String.format("{\"jobIdentifier\":\"%s\"}", CORRELATION_ID));
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", CORRELATION_ID);
        Exchange ex = producerTemplate.send(endpoint, exchange);

        assertThat(targetFile.exists(), is(true));

        // Check number of method invocation
        verify(jobStore, times(1)).addRequest(any(Job.class), anyObject(), anyString(), anyString(), anyString());
        verify(jobStore, times(0)).addRequestOnce(any(Job.class), anyObject(), anyString(), anyString(), anyString());
        // It will invoke addResponse when fileCopy success
        verify(jobStore, times(1)).addResponse(any(Job.class), anyObject(), anyString(), anyString());
        
        targetFile.delete();
        sourceFile.delete();
        sourceFile.getParentFile().delete();
        sourceFile.getParentFile().getParentFile().delete();
        sourceFile.getParentFile().getParentFile().getParentFile().delete();
    }
}
