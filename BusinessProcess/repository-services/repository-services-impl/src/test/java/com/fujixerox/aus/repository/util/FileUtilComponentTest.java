package com.fujixerox.aus.repository.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.exception.FileException;
import com.fujixerox.aus.repository.util.exception.ImageException;

/** 
 * Henry Niu
 * 2/4/2015
 */
public class FileUtilComponentTest implements AbstractComponentTest {

	private static final String SCANNING_IMAGE_JOB_IDENTIFIER = "JOB_UNIT_TEST";
	private static final String FRONT_IMAGE_NAME = "VOUCHER_07042015_11111111_FRONT.JPG";
	private static final String REAR_IMAGE_NAME = "VOUCHER_07042015_11111111_REAR.JPG";

	private static final String SCANNING_LISTING_JOB_IDENTIFIER = "JOB_UNIT_TEST_FOR_LISTING_MERGE";
	
	private static final String DRN = "11111111";
	private static final String ACCCOUT_NUMBER = "12345678"; 
	private static final String BSB = "063813";
	private static final String AMOUNT = "234.56";
	private static final String PROCESSING_DATE = "07042015";	
	
	private static final String JOB_UNIT_TEST_FOR_JSON = "JOB_UNIT_TEST_FOR_JSON";

    @Test(expected = FileException.class)
    @Category(AbstractComponentTest.class)
    public void shouldRaiseExceptionIfImageLockerPathIsMissing() throws FileException, ParseException, ImageException {
    	Voucher voucher = RepositoryServiceTestHelper.
    			buildVoucher(ACCCOUT_NUMBER, AMOUNT, BSB, DRN, PROCESSING_DATE, null, null);
    	
    	FileUtil fileUtil = new FileUtil();
    	fileUtil.getImageFile(voucher, SCANNING_IMAGE_JOB_IDENTIFIER);
    }
    
    @Test(expected = FileException.class)
    @Category(AbstractComponentTest.class)
    public void shouldRaiseExceptionIfImageBasePathNotExist() throws ParseException, FileException, ImageException {
    	Voucher voucher = RepositoryServiceTestHelper.
    			buildVoucher(ACCCOUT_NUMBER, AMOUNT, BSB, DRN, PROCESSING_DATE, null, null);
    	
    	FileUtil fileUtil = new FileUtil();
    	fileUtil.setLockerPath("C:\\Wrong Path");
    	fileUtil.getImageFile(voucher, SCANNING_IMAGE_JOB_IDENTIFIER);
    }

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldGetImageFilePath() throws ParseException, FileException, ImageException, IOException {
    	
    	// create a dir for test
    	File dir = new File(C.LOCKER_PATH, SCANNING_IMAGE_JOB_IDENTIFIER);
    	if (!dir.exists()) {
    		dir.mkdir();
    	}
    	
    	Resource frontImage = new ClassPathResource(C.SOURCE_IMAGE_PATH + "/" + SCANNING_IMAGE_JOB_IDENTIFIER + "/" + FRONT_IMAGE_NAME);
		Resource rearImage = new ClassPathResource(C.SOURCE_IMAGE_PATH + "/" + SCANNING_IMAGE_JOB_IDENTIFIER + "/" + REAR_IMAGE_NAME);

		FileUtils.copyFile(frontImage.getFile(), new File(dir, frontImage.getFile().getName()));
		FileUtils.copyFile(rearImage.getFile(), new File(dir, rearImage.getFile().getName()));

		Voucher voucher = RepositoryServiceTestHelper.
				buildVoucher(ACCCOUT_NUMBER, AMOUNT, BSB, DRN, PROCESSING_DATE, null, null);
    	    	
    	FileUtil fileUtil = new FileUtil();
    	fileUtil.setLockerPath(C.LOCKER_PATH);

    	File scannedImageFilePath = fileUtil.getImageFile(voucher, SCANNING_IMAGE_JOB_IDENTIFIER);

    	assertNotNull(scannedImageFilePath);
		assertThat(scannedImageFilePath.exists(), is(true));
    }

