package com.fujixerox.aus.repository.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.FileTypeEnum;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesRequest;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesResponse;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/** 
 * Zaka Lei
 * 09/09/2015
 */
public class RepositoryServiceQueryReceivedFilesIntegrationTest implements AbstractIntegrationTest {
	
	public static final String jobIdentifier = "000b246f-339c-4150-8385-c4a0fa040d70";
	public static final String receivedDate = "22/09/2015 13:00:00";
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void queryReceivedFilesWithinCurrentDate() throws Exception {
		queryReceivedFiles(new Date());
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void queryReceivedFilesBySpecificDate() throws Exception {
		Date businessDate = new SimpleDateFormat(Constant.DM_PROCESSING_DATETIME_FORMAT).parse(receivedDate);
		queryReceivedFiles(businessDate);
	}
	
	private void queryReceivedFiles(Date receivedDate) throws Exception {		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		GetReceivedFilesRequest request = new GetReceivedFilesRequest();
		request.setJobIdentifier(jobIdentifier);
		request.setFileType(FileTypeEnum.IMAGE_EXCHANGE);
		request.setReceivedDate(receivedDate);
		request.setSourceOrganisation("c1b1b351d860");
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		GetReceivedFilesResponse response = service.queryReceivedFiles(request);
		for(ReceivedFile receivedFile : response.getReceivedFiles()){
			LogUtil.log("file id ----" + receivedFile.getFileIdentifier(), LogUtil.DEBUG, null);
		}
		LogUtil.log("GetReceivedFilesResponse received file count = " + response.getReceivedFiles().size(), LogUtil.DEBUG, null);
	}
	
	
}
