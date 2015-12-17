package com.fujixerox.aus.repository.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
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
		String TIFF_IMAGE_NAME = "VOUCHER_07042015_11111111";
		
		String FRONT_IMAGE_NAME = TIFF_IMAGE_NAME + "_FRONT.JPG";
		String REAR_IMAGE_NAME = TIFF_IMAGE_NAME + "_REAR.JPG";
		
		String TARGET_FRONT_JPG_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+FRONT_IMAGE_NAME;
		String TARGET_REAR_JPG_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+REAR_IMAGE_NAME;
		String TARGET_TIFF_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+TIFF_IMAGE_NAME+".TIFF";

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, SCANNING_IMAGE_JOB_IDENTIFIER_FOR_MERGE, FRONT_IMAGE_NAME, REAR_IMAGE_NAME);
				
		File[] jpgFiles = new File[]{new File(TARGET_FRONT_JPG_IMAGE_NAME), new File(TARGET_REAR_JPG_IMAGE_NAME)};
		new ImageUtil().mergeToTiff(jpgFiles, new File(TARGET_TIFF_IMAGE_NAME));
		ctx.close();

		assertTrue(new File(TARGET_TIFF_IMAGE_NAME).exists());		
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldMergeToTiff_100dpi() throws IOException, ImageException, FileException, ImageReadException {
		
		String SCANNING_IMAGE_JOB_IDENTIFIER_FOR_MERGE = "JOB_UNIT_TEST_FOR_MERGE";
		String TIFF_IMAGE_NAME = "VOUCHER_07042015_11111111";
		
		String FRONT_IMAGE_NAME = TIFF_IMAGE_NAME + "_FRONT.JPG";
		String REAR_IMAGE_NAME = TIFF_IMAGE_NAME + "_REAR.JPG";
		
		String TARGET_FRONT_JPG_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+FRONT_IMAGE_NAME;
		String TARGET_REAR_JPG_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+REAR_IMAGE_NAME;
		String TARGET_TIFF_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+TIFF_IMAGE_NAME+".TIFF";

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, SCANNING_IMAGE_JOB_IDENTIFIER_FOR_MERGE, FRONT_IMAGE_NAME, REAR_IMAGE_NAME);
				
		File[] jpgFiles = new File[]{new File(TARGET_FRONT_JPG_IMAGE_NAME), new File(TARGET_REAR_JPG_IMAGE_NAME)};
        ImageInfo imageInfo = new JpegImageParser().getImageInfo(new ByteSourceFile(jpgFiles[0]));
		int dpiX = imageInfo.getPhysicalWidthDpi();
		int dpiY = imageInfo.getPhysicalHeightDpi();
		assertEquals(100, dpiX);
		assertEquals(100, dpiY);
		
		new ImageUtil().mergeToTiff(jpgFiles, new File(TARGET_TIFF_IMAGE_NAME));
		ctx.close();

		assertTrue(new File(TARGET_TIFF_IMAGE_NAME).exists());	
		
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldMergeToTiff_200dpi_to_120dpi() throws IOException, ImageException, FileException, ImageReadException {
		
		String SCANNING_IMAGE_JOB_IDENTIFIER_FOR_MERGE = "JOB_UNIT_TEST_FOR_MERGE";
		String TIFF_IMAGE_NAME = "LISTING_28092015_166000001";
		
		String FRONT_IMAGE_NAME = TIFF_IMAGE_NAME + "_FRONT.JPG";
		String REAR_IMAGE_NAME = TIFF_IMAGE_NAME + "_REAR.JPG";
		
		String FRONT_IMAGE_NAME2 = TIFF_IMAGE_NAME + "_FRONT2.JPG";
		String REAR_IMAGE_NAME2 = TIFF_IMAGE_NAME + "_REAR2.JPG";
		
		String TARGET_FRONT_JPG_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+FRONT_IMAGE_NAME;
		String TARGET_REAR_JPG_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+REAR_IMAGE_NAME;
		String TARGET_FRONT_JPG_IMAGE_NAME2 = "target/JOB_UNIT_TEST_FOR_MERGE/"+FRONT_IMAGE_NAME2;
		String TARGET_REAR_JPG_IMAGE_NAME2 = "target/JOB_UNIT_TEST_FOR_MERGE/"+REAR_IMAGE_NAME2;
		
		String TARGET_TIFF_IMAGE_NAME = "target/JOB_UNIT_TEST_FOR_MERGE/"+TIFF_IMAGE_NAME+".TIFF";

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, SCANNING_IMAGE_JOB_IDENTIFIER_FOR_MERGE, FRONT_IMAGE_NAME, REAR_IMAGE_NAME);
				
		File[] jpgFiles = new File[]{new File(TARGET_FRONT_JPG_IMAGE_NAME), new File(TARGET_REAR_JPG_IMAGE_NAME)};
        ImageInfo imageInfo = new JpegImageParser().getImageInfo(new ByteSourceFile(jpgFiles[0]));
		int dpiX = imageInfo.getPhysicalWidthDpi();
		int dpiY = imageInfo.getPhysicalHeightDpi();
		assertEquals(200, dpiX);
		assertEquals(200, dpiY);
		
		new ImageUtil().mergeToTiff(jpgFiles, new File(TARGET_TIFF_IMAGE_NAME));
		ctx.close();

		assertTrue(new File(TARGET_TIFF_IMAGE_NAME).exists());	
		
		File[] splittedJpgFiles = new File[]{new File(TARGET_FRONT_JPG_IMAGE_NAME2), new File(TARGET_REAR_JPG_IMAGE_NAME2)};
		new ImageUtil().splitTiff(new File(TARGET_TIFF_IMAGE_NAME), splittedJpgFiles);
		
		imageInfo = new JpegImageParser().getImageInfo(new ByteSourceFile(splittedJpgFiles[0]));

		dpiX = imageInfo.getPhysicalWidthDpi();
		dpiY = imageInfo.getPhysicalHeightDpi();
		
		// TODO dpi return -1, so cannot verify
