package com.fujixerox.aus.inclearings;

import com.fujixerox.aus.integration.store.ForValueSeedQuery;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.JaxbMapperFactory;
import com.fujixerox.aus.lombard.common.metadata.AgencyBankDetails;
import com.fujixerox.aus.lombard.common.metadata.AgencyBanksImageExchange;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 22/04/15
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnpackImageExchangeBatchRouteTest {
    private static final String ACCOUNT_NUMBER = "291895392";
    private static final String BSB_NUMBER = "013133";
    private static final DocumentTypeEnum DOCUMENT_TYPE = DocumentTypeEnum.DR;

    @Test
    public void shouldUnpackImageExchangeFile() throws Exception {
        DefaultCamelContext camelContext = new DefaultCamelContext();

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        ForValueSeedQuery forValueSeedStore = mock(ForValueSeedQuery.class);
        MetadataStore metadataStore = mock(MetadataStore.class);

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();
        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);

        UnpackImageExchangeBatchRoute unpackImageExchangeBatchRoute = new UnpackImageExchangeBatchRoute();
        unpackImageExchangeBatchRoute.setOptions("");
        unpackImageExchangeBatchRoute.setHostname("localhost");
        unpackImageExchangeBatchRoute.setPort("9576");
        unpackImageExchangeBatchRoute.setForValueSeedStore(forValueSeedStore);
        unpackImageExchangeBatchRoute.setObjectMapper(JaxbMapperFactory.createWithoutAnnotations());
        unpackImageExchangeBatchRoute.setMetadataStore(metadataStore);

        when(forValueSeedStore.findForValueEntry(BSB_NUMBER, ACCOUNT_NUMBER, DOCUMENT_TYPE)).thenReturn(true);

        File file = new File("src/test/resources/data");
        unpackImageExchangeBatchRoute.setLockerPath(file.getAbsolutePath());

        //Clean the directory from image files
        File ieFileDir = new File(file.getAbsolutePath(), "22042015-3EEB-4069-A2DD-SSS987654321");
        for (File testFile : ieFileDir.listFiles()) {
            if (!testFile.getName().endsWith("xml")) {
                testFile.delete();
            }
        }
        assertThat(ieFileDir.listFiles().length, is(1));

        camelContext.addRoutes(unpackImageExchangeBatchRoute);

        RouteDefinition rd = camelContext.getRouteDefinition("lombard-service-inclearings-unpackimageexchangebatch-service");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                mockEndpointsAndSkip("rabbitmq://localhost:9576/lombard.service.inclearings.unpackimageexchangebatch.response");
                replaceFromWith("direct:test-endpoint");
            }
        });

        MockEndpoint mockResponseQueue = MockEndpoint.resolve(camelContext, "mock:rabbitmq:localhost:9576/lombard.service.inclearings.unpackimageexchangebatch.response");

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody("{\"jobIdentifier\":\"22042015-3EEB-4069-A2DD-SSS987654321\"}");
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", "aaa-bbb-ccc");
        producerTemplate.send(endpoint, exchange);

        List<Exchange> exchanges = mockResponseQueue.getExchanges();
        assertThat(exchanges.size(), is(1));

        String out = exchanges.get(0).getIn().getBody(String.class);

        String expected = "{}";
        assertThat(out, is(expected));

        assertThat(exchanges.get(0).getIn().getHeader("rabbitmq.CORRELATIONID", String.class), is("aaa-bbb-ccc"));

        assertThat(ieFileDir.listFiles().length, is(4));

        for (File testFile : ieFileDir.listFiles()) {
            if (!testFile.getName().endsWith("xml")) {
                testFile.delete();
            }
        }
    }

}
