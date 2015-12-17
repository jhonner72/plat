package com.fujixerox.aus.repository.transform;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 
 * Henry Niu
 * 13/4/2015
 */
public class UpdateVouchersInformationRequestTransformComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldTransformForAdjustment() throws DfException, ParseException {
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
							    
	    new UpdateVouchersInformationRequestTransform().transform(
	    		RepositoryServiceTestHelper.buildVoucherInformationForAdjustment("11111111", "04122015"), fxaVoucher, null);

		assertNotNull(fxaVoucher);

/* TODO This unit test needs rework as it depends on the number of fields, yet relies on configuration (and test data).
   It would make sense to parse the mapping config and validate it against that!

		ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> acValue = ArgumentCaptor.forClass(Boolean.class);
	
		Mockito.verify(fxaVoucher, times(18)).setBoolean(acField.capture(), acValue.capture());
		//assertEquals(FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG, acField.getValue());
		//assertFalse(acValue.getValue());
		
		verify(fxaVoucher, times(2)).setString(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());
		verify(fxaVoucher, times(1)).setTime(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(IDfTime.class).capture());
		verify(fxaVoucher, times(0)).setInt(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(int.class).capture());
*/
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldTransformForAdjustmentFromFile() throws DfException, ParseException, JsonParseException, JsonMappingException, IOException {
		
		Resource resource = new DefaultResourceLoader().getResource("classpath:/request/UpdateVouchersInformationRequest.json");
    	ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());		 
		UpdateVouchersInformationRequest request = mapper.readValue(resource.getFile(), UpdateVouchersInformationRequest.class);
		VoucherInformation voucherInfo = request.getVoucherInformations().toArray(new VoucherInformation[]{})[0];			
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);							    
	    new UpdateVouchersInformationRequestTransform().transform(voucherInfo, fxaVoucher, null);

		assertNotNull(fxaVoucher);
/* TODO This unit test needs rework as it depends on the number of fields, yet relies on configuration (and test data).
   It would make sense to parse the mapping config and validate it against that!

		ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> acValue = ArgumentCaptor.forClass(Boolean.class);
	
		Mockito.verify(fxaVoucher, times(18)).setBoolean(acField.capture(), acValue.capture());
		//assertEquals(FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG, acField.getValue());
		//assertFalse(acValue.getValue());
		
		verify(fxaVoucher, times(2)).setString(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());
		verify(fxaVoucher, times(1)).setTime(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(IDfTime.class).capture());
		verify(fxaVoucher, times(0)).setInt(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(int.class).capture());
*/
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldTransformForCriteria() throws DfException, ParseException {
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.unprocessable", "false"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucher.extraAuxDom", "test1"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucher.processingDate", "24/05/2105"));		
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucher.documentType", "Cr"));		
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherBatch.processingState", "VIC"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherBatch.workType", "NABCHQ_INWARDFV"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.adjustmentReasonCode", "64"));
							    
	    new UpdateVouchersInformationRequestTransform().transform(criterias, fxaVoucher);

		assertNotNull(fxaVoucher);
/* TODO This unit test needs rework as it depends on the number of fields, yet relies on configuration (and test data).
   It would make sense to parse the mapping config and validate it against that!

		ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> acValue = ArgumentCaptor.forClass(Boolean.class);
	
		Mockito.verify(fxaVoucher, times(1)).setBoolean(acField.capture(), acValue.capture());		
		verify(fxaVoucher, times(5)).setString(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());
		verify(fxaVoucher, times(1)).setTime(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(IDfTime.class).capture());
		verify(fxaVoucher, times(0)).setInt(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(int.class).capture());
*/
    }
 
}
