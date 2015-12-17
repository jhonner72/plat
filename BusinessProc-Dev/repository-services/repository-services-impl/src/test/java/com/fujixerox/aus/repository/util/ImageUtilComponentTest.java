package com.fujixerox.aus.repository.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListing;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.util.exception.FileException;
import com.fujixerox.aus.repository.util.exception.ImageException;


/** 
 * Henry Niu
 * 02/04/2015
 */
public class ImageUtilComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldMergeToTiff() throws IOException, ImageException, FileException {
		
		String SCANNING_IMAGE_JOB_IDENTIFIER_FOR_MERGE = "JOB_UNIT_TEST_FOR_MERGE";
		String FRONT_IMAGE_NAME = "VOUCHER_07042015_11111111_FRONT.JPG";
		String REAR_IMAGE_NAME = "VOUCHER_07042015_11111111_REAR.JPG";
		
		String TARGET_FRONT_JPG_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/VOUCHER_07042015_11111111_FRONT.JPG";
		String TARGET_REAR_JPG_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/VOUCHER_07042015_11111111_REAR.JPG";
		String TARGET_TIFF_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/VOUCHER_07042015_11111111.TIFF";

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, SCANNING_IMAGE_JOB_IDENTIFIER_FOR_MERGE, FRONT_IMAGE_NAME, REAR_IMAGE_NAME);
				
		new ImageUtil().mergeToTiff(new File(TARGET_FRONT_JPG_IMAGE_NAME), new File(TARGET_REAR_JPG_IMAGE_NAME), 
				new File(TARGET_TIFF_IMAGE_NAME));
		ctx.close();

		assertTrue(new File(TARGET_TIFF_IMAGE_NAME).exists());		
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldSplitTiff() throws IOException, ImageException, FileException {
		
		String SCANNING_IMAGE_JOB_IDENTIFIER_FOR_SPLIT = "JOB_UNIT_TEST_FOR_SPLIT";
		String TIFF_IMAGE_NAME = "VOUCHER_07042015_11111111.TIFF";
		String IMAGE_NAME = TIFF_IMAGE_NAME.substring(0, TIFF_IMAGE_NAME.indexOf("."));

		String TARGET_FRONT_JPG_IMAGE_NAME = "target/" + SCANNING_IMAGE_JOB_IDENTIFIER_FOR_SPLIT + "/" + IMAGE_NAME + "_FRONT.JPG";
		String TARGET_REAR_JPG_IMAGE_NAME = "target/" + SCANNING_IMAGE_JOB_IDENTIFIER_FOR_SPLIT + "/" + IMAGE_NAME + "_REAR.JPG";
		String TARGET_TIFF_IMAGE_NAME = "target/" + SCANNING_IMAGE_JOB_IDENTIFIER_FOR_SPLIT + "/" + TIFF_IMAGE_NAME;
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, SCANNING_IMAGE_JOB_IDENTIFIER_FOR_SPLIT, TIFF_IMAGE_NAME);
		
		new ImageUtil().splitTiff(new File(TARGET_TIFF_IMAGE_NAME), new File(TARGET_FRONT_JPG_IMAGE_NAME), 
				new File(TARGET_REAR_JPG_IMAGE_NAME));
		ctx.close();

		assertTrue(new File(TARGET_FRONT_JPG_IMAGE_NAME).exists());
		assertTrue(new File(TARGET_REAR_JPG_IMAGE_NAME).exists());
	}

	@Test
	@Category(AbstractComponentTest.class)
	public void shouldMergeListingToTIFF() throws IOException, ImageException, FileException {

		String SCANNING_LISTING_JOB_IDENTIFIER_FOR_SPLIT = "JOB_UNIT_TEST_FOR_LISTING_MERGE";
		String listingImage1 = "LISTING_25052015_12121212_1.JPG";
		String listingImage2 = "LISTING_25052015_12121236_2.JPG";
		String listingImage3 = "LISTING_25052015_12121225_3.JPG";

		String TIFF_IMAGE_NAME = "LISTING_25052015-04333_121212.TIFF";

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, SCANNING_LISTING_JOB_IDENTIFIER_FOR_SPLIT, listingImage1, listingImage2, listingImage3);

		File baseDirPath = new File("target/JOB_UNIT_TEST_FOR_LISTING_MERGE/");

		File[] matchedfiles = baseDirPath.listFiles();

		File tiffFile = new File(baseDirPath, TIFF_IMAGE_NAME);

		new ImageUtil().mergeListingToTiff(matchedfiles, tiffFile);
		ctx.close();

		assertTrue(tiffFile.exists());

	}

}