package com.fujixerox.aus.repository.util;

import java.util.Date;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
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
public class MappingEntryFxaProcessorComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessWithNoValue() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);
		
		VoucherInformation voucherInformation = new VoucherInformation();
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher, times(0)).setString(nameArg.capture(), valueArg.capture());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessString() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);
		
		Voucher voucher = new Voucher();
		voucher.setBsbNumber("888888");
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucher(voucher);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_bsb", nameArg.getValue());
		assertEquals("888888", valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessBoolean() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_inactive_flag", "voucherProcess.inactiveFlag", "boolean", null);
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setInactiveFlag(true);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> valueArg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(fxaVoucher).setBoolean(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_inactive_flag", nameArg.getValue());
		assertEquals(true, valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessDocumentTypeEnumToString() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_classification", "voucher.documentType", "string", "DocumentTypeEnum");
				
		Voucher voucher = new Voucher();
		voucher.setDocumentType(DocumentTypeEnum.CR);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucher(voucher);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_classification", nameArg.getValue());
		assertEquals(DocumentTypeEnum.CR.value(), valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStateEnumToString() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_processing_state", "voucherBatch.processingState", "string", "StateEnum");
				
		VoucherBatch voucherBatch = new VoucherBatch();
		voucherBatch.setProcessingState(null);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherBatch(voucherBatch);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher, times(0)).setString(nameArg.capture(), valueArg.capture());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessIntToBoolean() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_manual_repair", "voucherProcess.manualRepair", "boolean", "int");
				
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setManualRepair(1);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> valueArg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(fxaVoucher).setBoolean(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_manual_repair", nameArg.getValue());
		assertEquals(true, valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessIntToString() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_adjustment_reason_code", "voucherProcess.adjustmentReasonCode", "string", "int");
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setAdjustmentReasonCode(1);;
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_adjustment_reason_code", nameArg.getValue());
		assertEquals("1", valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessDate() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_processing_date", "voucher.processingDate", "time", "date");
				
		Voucher voucher = new Voucher();
		voucher.setProcessingDate(new Date());		
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucher(voucher);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<IDfTime> valueArg = ArgumentCaptor.forClass(IDfTime.class);
		
		IDfTime expectProcessTime = new DfTime(new Date());
        String dateFormat = expectProcessTime.getDay() + "/" + expectProcessTime.getMonth() + "/" + expectProcessTime.getYear() + " 12:00:00";
        expectProcessTime = new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14);
		
		verify(fxaVoucher).setTime(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_processing_date", nameArg.getValue());
		assertEquals(expectProcessTime, valueArg.getValue());
    }		
	
/*	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessBooleanToPrimitive() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_generated_voucher_flag", "voucherProcess.isGeneratedVoucher", "boolean", "Boolean");
				
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setIsGeneratedVoucher(Boolean.TRUE);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> valueArg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(fxaVoucher).setBoolean(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_generated_voucher_flag", nameArg.getValue());
		assertEquals(true, valueArg.getValue());
    }*/
	
}
