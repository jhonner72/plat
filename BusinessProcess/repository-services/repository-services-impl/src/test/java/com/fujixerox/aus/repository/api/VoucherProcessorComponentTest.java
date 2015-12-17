package com.fujixerox.aus.repository.api;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfTime;
import com.documentum.operations.IDfCheckinOperation;
import com.documentum.operations.IDfCheckoutOperation;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.transform.StoreVoucherRequestTransform;
import com.fujixerox.aus.repository.transform.VoucherTransferTransform;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.DocumentumACL;
import com.fujixerox.aus.repository.util.dfc.DocumentumProcessor;
import com.fujixerox.aus.repository.util.dfc.FxaFileReceiptField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherIdHolder;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class VoucherProcessorComponentTest implements AbstractComponentTest {
		
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldSaveVoucherForIE() throws Exception {
		
		FileUtil fileUtil = mock(FileUtil.class);
		when(fileUtil.getImageFile(any(Voucher.class), any(String.class)))
			.thenReturn(new File("C:/Temp/test.txt"));	
		
		StoreVoucher storeVoucher = RepositoryServiceTestHelper.buildStoreVoucher(C.ACCCOUT_NUMBER, 
				C.AMOUNT, C.BSB, C.DRN, "25042015", false, "DBT", null, null, "1", C.LISTING_PAGE_NUMBER, false);
		when(fileUtil.parseMetaDataFile(any(File.class))).thenReturn(storeVoucher);	
		 
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		IDfSysObject fxaVoucherTransfer = RepositoryServiceTestHelper.buildFxaVoucherTransfer();
		IDfSysObject fxaFileReceipt = RepositoryServiceTestHelper.buildFxaFileReceipt();

		StoreVoucherRequestTransform transform = mock(StoreVoucherRequestTransform.class);
		when(transform.transform(any(IDfSession.class), any(VoucherInformation.class), any(String.class))).thenReturn(fxaVoucher);
		
		VoucherTransferTransform voucherTransferTransform = mock(VoucherTransferTransform.class);
		when(voucherTransferTransform.transform(any(IDfSession.class), any(IDfSysObject.class), any(String.class), 
				any(String.class), any(String.class), any(String.class),
				any(String.class), any(Date.class))).thenReturn(fxaVoucherTransfer);
			
		StoreBatchVoucherRequest request = RepositoryServiceTestHelper.buildStoreBatchVoucherRequest("343434",
				"JOB_UNIT_TEST_FOR_SAVE_VOUCHER", "01082015", false);	
		
		IDfSession session = mock(IDfSession.class);
		when(session.getACL(any(String.class), any(String.class))).thenReturn(mock(IDfACL.class));
		
		DocumentumACL documentumACL = mock(DocumentumACL.class);
		when(documentumACL.checkFolderExist(any(IDfSession.class), any(String.class), any(String.class), any(Date.class)))
			.thenReturn("C:/Temp/test.txt");
		when(documentumACL.getACL(any(IDfSession.class), any(String.class))).thenReturn(mock(IDfACL.class));
		
		DocumentumProcessor documentumProcessor = mock(DocumentumProcessor.class);
		
		List<TransferEndpoint> transferEndpoints = RepositoryServiceTestHelper.buildEndpoint("NAB");
		
		VoucherProcessor processor = new VoucherProcessor(fileUtil);	
		//processor.setDocumentumACL(documentumACL);
		processor.setStoreVoucherRequestTransform(transform);
		processor.setVoucherTransferTransform(voucherTransferTransform);
		processor.setDocumentumProcessor(documentumProcessor);
		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(storeVoucher.getVoucherInformation().getVoucher());
		voucherInfo.setVoucherBatch(request.getVoucherBatch());
		voucherInfo.setVoucherProcess(storeVoucher.getVoucherInformation().getVoucherProcess());
		
		processor.saveVoucher(C.IE_JOB_IDENTIFIER, session, voucherInfo, transferEndpoints, fxaFileReceipt.getString(FxaFileReceiptField.FILE_ID), null);
		
		Mockito.verify(fxaVoucher).setFile(ArgumentCaptor.forClass(String.class).capture());
		Mockito.verify(fxaVoucher, times(2)).setObjectName(ArgumentCaptor.forClass(String.class).capture());
		//Mockito.verify(fxaVoucher).link(ArgumentCaptor.forClass(String.class).capture());
        //Mockito.verify(fxaVoucher).setACL(ArgumentCaptor.forClass(IDfACL.class).capture());
		Mockito.verify(fxaVoucher).save();
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldSaveVoucherForIIE() throws Exception {
		
		FileUtil fileUtil = mock(FileUtil.class);
		when(fileUtil.getImageFile(Matchers.any(Voucher.class), Matchers.any(String.class)))
			.thenReturn(new File("C:/Temp/test.txt"));			

		StoreVoucher storeVoucher = RepositoryServiceTestHelper.buildStoreVoucher(C.ACCCOUT_NUMBER, 
				C.AMOUNT, C.BSB, C.DRN, "25042015", false, "DBT", null, null, "1", C.LISTING_PAGE_NUMBER, false);
		when(fileUtil.parseMetaDataFile(any(File.class))).thenReturn(storeVoucher);
		
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();	
		IDfSysObject fxaVoucherTransfer = RepositoryServiceTestHelper.buildFxaVoucherTransfer();
		IDfSysObject fxaFileReceipt = RepositoryServiceTestHelper.buildFxaFileReceipt();

		IDfSession session = mock(IDfSession.class);
		when(session.newObject(Matchers.eq(RepositoryProperties.doc_object_type))).thenReturn(fxaVoucher);
		when(session.newObject(Matchers.eq(RepositoryProperties.doc_voucher_transfer_type))).thenReturn(fxaVoucherTransfer);
		
		StoreVoucherRequestTransform transform = mock(StoreVoucherRequestTransform.class);
		when(transform.transform(any(IDfSession.class), any(VoucherInformation.class), any(String.class))).thenReturn(fxaVoucher);
			
		StoreBatchVoucherRequest request = RepositoryServiceTestHelper.buildStoreBatchVoucherRequest("343434",
				"JOB_PROCESSABLE", "25042015", false);		
		
		DocumentumACL documentumACL = mock(DocumentumACL.class);
		when(documentumACL.checkFolderExist(Matchers.any(IDfSession.class), any(String.class), Matchers.any(String.class), Matchers.any(Date.class)))
			.thenReturn("C:/Temp/test.txt");
		when(documentumACL.getACL(Matchers.any(IDfSession.class), any(String.class))).thenReturn(mock(IDfACL.class));
		
		List<TransferEndpoint> transferEndpoints = RepositoryServiceTestHelper.buildEndpoint("NAB");
		
		VoucherProcessor processor = new VoucherProcessor(fileUtil);	
		//processor.setDocumentumACL(documentumACL);
		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(storeVoucher.getVoucherInformation().getVoucher());
		voucherInfo.setVoucherBatch(request.getVoucherBatch());
		voucherInfo.setVoucherProcess(storeVoucher.getVoucherInformation().getVoucherProcess());
		
		processor.saveVoucher(C.INWARD_IE_JOB_IDENTIFIER, session, voucherInfo, transferEndpoints, fxaFileReceipt.getString(FxaFileReceiptField.FILE_ID), null);
		
		Mockito.verify(fxaVoucher).setFile(ArgumentCaptor.forClass(String.class).capture());
		Mockito.verify(fxaVoucher, times(2)).setObjectName(ArgumentCaptor.forClass(String.class).capture());
		//Mockito.verify(fxaVoucher).link(ArgumentCaptor.forClass(String.class).capture());
		//Mockito.verify(fxaVoucher).setACL(ArgumentCaptor.forClass(IDfACL.class).capture());
		Mockito.verify(fxaVoucher).save();		
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldQueryVoucherForIE() throws Exception {
		queryVoucher("OUTPUT_JOB_FOR_IE_MIN_NON_VOUCHER", DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND, 
				ImageType.JPEG, 2, -1, null);
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldQueryVoucherForVIF() throws Exception {
		queryVoucher("OUTPUT_JOB_FOR_VIF_VOUCHER", DocumentExchangeEnum.VIF_OUTBOUND, ImageType.NONE, 2, -1,
				QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
	}
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldUpdateVoucherForIE() throws Exception {
		updateVoucher("JOB_TEST", DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND, "Test.txt");
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldRepostVoucherForInsert() throws Exception {
				
		VoucherInformation voucherInfo = RepositoryServiceTestHelper.buildVoucherInformation(C.ACCCOUT_NUMBER, 
				C.AMOUNT, C.BSB, C.DRN, C.PROCESSING_DATE, false, 
				C.DOCUMENT_TYPE, C.AUX_DOM,	C.TARGET_END_POINT, C.TRAN_LINK_NO, C.LISTING_PAGE_NUMBER, C.VOUCHER_DELAYED_INDICATOR, false);
		
		StoreVoucher storeVoucher = new StoreVoucher();
		storeVoucher.setVoucherInformation(voucherInfo);
		storeVoucher.getTransferEndpoints().addAll(RepositoryServiceTestHelper.buildEndpoint(C.TARGET_END_POINT));
					
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		IDfSysObject fxaVoucherTransfer = RepositoryServiceTestHelper.buildFxaVoucherTransfer();
		IDfSysObject fxaFileReceipt = RepositoryServiceTestHelper.buildFxaFileReceipt();

		IDfSession session = mock(IDfSession.class);
		when(session.newObject(Matchers.eq(RepositoryProperties.doc_object_type))).thenReturn(fxaVoucher);
		when(session.newObject(Matchers.eq(RepositoryProperties.doc_voucher_transfer_type))).thenReturn(fxaVoucherTransfer);
		
		VoucherTransferTransform transform = mock(VoucherTransferTransform.class);
		when(transform.transform(Matchers.any(IDfSession.class), Matchers.any(IDfSysObject.class), Matchers.any(String.class), 
				Matchers.any(String.class), Matchers.any(String.class), Matchers.any(String.class),
				Matchers.any(String.class), Matchers.any(Date.class)))
				.thenReturn(fxaVoucherTransfer);	
		
		VoucherProcessor processor = new VoucherProcessor();	
		//processor.setVoucherTransferTransform(transform);
		processor.saveVoucher(null, session, storeVoucher.getVoucherInformation(), storeVoucher.getTransferEndpoints(),
				fxaFileReceipt.getString(FxaFileReceiptField.FILE_ID), null);
		
		Mockito.verify(fxaVoucher).save();
		Mockito.verify(fxaVoucher, times(27)).setString(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());	
		Mockito.verify(fxaVoucherTransfer, times(4)).save();
    }
	 
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldRepostVoucherForUpdate() throws Exception {		
		updateVoucher("JOB_TEST", DocumentExchangeEnum.INWARD_FOR_VALUE, null);	
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldUpdateVoucherForVIF() throws Exception {
		updateVoucher("JOB_TEST", DocumentExchangeEnum.VIF_OUTBOUND, "Test.txt");
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldQueryVoucherInfo() throws Exception {
		FileUtil fileUtil = mock(FileUtil.class);
		when(fileUtil.getTiffImageFile(any(String.class), any(String.class), any(String.class)))
			.thenReturn(new File("test.txt"));	
		
		GetVouchersInformationRequest request = new GetVouchersInformationRequest();
		request.setImageRequired(ImageType.JPEG);
		request.setImageResponseType(ResponseType.FILE);
		request.setMetadataResponseType(ResponseType.MESSAGE);
		request.setJobIdentifier("JOB_TEST"); 
		
		IDfCollection collection = mock(IDfCollection.class);
		when(collection.getString(eq(FxaVoucherField.OBJECT_ID))).thenReturn("11111");
				
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		
		IDfSession session = mock(IDfSession.class);
		when(session.getObject(eq(new DfId("11111")))).thenReturn(fxaVoucher);
			
		VoucherProcessor processor = new VoucherProcessor(fileUtil);
		
		DocumentumProcessor documentumProcessor = mock(DocumentumProcessor.class);
		processor.setDocumentumProcessor(documentumProcessor);
		processor.queryVoucherInfo(request, session, collection);
		
		verify(fxaVoucher, times(4)).getTime(ArgumentCaptor.forClass(String.class).capture());
		verify(fxaVoucher).getFile(ArgumentCaptor.forClass(String.class).capture());
		verify(fileUtil).splitAndSend(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture(), ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldQueryVoucherInfoWithUpdate() throws Exception {
		FileUtil fileUtil = mock(FileUtil.class);
		when(fileUtil.getTiffImageFile(any(String.class), any(String.class), any(String.class)))
			.thenReturn(new File("test.txt"));	
		
		GetVouchersInformationRequest request = new GetVouchersInformationRequest();
		request.setJobIdentifier("JOB_TEST"); 
		request.setImageRequired(ImageType.JPEG);
		request.setImageResponseType(ResponseType.FILE);
		request.setMetadataResponseType(ResponseType.FILE);
		
		request.getSearchCriterias().add(RepositoryServiceTestHelper.buildCriteria("voucher.processingDate", "29/07/2015"));
		request.getSearchCriterias().add(RepositoryServiceTestHelper.buildCriteria("voucher.drn", "11111111"));

		request.getUpdateCriterias().add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.adjustmentReasonCode", "1"));
		request.getSearchCriterias().add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.inactiveFlag", "true"));
		
		IDfCollection collection = mock(IDfCollection.class);
		when(collection.getString(eq(FxaVoucherField.OBJECT_ID))).thenReturn("11111");
				
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		
		IDfSession session = mock(IDfSession.class);
		when(session.getObject(eq(new DfId("11111")))).thenReturn(fxaVoucher);
			
		VoucherProcessor processor = new VoucherProcessor(fileUtil);
		
		DocumentumProcessor documentumProcessor = mock(DocumentumProcessor.class);
		processor.setDocumentumProcessor(documentumProcessor);
		processor.queryVoucherInfo(request, session, collection);
		
		verify(fxaVoucher, times(4)).getTime(ArgumentCaptor.forClass(String.class).capture());
		verify(fxaVoucher).getFile(ArgumentCaptor.forClass(String.class).capture());
		verify(fileUtil).splitAndSend(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture(), ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldUpdateVoucherInfoForAdjustment() throws Exception {
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		
		IDfSession session = mock(IDfSession.class);
		when(session.getObjectByQualification(any(String.class))).thenReturn(fxaVoucher);
		
		IDfCheckoutOperation checkoutOperation = mock(IDfCheckoutOperation.class);
		IDfCheckinOperation checkinOperation = mock(IDfCheckinOperation.class);
		
		DfClientX dfClientX = mock(DfClientX.class);
		when(dfClientX.getCheckoutOperation()).thenReturn(checkoutOperation);
		when(dfClientX.getCheckinOperation()).thenReturn(checkinOperation);
		
		UpdateVouchersInformationRequest request = new UpdateVouchersInformationRequest();
		request.setVoucherTransferStatusFrom(VoucherStatus.ON_HOLD);
		request.setVoucherTransferStatusTo(VoucherStatus.NEW);
		request.getVoucherInformations().add(RepositoryServiceTestHelper.buildVoucherInformationForAdjustment("00000000", "04122015"));
		request.getVoucherInformations().add(RepositoryServiceTestHelper.buildVoucherInformationForAdjustment("11111111", "04122015"));
						
		DfQuery dfQuery = mock(DfQuery.class);
		
		VoucherProcessor processor = new VoucherProcessor();	
		processor.setDfQuery(dfQuery);
		processor.setDfClientX(dfClientX);
		processor.updateVoucherInfo(session, request,
				RepositoryServiceTestHelper.buildVoucherInformationForAdjustment("11111111", "04122015"), null);
									
		verify(session).getObjectByQualification(ArgumentCaptor.forClass(String.class).capture());
		verify(dfQuery).setDQL(ArgumentCaptor.forClass(String.class).capture());
		verify(dfQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), ArgumentCaptor.forClass(Integer.class).capture());
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldUpdateVoucherInfoAndTransferEndPointStatus() throws Exception {
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		
		IDfSession session = mock(IDfSession.class);
		when(session.getObjectByQualification(any(String.class))).thenReturn(fxaVoucher);
		
		IDfCheckoutOperation checkoutOperation = mock(IDfCheckoutOperation.class);
		IDfCheckinOperation checkinOperation = mock(IDfCheckinOperation.class);
		
		DfClientX dfClientX = mock(DfClientX.class);
		when (dfClientX.getCheckoutOperation()).thenReturn(checkoutOperation);
		when (dfClientX.getCheckinOperation()).thenReturn(checkinOperation);
		
		List<TransferEndpoint> endpoints = new ArrayList<TransferEndpoint>();
		TransferEndpoint transferEndpoint= new TransferEndpoint();
		transferEndpoint.setVoucherStatus(VoucherStatus.NEW);
		endpoints.add(transferEndpoint);
		
		DfQuery dfQuery = mock(DfQuery.class);
		
		VoucherProcessor processor = new VoucherProcessor();	
		processor.setDfQuery(dfQuery);
		processor.setDfClientX(dfClientX);
		
		VoucherInformation voucherInformation = RepositoryServiceTestHelper.buildVoucherInformation(C.ACCCOUT_NUMBER, C.AMOUNT, C.BSB, C.DRN, 
			C.PROCESSING_DATE, false, C.DOCUMENT_TYPE, C.AUX_DOM,	C.TARGET_END_POINT, C.TRAN_LINK_NO, 
			C.LISTING_PAGE_NUMBER, C.VOUCHER_DELAYED_INDICATOR, false);
		
		processor.updateVoucherInfo(session, null, voucherInformation, endpoints);
									
		verify(session).getObjectByQualification(ArgumentCaptor.forClass(String.class).capture());
		verify(dfQuery).setDQL(ArgumentCaptor.forClass(String.class).capture());
		verify(dfQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), ArgumentCaptor.forClass(Integer.class).capture());
    }
	
	@Test 
    @Category(AbstractComponentTest.class)
    public void shouldSaveAdjustmentLetter() throws Exception {
		IDfSysObject fxaAdjustmentLetter = Mockito.mock(IDfSysObject.class);	
		
		IDfSession session = mock(IDfSession.class);
		when(session.newObject(any(String.class))).thenReturn(fxaAdjustmentLetter);
		
		FileUtil fileUtil = mock(FileUtil.class);
		when(fileUtil.getFile(any(String.class), any(String.class))).thenReturn(new File("C:/Temp/test.txt"));
		
		DocumentumACL documentumACL = mock(DocumentumACL.class);
		when(documentumACL.checkFolderExist(any(IDfSession.class), any(String.class), any(String.class), any(Date.class)))
			.thenReturn("C:/Temp/test.txt");
		when(documentumACL.getACL(any(IDfSession.class), any(String.class))).thenReturn(mock(IDfACL.class));
		
		StoreAdjustmentLettersRequest request = RepositoryServiceTestHelper.buildStoreAdjustmentLettersRequest("11111111",
				"test.txt", "JOB_1111111", new Date());
		
		StoreBatchAdjustmentLettersRequest batchRequest = new StoreBatchAdjustmentLettersRequest();
		batchRequest.setJobIdentifier("JOB_1111111");
		
		VoucherProcessor processor = new VoucherProcessor(fileUtil);	
		processor.setDocumentumACL(documentumACL);
		processor.saveAdjustmentLetter(session, batchRequest, request);
	
		verify(session).newObject(ArgumentCaptor.forClass(String.class).capture());
		verify(fxaAdjustmentLetter).save();
    }

	@Test
	@Category(AbstractComponentTest.class)
	public void shouldSaveVoucherForLockedBox() throws Exception {

		FileUtil fileUtil = mock(FileUtil.class);
		when(fileUtil.getImageFile(any(Voucher.class), any(String.class)))
				.thenReturn(new File("C:/Temp/test.txt"));

		StoreVoucher storeVoucher = RepositoryServiceTestHelper.buildStoreVoucherForLockedBox(C.ACCCOUT_NUMBER,
				C.AMOUNT, C.BSB, C.DRN, "25042015", false, "DBT", null, null, "1", C.LISTING_PAGE_NUMBER, false);
		when(fileUtil.parseMetaDataFile(any(File.class))).thenReturn(storeVoucher);
//		StoreVoucher storeVoucher = RepositoryServiceTestHelper.buildStoreVoucher(C.ACCCOUT_NUMBER,
//				C.AMOUNT, C.BSB, C.DRN, "25042015", false, "DBT", null, null, "1", C.LISTING_PAGE_NUMBER, false);
//		when(fileUtil.parseMetaDataFile(any(File.class))).thenReturn(storeVoucher);

		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		IDfSysObject fxaVoucherTransfer = RepositoryServiceTestHelper.buildFxaVoucherTransfer();
		IDfSysObject fxaFileReceipt = RepositoryServiceTestHelper.buildFxaFileReceipt();

		StoreVoucherRequestTransform transform = mock(StoreVoucherRequestTransform.class);
		when(transform.transform(any(IDfSession.class), any(VoucherInformation.class), any(String.class))).thenReturn(fxaVoucher);

		VoucherTransferTransform voucherTransferTransform = mock(VoucherTransferTransform.class);
		when(voucherTransferTransform.transform(any(IDfSession.class), any(IDfSysObject.class), any(String.class),
				any(String.class), any(String.class), any(String.class),
				any(String.class), any(Date.class))).thenReturn(fxaVoucherTransfer);

		StoreBatchVoucherRequest request = RepositoryServiceTestHelper.buildStoreBatchVoucherRequestForLockedBox("343434",
				"JOB_UNIT_TEST_FOR_SAVE_VOUCHER", "01082015", false);
//		StoreBatchVoucherRequest request = RepositoryServiceTestHelper.buildStoreBatchVoucherRequest("343434",
//				"JOB_UNIT_TEST_FOR_SAVE_VOUCHER", "01082015", false);


		IDfSession session = mock(IDfSession.class);
		when(session.getACL(any(String.class), any(String.class))).thenReturn(mock(IDfACL.class));

		DocumentumACL documentumACL = mock(DocumentumACL.class);
		when(documentumACL.checkFolderExist(any(IDfSession.class), any(String.class), any(String.class), any(Date.class)))
				.thenReturn("C:/Temp/test.txt");
		when(documentumACL.getACL(any(IDfSession.class), any(String.class))).thenReturn(mock(IDfACL.class));
		when(documentumACL.getIdForDateFolder(any(IDfSession.class), any(String.class), any(String.class), any(Date.class)))
				.thenReturn("unitTestFolder");

		DocumentumProcessor documentumProcessor = mock(DocumentumProcessor.class);

		List<TransferEndpoint> transferEndpoints = RepositoryServiceTestHelper.buildEndpoint("NAB");

		VoucherProcessor processor = new VoucherProcessor(fileUtil);
		processor.setStoreVoucherRequestTransform(transform);
		processor.setVoucherTransferTransform(voucherTransferTransform);
		processor.setDocumentumProcessor(documentumProcessor);
		processor.setDocumentumACL(documentumACL);

		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(storeVoucher.getVoucherInformation().getVoucher());
		voucherInfo.setVoucherBatch(request.getVoucherBatch());
		voucherInfo.setVoucherProcess(storeVoucher.getVoucherInformation().getVoucherProcess());

		processor.saveVoucher(C.IE_JOB_IDENTIFIER, session, voucherInfo, transferEndpoints, fxaFileReceipt.getString(FxaFileReceiptField.FILE_ID), null);

		Mockito.verify(fxaVoucher).setFile(ArgumentCaptor.forClass(String.class).capture());
		Mockito.verify(fxaVoucher, times(2)).setObjectName(ArgumentCaptor.forClass(String.class).capture());
		Mockito.verify(fxaVoucher).save();
	}

	@SuppressWarnings("unchecked")
	private void queryVoucher(String jobIdentifier, DocumentExchangeEnum voucherTransition, ImageType imageType,
    		int minSize, int maxSize, QueryLinkTypeEnum queryLinkType) throws Exception {
		
		FileUtil fileUtil = mock(FileUtil.class);
		when(fileUtil.getTiffImageFile(any(String.class), any(String.class), any(String.class)))
			.thenReturn(new File("C:/Temp/test.txt"));	
		
		GetVouchersRequest request = new GetVouchersRequest();
		request.setVoucherTransfer(voucherTransition);
		request.setTargetEndPoint("XYZ");
		request.setImageType(imageType);
		request.setJobIdentifier(jobIdentifier);
		request.setMaxReturnSize(maxSize);
		request.setMinReturnSize(minSize);
		request.setQueryLinkType(queryLinkType);
		request.setVoucherStatusTo(VoucherStatus.IN_PROGRESS);
						
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		IDfSysObject fxaVoucherTransfer = RepositoryServiceTestHelper.buildFxaVoucherTransfer();
		
		IDfSession session = mock(IDfSession.class);
		when(session.getObject(eq(new DfId("11111")))).thenReturn(fxaVoucher);
		when(session.getObject(eq(new DfId("22222")))).thenReturn(fxaVoucherTransfer);	
		
		VoucherIdHolder VoucherIdHolder = new VoucherIdHolder("11111", "22222");
			
		VoucherProcessor processor = new VoucherProcessor(fileUtil);
		processor.queryVoucher(request, session, VoucherIdHolder);
		
		verify(fileUtil).createMetaDataFile(ArgumentCaptor.forClass(IDfSysObject.class).capture(), ArgumentCaptor.forClass(String.class).capture(),
			ArgumentCaptor.forClass(String.class).capture(), ArgumentCaptor.forClass(String.class).capture(),
			ArgumentCaptor.forClass(String.class).capture(), ArgumentCaptor.forClass(String.class).capture());
    }
    
    @SuppressWarnings("unchecked")
	private void updateVoucher(String jobIdentifier, DocumentExchangeEnum voucherTransition, String fileName) throws Exception {
		
		IDfSysObject fxaVoucherTransfer = RepositoryServiceTestHelper.buildFxaVoucherTransfer();
		
		IDfSession session = mock(IDfSession.class);
		when(session.getObjectByQualification(any(String.class))).thenReturn(fxaVoucherTransfer);
				
		File jsonFile = mock(File.class);
		when(jsonFile.getName()).thenReturn("VOUCHER_01092015_33333333_121212_434343.JSON");
		
		VoucherProcessor processor = new VoucherProcessor(mock(FileUtil.class));		
		DocumentumProcessor documentumProcessor = mock(DocumentumProcessor.class);
		processor.setDocumentumProcessor(documentumProcessor);
		processor.updateVoucher(session, jsonFile.getName(), new Date(), voucherTransition, VoucherStatus.COMPLETED, fileName);
									
		verify(session).getObjectByQualification(ArgumentCaptor.forClass(String.class).capture());
		verify(jsonFile).getName();
		
		int count = 2;
		if (fileName == null) {
			count = 1;
		}
		verify(fxaVoucherTransfer, times(count)).setString(ArgumentCaptor.forClass(String.class).capture(), 
				ArgumentCaptor.forClass(String.class).capture());
		verify(fxaVoucherTransfer).setTime(ArgumentCaptor.forClass(String.class).capture(), 
				ArgumentCaptor.forClass(IDfTime.class).capture());
    }
    
     
}
