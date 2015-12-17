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

/**
 * Created by vidyavenugopal on 6/07/15.
 */
public class UnpackageListingRouteTest {

    @Test
    public void shouldUnpackageFromFile() throws Exception {
        String guid = UUID.randomUUID().toString();
        File bitLocker = new File("target");
        File jobFolder = new File(bitLocker, guid);
        jobFolder.mkdirs();

        new File(jobFolder, "LISTING_06072015_A83000013_1.JPG").createNewFile();
        new File(jobFolder, "LISTING_06072015_A83000013_2.JPG").createNewFile();
        new File(jobFolder, "LISTING_06072015_A83000014_3.JPG").createNewFile();
        new File(jobFolder, "LISTING_06072015_A83000014_4.JPG").createNewFile();
        Files.copy(new File("src/test/resources/data/listingmetadata.xml").toPath(), new File(jobFolder, "OUTCLEARINGSPKG_06072015_NSBI68200068.xml").toPath());

        DefaultCamelContext camelContext = new DefaultCamelContext();

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        UnpackageListingRoute unpackageListingRoute = new UnpackageListingRoute();
        unpackageListingRoute.setOptions("");
        unpackageListingRoute.setHostname("localhost");
        unpackageListingRoute.setPort("9576");
        unpackageListingRoute.setFilter("(?i).*?\\.jpg|.*?\\.tif");

        unpackageListingRoute.setLockerPath(bitLocker.getAbsolutePath());

        camelContext.addRoutes(unpackageListingRoute);

        RouteDefinition rd = camelContext.getRouteDefinition("lombard-service-outclearings-unpackagelisting-service");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                mockEndpointsAndSkip("rabbitmq://localhost:9576/lombard.service.outclearings.unpackagelisting.response");
                replaceFromWith("direct:test-endpoint");
            }
        });

        MockEndpoint mockResponseQueue = MockEndpoint.resolve(camelContext, "mock:rabbitmq:localhost:9576/lombard.service.outclearings.unpackagelisting.response");

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody(String.format("{\"jobIdentifier\":\"%s\"}", guid));
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", "aaa-bbb-ccc");
        producerTemplate.send(endpoint, exchange);

        List<Exchange> exchanges = mockResponseQueue.getExchanges();
        assertThat(exchanges.size(), is(1));

        String out = exchanges.get(0).getIn().getBody(String.class);

        String expected = "{\"scannedListing\":{\"listingProcessingDate\":\"2015-07-07\",\"batchNumber\":\"68200027\",\"workType\":\"NABCHQ_LISTINGS\",\"batchType\":\"OTC_Listings\",\"operator\":\"a\",\"unitId\":\"083\",\"captureBsb\":\"083029\",\"collectingBsb\":\"083054\",\"transactionCode\":\"22\",\"auxDom\":\"6590\",\"extraAuxDom\":\"\",\"accountNumber\":\"0000000090\",\"documentReferenceNumber\":\"A83000018\",\"listingPages\":[{\"documentReferenceNumber\":\"A83000019\",\"inactiveFlag\":false}]}}";
        assertThat(out, is(expected));

        assertThat(exchanges.get(0).getIn().getHeader("rabbitmq.CORRELATIONID", String.class), is("aaa-bbb-ccc"));
    }
}
