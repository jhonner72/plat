package com.fujixerox.aus.outclearings;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fujixerox.aus.integration.service.ErrorHandlingProcessor;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersResponse;

/**
 * User: Henry Niu
 * Date: 17/07/15
 */
public class SendAdjustmentLetterRoute extends RouteBuilder {
	
	private static final String ZIP = "ZIP";
	
	private String lockerPath;
	private String fileDropPath;
    private String serviceName;
    private String subject;
    private String predicate;
    
    Log log = LogFactory.getLog(SendAdjustmentLetterRoute.class);

    @Override
    public void configure() throws Exception {
    	
        fromF("direct:%s-request", serviceName.replaceAll("\\.", "-")).
        routeId(serviceName).
        log("Service Start: Send Adjustment Letter").
        onException(Exception.class).
	        log("Exception Start").
	        useOriginalMessage().
	        process(new ErrorHandlingProcessor("integration")).
	        handled(true).
	        log("Exception End").
	    end().
        beanRef("jobStore", "findJob(${property.CamundaBpmBusinessKey})").
        setProperty("initiatingJobIdentifier").spel("#{body.initiatingJobIdentifier}").        
        setProperty("jobIdentifier").spel("#{body.jobIdentifier}").        
        setProperty("job").spel("#{body}").
        beanRef(String.format("transform.%s.request", serviceName), "transform(${property.job})").
        beanRef("jobStore", String.format("addRequest(${property.job}, ${body}, %s, %s)", subject, predicate)).
        //marshal().json(JsonLibrary.Jackson).
        process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {                    	
            	try {
            		String initJobIdentifier = exchange.getProperty("initiatingJobIdentifier", String.class);
            		String jobIdentifier = exchange.getProperty("jobIdentifier", String.class);

            		final CreateBatchAdjustmentLettersResponse response = exchange.getIn().getBody(CreateBatchAdjustmentLettersResponse.class);
            	    
                	File sourceFile = new File(lockerPath, initJobIdentifier + "/" + jobIdentifier + "/" + response.getZipFilename());

                	if (!sourceFile.exists()) {
                		log.debug("No AdjustmentLetter ZIP file exist..");
                		return;
                	}
                	
                	log.debug("lockerPath = " + lockerPath);
                	log.debug("fileDropPath = " + fileDropPath);

                	File fileDropPathFile = new File(fileDropPath, "Outbound/AdjustmentLetters");
                	if (!fileDropPathFile.exists()) {
                    	log.debug("Creating folder " + fileDropPathFile.getAbsolutePath());
                		fileDropPathFile.mkdirs();
                	}
                    	
                	log.debug("Copying file " + sourceFile.getAbsolutePath() + " to " + fileDropPathFile.getAbsolutePath());
            		FileUtils.copyFileToDirectory(sourceFile, fileDropPathFile);

            	} catch(Exception ex) {
                	log.error("Error in SendAdjustmentLetterRoute: ");
                	ex.printStackTrace();
            		throw ex;
            	}
            }
        }).
        log("Service End: Send Adjustment Letter").end();
    }

    public void setLockerPath(String lockerPath) {
		this.lockerPath = lockerPath;
	}

	public void setFileDropPath(String fileDropPath) {
		this.fileDropPath = fileDropPath;
	}

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
}