	@Test
	@Category(AbstractComponentTest.class)
	public void shouldGetListingFilePath() throws ParseException, FileException, ImageException, IOException {

		// create a dir for test
		File dir = new File(C.LOCKER_PATH, SCANNING_LISTING_JOB_IDENTIFIER);
		if (!dir.exists()) {
			dir.mkdir();
		}


		String listingImage1 = "LISTING_25052015_12121212_1.JPG";
		String listingImage2 = "LISTING_25052015_12121236_2.JPG";
		String listingImage3 = "LISTING_25052015_12121225_3.JPG";


		Resource image1 = new ClassPathResource(C.SOURCE_IMAGE_PATH + "/" + SCANNING_LISTING_JOB_IDENTIFIER + "/" + listingImage1);
		Resource image2 = new ClassPathResource(C.SOURCE_IMAGE_PATH + "/" + SCANNING_LISTING_JOB_IDENTIFIER + "/" + listingImage2);
		Resource image3 = new ClassPathResource(C.SOURCE_IMAGE_PATH + "/" + SCANNING_LISTING_JOB_IDENTIFIER + "/" + listingImage3);

		FileUtils.copyFile(image1.getFile(), new File(dir, image1.getFile().getName()));
		FileUtils.copyFile(image2.getFile(), new File(dir, image2.getFile().getName()));
		FileUtils.copyFile(image3.getFile(), new File(dir, image3.getFile().getName()));

		ScannedListingBatchHeader scannedListingBatchHeader = RepositoryServiceTestHelper.buildScannedListingBatchHeader();

		FileUtil fileUtil = new FileUtil();
		fileUtil.setLockerPath(C.LOCKER_PATH);

		File scannedListingFilePath = fileUtil.getListingTiffFile(scannedListingBatchHeader, SCANNING_LISTING_JOB_IDENTIFIER);

		assertNotNull(scannedListingFilePath);
		assertThat(scannedListingFilePath.exists(), is(true));
	}

 	@Test
    @Category(AbstractComponentTest.class)
    public void shouldGetTiffImageFile() throws FileException, DfException {
    	FileUtil fileUtil = new FileUtil();
    	fileUtil.setLockerPath("target");
    	
    	File file = fileUtil.getTiffImageFile("IE_111-222-333-444", "07042015", "11111111");
    	
    	assertNotNull(file);
    	assertEquals("target\\IE_111-222-333-444\\VOUCHER_07042015_11111111.TIFF", file.getPath());
		assertEquals("target\\IE_111-222-333-444\\VOUCHER_07042015_11111111.TIFF", file.getPath());
	}

    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldSplitAndSend() throws FileException, DfException, IOException, ImageException {
    	ImageUtil imageUtil = mock(ImageUtil.class);    	
    	
    	FileUtil fileUtil = new FileUtil();
    	fileUtil.setLockerPath("target");
    	fileUtil.setImageUtil(imageUtil);
    	
    	fileUtil.splitAndSend("IE_111-222-333-444", "VOUCHER_07042015_11111111.TIFF", "07042015", "11111111");
    	
    	Mockito.verify(imageUtil).splitTiff(ArgumentCaptor.forClass(File.class).capture(),
    			ArgumentCaptor.forClass(File[].class).capture());
    }
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldDeleteDir()  {
    	//TODO
    }
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldCreateMetaDataFile1() throws DfException, ParseException, JsonGenerationException, JsonMappingException, 
    	IOException, FileException {
    	
    	FileUtil fileUtil = new FileUtil("target");
    	
    	VoucherInformation inputVoucherInfo = RepositoryServiceTestHelper.buildVoucherInformation(C.ACCCOUT_NUMBER, 
				C.AMOUNT, C.BSB, C.DRN, C.PROCESSING_DATE, false, 
				C.DOCUMENT_TYPE, C.AUX_DOM,	C.TARGET_END_POINT, C.TRAN_LINK_NO, C.LISTING_PAGE_NUMBER, C.VOUCHER_DELAYED_INDICATOR, false);
		
    	File metadataFile = fileUtil.createMetaDataFile(C.IE_JOB_IDENTIFIER, inputVoucherInfo);
    	
    	ObjectMapper mapper = new ObjectMapper();
		JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule();
		mapper.registerModule(jaxbAnnotationModule);

    	VoucherInformation outputVoucherInfo = mapper.readValue(metadataFile, VoucherInformation.class);
    	assertNotNull(outputVoucherInfo);
    	RepositoryServiceTestHelper.compareVoucher(outputVoucherInfo.getVoucher());
    }
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldCreateMetaDataFile2() throws DfException, ParseException, JsonGenerationException, JsonMappingException, 
    	IOException, FileException {
    	
    	FileUtil fileUtil = new FileUtil("target");
    	
    	IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
    	File metadataFile = fileUtil.createMetaDataFile(fxaVoucher, C.IE_JOB_IDENTIFIER, C.PROCESSING_DATE, C.DRN,
    			C.BATCH_NUMBER, C.TRAN_LINK_NO);
    	
    	ObjectMapper mapper = new ObjectMapper();
		JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule();
		mapper.registerModule(jaxbAnnotationModule);

    	VoucherInformation voucherInfo = mapper.readValue(metadataFile, VoucherInformation.class);
    	assertNotNull(voucherInfo);
    	RepositoryServiceTestHelper.compareVoucher(voucherInfo.getVoucher());
    }
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldGetJasonFilesForJobIdentifier() throws FileException, IOException  {
    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, JOB_UNIT_TEST_FOR_JSON);
		
