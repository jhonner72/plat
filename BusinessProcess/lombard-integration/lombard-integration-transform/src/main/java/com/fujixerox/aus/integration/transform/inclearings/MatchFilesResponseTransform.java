package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.CodelineStatus;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.inclearings.matchfiles.MatchFilesResponse;
import com.fujixerox.aus.lombard.outclearings.correcttransaction.CorrectBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatecodeline.ValidateCodelineResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateBatchTransactionResponse;
import com.fujixerox.aus.lombard.outclearings.validatetransaction.ValidateTransactionResponse;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * If one or more vouchers have one or more status of false then the validate has failed.
 * Created by warwick on 11/03/2015.
 */
public class MatchFilesResponseTransform extends AbstractOutclearingsTransform implements Transformer <Map<String, Object>>{

    @Override
    public Map<String, Object> transform(Job job) {

        MatchFilesResponse matchFilesResponse = (MatchFilesResponse)retrieveActivityResponse(job, "receivedfiles", "match");

        boolean isMatched = true;

        Map<String, Object> map = new HashMap<>();

        if((matchFilesResponse.getUnmatchedFilesFromDats().size() != 0) || (matchFilesResponse.getUnmatchedFilesReceiveds().size() != 0)){
            isMatched = false;
        }

        map.put("isMatched", isMatched);
        return map;
    }

}
