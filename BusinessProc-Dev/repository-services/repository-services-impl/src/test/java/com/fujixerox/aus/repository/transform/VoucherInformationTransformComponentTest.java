package com.fujixerox.aus.repository.transform;

import java.text.ParseException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class VoucherInformationTransformComponentTest implements AbstractComponentTest {

	@Test
    @Category(AbstractComponentTest.class)
    public void shouldTransform() throws DfException, ParseException {
		
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();			
		VoucherInformation voucherInfo = new VoucherInformationTransform().transform(fxaVoucher);		
    	RepositoryServiceTestHelper.compareVoucherInformation(voucherInfo);
    }

} 
