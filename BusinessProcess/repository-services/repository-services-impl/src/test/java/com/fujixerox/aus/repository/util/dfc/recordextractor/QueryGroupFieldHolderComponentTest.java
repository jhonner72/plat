package com.fujixerox.aus.repository.util.dfc.recordextractor;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfTime;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.recordextactor.QueryGroupFieldHolder;

public class QueryGroupFieldHolderComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldCreateTransactionLinkHolder() throws ParseException { 
		Date date = new SimpleDateFormat("ddMMyyyy").parse("19052015");		
		QueryGroupFieldHolder holder = new QueryGroupFieldHolder("123456", new DfTime(date), "BATCH_11111111");
		assertEquals("BATCH_11111111", holder.getBatchNumber());
		assertEquals("19/05/2015", holder.getProcessingDate());
		assertEquals("123456", holder.getTranLinkNo());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldCreateCustomerLinkHolder() throws ParseException { 
		Date date = new SimpleDateFormat("ddMMyyyy").parse("19052015");		
		QueryGroupFieldHolder holder = new QueryGroupFieldHolder("3456789", new DfTime(date));
		assertEquals("19/05/2015", holder.getProcessingDate());
		assertEquals("3456789", holder.getCustomerLinkNo());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldBuildQueryCondition() throws ParseException { 		
		String expected = "AND fxa_voucher.fxa_batch_number = 'BATCH_11111111' "
				+ "AND fxa_voucher.fxa_processing_date = date('19/05/2015, DD/MM/YYYY') "
				+ "AND fxa_voucher.fxa_tran_link_no = '123456' ";
			
		Date date = new SimpleDateFormat("ddMMyyyy").parse("19052015");		
		QueryGroupFieldHolder holder = new QueryGroupFieldHolder("123456", new DfTime(date), "BATCH_11111111");		
		String actual = holder.buildQueryCondition();
		assertEquals(expected, actual);
	}

}
