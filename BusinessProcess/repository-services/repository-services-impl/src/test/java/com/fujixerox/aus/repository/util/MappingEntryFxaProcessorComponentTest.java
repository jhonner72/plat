package com.fujixerox.aus.repository.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;

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
    public void shouldProcessInt() throws Exception {
		MappingEntry entry = new MappingEntry("fxa_ap_match_attempt", "voucherProcess.apMatchAttempts", "int", null);

		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setApMatchAttempts(5);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);

		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);

		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);

		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Integer> valueArg = ArgumentCaptor.forClass(Integer.class);

		verify(fxaVoucher).setInt(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_ap_match_attempt", nameArg.getValue());
		assertEquals(5, valueArg.getValue().intValue());
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
    public void shouldProcessReleaseStatusEnumToString() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_release_flag", "voucherProcess.releaseFlag", "string", "ReleaseStatusEnum");
				
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setReleaseFlag(ReleaseStatusEnum.RELEASE);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher, times(1)).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_release_flag", nameArg.getValue());
		assertEquals(ReleaseStatusEnum.RELEASE.value(), valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessAPPresentmentTypeEnumToString() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_ap_presentment_type", "voucherProcess.apPresentmentType", "string", "APPresentmentTypeEnum");
				
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setApPresentmentType(APPresentmentTypeEnum.M);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher, times(1)).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_ap_presentment_type", nameArg.getValue());
		assertEquals(APPresentmentTypeEnum.M.value(), valueArg.getValue());
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
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaWithNoValue() throws Exception {		
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucher.bsbNumber", null);		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher, times(0)).setString(nameArg.capture(), valueArg.capture());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaString() throws Exception {		
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucher.bsbNumber", "888888");		
		MappingEntry entry = new MappingEntry("fxa_bsb", "voucher.bsbNumber", null, null);		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_bsb", nameArg.getValue());
		assertEquals("888888", valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaBoolean() throws Exception {		
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherProcess.inactiveFlag", "true");		
		MappingEntry entry = new MappingEntry("fxa_inactive_flag", "voucherProcess.inactiveFlag", "boolean", null);
				IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> valueArg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(fxaVoucher).setBoolean(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_inactive_flag", nameArg.getValue());
		assertEquals(true, valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaDocumentTypeEnumToString() throws Exception {		
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucher.documentType", "Cr");		
		MappingEntry entry = new MappingEntry("fxa_classification", "voucher.documentType", "string", "DocumentTypeEnum");
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_classification", nameArg.getValue());
		assertEquals(DocumentTypeEnum.CR.value(), valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaStateEnumToString() throws Exception {		
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherBatch.processingState", "VIC");		
		MappingEntry entry = new MappingEntry("fxa_processing_state", "voucherBatch.processingState", "string", "StateEnum");
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_processing_state", nameArg.getValue());
		assertEquals(StateEnum.VIC.value(), valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaReleaseTypeEnumToString() throws Exception {		
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherProcess.repeaseType", "release");		
		MappingEntry entry = new MappingEntry("fxa_release_flag", "voucherProcess.releaseFlag", "string", "ReleaseStatusEnum");
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_release_flag", nameArg.getValue());
		assertEquals(ReleaseStatusEnum.RELEASE.value(), valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaIntToBoolean() throws Exception {		
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherProcess.manualRepair", "1");		
		MappingEntry entry = new MappingEntry("fxa_manual_repair", "voucherProcess.manualRepair", "boolean", "int");
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> valueArg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(fxaVoucher).setBoolean(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_manual_repair", nameArg.getValue());
		assertEquals(true, valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaIntToString() throws Exception {		
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucherProcess.adjustmentReasonCode", "64");		
		MappingEntry entry = new MappingEntry("fxa_adjustment_reason_code", "voucherProcess.adjustmentReasonCode", "string", "int");
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);
		
		verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_adjustment_reason_code", nameArg.getValue());
		assertEquals("64", valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessCriteriaDate() throws Exception {	
		Date now = new Date();
		String nowString = new SimpleDateFormat("dd/MM/yyyy").format(now);
		Criteria criteria = RepositoryServiceTestHelper.buildCriteria("voucher.processingDate", nowString);		
		MappingEntry entry = new MappingEntry("fxa_processing_date", "voucher.processingDate", "time", "date");
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(criteria, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<IDfTime> valueArg = ArgumentCaptor.forClass(IDfTime.class);
		
		IDfTime expectProcessTime = new DfTime(now);
        String dateFormat = expectProcessTime.getDay() + "/" + expectProcessTime.getMonth() + "/" + expectProcessTime.getYear() + " 12:00:00";
        expectProcessTime = new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14);
		
		verify(fxaVoucher).setTime(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_processing_date", nameArg.getValue());
		assertEquals(expectProcessTime, valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToBooleanTrue() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_credit_note_flag", "voucherProcess.creditNoteFlag", "boolean", null);
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setCreditNoteFlag(true);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> valueArg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(fxaVoucher).setBoolean(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_credit_note_flag", nameArg.getValue());
		assertEquals(true, valueArg.getValue());
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessStringToBooleanFalse() throws Exception {		
		MappingEntry entry = new MappingEntry("fxa_credit_note_flag", "voucherProcess.creditNoteFlag", "boolean", "string");
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setCreditNoteFlag(false);
		VoucherInformation voucherInformation = new VoucherInformation();
		voucherInformation.setVoucherProcess(voucherProcess);
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);
		
		ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> valueArg = ArgumentCaptor.forClass(Boolean.class);
		
		verify(fxaVoucher).setBoolean(nameArg.capture(), valueArg.capture());
		assertEquals("fxa_credit_note_flag", nameArg.getValue());
		assertEquals(false, valueArg.getValue());
    }

    @Test
    @Category(AbstractComponentTest.class)
    public void shouldProcessInsertedCreditTypeEnum () throws Exception {
        MappingEntry entry = new MappingEntry("fxa_inserted_credit_type", "voucherProcess.insertedCreditType", "string", "InsertedCreditTypeEnum");

        VoucherProcess voucherProcess = new VoucherProcess();
        voucherProcess.setInsertedCreditType(InsertedCreditTypeEnum.MISSING_CUSTOMER_CREDIT);
        VoucherInformation voucherInformation = new VoucherInformation();
        voucherInformation.setVoucherProcess(voucherProcess);

        IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);

        new MappingEntryFxaProcessor().process(voucherInformation, fxaVoucher, entry);

        ArgumentCaptor<String> nameArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueArg = ArgumentCaptor.forClass(String.class);

        verify(fxaVoucher).setString(nameArg.capture(), valueArg.capture());
        assertEquals("fxa_inserted_credit_type", nameArg.getValue());
        assertEquals(InsertedCreditTypeEnum.MISSING_CUSTOMER_CREDIT.value(), valueArg.getValue());
    }

}
