package com.fujixerox.aus.lombard.outclearings.assetManagement;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.metadata.AssetManagement;
import com.fujixerox.aus.lombard.common.metadata.AssetRetention;

public class AssetmanagementJobAdapterTest {

	private static final String JOB_DB = "jobdb";
	private static final String META_JOBFILE = "jobfile";
	private static final String META_INBOUNDFILE = "inboundarchivefile";
	private static final String META_OUTBOUNDFILE = "outboundarchivefile";
	private static final String CAMUNDA_DB = "camundadb";
	
	String lockerPath;
	String archiveInboundPath;
	String archiveOutboundPath;
    JobStore jobStore;
    MetadataStore metadataStore;

    @Before
    public void setup()
    {
    	lockerPath = "target/bitlocker/job";
    	archiveInboundPath = "target/archive/inbound";
    	archiveOutboundPath = "target/archive/outbound";

        metadataStore = mock(MetadataStore.class);
        
        AssetManagement mockAssetManagement = mock(AssetManagement.class);
        when(metadataStore.getMetadata(AssetManagement.class)).thenReturn(mockAssetManagement);
        
        ArrayList<AssetRetention> mockAssetRetentionList = new ArrayList<AssetRetention>();
        AssetRetention mockAssetRetention = new AssetRetention();
        mockAssetRetention.setAssetName(JOB_DB);
        mockAssetRetention.setRetentionDays(7);
        mockAssetRetentionList.add(mockAssetRetention);
        mockAssetRetention = new AssetRetention();
        mockAssetRetention.setAssetName(META_JOBFILE);
        mockAssetRetention.setRetentionDays(7);
        mockAssetRetentionList.add(mockAssetRetention);
        mockAssetRetention = new AssetRetention();
        mockAssetRetention.setAssetName(META_INBOUNDFILE);
        mockAssetRetention.setRetentionDays(14);
        mockAssetRetentionList.add(mockAssetRetention);
        mockAssetRetention = new AssetRetention();
        mockAssetRetention.setAssetName(META_OUTBOUNDFILE);
        mockAssetRetention.setRetentionDays(14);
        mockAssetRetentionList.add(mockAssetRetention);
        mockAssetRetention = new AssetRetention();
        mockAssetRetention.setAssetName(CAMUNDA_DB);
        mockAssetRetention.setRetentionDays(60);
        mockAssetRetentionList.add(mockAssetRetention);
        when(mockAssetManagement.getAssetRetentions()).thenReturn(mockAssetRetentionList);
        
    }
    
    @Test
    public void testClearJobDatabase() {
    	int retentionDays = findAssetRetentionDays(JOB_DB);
    	assertThat(retentionDays, is(7));
    	
    	// TODO
    }

