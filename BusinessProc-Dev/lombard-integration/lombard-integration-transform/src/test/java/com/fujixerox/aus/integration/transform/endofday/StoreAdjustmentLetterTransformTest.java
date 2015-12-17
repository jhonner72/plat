package com.fujixerox.aus.integration.transform.endofday;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.createadjustmentletters.CreateBatchAdjustmentLettersResponse;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;

public class StoreAdjustmentLetterTransformTest {

	private static final String INIT_JOB_IDENTIFIER = "22042015-2WWA-4069-G6CC-SSS123456789";
	private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";
	private static final String PREDICATE = "create";
	private static final String SUBJECT = "adjustmentletter";
	
	@Test
    public void shouldTransform() throws ParseException {
				
        Job job = new Job();
        job.setInitiatingJobIdentifier(INIT_JOB_IDENTIFIER);
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockActivity());

        StoreAdjustmentLetterTransform transform = new StoreAdjustmentLetterTransform();
		
        StoreBatchAdjustmentLettersRequest request = transform.transform(job);
        
        assertNotNull(request.getAdjustmentLetters());
        assertEquals(INIT_JOB_IDENTIFIER + "/" + JOB_IDENTIFIER, 
        		request.getAdjustmentLetters().toArray(new StoreAdjustmentLettersRequest[]{})[0].getJobIdentifier());
        assertThat(request.getAdjustmentLetters().size(), is(4));
    }
	
    protected Activity mockActivity() throws ParseException {        
        
        CreateBatchAdjustmentLettersResponse batchResponse = new CreateBatchAdjustmentLettersResponse();
        batchResponse.getAdjustmentLetters().add(buildResponse("11111111", "22222222"));
        batchResponse.getAdjustmentLetters().add(buildResponse("33333333", "44444444"));
        batchResponse.getAdjustmentLetters().add(buildResponse("55555555", "66666666"));
        batchResponse.getAdjustmentLetters().add(buildResponse("77777777", "88888888"));

        Activity activity = new Activity();
        activity.setSubject(SUBJECT);
        activity.setPredicate(PREDICATE);
        activity.setResponse(batchResponse);
        return activity;

    }
    
	private CreateAdjustmentLettersResponse buildResponse(String drn, String batchNumber) throws ParseException {
		
		CreateAdjustmentLettersResponse response = new CreateAdjustmentLettersResponse();
		response.setDocumentReferenceNumber(drn);
		response.setFilename("test.txt");
		response.setProcessingDate(new Date());
		response.setScannedBatchNumber(batchNumber);
		response.setTransactionLinkNumber("222222");
			
		return response;
	}

}
