package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.util.List;

import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;

public interface AssociateVouchersTransformHelper {

    List<VoucherDetail> transformInsertVoucher(VoucherInformation creditVoucherInformation, String endpoint);

    List<VoucherDetail> transformUpdateVouchers(List<Voucher> debitVouchers, String endpoint);

    void linkVouchers(List<VoucherDetail> updateVoucherDetails, List<VoucherDetail> insertVoucherDetails, String customerLinkNumber);

}
