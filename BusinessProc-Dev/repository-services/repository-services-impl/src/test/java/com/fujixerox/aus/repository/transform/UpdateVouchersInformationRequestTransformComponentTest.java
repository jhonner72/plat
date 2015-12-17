package com.fujixerox.aus.repository.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
	    		RepositoryServiceTestHelper.buildVoucherInformationForAdjustment("11111111", "04122015"), fxaVoucher);
			
		ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> acValue = ArgumentCaptor.forClass(Boolean.class);
	
		Mockito.verify(fxaVoucher).setBoolean(acField.capture(), acValue.capture());
		assertEquals(FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG, acField.getValue());
		assertFalse(acValue.getValue());
		
		verify(fxaVoucher, times(0)).setString(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());
		verify(fxaVoucher, times(0)).setTime(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(IDfTime.class).capture());
		verify(fxaVoucher, times(0)).setInt(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(int.class).capture());
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
	    new UpdateVouchersInformationRequestTransform().transform(voucherInfo, fxaVoucher);
			
		ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> acValue = ArgumentCaptor.forClass(Boolean.class);
	
		Mockito.verify(fxaVoucher).setBoolean(acField.capture(), acValue.capture());
		assertEquals(FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG, acField.getValue());
		assertFalse(acValue.getValue());
		
		verify(fxaVoucher, times(0)).setString(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());
		verify(fxaVoucher, times(0)).setTime(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(IDfTime.class).capture());
		verify(fxaVoucher, times(0)).setInt(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(int.class).capture());
    }
 
}
