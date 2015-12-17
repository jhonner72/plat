package com.fujixerox.aus.repository.util.dfc.recordextractor;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfTime;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VIFHolder;

public class VIFHolderComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldCreateVIFHolder() throws ParseException { 
		Date date = new SimpleDateFormat("ddMMyyyy").parse("19052015");		
		VIFHolder vifHolder = new VIFHolder("BATCH_11111111", new DfTime(date), "123456");
		assertEquals("BATCH_11111111", vifHolder.getBatchNumber());
		assertEquals("19052015", new SimpleDateFormat("ddMMyyyy").format(vifHolder.getProcessingDate().getDate()));
		assertEquals("123456", vifHolder.getTranLinkNo());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldBuildQueryCondition() throws ParseException { 		
		String expected = "((fxa_voucher.fxa_processing_date = date('19/05/2015, DD/MM/YYYY') AND fxa_voucher.fxa_tran_link_no = '123456' "
				+ "AND fxa_voucher.fxa_batch_number = 'BATCH_11111111' ) OR (fxa_voucher.fxa_processing_date = date('20/05/2015, DD/MM/YYYY')"
				+ " AND fxa_voucher.fxa_tran_link_no = '234567' AND fxa_voucher.fxa_batch_number = 'BATCH_22222222' ) "
				+ "OR (fxa_voucher.fxa_processing_date = date('21/05/2015, DD/MM/YYYY') AND fxa_voucher.fxa_tran_link_no = '345678' "
				+ "AND fxa_voucher.fxa_batch_number = 'BATCH_33333333' ) )";
		
		List<VIFHolder> vifHolders = new ArrayList<VIFHolder>();
		
		Date date = new SimpleDateFormat("ddMMyyyy").parse("19052015");		
		vifHolders.add(new VIFHolder("BATCH_11111111", new DfTime(date), "123456"));
		
		date = new SimpleDateFormat("ddMMyyyy").parse("20052015");		
		vifHolders.add(new VIFHolder("BATCH_22222222", new DfTime(date), "234567"));
		
		date = new SimpleDateFormat("ddMMyyyy").parse("21052015");		
		vifHolders.add(new VIFHolder("BATCH_33333333", new DfTime(date), "345678"));
		
		String actual = VIFHolder.buildQueryCondition(vifHolders);
		assertEquals(expected, actual);
	}

}
