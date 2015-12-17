package com.fujixerox.aus.repository.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.repository.AbstractComponentTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/** 
 * Henry Niu
 * 23/07/2015
 */
public class MappingEntryQueryProcessorComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessWithNoValue() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);
		
		VoucherInformation voucherInformation = new VoucherInformation();
		
		String result = new MappingEntryQueryProcessor().process(voucherInformation, entry);
		assertEquals("", result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessString() throws Exception {	
		String expectedResult = "fxa_bsb = '888888' \nAND ";
		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);
		
		Voucher voucher = new Voucher();
		voucher.setBsbNumber("888888");
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucher(voucher);		
		
		String result = new MappingEntryQueryProcessor().process(voucherInformation, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessBoolean() throws Exception {		
		String expectedResult = "fxa_inactive_flag = true \nAND ";
		
		MappingEntry entry = new MappingEntry("fxa_inactive_flag", "voucherProcess.inactiveFlag", "boolean", null);
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setInactiveFlag(true);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		String result = new MappingEntryQueryProcessor().process(voucherInformation, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessDocumentTypeEnumToString() throws Exception {		
		String expectedResult = "fxa_classification = 'CR' \nAND ";
		
		MappingEntry entry = new MappingEntry("fxa_classification", "voucher.documentType", "string", "DocumentTypeEnum");
				
		Voucher voucher = new Voucher();
		voucher.setDocumentType(DocumentTypeEnum.CR);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucher(voucher);
		
		String result = new MappingEntryQueryProcessor().process(voucherInformation, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStateEnumToString() throws Exception {		
		String expectedResult = "fxa_processing_state = 'VIC' \nAND ";
		
		MappingEntry entry = new MappingEntry("fxa_processing_state", "voucherBatch.processingState", "string", "StateEnum");
				
		VoucherBatch voucherBatch = new VoucherBatch();
		voucherBatch.setProcessingState(StateEnum.VIC);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherBatch(voucherBatch);
		
		String result = new MappingEntryQueryProcessor().process(voucherInformation, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessIntToBoolean() throws Exception {		
		String expectedResult = "fxa_manual_repair = true \nAND ";
		
		MappingEntry entry = new MappingEntry("fxa_manual_repair", "voucherProcess.manualRepair", "boolean", "int");
				
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setManualRepair(1);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		String result = new MappingEntryQueryProcessor().process(voucherInformation, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessIntToString() throws Exception {		
		String expectedResult = "fxa_adjustment_reason_code = '1' \nAND ";
		
		MappingEntry entry = new MappingEntry("fxa_adjustment_reason_code", "voucherProcess.adjustmentReasonCode", "string", "int");
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setAdjustmentReasonCode(1);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		String result = new MappingEntryQueryProcessor().process(voucherInformation, entry);
		assertEquals(expectedResult, result);
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessDate() throws Exception {		
		String expectedResult = "fxa_processing_date = date('29/07/2015', 'DD/MM/YYYY') \nAND ";
		
		MappingEntry entry = new MappingEntry("fxa_processing_date", "voucher.processingDate", "time", "date");
		
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2015-07-29");
				
		Voucher voucher = new Voucher();
		voucher.setProcessingDate(date);		
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucher(voucher);
		
		String result = new MappingEntryQueryProcessor().process(voucherInformation, entry);
		assertEquals(expectedResult, result);
    }		
	
}
