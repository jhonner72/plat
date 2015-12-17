package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fujixerox.aus.integration.transform.TransformationTestUtil;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;

public class DirectoryVoucherCollectorTest {
	
	private DirectoryVoucherCollector unitUnderTest;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	
	@Before
	public void setup(){
		unitUnderTest = new DirectoryVoucherCollector();
	}
	
	@Test
	public void shouldProcessAllJsonFiles() throws Exception{
	    int voucherInfoCount = 13;
	    String accountPrefix = "accountNumber";
	    Date expectedDate  = TransformationTestUtil.getDateWithoutTimestamp();
	   
		// Setup
        List<VoucherInformation> inputVoucherInformationList = TransformationTestUtil.buildVoucherInformationList(voucherInfoCount, expectedDate, "someCaptureBSB", accountPrefix);
		
		File voucherInforDirectory = TransformationTestUtil.writeVoucherInformationFiles(inputVoucherInformationList);
		
		writeNonJsonFile(voucherInforDirectory);

		// Execute
		List<VoucherInformation> collectedVoucherInformationList = unitUnderTest.collectVoucherInformationFrom(voucherInforDirectory);
		
		//Assert
        assertCollectedVoucherInformationList(collectedVoucherInformationList, voucherInfoCount, accountPrefix, expectedDate);
		
	}
	
	@Test
	public void shouldFailProcessWhenAtLeastOneJsonFIleCannotBeMarshaled() throws Exception{
	    int voucherInfoCount = 7;
	    String accountPrefix = "accountNumber";
	    Date expectedDate  = TransformationTestUtil.getDateWithoutTimestamp();
	    String invalidVoucherInformationFileName = "nonVoucherInformationFile.json";
	    
	    // Setup
	    List<VoucherInformation> inputVoucherInformationList = TransformationTestUtil.buildVoucherInformationList(voucherInfoCount, expectedDate, "someCaptureBSB", accountPrefix);
	    
	    File voucherInforDirectory = TransformationTestUtil.writeVoucherInformationFiles(inputVoucherInformationList);
	    
	    writeNonJsonFile(voucherInforDirectory);
	    writeNonVoucherInformationFile(voucherInforDirectory, invalidVoucherInformationFileName);
	    
	    
	    expectedException.expect(RuntimeException.class);
	    expectedException.expectMessage(Matchers.containsString(invalidVoucherInformationFileName));
	    // Execute
	    unitUnderTest.collectVoucherInformationFrom(voucherInforDirectory);
	    
	}
	
	private void writeNonVoucherInformationFile(File voucherInforDirectory, String fileName) throws Exception {
	    
	    
        File file = new File(voucherInforDirectory, fileName);
	    String content = "I'm not a valid VoucherInformationFile, maybe I was corrupted";
	    
	    writteTextFile(file, content);        
	}

	private void writeNonJsonFile(File voucherInforDirectory) throws Exception {
        
        File file = new File(voucherInforDirectory, "nonJsonFile.txt");
        String content = "I'm not Json content please do not collect me!!!";
        
        writteTextFile(file, content);        
    }

    private void writteTextFile(File file, String content) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
        } finally {
            fileWriter.close();
        }
    }

    private void assertCollectedVoucherInformationList(List<VoucherInformation> collectedVoucherInformationList, int voucherInfoCount, String accountPrefix, Date expectedDate) {

        assertThat(collectedVoucherInformationList, Matchers.hasSize(voucherInfoCount));
        
        accountingFor:for (int i = 0; i < voucherInfoCount; i++) {
            
            String expectedAccountNumber = accountPrefix + i;
            for (VoucherInformation voucherInformation : collectedVoucherInformationList) {
                
                assertThat(voucherInformation.getVoucher(), is(notNullValue()));
                assertThat(voucherInformation.getVoucher().getAccountNumber(), is(notNullValue()));
                String accountNumber = voucherInformation.getVoucher().getAccountNumber();

                if(expectedAccountNumber.equals(accountNumber)){
                    assertThat(voucherInformation.getVoucher().getProcessingDate(), is(equalTo(expectedDate)));
                    continue accountingFor;
                }
            }
            
            fail("Could not find VoucherInfo for Voucher.accountNumber " + expectedAccountNumber);
            
        }
        
    }
    
  
}
