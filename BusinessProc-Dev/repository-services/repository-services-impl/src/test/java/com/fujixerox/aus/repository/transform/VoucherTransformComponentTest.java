package com.fujixerox.aus.repository.transform;

import java.text.ParseException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;

/** 
 * Henry Niu
 * 13/4/2015
 */
public class VoucherTransformComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldTransform() throws DfException, ParseException {		
		IDfSysObject fxaVoucher = RepositoryServiceTestHelper.buildFxaVoucher();
		Voucher voucher = new VoucherTransform().transform(fxaVoucher);
		RepositoryServiceTestHelper.compareVoucher(voucher);
    }
}
