package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.text.ParseException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class DocumentumQueryBuilderComponentTest implements AbstractComponentTest {
			
	private String expectedQuery = "SELECT * FROM fxa_voucher WHERE fxa_account_number = '12345678' "
		+ "AND fxa_amount = '234.56' "
		+ "AND fxa_aux_dom = 'Test1' "
		+ "AND fxa_bsb = '063813' "
		+ "AND fxa_drn = '11111111' "
		+ "AND fxa_classification = 'CRT' "
		+ "AND fxa_extra_aux_dom = 'test' "
		+ "AND fxa_processing_date = date('24/05/2105', 'DD/MM/YYYY') "
		+ "AND fxa_trancode = '12' "
		+ "AND fxa_batch_account_number = '234567' "
		+ "AND fxa_capture_bsb = '456544' "
		+ "AND fxa_collecting_bsb = '333222' "
		+ "AND fxa_processing_state = 'NSW' "
		+ "AND fxa_migration_batch_no = '343434' "
		+ "AND fxa_batch_number = '343434' "
		+ "AND fxa_unit_id = '5434565' "
		+ "AND fxa_work_type_code = 'NABCHQ_INWARDFV' "
		+ "AND fxa_adjusted_by_id = '5555555' "
		+ "AND fxa_listing_page_number = '5555555' "
		+ "AND fxa_presentation_mode = '1' "
		+ "AND fxa_raw_micr = 'test' "
		+ "AND fxa_tran_link_no = '5555555' "
		+ "AND fxa_voucher_delayed_id = 'D' "
		+ "AND fxa_high_value_flag = true "
		+ "AND fxa_micr_flag = true ";
	
	private String expectedQueryForAdjustment = "SELECT * FROM fxa_voucher WHERE fxa_processing_date = date('13/08/2015', 'DD/MM/YYYY') "
			+ "AND fxa_adjustment_on_hold_flag = true ";
	
	private String expectedQuery1 = "SELECT * FROM fxa_voucher WHERE fxa_extra_aux_dom = 'test' " 
		+ "AND fxa_aux_dom = 'Test1' "
		+ "AND fxa_bsb = '063813' "
		+ "AND fxa_account_number = '12345678' "
		+ "AND fxa_amount = '234.56' "
		+ "AND fxa_drn = '11111111' "
		+ "AND fxa_trancode = '12' "
		+ "AND fxa_processing_date = date('24/05/2105', 'DD/MM/YYYY') "
		+ "AND fxa_classification = 'CRT' "
		+ "AND fxa_batch_number = '343434' "
		+ "AND fxa_migration_batch_no = '343434' "
		+ "AND fxa_unit_id = '5434565' "
		+ "AND fxa_capture_bsb = '456544' "
		+ "AND fxa_batch_account_number = '234567' "
		+ "AND fxa_collecting_bsb = '333222' "
		+ "AND fxa_processing_state = 'NSW' "
		+ "AND fxa_work_type_code = 'NABCHQ_INWARDFV' "
		+ "AND fxa_tran_link_no = '5555555' "
		+ "AND fxa_presentation_mode = '1' "
		+ "AND fxa_micr_flag = true "
		+ "AND fxa_raw_micr = 'test' "
		+ "AND fxa_voucher_delayed_id = 'D' "
		+ "AND fxa_adjusted_by_id = '5555555' "
		+ "AND fxa_listing_page_number = '5555555' "
		+ "AND fxa_high_value_flag = true ";
			
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldBuildQuery() throws DfException, ACLException, ParseException {	
		
		VoucherInformation voucherInfo = RepositoryServiceTestHelper.buildVoucherInformation(C.ACCCOUT_NUMBER, C.AMOUNT, C.BSB, C.DRN, 
				"24052105", false, "CRT", "Test1", null, "1", C.LISTING_PAGE_NUMBER, C.VOUCHER_DELAYED_INDICATOR, true);
		
		String query = DocumentumQueryBuilder.buildSearchQuery(voucherInfo);
		System.out.println(query);
		assertNotNull(query);
		assertEquals(expectedQuery, query.replace("\n", ""));
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldBuildQueryForAdjustment() throws Exception {		
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());	
		File inputFile = new File("target/test-classes/request/GetVouchersInformationRequestForAdjustment.json");
		GetVouchersInformationRequest request = mapper.readValue(inputFile, GetVouchersInformationRequest.class);
				
		String query = DocumentumQueryBuilder.buildSearchQuery(request.getVoucherInformation());
		System.out.println(query);
		assertNotNull(query);
		assertEquals(expectedQueryForAdjustment, query.replace("\n", ""));
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldBuildQuery1() throws DfException, ACLException, ParseException {
		
		VoucherInformation voucherInfo = RepositoryServiceTestHelper.buildVoucherInformation(C.ACCCOUT_NUMBER, C.AMOUNT, C.BSB, C.DRN, 
				"24052105", false, "CRT", "Test1", null, "1", C.LISTING_PAGE_NUMBER, C.VOUCHER_DELAYED_INDICATOR, true);
		
		String query = new DocumentumQueryBuilder().buildSearchQuery1(voucherInfo);
		System.out.println(query);
		assertNotNull(query);
		assertEquals(expectedQuery1, query.replace("\n", ""));
	}
}