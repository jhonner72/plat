package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.createvalueinstructionfile.CreateValueInstructionFileRequest;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

/**
 * User: Eloisa.Redubla
 * Date: 16/04/15
 */
public class CreateBatchValueInstructionFileTransformTest {

    private static final String JOB_IDENTIFIER = "20141231_000111222";
    private static final String ENDPOINT = "NSW";
    private static final String CAPTURE_BSB = "111";
    private static final String COLLECTING_BSB = "111";
    private static final int MAX_QUERY_SIZE = 10;
    private static final int SEQUENCE_NUMBER = 8001;
    private static final String FINANCIAL_INSTITUTION = "BQL";
    private static final StateEnum STATE = StateEnum.NSW;

    private static final String ISO_DATE = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    private static final String EXPECTED_DATE = "2014-12-31T23:59:59.999+1100";

    @Test
    public void shouldTransformIntoCreateBatchValueInstructionFileRequest() throws ParseException {
        MetadataStore metadataStore = mock(MetadataStore.class);

        StateOrdinals stateOrdinals = new StateOrdinals();
        StateOrdinal nswOrdinal = new StateOrdinal();
        nswOrdinal.setState(StateEnum.NSW);
        nswOrdinal.setOrdinal(2);
        stateOrdinals.getStateOrdinals().add(nswOrdinal);
        Mockito.when(metadataStore.getMetadata(StateOrdinals.class)).thenReturn(stateOrdinals);

        ValueInstructionFile valueInstructionFile = new ValueInstructionFile();
        valueInstructionFile.getRecordTypeCodes();
        Mockito.when(metadataStore.getMetadata(ValueInstructionFile.class)).thenReturn(valueInstructionFile);

        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setBusinessDay(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));
        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);

        CreateBatchValueInstructionFileTransform target = new CreateBatchValueInstructionFileTransform();
        target.setMetadataStore(metadataStore);

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockStartOfVifProcessActivity());

        CreateValueInstructionFileRequest request = target.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getSequenceNumber(), is(SEQUENCE_NUMBER));
        assertThat(request.getState(), is(2));
        assertThat(request.getBusinessDate(), is(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE)));
        assertThat(request.getCaptureBsb(), is(CAPTURE_BSB));
        assertThat(request.getCollectingBsb(), is(COLLECTING_BSB));
        assertThat(request.getEntity(), is(FINANCIAL_INSTITUTION));
        assertThat(request.getRecordTypeCodes().size(), is(0));

    }

    protected Activity mockStartOfVifProcessActivity() throws ParseException {
        Activity activity = new Activity();
        activity.setPredicate("valueinstructionfile");
        activity.setSubject("outclearings");
        ValueInstructionFileRequest valueInstructionFileRequest = new ValueInstructionFileRequest();
        ValueInstructionFileTarget valueInstructionFileTarget = new ValueInstructionFileTarget();
        valueInstructionFileTarget.setCaptureBsb(CAPTURE_BSB);
        valueInstructionFileTarget.setCollectingBsb(COLLECTING_BSB);
        valueInstructionFileTarget.setState(STATE);
        valueInstructionFileTarget.setFinancialInstitution(FINANCIAL_INSTITUTION);

        valueInstructionFileRequest.setEndpoint(ENDPOINT);
        valueInstructionFileRequest.setMaxQuerySize(MAX_QUERY_SIZE);
        valueInstructionFileRequest.setSequenceNumber(SEQUENCE_NUMBER);
        valueInstructionFileRequest.setTarget(valueInstructionFileTarget);

        activity.setRequest(valueInstructionFileRequest);
        return activity;
    }
}
