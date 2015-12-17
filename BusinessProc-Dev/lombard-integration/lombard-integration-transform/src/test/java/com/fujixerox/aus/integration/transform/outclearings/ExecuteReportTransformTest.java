package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.*;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.reporting.ExecuteBatchReportRequest;
import com.fujixerox.aus.lombard.reporting.metadata.*;

import org.junit.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 16/06/15
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteReportTransformTest {

    private static final String PARENT_JOB_IDENTIFIER = "11-22-33";
    private static final String JOB_IDENTIFIER = "aa-bb-cc";
    private static final String ISO_DATE = "yyyy-MM-dd";
    private static final String EXPECTED_DATE = "2015-06-16";

    @Test
    public void shouldGenerateAllReports() throws ParseException {
        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.setInitiatingJobIdentifier(PARENT_JOB_IDENTIFIER);
        job.getActivities().add(mockCreateReportJobRActivity());

        MetadataStore metadataStore = mock(MetadataStore.class);

        AgencyBanksImageExchange agencyBanks = new AgencyBanksImageExchange();

        AgencyBankDetails agencyBankDetails = new AgencyBankDetails();
        agencyBankDetails.setTargetEndpoint("MQG");
        agencyBankDetails.setFourCharactersEndpointName("MACQ");
        agencyBankDetails.getBsbs().add("18");
        agencyBankDetails.setFileType(ImageExchangeType.AGENCY_XML);
        agencyBankDetails.setIncludeCredit(false);
        agencyBanks.getAgencyBanks().add(agencyBankDetails);

        AgencyBankDetails agencyBankDetails2 = new AgencyBankDetails();
        agencyBankDetails2.setTargetEndpoint("CIT");
        agencyBankDetails2.setFourCharactersEndpointName("CITI");
        agencyBankDetails2.getBsbs().add("24");
        agencyBankDetails2.setFileType(ImageExchangeType.AGENCY_XML);
        agencyBankDetails2.setIncludeCredit(true);
        agencyBanks.getAgencyBanks().add(agencyBankDetails2);

        Mockito.when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanks);

        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("RBA");

        Mockito.when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);

        // Mock data for Report Agency Banks 20150807000001.json
        ReportAgencyBanksMetadata reportAgencyBancksMock = new ReportAgencyBanksMetadata();
        ReportAgencyBanksDetails reportAgencyBanksDetails = new ReportAgencyBanksDetails();
        reportAgencyBanksDetails.setAgencyBankGroupCode("ARA");
        reportAgencyBanksDetails.setAgencyBankName("Arab Bank Australia Ltd");
        reportAgencyBanksDetails.getIncludeReports().add("Agency_Surplus_Items_Report");
        reportAgencyBanksDetails.getIncludeReports().add("Agency_Bank_Adjustments_Letter_Report");
        reportAgencyBancksMock.getAgencyBanks().add(reportAgencyBanksDetails);
        
        reportAgencyBanksDetails = new ReportAgencyBanksDetails();
        reportAgencyBanksDetails.setAgencyBankGroupCode("ADC");
        reportAgencyBanksDetails.setAgencyBankName("Australian Defence Credit Union");
        reportAgencyBanksDetails.getIncludeReports().add("Agency_Surplus_Items_Report");
        reportAgencyBanksDetails.getIncludeReports().add("Agency_Bank_Adjustments_Letter_Report");
        reportAgencyBancksMock.getAgencyBanks().add(reportAgencyBanksDetails);
        
        reportAgencyBanksDetails = new ReportAgencyBanksDetails();
        reportAgencyBanksDetails.setAgencyBankGroupCode("BQL");
        reportAgencyBanksDetails.setAgencyBankName("Bank of Queensland Ltd");
        reportAgencyBanksDetails.getIncludeReports().add("Agency_Bank_Adjustments_Letter_Report");	// This report should be skipped cause of not having Agency_Surplus_Items_Report
        reportAgencyBancksMock.getAgencyBanks().add(reportAgencyBanksDetails);
        
        Mockito.when(metadataStore.getMetadata(ReportAgencyBanksMetadata.class)).thenReturn(reportAgencyBancksMock);
        
        StateOrdinals stateOrdinals = new StateOrdinals();
        StateOrdinal nswOrdinal = new StateOrdinal();
        nswOrdinal.setState(StateEnum.NSW);
        nswOrdinal.setOrdinal(2);
        stateOrdinals.getStateOrdinals().add(nswOrdinal);

        StateOrdinal nswOrdinalVic = new StateOrdinal();
        nswOrdinalVic.setState(StateEnum.VIC);
        nswOrdinalVic.setOrdinal(3);
        stateOrdinals.getStateOrdinals().add(nswOrdinalVic);

        StateOrdinal nswOrdinalQld = new StateOrdinal();
        nswOrdinalQld.setState(StateEnum.QLD);
        nswOrdinalQld.setOrdinal(4);
        stateOrdinals.getStateOrdinals().add(nswOrdinalQld);

        Mockito.when(metadataStore.getMetadata(StateOrdinals.class)).thenReturn(stateOrdinals);

        ReportMetadata reportMetadata = new ReportMetadata();

        ReportGroup reportGroup = new ReportGroup();
        reportGroup.setFrequency(ReportFrequencyEnum.END_OF_DAY);

        ParameterDefinition param1 = new ParameterDefinition();
        param1.setMetadataName(ParameterType.BUSINESS_DAY);
        param1.setSsrsName("processdate");

        ParameterDefinition param2 = new ParameterDefinition();
        param2.setMetadataName(ParameterType.AGENCY_BANK);
        param2.setSsrsName("agencybank");
        
        ParameterDefinition param2_1 = new ParameterDefinition();
        param2_1.setMetadataName(ParameterType.AGENCY_BANK_GROUP);
        param2_1.setSsrsName("agencybankgroup");

        ParameterDefinition param3 = new ParameterDefinition();
        param3.setMetadataName(ParameterType.STATE);
        param3.setSsrsName("state");

        ParameterDefinition param4 = new ParameterDefinition();
        param4.setMetadataName(ParameterType.STATE_ORDINAL);
        param4.setSsrsName("stateordinal");

        ParameterDefinition param5 = new ParameterDefinition();
        param5.setMetadataName(ParameterType.START_MONTH_DATE);
        param5.setSsrsName("startdate");

        ParameterDefinition param6 = new ParameterDefinition();
        param6.setMetadataName(ParameterType.BUSINESS_DAY);
        param6.setSsrsName("enddate");

        ParameterDefinition param7 = new ParameterDefinition();
        param7.setMetadataName(ParameterType.FINANCIAL_INSTITUTION);
        param7.setSsrsName("financialinstitution");

        ParameterDefinition param8 = new ParameterDefinition();
        param8.setMetadataName(ParameterType.SYSTEM_DATE);
        param8.setSsrsName("sysdate");

        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setFormatType(FormatType.CSV);
        reportDefinition.setOutputFilenamePattern("PROD.FXA.BQL.RPT.AIR.AIR.%1$tY%1$tm%1$td.%3$s");
        reportDefinition.setReportName("BQL_All_Items_Report");
        reportDefinition.getParameterDefinitions().add(param1);
        reportDefinition.getParameterDefinitions().add(param4);

        reportGroup.getReports().add(reportDefinition);

        ReportDefinition reportDefinition2 = new ReportDefinition();
        reportDefinition2.setFormatType(FormatType.CSV);
        reportDefinition2.setOutputFilenamePattern("Over $100k Report High Dollar NAB %1$tY%1$tm%1$td");
        reportDefinition2.setReportName("NAB_Over_100K_Report");
        reportDefinition2.getParameterDefinitions().add(param1);

        reportGroup.getReports().add(reportDefinition2);

        ReportDefinition reportDefinition3 = new ReportDefinition();
        reportDefinition3.setFormatType(FormatType.CSV);
        reportDefinition3.setOutputFilenamePattern("%2$s Surplus Items Report %1$tY%1$tm%1$td");
        reportDefinition3.setReportName("Agency_Surplus_Items_Report");
        reportDefinition3.getParameterDefinitions().add(param1);
        reportDefinition3.getParameterDefinitions().add(param2);
        reportDefinition3.getParameterDefinitions().add(param2_1);
        
        reportGroup.getReports().add(reportDefinition3);

        ReportDefinition reportDefinition4 = new ReportDefinition();
        reportDefinition4.setFormatType(FormatType.CSV);
        reportDefinition4.setOutputFilenamePattern("PROD.FXA.NAB.RPT.AIR.AIR.%1$tY%1$tm%1$td.%3$s");
        reportDefinition4.setReportName("NAB_All_Items_Report");
        reportDefinition4.getParameterDefinitions().add(param1);
        reportDefinition4.getParameterDefinitions().add(param3);
        reportDefinition4.getParameterDefinitions().add(param4);

        reportGroup.getReports().add(reportDefinition4);

        ReportDefinition reportDefinition5 = new ReportDefinition();
        reportDefinition5.setFormatType(FormatType.DAT);
        reportDefinition5.setOutputFilenamePattern("IMGEXCHEOD.%1$tY%1$tm%1$td.%3$tY%3$tm%3$td.%3$tH%3$tM%3$tS.FXA.%2$s");
        reportDefinition5.setReportName("NAB_EOD_COIN");
        reportDefinition5.getParameterDefinitions().add(param1);
        reportDefinition5.getParameterDefinitions().add(param7);
        reportDefinition5.getParameterDefinitions().add(param8);

        reportGroup.getReports().add(reportDefinition5);

        ReportDefinition reportDefinition6 = new ReportDefinition();
        reportDefinition6.setFormatType(FormatType.CSV);
        reportDefinition6.setOutputFilenamePattern("NAB LockedBox Monthly Report %2$tY%2$tm%2$td");
        reportDefinition6.setReportName("NAB_LockedBox_MONTHLY_Processing_Details");
        reportDefinition6.getParameterDefinitions().add(param5);
        reportDefinition6.getParameterDefinitions().add(param6);

        reportGroup.getReports().add(reportDefinition6);

        reportMetadata.getReportGroups().add(reportGroup);

        Mockito.when(metadataStore.getMetadata(ReportMetadata.class)).thenReturn(reportMetadata);

        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setBusinessDay(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));

        Mockito.when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);

        ExecuteReportTransform target = new ExecuteReportTransform();
        target.setMetadataStore(metadataStore);

        ExecuteBatchReportRequest request = target.transform(job);
        assertThat(request.getBatchName(), is(ReportFrequencyEnum.END_OF_DAY.value()+" Reports"));
        assertThat(request.getReports().size(), is(11));
    }

    private Activity mockCreateReportJobRActivity() throws ParseException{
        Activity activity = new Activity();
        activity.setSubject("report");
        activity.setPredicate("prepare");
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setFrequency(ReportFrequencyEnum.END_OF_DAY);
        reportRequest.setBusinessDay(new SimpleDateFormat(ISO_DATE).parse(EXPECTED_DATE));

        activity.setRequest(reportRequest);

        return activity;
    }
}
