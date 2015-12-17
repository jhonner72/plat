package com.fujixerox.aus.outclearings;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UnpackageBatchVoucherRouteTest {

    @Test
    public void shouldUnpackageFromFile() throws Exception {
        String guid = UUID.randomUUID().toString();
        File bitLocker = new File("target");
        File jobFolder = new File(bitLocker, guid);
        jobFolder.mkdirs();

        new File(jobFolder, "VOUCHER_22042015_000083400_FRONT.jpg").createNewFile();
        new File(jobFolder, "VOUCHER_22042015_000083400_FRONT.TIF").createNewFile();
        new File(jobFolder, "VOUCHER_22042015_000083400_FRONT.xml").createNewFile();
        Files.copy(new File("src/test/resources/data/metadata.xml").toPath(), new File(jobFolder, "OUTCLEARINGSPKG_20150623_111222333.XML").toPath());

        DefaultCamelContext camelContext = new DefaultCamelContext();

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        UnpackageBatchVoucherRoute unpackageBatchVoucherRoute = new UnpackageBatchVoucherRoute();
        unpackageBatchVoucherRoute.setOptions("");
        unpackageBatchVoucherRoute.setHostname("localhost");
        unpackageBatchVoucherRoute.setPort("9576");
        unpackageBatchVoucherRoute.setFilter("(?i).*?\\.jpg|.*?\\.tif");

        unpackageBatchVoucherRoute.setLockerPath(bitLocker.getAbsolutePath());

        camelContext.addRoutes(unpackageBatchVoucherRoute);

        RouteDefinition rd = camelContext.getRouteDefinition("lombard-service-outclearings-unpackagevoucher-service");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                mockEndpointsAndSkip("rabbitmq://localhost:9576/lombard.service.outclearings.unpackagevoucher.response");
                replaceFromWith("direct:test-endpoint");
            }
        });

        MockEndpoint mockResponseQueue = MockEndpoint.resolve(camelContext, "mock:rabbitmq:localhost:9576/lombard.service.outclearings.unpackagevoucher.response");

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody(String.format("{\"jobIdentifier\":\"%s\", \"receivedFile\": {\"fileIdentifier\":\"OUTCLEARINGSPKG_20150623_111222333.XML\"}}", guid));
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", "aaa-bbb-ccc");
        producerTemplate.send(endpoint, exchange);

        List<Exchange> exchanges = mockResponseQueue.getExchanges();
        assertThat(exchanges.size(), is(1));

        String out = exchanges.get(0).getIn().getBody(String.class);

        String expected = "{\"batch\":{\"client\":\"NabChqEdit\",\"processingDate\":\"2015-03-12\",\"batchNumber\":\"batchNumber\",\"batchType\":\"batchType\",\"subBatchType\":\"subBatchType\",\"operator\":\"operator\",\"unitID\":\"unitId\",\"processingState\":\"VIC\",\"collectingBank\":\"collectingBank\",\"workType\":\"BQL_POD\",\"voucher\":[{\"rawOCR\":\"rawOCR\",\"rawMICR\":\"rawMICR\",\"micrFlag\":true,\"micrUnprocessableFlag\":true,\"traceID\":\"traceID\",\"processingDate\":\"2015-03-12\",\"documentType\":\"Cr\",\"transactionCode\":\"transactionCode\",\"documentReferenceNumber\":\"documentReferenceNumber\",\"bsbNumber\":\"bsbNumber\",\"auxDom\":\"auxDom\",\"extraAuxDom\":\"extraAuxDom\",\"amount\":\"amount\",\"accountNumber\":\"accountNumber\",\"inactiveFlag\":false}],\"source\":\"source\",\"captureBsb\":\"000222\"}}";
        assertThat(out, is(expected));

        assertThat(exchanges.get(0).getIn().getHeader("rabbitmq.CORRELATIONID", String.class), is("aaa-bbb-ccc"));
    }
}