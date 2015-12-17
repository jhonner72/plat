package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.reporting.metadata.*;
import java.text.ParseException;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 16/06/15
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteReportResponseTransformTest {
    private static final String ISO_DATE = "yyyyMMdd";
    private static final String EXPECTED_DATE = "20150616";

    @Test
    public void storeReportsShouldBeTrue() throws Exception {
        ReportDefinition reportDefinition1 = new ReportDefinition();

        DeliveryEndpoint deliveryEndpointDocumentum = new DeliveryEndpoint();
        deliveryEndpointDocumentum.setEndpointType(DeliveryEndpointType.DOCUMENTUM);
        reportDefinition1.getDeliveryEndpoints().add(deliveryEndpointDocumentum);

        execute(reportDefinition1, true, false, false);
    }

    @Test
    public void deliverEmailShouldTrue() throws Exception {
        ReportDefinition reportDefinition2 = new ReportDefinition();

        DeliveryEndpoint deliveryEndpointEmail = new DeliveryEndpoint();
        deliveryEndpointEmail.setEndpointType(DeliveryEndpointType.EMAIL);
        reportDefinition2.getDeliveryEndpoints().add(deliveryEndpointEmail);

        execute(reportDefinition2, true, false, false);
    }

    @Test
    public void deliverB2BShouldTrue() throws Exception {
        ReportDefinition reportDefinition = new ReportDefinition();

        DeliveryEndpoint deliveryEndpoint = new DeliveryEndpoint();
        deliveryEndpoint.setEndpointType(DeliveryEndpointType.B_2_B);
        reportDefinition.getDeliveryEndpoints().add(deliveryEndpoint);

        execute(reportDefinition, true, false, false);
    }

    @Test
    public void allVariablesShouldBeFalse() throws Exception {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.getDeliveryEndpoints();
        execute(reportDefinition, true, false, false);
    }

    protected void execute(ReportDefinition reportDefnition, boolean store, boolean email, boolean b2b) throws ParseException{
        Job job = new Job();

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setFrequency(ReportFrequencyEnum.END_OF_DAY);
        reportRequest.setBusinessDay(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));

        Activity activity = new Activity();
        activity.setSubject("report");
        activity.setPredicate("prepare");
        activity.setRequest(reportRequest);
        job.getActivities().add(activity);

        ReportMetadata reportMetadata = new ReportMetadata();

        ReportGroup reportGroup = new ReportGroup();
        reportGroup.setFrequency(ReportFrequencyEnum.END_OF_DAY);
        reportGroup.getReports().add(reportDefnition);

        reportMetadata.getReportGroups().add(reportGroup);

        MetadataStore metadataStore = mock(MetadataStore.class);
        Mockito.when(metadataStore.getMetadata(ReportMetadata.class)).thenReturn(reportMetadata);

        ExecuteReportResponseTransform target = new ExecuteReportResponseTransform();
        target.setMetadataStore(metadataStore);

        Map<String, Object> values = target.transform(job);

        Boolean storeReports = (Boolean) values.get("storeReports");
        assertThat(storeReports, is(notNullValue()));
        assertThat(storeReports, is(store));

        Boolean deliverEmail = (Boolean) values.get("deliverEmail");
        assertThat(deliverEmail, is(notNullValue()));
        assertThat(deliverEmail, is(email));

        Boolean deliverB2B = (Boolean) values.get("deliverB2B");
        assertThat(deliverB2B, is(notNullValue()));
        assertThat(deliverB2B, is(b2b));
    }
}
