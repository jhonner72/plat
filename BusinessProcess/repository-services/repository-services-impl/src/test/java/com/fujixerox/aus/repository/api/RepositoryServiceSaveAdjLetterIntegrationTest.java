package com.fujixerox.aus.repository.api;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

/** 
 * Henry Niu
 * 22/07/2015
 */
public class RepositoryServiceSaveAdjLetterIntegrationTest implements AbstractIntegrationTest {
	
	String[] drns = new String[]{"11111111", "22222222", "33333333", "44444444", "55555555"};
	String[] fileNames = new String[]{"adjust1.pdf", "adjust2.pdf", "adjust3.pdf", "adjust4.pdf", "adjust5.pdf"}; 
	String initJobIdentifier = "INIT_JOB_ADJUSTMENT_LETTER";
	String jobIdentifier = "JOB_ADJUSTMENT_LETTER";
	Date processingDate = new Date(); 
		
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveAdjustmentLetter() throws Exception {		
		StoreBatchAdjustmentLettersRequest request = buildStoreBatchAdjustmentLettersRequest();
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		
		ImageHelper.prepare(ctx, request.getJobIdentifier());
				
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();		
	
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		
		service.saveAdjustmentLetter(request);
	}
		
	private StoreBatchAdjustmentLettersRequest buildStoreBatchAdjustmentLettersRequest() {
		StoreBatchAdjustmentLettersRequest batchRequest = new StoreBatchAdjustmentLettersRequest();
		batchRequest.setJobIdentifier(initJobIdentifier + "/" + jobIdentifier);
		batchRequest.setProcessingDate(processingDate);
		batchRequest.setZipFilename("adjustment.zip.0");
		
		List<StoreAdjustmentLettersRequest> batchRequests = batchRequest.getAdjustmentLetters();
		
		for (int  i = 0; i < drns.length; i++) {
			batchRequests.add(RepositoryServiceTestHelper.buildStoreAdjustmentLettersRequest(drns[i], fileNames[i], 
					jobIdentifier, processingDate));
		}

		return batchRequest;		
	}
	
}
