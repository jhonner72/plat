package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.fujixerox.aus.integration.transform.TransformationTestUtil;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.outclearings.generatebulkcredit.GenerateBulkCreditResponse;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersRequest;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;

public class AssociateVouchersRequestTransformTest {
    
    private AssociateVouchersRequestTransform unitUnderTest;
    
    @Before
    public void setup(){
        unitUnderTest = new AssociateVouchersRequestTransform();
    }
    

   // @Test
    public void initialTest(){
        
        
        String someCustomerLinkNumber = "theCustomerLinkNumber";
        List<Voucher> debitVouchers = new ArrayList<Voucher>();
        VoucherInformation creditVoucherInformation = new VoucherInformation();
        

        // Setup
        GenerateBulkCreditResponse mockedGenerateBulkCreditResponse = mock(GenerateBulkCreditResponse.class);
        when(mockedGenerateBulkCreditResponse.getCustomerLinkNumber()).thenReturn(someCustomerLinkNumber);
        when(mockedGenerateBulkCreditResponse.getBulkCreditVoucher()).thenReturn(creditVoucherInformation);
        when(mockedGenerateBulkCreditResponse.getAssociatedDebitVouchers()).thenReturn(debitVouchers);
        
        Job job = buildJobWith(mockedGenerateBulkCreditResponse);
        

        
        
        String someEndpoint = "theEndpoint";
        TransferEndpointBuilder mockedEndpointBuilder = mock(TransferEndpointBuilder.class);
        when(mockedEndpointBuilder.buildTransferEndpoint(job)).thenReturn(someEndpoint);

        
        
        List<VoucherDetail> debitVoucherDetails = TransformationTestUtil.buildVoucherDetailList(54);
        List<VoucherDetail> creditVoucherDetails = TransformationTestUtil.buildVoucherDetailList(26);

        AssociateVouchersTransformHelper mockedTransformHelper = mock(AssociateVouchersTransformHelper.class);
        when(mockedTransformHelper.transformUpdateVouchers(debitVouchers, someEndpoint)).thenReturn(debitVoucherDetails);
        when(mockedTransformHelper.transformInsertVoucher(creditVoucherInformation, someEndpoint)).thenReturn(creditVoucherDetails);
        
        unitUnderTest.setTransformHelper(mockedTransformHelper);
        unitUnderTest.setEndpointBuilder(mockedEndpointBuilder);

        
        // Execute
        AssociateVouchersRequest transformedRequest = unitUnderTest.transform(job);
        
        assertThat(transformedRequest, is(notNullValue()));
        assertThat(transformedRequest.getInsertVouchers(), is(equalTo(creditVoucherDetails)));
        assertThat(transformedRequest.getUpdateVouchers(), is(equalTo(debitVoucherDetails)));
        
        verify(mockedTransformHelper, atMost(1)).linkVouchers(debitVoucherDetails, creditVoucherDetails, someCustomerLinkNumber);
        
    }


    private Job buildJobWith(GenerateBulkCreditResponse generateBulkCreditResponse) {
        
        Activity activity = new Activity();
        
        activity.setPredicate("generate");
        activity.setSubject("bulkCredit");
        activity.setResponse(generateBulkCreditResponse);
        
        Job job = new Job();
        
        job.getActivities().add(activity);
        return job;
    }

}
