package com.fujixerox.aus.integration.transform.endofday;

import static org.junit.Assert.assertEquals;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.Test;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersResponse;

public class SendAdjustmentLetterTransformTest {

	private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String PREDICATE = "create";
	private static final String SUBJECT = "adjustmentletter";
	
	@Test
    public void shouldTransform() throws ParseException {
				
        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockActivity()); 

        SendAdjustmentLetterTransform transform = new SendAdjustmentLetterTransform();
		
        CreateBatchAdjustmentLettersResponse response = transform.transform(job);
        assertEquals(3, response.getAdjustmentLetters().size());
    }
	
    protected Activity mockActivity() throws ParseException {
        Activity activity = new Activity();
        activity.setSubject(SUBJECT);
        activity.setPredicate(PREDICATE);
        
        CreateBatchAdjustmentLettersResponse response = new CreateBatchAdjustmentLettersResponse();
        response.setZipFilename("test.txt");      
        response.getAdjustmentLetters().add(buildResponse("11111111", "test1.pdf", "1206015"));
        response.getAdjustmentLetters().add(buildResponse("22222222", "test2.pdf", "1306015"));
        response.getAdjustmentLetters().add(buildResponse("33333333", "test3.pdf", "1406015"));

        activity.setResponse(response);
        return activity;
    }
    
	private CreateAdjustmentLettersResponse buildResponse(String drn, String fileName, String processingDateString) throws ParseException {
		
		CreateAdjustmentLettersResponse response = new CreateAdjustmentLettersResponse();
		response.setDocumentReferenceNumber(drn);
		response.setFilename(fileName);
		response.setProcessingDate(new SimpleDateFormat("ddMMyyyy").parse(processingDateString));
		response.setScannedBatchNumber("BATCH_11111111");
		response.setTransactionLinkNumber("TRANLINK_22222222");
			
		return response;
	}

}
