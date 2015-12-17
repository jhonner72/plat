package com.fujixerox.aus.repository.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import com.fujixerox.aus.lombard.common.voucher.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfTime;
import com.fujixerox.aus.repository.AbstractComponentTest;

/** 
 * Henry Niu
 * 23/07/2015
 */
public class MappingEntryObjectProcessorComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessWithNoValue() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);
		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(new Voucher());
		voucherInfo.setVoucherBatch(new VoucherBatch());
		voucherInfo.setVoucherProcess(new VoucherProcess());
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);		
		verify(fxaVoucher, times(0)).setString(nameArg.capture(), valueArg.capture());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessString() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);
		
		Voucher voucher = mock(Voucher.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucher()).thenReturn(voucher);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getString(matches("fxa_bsb"))).thenReturn("888888");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
		
		verify(voucherInfo).getVoucher();
		verify(voucher).setBsbNumber(arg.capture());		
		assertEquals("888888", arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessBoolean() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_inactive_flag", "voucherProcess.inactiveFlag", "boolean", null);
		
		VoucherProcess voucherProcess = mock(VoucherProcess.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getBoolean(matches("fxa_inactive_flag"))).thenReturn(true);
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<Boolean> arg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(voucherInfo).getVoucherProcess();
		verify(voucherProcess).setInactiveFlag(arg.capture());		
		assertEquals(true, arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessInt() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_ap_match_attempt", "voucherProcess.apMatchAttempts", "int", null);
		
		VoucherProcess voucherProcess = mock(VoucherProcess.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getInt(matches("fxa_ap_match_attempt"))).thenReturn(5);
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<Integer> arg = ArgumentCaptor.forClass(Integer.class);
		
		verify(voucherInfo).getVoucherProcess();
		verify(voucherProcess).setApMatchAttempts(arg.capture());		
		assertEquals(new Integer(5), arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToDocumentTypeEnum() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_classification", "voucher.documentType", "string", "DocumentTypeEnum");
				
		Voucher voucher = mock(Voucher.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucher()).thenReturn(voucher);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getString(matches("fxa_classification"))).thenReturn("Cr");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<DocumentTypeEnum> arg = ArgumentCaptor.forClass(DocumentTypeEnum.class);
		
		verify(voucherInfo).getVoucher();
		verify(voucher).setDocumentType(arg.capture());		
		assertEquals(DocumentTypeEnum.CR, arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToWorkTypeEnum() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_work_type_code", "voucherBatch.workType", "string", "WorkTypeEnum");
				
		VoucherBatch voucherBatch = mock(VoucherBatch.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherBatch()).thenReturn(voucherBatch);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getString(matches("fxa_work_type_code"))).thenReturn("NABCHQ_INWARDFV");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<WorkTypeEnum> arg = ArgumentCaptor.forClass(WorkTypeEnum.class);
		
		verify(voucherInfo).getVoucherBatch();
		verify(voucherBatch).setWorkType(arg.capture());		
		assertEquals(WorkTypeEnum.NABCHQ_INWARDFV, arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToStateEnum() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_processing_state", "voucherBatch.processingState", "string", "StateEnum");
				
		VoucherBatch voucherBatch = mock(VoucherBatch.class);		
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherBatch()).thenReturn(voucherBatch);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getString(matches("fxa_processing_state"))).thenReturn("VIC");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<StateEnum> arg = ArgumentCaptor.forClass(StateEnum.class);
		
		verify(voucherInfo).getVoucherBatch();
		verify(voucherBatch).setProcessingState(arg.capture());		
		assertEquals(StateEnum.VIC, arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToReleaseStatusEnum() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_release_flag", "voucherProcess.releaseFlag", "string", "ReleaseStatusEnum");
				
		VoucherProcess voucherProcess = mock(VoucherProcess.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getString(matches("fxa_release_flag"))).thenReturn("release");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<ReleaseStatusEnum> arg = ArgumentCaptor.forClass(ReleaseStatusEnum.class);
		
		verify(voucherInfo).getVoucherProcess();
		verify(voucherProcess).setReleaseFlag(arg.capture());		
		assertEquals(ReleaseStatusEnum.RELEASE, arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToAPPresentmentTypeEnum() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_ap_presentment_type", "voucherProcess.apPresentmentType", "string", "APPresentmentTypeEnum");
				
		VoucherProcess voucherProcess = mock(VoucherProcess.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getString(matches("fxa_ap_presentment_type"))).thenReturn("M");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<APPresentmentTypeEnum> arg = ArgumentCaptor.forClass(APPresentmentTypeEnum.class);
		
		verify(voucherInfo).getVoucherProcess();
		verify(voucherProcess).setApPresentmentType(arg.capture());		
		assertEquals(APPresentmentTypeEnum.M, arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessBooleanToInt() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_manual_repair", "voucherProcess.manualRepair", "boolean", "int");
				
		VoucherProcess voucherProcess = mock(VoucherProcess.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getBoolean(matches("fxa_manual_repair"))).thenReturn(true);
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<Integer> arg = ArgumentCaptor.forClass(Integer.class);
		
		verify(voucherInfo).getVoucherProcess();
		verify(voucherProcess).setManualRepair(arg.capture());		
		assertEquals(new Integer(1), arg.getValue());		
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToInt() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_adjustment_reason_code", "voucherProcess.adjustmentReasonCode", "string", "int");
				
		VoucherProcess voucherProcess = mock(VoucherProcess.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);   
		when(fxaVoucher.getString(matches("fxa_adjustment_reason_code"))).thenReturn("64");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<Integer> arg = ArgumentCaptor.forClass(Integer.class);
		
		verify(voucherInfo).getVoucherProcess();
		verify(voucherProcess).setAdjustmentReasonCode(arg.capture());		  
		assertEquals(new Integer(64), arg.getValue());		
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessDate() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_processing_date", "voucher.processingDate", "time", "date");
				
		Voucher voucher = mock(Voucher.class);		
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucher()).thenReturn(voucher);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		Date date = new Date();
		when(fxaVoucher.getTime(matches("fxa_processing_date"))).thenReturn(new DfTime(date));
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<Date> arg = ArgumentCaptor.forClass(Date.class);
		
		verify(voucherInfo).getVoucher();
		verify(voucher).setProcessingDate(arg.capture());		
		assertTrue(date.compareTo(arg.getValue()) >= 0);
    }		
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToStateEnumWithSkip() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_processing_state", "voucherBatch.processingState", "string", "StateEnum");
				
		VoucherBatch voucherBatch = mock(VoucherBatch.class);		
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherBatch()).thenReturn(voucherBatch);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getString(matches("fxa_processing_state"))).thenReturn("");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<StateEnum> arg = ArgumentCaptor.forClass(StateEnum.class);
		
		verify(voucherInfo, times(0)).getVoucherBatch();
		verify(voucherBatch, times(0)).setProcessingState(arg.capture());		
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessEmptyStringWithoutSkip() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_aux_dom", "voucher.auxDom", null, null);
				
		Voucher voucher = mock(Voucher.class);		
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucher()).thenReturn(voucher);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getString(matches("fxa_aux_dom"))).thenReturn("");
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		
		ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
		
		verify(voucherInfo).getVoucher();
		verify(voucher).setAuxDom(arg.capture());		
		assertEquals("", arg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessBooleanTrueToString() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_credit_note_flag", "voucherProcess.creditNoteFlag", "boolean", null);
				
		VoucherProcess voucherProcess = mock(VoucherProcess.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);
		
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getBoolean(matches("fxa_credit_note_flag"))).thenReturn(true);
		
		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);

		ArgumentCaptor<Boolean> arg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(voucherInfo).getVoucherProcess();
		verify(voucherProcess).setCreditNoteFlag(arg.capture());		
		assertEquals(true , arg.getValue());
    }

	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessBooleanFalseToString() throws Exception {
		MappingEntry entry = new MappingEntry("fxa_credit_note_flag", "voucherProcess.creditNoteFlag", "boolean", null);

		VoucherProcess voucherProcess = mock(VoucherProcess.class);
		VoucherInformation voucherInfo = mock(VoucherInformation.class);
		when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);

		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getBoolean(matches("fxa_credit_note_flag"))).thenReturn(false);

		new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);

		ArgumentCaptor<Boolean> arg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(voucherInfo).getVoucherProcess();
		verify(voucherProcess).setCreditNoteFlag(arg.capture());		
		assertEquals(false, arg.getValue());
    }

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToInsertedCreditTypeEnum() throws Exception {
        MappingEntry entry = new MappingEntry("fxa_inserted_credit_type", "voucherProcess.insertedCreditType", "string", "InsertedCreditTypeEnum");

        VoucherProcess voucherProcess = mock(VoucherProcess.class);
        VoucherInformation voucherInfo = mock(VoucherInformation.class);
        when(voucherInfo.getVoucherProcess()).thenReturn(voucherProcess);

        IDfSysObject fxaVoucher = mock(IDfSysObject.class);
        when(fxaVoucher.getString(matches("fxa_inserted_credit_type"))).thenReturn("MISSING_CUSTOMER_CREDIT");

        new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);

        ArgumentCaptor<InsertedCreditTypeEnum> arg = ArgumentCaptor.forClass(InsertedCreditTypeEnum.class);

        verify(voucherInfo).getVoucherProcess();
        verify(voucherProcess).setInsertedCreditType(arg.capture());
        assertEquals(InsertedCreditTypeEnum.MISSING_CUSTOMER_CREDIT, arg.getValue());
    }

}
