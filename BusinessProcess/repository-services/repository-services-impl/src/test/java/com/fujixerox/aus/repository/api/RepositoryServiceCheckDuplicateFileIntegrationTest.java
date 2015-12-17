package com.fujixerox.aus.repository.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.fujixerox.aus.lombard.outclearings.checkduplicatefile.CheckDuplicateFileRequest;
import com.fujixerox.aus.lombard.outclearings.checkduplicatefile.CheckDuplicateFileResponse;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

/** 
 * Henry Niu
 * 30/10/2015
 */
public class RepositoryServiceCheckDuplicateFileIntegrationTest implements AbstractIntegrationTest {
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldHaveNoDuplicateFile() throws Exception {
			
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		CheckDuplicateFileRequest request = new CheckDuplicateFileRequest();
		request.setFilename("file_not_exist.txt");		
				
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		
		CheckDuplicateFileResponse response = service.checkDuplicateFile(request);
		assertEquals(false, response.isIsDuplicatedFile());
	}
    
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldHaveDuplicateFile() throws Exception {
			
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		CheckDuplicateFileRequest request = new CheckDuplicateFileRequest();
		request.setFilename("OUTCLEARINGSPKG_10062015-D379-4073-BA7C-SSSS68500861.xml");		
				
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		
		CheckDuplicateFileResponse response = service.checkDuplicateFile(request);
		assertEquals(true, response.isIsDuplicatedFile());
	}

	
}
