package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.outclearings.createimageexchangefile.CreateImageExchangeFileRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Eloisa.Redubla
 * Date: 16/04/15
 */
public class CreateBatchImageExchangeFileTransformTest {

    private static final String JOB_IDENTIFIER_TIERONEBANK = "NIEO-20141231_000111222";
    private static final String JOB_IDENTIFIER_AGENCYBANK = "NIET-20141231_000111222";
    private static final int NEXT_SEQUENCE_NUMBER = 100;
    private static final int AGENCYBANK_SEQUENCE_NUMBER = 1;

    @Test
    public void shouldTransform_TierOneBank_CreateBatchImageExchangeFileRequest() throws ParseException {
        CreateBatchImageExchangeFileTransform target = new CreateBatchImageExchangeFileTransform();

        SequenceNumberGenerator sequenceNumberGenerator = mock(SequenceNumberGenerator.class);
        when(sequenceNumberGenerator.nextSequenceNumber(TierOneBanksImageExchange.class)).thenReturn(NEXT_SEQUENCE_NUMBER);
        when(sequenceNumberGenerator.nextSequenceNumber(AgencyBanksImageExchange.class)).thenReturn(AGENCYBANK_SEQUENCE_NUMBER);
        target.setSequenceNumberGenerator(sequenceNumberGenerator);

        MetadataStore metadataStore = mock(MetadataStore.class);
        BusinessCalendar businessCalendar = new BusinessCalendar();
        Date businessDay = (new SimpleDateFormat("yyyy-MM-dd")).parse("2015-02-20");
        businessCalendar.setBusinessDay(businessDay);
        when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.setMaxQuerySize(1);
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");
        when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);
        
        target.setMetadataStore(metadataStore);

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER_TIERONEBANK);
        job.getActivities().add(mockGetVouchersForTierOneBankImageExchangeActivity());

        CreateImageExchangeFileRequest request = target.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER_TIERONEBANK));
        assertThat(request.getSequenceNumber(), is(NEXT_SEQUENCE_NUMBER));
        assertThat(request.getBusinessDate(), is(businessDay));
        assertThat(request.getTargetEndPoint(), is("ANZ"));
        assertThat(request.getFileType(), is(ImageExchangeType.IMAGE_EXCHANGE));
    }
    
    @Test
    public void shouldTransform_AgencyBankOrCuscal_CreateBatchImageExchangeFileRequest() throws ParseException {
        CreateBatchImageExchangeFileTransform target = new CreateBatchImageExchangeFileTransform();

        SequenceNumberGenerator sequenceNumberGenerator = mock(SequenceNumberGenerator.class);
        when(sequenceNumberGenerator.nextSequenceNumber(TierOneBanksImageExchange.class)).thenReturn(NEXT_SEQUENCE_NUMBER);
        when(sequenceNumberGenerator.nextSequenceNumber(AgencyBanksImageExchange.class)).thenReturn(AGENCYBANK_SEQUENCE_NUMBER);
        target.setSequenceNumberGenerator(sequenceNumberGenerator);

        MetadataStore metadataStore = mock(MetadataStore.class);
        BusinessCalendar businessCalendar = new BusinessCalendar();
        Date businessDay = (new SimpleDateFormat("yyyy-MM-dd")).parse("2015-02-20");
        businessCalendar.setBusinessDay(businessDay);
        when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("000");
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFileType(ImageExchangeType.AGENCY_XML);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);
        
        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.setMaxQuerySize(1);
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");
        when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);

        target.setMetadataStore(metadataStore);

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER_AGENCYBANK);
        job.getActivities().add(mockGetVouchersForAgencyBankImageExchangeActivity());

        CreateImageExchangeFileRequest request = target.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER_AGENCYBANK));
        assertThat(request.getSequenceNumber(), is(AGENCYBANK_SEQUENCE_NUMBER));
        assertThat(request.getBusinessDate(), is(businessDay));
        assertThat(request.getTargetEndPoint(), is("MQG"));
        assertThat(request.getFileType(), is(ImageExchangeType.AGENCY_XML));
        assertThat(request.getFourCharactersEndPoint(), is("MACQ"));
    }

    protected Activity mockGetVouchersForTierOneBankImageExchangeActivity() throws ParseException {
        Activity getVouchersForImageExchangeActivity = new Activity();
        getVouchersForImageExchangeActivity.setPredicate("get");
        getVouchersForImageExchangeActivity.setSubject("vouchers");
        GetVouchersResponse response = new GetVouchersResponse();

        response.setVoucherCount(10);
        response.setTargetEndPoint("ANZ");
        getVouchersForImageExchangeActivity.setResponse(response);
        return getVouchersForImageExchangeActivity;
    }
    
    protected Activity mockGetVouchersForAgencyBankImageExchangeActivity() throws ParseException {
        Activity getVouchersForImageExchangeActivity = new Activity();
        getVouchersForImageExchangeActivity.setPredicate("get");
        getVouchersForImageExchangeActivity.setSubject("vouchers");
        GetVouchersResponse response = new GetVouchersResponse();

        response.setVoucherCount(10);
        response.setTargetEndPoint("MQG");
        getVouchersForImageExchangeActivity.setResponse(response);
        return getVouchersForImageExchangeActivity;
    }
}
