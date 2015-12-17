package com.fujixerox.aus.integration.transform.outclearings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.common.metadata.StateOrdinal;
import com.fujixerox.aus.lombard.common.metadata.StateOrdinals;
import com.fujixerox.aus.lombard.common.metadata.TierOneBanksImageExchange;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.reporting.ExecuteBatchReportRequest;
import com.fujixerox.aus.lombard.reporting.ExecuteReportRequest;
import com.fujixerox.aus.lombard.reporting.Parameter;
import com.fujixerox.aus.lombard.reporting.metadata.FormatType;
import com.fujixerox.aus.lombard.reporting.metadata.ParameterDefinition;
import com.fujixerox.aus.lombard.reporting.metadata.ParameterType;
import com.fujixerox.aus.lombard.reporting.metadata.ReportAgencyBanksDetails;
import com.fujixerox.aus.lombard.reporting.metadata.ReportAgencyBanksMetadata;
import com.fujixerox.aus.lombard.reporting.metadata.ReportDefinition;
import com.fujixerox.aus.lombard.reporting.metadata.ReportGroup;
import com.fujixerox.aus.lombard.reporting.metadata.ReportMetadata;
import com.fujixerox.aus.lombard.reporting.metadata.ReportRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 8/06/15
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteReportTransform extends AbstractOutclearingsTransform implements Transformer<ExecuteBatchReportRequest> {
    private MetadataStore metadataStore;

    @Override
    public ExecuteBatchReportRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        ReportRequest reportRequest = retrieveActivityRequest(job, "report", "prepare");
        String frequency = reportRequest.getFrequency().value();

        ExecuteBatchReportRequest request = new ExecuteBatchReportRequest();
        request.setJobIdentifier(jobIdentifier);
        request.setBatchName(frequency+" Reports");

        ReportMetadata reportMetadata = metadataStore.getMetadata(ReportMetadata.class);

        List<ReportGroup> reportGroupList = new ArrayList<>();
        for (ReportGroup reportGroup : reportMetadata.getReportGroups()) {
            if (reportGroup.getFrequency().value().equalsIgnoreCase(frequency)) {
                reportGroupList.add(reportGroup);
                break;
            }
        }

        for (ReportGroup reportGroup:reportGroupList) {
            for (ReportDefinition reportDefinition : reportGroup.getReports()) {

            	// init parameters
                String ssrsNameBusinessDay = "";
                String ssrsNameStateOrdinal = "";
                String ssrsNameAgencyBank = "";
                String ssrsNameSystemDate = "";

                for (ParameterDefinition param : reportDefinition.getParameterDefinitions()) {
                    if (param.getMetadataName().equals(ParameterType.BUSINESS_DAY)) {
                        ssrsNameBusinessDay = param.getSsrsName();
                        continue;
                    }
                    if (param.getMetadataName().equals(ParameterType.STATE_ORDINAL)) {
                        ssrsNameStateOrdinal = param.getSsrsName();
                        continue;
                    }
                    if (param.getMetadataName().equals(ParameterType.AGENCY_BANK)) {
                    	ssrsNameAgencyBank = param.getSsrsName();
                        continue;
                    }
                    if (param.getMetadataName().equals(ParameterType.SYSTEM_DATE)) {
                        ssrsNameSystemDate = param.getSsrsName();
                        continue;
                    }
                }

                // building report list
                for (ParameterDefinition parameterDefinition: reportDefinition.getParameterDefinitions()) {

                	// BUSINESS_DAY only report definition in parameter definitions
                    if (parameterDefinition.getMetadataName().equals(ParameterType.BUSINESS_DAY) && reportDefinition.getParameterDefinitions().size() == 1) {
                        BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
                        String businessDay = new SimpleDateFormat("yyyy-MM-dd").format(businessCalendar.getBusinessDay());

                        ExecuteReportRequest executeReportRequest = new ExecuteReportRequest();

                        String fileName = String.format(reportDefinition.getOutputFilenamePattern(), businessCalendar.getBusinessDay()) + "." +reportDefinition.getFormatType().value();
                        executeReportRequest.setOutputFilename(fileName);
                        executeReportRequest.setOutputFormatType(reportDefinition.getFormatType());
                        executeReportRequest.setReportName(reportDefinition.getReportName());

                        Parameter businessDayParameter = new Parameter();
                        businessDayParameter.setName(parameterDefinition.getSsrsName());
                        businessDayParameter.setValue(businessDay);
                        executeReportRequest.getParameters().add(businessDayParameter);

                        request.getReports().add(executeReportRequest);

                    }

                    // START_MONTH_DATE contained report definition in parameter definitions
                    if (parameterDefinition.getMetadataName().equals(ParameterType.START_MONTH_DATE)) {
                        BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
                        String businessDay = new SimpleDateFormat("yyyy-MM-dd").format(businessCalendar.getBusinessDay());
                        String startOfMonth = new SimpleDateFormat("yyyy-MM-dd").format(firstDayOfMonth(businessCalendar.getBusinessDay()));

                        ExecuteReportRequest executeReportRequest = new ExecuteReportRequest();

                        String fileName = String.format(reportDefinition.getOutputFilenamePattern(), "", businessCalendar.getBusinessDay()) + "." +reportDefinition.getFormatType().value();
                        executeReportRequest.setOutputFilename(fileName);
                        executeReportRequest.setOutputFormatType(reportDefinition.getFormatType());
                        executeReportRequest.setReportName(reportDefinition.getReportName());

                        Parameter startOfMonthParameter = new Parameter();
                        startOfMonthParameter.setName(parameterDefinition.getSsrsName());
                        startOfMonthParameter.setValue(startOfMonth);
                        executeReportRequest.getParameters().add(startOfMonthParameter);

                        Parameter businessDayParameter = new Parameter();
                        businessDayParameter.setName(ssrsNameBusinessDay);
                        businessDayParameter.setValue(businessDay);
                        executeReportRequest.getParameters().add(businessDayParameter);

                        request.getReports().add(executeReportRequest);

                    }

                    // 20937 Fixing 
                    // For AGENCY_BANK_GROUP
                    if (parameterDefinition.getMetadataName().equals(ParameterType.AGENCY_BANK_GROUP)) { 	// This sould be 3rd parameter definition
                    	ReportAgencyBanksMetadata reportAgencyBanks = metadataStore.getMetadata(ReportAgencyBanksMetadata.class);
                        for (ReportAgencyBanksDetails reportAgencyBankDetail : reportAgencyBanks.getAgencyBanks()) {
   
                        	if (reportAgencyBankDetail.getIncludeReports() != null &&
                        			!reportAgencyBankDetail.getIncludeReports().contains(reportDefinition.getReportName())) {
                        		// if report agency bank dosen't have report name in the list, just skip to generate requests.
                        		continue;
                        	}
                        	
                        	BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
                            String businessDay = new SimpleDateFormat("yyyy-MM-dd").format(businessCalendar.getBusinessDay());

                            ExecuteReportRequest executeReportRequest = new ExecuteReportRequest();

                            // Fixing bug 20937 for outputfilename
                            String fileName = String.format(reportDefinition.getOutputFilenamePattern(), businessCalendar.getBusinessDay(), reportAgencyBankDetail.getAgencyBankName()) + "." +reportDefinition.getFormatType().value();
                            executeReportRequest.setOutputFilename(fileName);
                            executeReportRequest.setOutputFormatType(reportDefinition.getFormatType());
                            executeReportRequest.setReportName(reportDefinition.getReportName());

                            Parameter parameter = new Parameter();
                            parameter.setName(ssrsNameBusinessDay);
                            parameter.setValue(businessDay);
                            executeReportRequest.getParameters().add(parameter);
                            
                            parameter = new Parameter();
                            parameter.setName(ssrsNameAgencyBank);
                            parameter.setValue(reportAgencyBankDetail.getAgencyBankName());
                            executeReportRequest.getParameters().add(parameter);

                            parameter = new Parameter();
                            parameter.setName(parameterDefinition.getSsrsName());
                            parameter.setValue(reportAgencyBankDetail.getAgencyBankGroupCode());
                            executeReportRequest.getParameters().add(parameter);

                            request.getReports().add(executeReportRequest);
                            
                        }
                    }

                    // 22104
                    if (parameterDefinition.getMetadataName().equals(ParameterType.FINANCIAL_INSTITUTION) && reportDefinition.getParameterDefinitions().size() == 3) {
                        TierOneBanksImageExchange tierOneBanksImageExchange = metadataStore.getMetadata(TierOneBanksImageExchange.class);

                        for (String targetEndpoint : tierOneBanksImageExchange.getTargetEndpoints()) {

                            BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
                            String businessDay = new SimpleDateFormat("yyyy-MM-dd").format(businessCalendar.getBusinessDay());
                            Date date = new Date();
                            String systemDate = new SimpleDateFormat("yyyyMMdd.HHmmss").format(date);

                            ExecuteReportRequest executeReportRequest = new ExecuteReportRequest();

                            String fileName = String.format(reportDefinition.getOutputFilenamePattern(), businessCalendar.getBusinessDay(), targetEndpoint, date) + "." +reportDefinition.getFormatType().value();

                            executeReportRequest.setOutputFilename(fileName);
                            executeReportRequest.setOutputFormatType(reportDefinition.getFormatType());
                            executeReportRequest.setReportName(reportDefinition.getReportName());

                            Parameter businessDayParameter = new Parameter();
                            businessDayParameter.setName(ssrsNameBusinessDay);
                            businessDayParameter.setValue(businessDay);
                            executeReportRequest.getParameters().add(businessDayParameter);

                            Parameter financialInstitutionParameter = new Parameter();
                            financialInstitutionParameter.setName(parameterDefinition.getSsrsName());
                            financialInstitutionParameter.setValue(targetEndpoint);
                            executeReportRequest.getParameters().add(financialInstitutionParameter);

                            Parameter systemDateParameter = new Parameter();
                            systemDateParameter.setName(ssrsNameSystemDate);
                            systemDateParameter.setValue(systemDate);
                            executeReportRequest.getParameters().add(systemDateParameter);

                            request.getReports().add(executeReportRequest);
                        }
                    }

                    // STATE contained report definition with 2 parameter definitions (Daily log)
                    if (parameterDefinition.getMetadataName().equals(ParameterType.STATE) && reportDefinition.getParameterDefinitions().size() == 2) {
                        StateOrdinals stateOrdinals = metadataStore.getMetadata(StateOrdinals.class);

                        for (StateOrdinal stateOrdinal : stateOrdinals.getStateOrdinals()) {

                            String state =  stateOrdinal.getState().value();
                            int ordinal = stateOrdinal.getOrdinal();

                            BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
                            String businessDay = new SimpleDateFormat("yyyy-MM-dd").format(businessCalendar.getBusinessDay());

                            ExecuteReportRequest executeReportRequest = new ExecuteReportRequest();


                            String fileName = String.format(reportDefinition.getOutputFilenamePattern(), businessCalendar.getBusinessDay(), state, ordinal);
                            if (reportDefinition.getFormatType() != FormatType.TXT) {  // TODO Verify Business Rule for file EXT // Fixing AIR report name to remove TXT
                                fileName += "." +reportDefinition.getFormatType().value();
                            }

                            executeReportRequest.setOutputFilename(fileName);
                            executeReportRequest.setOutputFormatType(reportDefinition.getFormatType());
                            executeReportRequest.setReportName(reportDefinition.getReportName());

                            Parameter businessDayParameter = new Parameter();
                            businessDayParameter.setName(ssrsNameBusinessDay);
                            businessDayParameter.setValue(businessDay);
                            executeReportRequest.getParameters().add(businessDayParameter);

                            Parameter stateParameter = new Parameter();
                            stateParameter.setName(parameterDefinition.getSsrsName());
                            stateParameter.setValue(state);
                            executeReportRequest.getParameters().add(stateParameter);

                            request.getReports().add(executeReportRequest);
                        }
                    }
                    // STATE contained report definition in parameter definitions
                    else if (parameterDefinition.getMetadataName().equals(ParameterType.STATE)) {
                            StateOrdinals stateOrdinals = metadataStore.getMetadata(StateOrdinals.class);

                            for (StateOrdinal stateOrdinal : stateOrdinals.getStateOrdinals()) {

                                String state =  stateOrdinal.getState().value();
                                int ordinal = stateOrdinal.getOrdinal();

                                BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
                                String businessDay = new SimpleDateFormat("yyyy-MM-dd").format(businessCalendar.getBusinessDay());

                                ExecuteReportRequest executeReportRequest = new ExecuteReportRequest();


                                String fileName = String.format(reportDefinition.getOutputFilenamePattern(), businessCalendar.getBusinessDay(), state, ordinal);
                                if (reportDefinition.getFormatType() != FormatType.TXT) {  // TODO Verify Business Rule for file EXT // Fixing AIR report name to remove TXT
                                    fileName += "." +reportDefinition.getFormatType().value();
                                }

                                executeReportRequest.setOutputFilename(fileName);
                                executeReportRequest.setOutputFormatType(reportDefinition.getFormatType());
                                executeReportRequest.setReportName(reportDefinition.getReportName());

                                Parameter businessDayParameter = new Parameter();
                                businessDayParameter.setName(ssrsNameBusinessDay);
                                businessDayParameter.setValue(businessDay);
                                executeReportRequest.getParameters().add(businessDayParameter);

                                Parameter stateParameter = new Parameter();
                                stateParameter.setName(parameterDefinition.getSsrsName());
                                stateParameter.setValue(state);
                                executeReportRequest.getParameters().add(stateParameter);

                                Parameter parameter = new Parameter();
                                parameter.setName(ssrsNameStateOrdinal);
                                parameter.setValue(Integer.toString(ordinal));
                                executeReportRequest.getParameters().add(parameter);

                                request.getReports().add(executeReportRequest);
                            }

                    }



                    // STATE_ORDINAL contained report definition with 2 parameter definitions
                    if (parameterDefinition.getMetadataName().equals(ParameterType.STATE_ORDINAL) && reportDefinition.getParameterDefinitions().size() == 2) {
                        StateOrdinals stateOrdinals = metadataStore.getMetadata(StateOrdinals.class);

                        for (StateOrdinal stateOrdinal : stateOrdinals.getStateOrdinals()) {
                            if (stateOrdinal.getState().equals(StateEnum.QLD)) {

                                int ordinal = stateOrdinal.getOrdinal();
                                BusinessCalendar businessCalendar = metadataStore.getMetadata(BusinessCalendar.class);
                                String businessDay = new SimpleDateFormat("yyyy-MM-dd").format(businessCalendar.getBusinessDay());

                                ExecuteReportRequest executeReportRequest = new ExecuteReportRequest();
                                String fileName = String.format(reportDefinition.getOutputFilenamePattern(), businessCalendar.getBusinessDay(), "", ordinal); // Fixing AIR report name to remove TXT
                                
                                executeReportRequest.setOutputFilename(fileName);
                                executeReportRequest.setOutputFormatType(reportDefinition.getFormatType());
                                executeReportRequest.setReportName(reportDefinition.getReportName());

                                Parameter businessDayParameter = new Parameter();
                                businessDayParameter.setName(ssrsNameBusinessDay);
                                businessDayParameter.setValue(businessDay);
                                executeReportRequest.getParameters().add(businessDayParameter);

                                Parameter parameter = new Parameter();
                                parameter.setName(ssrsNameStateOrdinal);
                                parameter.setValue(Integer.toString(ordinal));
                                executeReportRequest.getParameters().add(parameter);

                                request.getReports().add(executeReportRequest);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return request;
    }

    private static Date firstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
}
