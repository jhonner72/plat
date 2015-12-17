package com.fujixerox.aus.repository.util;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.util.exception.FileException;
import com.fujixerox.aus.repository.util.exception.ImageException;

/** 
 * Henry Niu
 * 30/09/2015
 */
public class FileComparatorComponentTest implements AbstractComponentTest {
      	
    @Test
    @Category(AbstractComponentTest.class)
    public void shouldSortBasedOnFileName() throws FileException, ImageException, IOException  {    	
    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-component-test.xml");
		ImageHelper.prepare(ctx, "JOB_UNIT_TEST_FOR_LISTING_SORT");
		
		File dir = new File("target", "JOB_UNIT_TEST_FOR_LISTING_SORT");
		List<File> list = Arrays.asList(dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".JPG"); // or something else
			}
		}));

		File[] files = (File[]) list.toArray();
		Arrays.sort(files, new FileComparator());
    	
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
