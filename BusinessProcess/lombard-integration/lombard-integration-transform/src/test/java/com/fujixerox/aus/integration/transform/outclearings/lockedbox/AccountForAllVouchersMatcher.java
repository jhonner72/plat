package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;

public class AccountForAllVouchersMatcher  extends BaseMatcher<List<VoucherDetail>>{
    
    private static final String EXPECTATION_MSG = "a list of VoucherDetails but was [%s]";
    private List<Voucher> vouchersToAccountFor;
    private String nonMatchingDescription;
    
    public static AccountForAllVouchersMatcher containsAllVouchersIn(List<Voucher> vouchersToAccountFor){
        return new AccountForAllVouchersMatcher(vouchersToAccountFor);
        
    }
    

    public AccountForAllVouchersMatcher(List<Voucher> vouchersToAccountFor) {
        super();
        this.vouchersToAccountFor = vouchersToAccountFor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(Object item) {

        if(item == null){
            nonMatchingDescription = String.format(EXPECTATION_MSG , "null");
            return false;
        }
        
        if(!List.class.isAssignableFrom(item.getClass())){
            nonMatchingDescription = String.format(EXPECTATION_MSG , item.getClass());
            return false;
        }
        
        if(vouchersToAccountFor.size() == 0){
            return true;
        }
        
        List<VoucherDetail> actualVoucherDetailList = (List<VoucherDetail>)item;

        if(actualVoucherDetailList.size() != vouchersToAccountFor.size()){
            nonMatchingDescription = String.format(" a list with [%s] VoucherDetails but got [%s]", vouchersToAccountFor.size(), actualVoucherDetailList.size());
            return false;
        }
        
        accountingFor: for (Voucher voucherToAccountFor : vouchersToAccountFor) {
            for (VoucherDetail actualVoucherDetail : actualVoucherDetailList) {

                Voucher actualVoucher = getVoucherFrom(actualVoucherDetail);

                if (actualVoucher == null) {
                    continue;
                }

                if (voucherToAccountFor.equals(actualVoucher)) {
                    continue accountingFor;
                }
            }
            nonMatchingDescription = String.format(" Could not find a VoucherDetail containing Voucher [%s]",voucherToAccountFor);
            return false;
        }

        return true;
    }

    private Voucher getVoucherFrom(VoucherDetail actualVoucherDetail) {

        if (actualVoucherDetail == null) {
            return null;
        }

        if (actualVoucherDetail.getVoucher() == null) {
            return null;
        }

        return actualVoucherDetail.getVoucher().getVoucher();

    }

    @Override
    public void describeTo(Description description) {
        description.appendText(nonMatchingDescription);
    }
    

}