    /**
     * Local folder manual test only
     */
    @Test
    @Ignore
    public void testFileTimestampChangeForManualTest() {
    	// prepare test folder
    	File srcFolder = new File("C:\\bitlocker\\job_test");
    	File targetFolder = new File("C:\\bitlocker\\job");
    	try {
    		if (targetFolder.exists()) FileUtils.cleanDirectory(targetFolder);
			FileUtils.copyDirectory(srcFolder, targetFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	assertThat(targetFolder.exists(), is(true));
    	
    	int retentionDays = findAssetRetentionDays(META_INBOUNDFILE);
    	assertThat(retentionDays, is(14));
    	
    	// change last modified date for testing files
        Date testingFileDate = getRetentionDate(retentionDays+1);	// 1 day older
        for (File file: targetFolder.listFiles()) {
        	file.setLastModified(testingFileDate.getTime()); // update date
        }
    }
    
    
    @Test
    public void testClearBitLockerJobs() {
    	// prepare test folder
    	File srcFolder = new File("src/test/resources/data/bitLockerPath/job");
    	File targetFolder = new File(lockerPath);
    	try {
    		if (targetFolder.exists()) FileUtils.cleanDirectory(targetFolder);
			FileUtils.copyDirectory(srcFolder, targetFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	assertThat(targetFolder.exists(), is(true));
    	
    	int retentionDays = findAssetRetentionDays(META_JOBFILE);
    	assertThat(retentionDays, is(7));
    	
    	// change last modified date for testing files
        Date testingFileDate = getRetentionDate(retentionDays+1);	// 1 day older
        Date dontRemoveFileDate = getRetentionDate(retentionDays-1);	// 1 day early
        for (File file: targetFolder.listFiles()) {
        	if (file.getName().startsWith("NVIF-")) {	// ONLY CHANGE DATE START WITH NVIF
        		file.setLastModified(testingFileDate.getTime()); // update date
        	} else {
        		file.setLastModified(dontRemoveFileDate.getTime()); // DONT-REMOVE-12c8d3e4-eeec-45e7-be43
        	}
        }
    	
    	Date retentionDate = getRetentionDate(retentionDays);
    	System.out.println("retentionDate:"+retentionDate);
    	
        for (File file: targetFolder.listFiles()) {
            System.out.println(file.toString());
            
            Date fileDate = new Date(file.lastModified());
            if (fileDate.before(retentionDate)) {
            	deleteFileOrFolder(file);
            	System.out.println("DELETE:"+file.getAbsolutePath()+":"+fileDate);
            } 
        }
        
        assertThat(targetFolder.listFiles().length, is(1));
        assertThat(targetFolder.listFiles()[0].getName(), is("DONT-REMOVE-12c8d3e4-eeec-45e7-be43"));
    }
    
    @Test
    public void testClearArchiveInbound() {
    	// prepare test folder
    	File srcFolder = new File("src/test/resources/data/bitLockerPath/job");// reuse Job folder test files
    	File targetFolder = new File(archiveInboundPath);
    	try {
    		if (targetFolder.exists()) FileUtils.cleanDirectory(targetFolder);
			FileUtils.copyDirectory(srcFolder, targetFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	assertThat(targetFolder.exists(), is(true));
    	
    	int retentionDays = findAssetRetentionDays(META_JOBFILE);
    	assertThat(retentionDays, is(7));
    	
    	// change last modified date for testing files
        Date testingFileDate = getRetentionDate(retentionDays+1);	// 1 day older
        Date dontRemoveFileDate = getRetentionDate(retentionDays-1);	// 1 day early
        for (File file: targetFolder.listFiles()) {
        	if (file.getName().startsWith("NVIF-")) {	// ONLY CHANGE DATE START WITH NVIF
        		file.setLastModified(testingFileDate.getTime()); // update date
        	} else {
        		file.setLastModified(dontRemoveFileDate.getTime()); // DONT-REMOVE-12c8d3e4-eeec-45e7-be43
        	}
        }
    	
    	Date retentionDate = getRetentionDate(retentionDays);
    	System.out.println("retentionDate:"+retentionDate);
    	
        for (File file: targetFolder.listFiles()) {
            System.out.println(file.toString());
            
            Date fileDate = new Date(file.lastModified());
            if (fileDate.before(retentionDate)) {
            	deleteFileOrFolder(file);
            	System.out.println("DELETE:"+file.getAbsolutePath()+":"+fileDate);
            } 
        }
        
        assertThat(targetFolder.listFiles().length, is(1));
        assertThat(targetFolder.listFiles()[0].getName(), is("DONT-REMOVE-12c8d3e4-eeec-45e7-be43"));
    }
    
    @Test
    public void testClearArchiveOutbound() {
    	// prepare test folder
    	File srcFolder = new File("src/test/resources/data/bitLockerPath/job");// reuse Job folder test files
    	File targetFolder = new File(archiveOutboundPath);
    	try {
    		if (targetFolder.exists()) FileUtils.cleanDirectory(targetFolder);
			FileUtils.copyDirectory(srcFolder, targetFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	assertThat(targetFolder.exists(), is(true));
    	
    	int retentionDays = findAssetRetentionDays(META_JOBFILE);
    	assertThat(retentionDays, is(7));
    	
    	// change last modified date for testing files
        Date testingFileDate = getRetentionDate(retentionDays+1);	// 1 day older
        Date dontRemoveFileDate = getRetentionDate(retentionDays-1);	// 1 day early
        for (File file: targetFolder.listFiles()) {
        	if (file.getName().startsWith("NVIF-")) {	// ONLY CHANGE DATE START WITH NVIF
        		file.setLastModified(testingFileDate.getTime()); // update date
        	} else {
        		file.setLastModified(dontRemoveFileDate.getTime()); // DONT-REMOVE-12c8d3e4-eeec-45e7-be43
        	}
        }
    	
    	Date retentionDate = getRetentionDate(retentionDays);
    	System.out.println("retentionDate:"+retentionDate);
    	
        for (File file: targetFolder.listFiles()) {
            System.out.println(file.toString());
            
            Date fileDate = new Date(file.lastModified());
            if (fileDate.before(retentionDate)) {
            	deleteFileOrFolder(file);
            	System.out.println("DELETE:"+file.getAbsolutePath()+":"+fileDate);
            } 
        }
        
        assertThat(targetFolder.listFiles().length, is(1));
        assertThat(targetFolder.listFiles()[0].getName(), is("DONT-REMOVE-12c8d3e4-eeec-45e7-be43"));
    }

    private int findAssetRetentionDays(String assetName) {
        AssetManagement assetManagement = metadataStore.getMetadata(AssetManagement.class);
        for (AssetRetention assetRetention: assetManagement.getAssetRetentions()) {
            if (assetRetention.getAssetName().equalsIgnoreCase(assetName)) {
            	return assetRetention.getRetentionDays();
            }
        }
        return -1;
	}
    
	private void deleteFileOrFolder(File file) {
		if (file.isDirectory()) {
			try {
				FileUtils.deleteDirectory(file);	// delete folder
			} catch (IOException e) {
			}
		} else {
			FileUtils.deleteQuietly(file);	// delete file
		}
	}
    
    private Date getRetentionDate(int retentionDays) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -retentionDays);
        Date retentionDate = c.getTime();
        return retentionDate;
    }
    
    @Test
    public void testClearCamundaDatabase() {
    	int retentionDays = findAssetRetentionDays(CAMUNDA_DB);
    	assertThat(retentionDays, is(60));

    	// TODO
    }
    
}
