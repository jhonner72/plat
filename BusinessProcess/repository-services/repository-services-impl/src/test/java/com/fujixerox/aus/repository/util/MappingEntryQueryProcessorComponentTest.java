package com.fujixerox.aus.repository.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.ReleaseStatusEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/** 
 * Henry Niu
 * 08/09/2015
 */
public class MappingEntryQueryProcessorComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessWithNoValue() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);		
		Criteria criteria = new Criteria();
		
		String result = new MappingEntryQueryProcessor().process(criteria, entry);
		assertNull("", result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessString() throws Exception {	
		String expectedResult = "fxa_bsb = '888888'";
		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucher.bsbNumber", "888888");	
		
		String result = new MappingEntryQueryProcessor().process(criteria, entry); 
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessBoolean() throws Exception {		
		String expectedResult = "fxa_inactive_flag = true";
		
		MappingEntry entry = new MappingEntry("fxa_inactive_flag", "voucherProcess.inactiveFlag", "boolean", null);
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherProcess.inactiveFlag", "true");
		
		String result = new MappingEntryQueryProcessor().process(criteria, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessDocumentTypeEnumToString() throws Exception {		
		String expectedResult = "fxa_classification = 'CR'";
		
		MappingEntry entry = new MappingEntry("fxa_classification", "voucher.documentType", "string", "DocumentTypeEnum");
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucher.documentType", "CR");
				
		String result = new MappingEntryQueryProcessor().process(criteria, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStateEnumToString() throws Exception {		
		String expectedResult = "fxa_processing_state = 'VIC'";
		
		MappingEntry entry = new MappingEntry("fxa_processing_state", "voucherBatch.processingState", "string", "StateEnum");
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherBatch.processingState", "VIC");
		
		String result = new MappingEntryQueryProcessor().process(criteria, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessReleaseStatusEnumToString() throws Exception {		
		String expectedResult = "fxa_release_flag = 'release'";
		
		MappingEntry entry = new MappingEntry("fxa_release_flag", "voucherProcess.releaseFlag", "string", "ReleaseStatusEnum");
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherProcess.releaseFlag", "release");
		
		String result = new MappingEntryQueryProcessor().process(criteria, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessIntToBoolean() throws Exception {		
		String expectedResult = "fxa_manual_repair = true";
		
		MappingEntry entry = new MappingEntry("fxa_manual_repair", "voucherProcess.manualRepair", "boolean", "int");
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherProcess.manualRepair", "1");
		
		String result = new MappingEntryQueryProcessor().process(criteria, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessIntToString() throws Exception {		
		String expectedResult = "fxa_adjustment_reason_code = '1'";
		
		MappingEntry entry = new MappingEntry("fxa_adjustment_reason_code", "voucherProcess.adjustmentReasonCode", "string", "int");
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherProcess.adjustmentReasonCode", "1");
		
		String result = new MappingEntryQueryProcessor().process(criteria, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessDate() throws Exception {		
		String expectedResult = "fxa_processing_date = date('29/07/2015', 'DD/MM/YYYY')";
		
		MappingEntry entry = new MappingEntry("fxa_processing_date", "voucher.processingDate", "time", "date");
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucher.processingDate", "29/07/2015");
				
		String result = new MappingEntryQueryProcessor().process(criteria, entry);
		assertEquals(expectedResult, result);
    }
	
}