    	FileUtil fileUtil = new FileUtil("target");
    	File[] jsonFiles = fileUtil.getJasonFilesForJobIdentifier(JOB_UNIT_TEST_FOR_JSON);
    	
    	assertEquals(jsonFiles.length, 4);
    }  
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldParseMetaDataFile() throws DfException, ParseException, JsonGenerationException, JsonMappingException, IOException, FileException {
    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, JOB_UNIT_TEST_FOR_JSON);
		
		FileUtil fileUtil = new FileUtil("target");    	
    	StoreVoucher storeVoucher = fileUtil.parseMetaDataFile(
    			new File("target", JOB_UNIT_TEST_FOR_JSON + "/STORE_VOUCHER.JSON"));    	
    	
    	assertNotNull(storeVoucher);
    }
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldSplitAndEncode() throws Exception  {
    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, "JOB_UNIT_TEST_FOR_SPLIT");
		
		Voucher voucher = new Voucher();
		voucher.setProcessingDate(new SimpleDateFormat("ddMMyyyy").parse("07042015"));
		voucher.setDocumentReferenceNumber("11111111");
		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(voucher);
		
    	FileUtil fileUtil = new FileUtil("target");
    	fileUtil.splitAndEncode(voucherInfo, "target/JOB_UNIT_TEST_FOR_SPLIT/VOUCHER_07042015_11111111.TIFF");
    	
    } 
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldEncodeFileContent() throws FileException, IOException  {
    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, "JOB_UNIT_TEST_FOR_MERGE");
		
    	FileUtil fileUtil = new FileUtil("target");
    	String encodeFileContent = fileUtil.encodeFileContent(new File("target/JOB_UNIT_TEST_FOR_MERGE/VOUCHER_07042015_11111111_FRONT.JPG"));
    	
    	assertNotNull(encodeFileContent);
    } 
    
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldSortBasedOnFileName() throws FileException, ImageException, IOException  {    	
    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, "JOB_UNIT_TEST_FOR_LISTING_SORT");
		
		FileUtil fileUtil = new FileUtil("target");
    	File[] files = fileUtil.getJpgFile("JOB_UNIT_TEST_FOR_LISTING_SORT");
    	
    	assertEquals("LISTING_28092015_166000001_1.JPG", files[0].getName());
    	assertEquals("LISTING_28092015_166000001_2.JPG", files[1].getName());
    	assertEquals("LISTING_28092015_166000002_3.JPG", files[2].getName());
    	assertEquals("LISTING_28092015_166000002_4.JPG", files[3].getName());
    	assertEquals("LISTING_28092015_166000003_5.JPG", files[4].getName());
    	assertEquals("LISTING_28092015_166000003_6.JPG", files[5].getName());
    	assertEquals("LISTING_28092015_166000004_7.JPG", files[6].getName());
    	assertEquals("LISTING_28092015_166000004_8.JPG", files[7].getName());
    	assertEquals("LISTING_28092015_166000005_9.JPG", files[8].getName());
    	assertEquals("LISTING_28092015_166000005_10.JPG", files[9].getName());
    	assertEquals("LISTING_28092015_166000006_11.JPG", files[10].getName());
    	assertEquals("LISTING_28092015_166000006_12.JPG", files[11].getName());    	
    }
    
}
