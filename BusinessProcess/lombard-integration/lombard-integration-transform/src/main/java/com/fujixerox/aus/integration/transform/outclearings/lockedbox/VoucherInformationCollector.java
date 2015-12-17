package com.fujixerox.aus.integration.transform.outclearings.lockedbox;

import java.util.List;

import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;

public interface VoucherInformationCollector<T> {

	List<VoucherInformation> collectVoucherInformationFrom(T voucherInfoSource);
}
