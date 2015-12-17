package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.CodelineStatus;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * If one or more vouchers have one or more status of false then the validate has failed.
 * Created by warwick on 11/03/2015.
 */
public class ValidateBatchTransactionResponseTransform extends AbstractOutclearingsTransform implements Transformer <Map<String, Object>>{

    @Override
    public Map<String, Object> transform(Job job) {

        ValidateBatchTransactionResponse response = retrieveActivityResponse(job, TRANSACTION, VALIDATE);
        Map<String, Object> map = new HashMap<>();
        map.put("validateTransaction", isSuccessful(response.getVouchers()));
        map.put("checkThirdParty", isThirdParty(response.getVouchers()));
        return map;
    }

    private boolean isThirdParty(List<ValidateTransactionResponse> vouchers) {

        for (ValidateTransactionResponse voucher : vouchers)
        {
            if (voucher.isThirdPartyCheckRequired())
            {
                return true;
            }

        }

        return false;
    }

    protected boolean isSuccessful(List<ValidateTransactionResponse> vouchers)
    {
        for (ValidateTransactionResponse voucher : vouchers)
        {
            if (voucher.isUnprocessable())
            {
                return false;
            }
            if (voucher.isSurplusItemFlag() && voucher.getVoucher().getDocumentType().equals(DocumentTypeEnum.SP))
            {
                return true;
            }
            CodelineStatus codelineFieldsStatus = voucher.getCodelineFieldsStatus();
            if (!codelineFieldsStatus.isAccountNumberStatus() ||
                    !codelineFieldsStatus.isAmountStatus() ||
                    !codelineFieldsStatus.isAuxDomStatus() ||
                    !codelineFieldsStatus.isBsbNumberStatus() ||
                    !codelineFieldsStatus.isExtraAuxDomStatus() ||
                    !codelineFieldsStatus.isTransactionCodeStatus())
            {
                return false;
            }
        }

        return true;
    }
    public static boolean isVoucherOk(ValidateCodelineResponse voucher)
    {
        if (!voucher.isAccountNumberStatus() ||
                !voucher.isAmountStatus() ||
                !voucher.isAuxDomStatus() ||
                !voucher.isBsbNumberStatus() ||
                !voucher.isExtraAuxDomStatus() ||
                !voucher.isTransactionCodeStatus())
        {
            return false;
        }
        return true;
    }
}