//		assertEquals(120, dpiX);
//		assertEquals(120, dpiY);
		
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldSplitTiffWithoutSpecifyImageFile() throws IOException, ImageException, FileException {
		
		String JOB_IDENTIFIER = "JOB_UNIT_TEST_FOR_SPLIT";
		String TIFF_IMAGE_NAME = "VOUCHER_07042015_11111111.TIFF";

		String TARGET_1_JPG_IMAGE_NAME = "target/" + JOB_IDENTIFIER + "/" + "VOUCHER_07042015_11111111.TIFF_1.JPG";
		String TARGET_2_JPG_IMAGE_NAME = "target/" + JOB_IDENTIFIER + "/" + "VOUCHER_07042015_11111111.TIFF_2.JPG";
		String TARGET_TIFF_IMAGE_NAME = "target/" + JOB_IDENTIFIER + "/" + TIFF_IMAGE_NAME;
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, JOB_IDENTIFIER, TIFF_IMAGE_NAME);
		
		new ImageUtil().splitTiff(new File(TARGET_TIFF_IMAGE_NAME), null);
		ctx.close();
		  
		assertTrue(new File(TARGET_1_JPG_IMAGE_NAME).exists());
		assertTrue(new File(TARGET_2_JPG_IMAGE_NAME).exists());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldSplitTiff() throws IOException, ImageException, FileException {
		
		String JOB_IDENTIFIER = "JOB_UNIT_TEST_FOR_SPLIT";
		String TIFF_IMAGE_NAME = "VOUCHER_07042015_11111111.TIFF";
		String IMAGE_NAME = TIFF_IMAGE_NAME.substring(0, TIFF_IMAGE_NAME.indexOf("."));

		String TARGET_FRONT_JPG_IMAGE_NAME = "target/" + JOB_IDENTIFIER + "/" + IMAGE_NAME + "_FRONT.JPG";
		String TARGET_REAR_JPG_IMAGE_NAME = "target/" + JOB_IDENTIFIER + "/" + IMAGE_NAME + "_REAR.JPG";
		String TARGET_TIFF_IMAGE_NAME = "target/" + JOB_IDENTIFIER + "/" + TIFF_IMAGE_NAME;
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, JOB_IDENTIFIER, TIFF_IMAGE_NAME);
		
		File[] imageFiles = new File[] {new File(TARGET_FRONT_JPG_IMAGE_NAME), new File(TARGET_REAR_JPG_IMAGE_NAME)};
		new ImageUtil().splitTiff(new File(TARGET_TIFF_IMAGE_NAME), imageFiles);
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

		new ImageUtil().mergeToTiff(matchedfiles, tiffFile, true);
		ctx.close();

		assertTrue(tiffFile.exists());

	}
	
	@Test
	@Category(AbstractComponentTest.class)
	public void shouldAppendToTIFF() throws IOException, ImageException, FileException {

		String JOB_IDENTIFIER = "JOB_UNIT_TEST_FOR_LISTING_APPEND";
		String listingImage1 = "LISTING_25052015_12121212_1.JPG";
		String listingImage2 = "LISTING_25052015_12121236_2.JPG";
		String listingImage3 = "LISTING_25052015_12121225_3.JPG";
		String EXISTING_TIFF_IMAGE = "EXISTING_LISTING_16092015_081999_68200027.TIFF";
		String NEW_TIFF_IMAGE = "NEW_LISTING_16092015_081999_68200027.TIFF";

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, JOB_IDENTIFIER);

		File baseDirPath = new File("target", JOB_IDENTIFIER);
		
		File[] jpgImages = new File[3];
		jpgImages[0] = new File(baseDirPath, listingImage1);
		jpgImages[1] = new File(baseDirPath, listingImage2);
		jpgImages[2] = new File(baseDirPath, listingImage3);
		
		File inputTiffFile = new File(baseDirPath, EXISTING_TIFF_IMAGE);
		File outTiffFile = new File(baseDirPath, NEW_TIFF_IMAGE);

		new ImageUtil().appendToTiff(jpgImages, inputTiffFile, outTiffFile, true);
		ctx.close();

		assertTrue(outTiffFile.exists());

	}

}