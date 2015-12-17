package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.documentum.fc.common.DfException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class DocumentumQueryBuilderComponentTest implements AbstractComponentTest {
			
	private String expectedQuery = "SELECT * FROM fxa_voucher WHERE fxa_extra_aux_dom = 'test1' "
		+ "AND fxa_processing_date = date('24/05/2105', 'DD/MM/YYYY') "
		+ "AND fxa_classification = 'TTT' "
		+ "AND fxa_processing_state = 'VIC' "
		+ "AND fxa_work_type_code = 'NABCHQ_INWARDFV' "
		+ "AND fxa_unprocessable_item_flag = false "
		+ "AND fxa_adjustment_reason_code = '64' "; 
	
	private String expectedQueryForAdjustment = "SELECT * FROM fxa_voucher WHERE fxa_processing_date = date('13/08/2015', 'DD/MM/YYYY') "
			+ "AND fxa_adjustment_on_hold_flag = true ";
				
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldBuildQuery() throws DfException, ACLException, ParseException {	
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucher.extraAuxDom", "test1"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucher.processingDate", "24/05/2105"));		
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucher.documentType", "TTT"));		
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherBatch.processingState", "VIC"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherBatch.workType", "NABCHQ_INWARDFV"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.unprocessable", "false"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.adjustmentReasonCode", "64"));
		
		String query = new DocumentumQueryBuilder().buildSearchQuery(criterias);
		assertNotNull(query);
		assertEquals(expectedQuery, query.replace("\n", " "));
	}
	
	//@Test
    @Category(AbstractComponentTest.class)
    public void shouldBuildQueryForAdjustment() throws Exception {			
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucher.processingDate", "13/08/2015"));
		criterias.add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.New", "true"));

		String query = new DocumentumQueryBuilder().buildSearchQuery(criterias);
		System.out.println(query);
		assertNotNull(query);
		assertEquals(expectedQueryForAdjustment, query.replace("\n", " "));
	}
}