package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import static com.fujixerox.aus.integration.transform.outclearings.lockedbox.AccountForAllVouchersMatcher.containsAllVouchersIn;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.integration.transform.TransformationTestUtil;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;

public class AssociateVouchersTransformHelperImplTest {

    private static final String EXPECTED_ENDPOINT = "theExpectedEndpoint";
    private AssociateVouchersTransformHelperImpl unitUnderTest;

    @Before
    public void setup() {
        unitUnderTest = new AssociateVouchersTransformHelperImpl();
    }
    
    @Test
    public void shouldLinkUpdateANdInsertVouchersWithTheSameCustomerLinkNumber() {
        
        String expectedCustomerLinkNumber = "theLinkingNumber";
        List<VoucherDetail> updateVoucherDetails = TransformationTestUtil.buildVoucherDetailList(4);
        List<VoucherDetail> insertVoucherDetails = TransformationTestUtil.buildVoucherDetailList(8);
        
        unitUnderTest.linkVouchers(updateVoucherDetails, insertVoucherDetails, expectedCustomerLinkNumber);
        
        assertThatVouchersHaveCustomerLinkNumber(insertVoucherDetails, expectedCustomerLinkNumber);
        assertThatVouchersHaveCustomerLinkNumber(updateVoucherDetails, expectedCustomerLinkNumber);
    }

    @Test
    public void shouldTransformFromCreditVoucherIntoVoucherDetailList() {

        // Setup
        VoucherInformation creditVoucherInformation = new VoucherInformation();
        creditVoucherInformation = TransformationTestUtil.buildVoucherInformationList(1, new Date(), "TEST").get(0);


        // Execute
        List<VoucherDetail> actualVoucherDetails = unitUnderTest.transformInsertVoucher(creditVoucherInformation, EXPECTED_ENDPOINT);

        // Asserts
        assertThat(actualVoucherDetails, is(notNullValue()));
        assertThat(actualVoucherDetails, hasSize(1));

        VoucherDetail actualVoucherDetail = actualVoucherDetails.get(0);
        assertThat(actualVoucherDetail.getVoucher(), is(equalTo(creditVoucherInformation)));

        List<TransferEndpoint> actualTransferEndpoints = actualVoucherDetail.getTransferEndpoints();
        assertThat(actualTransferEndpoints, hasSize(2));

        assertTransferEndpoint(actualTransferEndpoints, DocumentExchangeEnum.VIF_OUTBOUND);
        assertTransferEndpoint(actualTransferEndpoints, DocumentExchangeEnum.VIF_ACK_OUTBOUND);

    }

    @Test
    public void shouldTransformFromDebitVouchersIntoVoucherDetailList() {

        int vouchersCount = 24;

        // Setup
        List<Voucher> debitVouchers = TransformationTestUtil.buildVoucherList(vouchersCount);

        // Execute
        List<VoucherDetail> actualVoucherDetails = unitUnderTest.transformUpdateVouchers(debitVouchers, EXPECTED_ENDPOINT);

        // Asserts
        assertThat(actualVoucherDetails, containsAllVouchersIn(debitVouchers));
        assertTargetEndpoints(actualVoucherDetails);
    }

    @Test
    public void shouldReturnNonNullIfNoVouchersInList() {

        int vouchersCount = 0;

        // Setup
        List<Voucher> debitVouchers = TransformationTestUtil.buildVoucherList(vouchersCount);

        // Execute
        List<VoucherDetail> actualVoucherDetails = unitUnderTest.transformUpdateVouchers(debitVouchers, EXPECTED_ENDPOINT);

        // Assert
        assertThat(actualVoucherDetails, is(notNullValue()));
        assertThat(actualVoucherDetails, hasSize(vouchersCount));
    }

    private void assertTargetEndpoints(List<VoucherDetail> actualVoucherDetails) {

        for (VoucherDetail voucherDetail : actualVoucherDetails) {
            assertThat(voucherDetail.getTransferEndpoints(), hasSize(1));

            assertTransferEndpointStatusNew(voucherDetail.getTransferEndpoints().get(0), EXPECTED_ENDPOINT);
        }
    }

    private void assertTransferEndpoint(List<TransferEndpoint> actualTransferEndpoints, DocumentExchangeEnum documentExchangeType) {

        for (TransferEndpoint actualTransferEndpoint : actualTransferEndpoints) {

            if (actualTransferEndpoint.getDocumentExchange().equals(documentExchangeType)) {
                assertTransferEndpointStatusNew(actualTransferEndpoint, EXPECTED_ENDPOINT);
                return;
            }
        }

        fail(String.format("Could not find an Transfer Endpoint for DocumentExchange %s", documentExchangeType));

    }
    private void assertTransferEndpointStatusNew(TransferEndpoint transferEndpoint, String expectedEndpoint) {
        assertThat(transferEndpoint.getVoucherStatus(), is(VoucherStatus.NEW));
        assertThat(transferEndpoint.getEndpoint(), is(equalTo(expectedEndpoint)));
    }
    
    private void assertThatVouchersHaveCustomerLinkNumber(List<VoucherDetail> voucherDetails, String expectedCustomerLinkNumber) {
        for (VoucherDetail voucherDetail : voucherDetails) {
            VoucherInformation voucherInfo = voucherDetail.getVoucher();
            assertThat(voucherInfo, is(notNullValue()));
            
            VoucherProcess voucherProcess = voucherInfo.getVoucherProcess();
            assertThat(voucherProcess, is(notNullValue()));
            
            assertThat(voucherProcess.getCustomerLinkNumber(), is(equalTo(expectedCustomerLinkNumber)));
            
        }
    }

}
