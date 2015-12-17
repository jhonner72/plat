package com.fujixerox.aus.repository.api;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.VoucherAudit;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;

import java.text.ParseException;
import java.util.List;

/**
 * Created by vidya on 9/09/15.
 */
public class VoucherAuditProcessor {

    /**
     * Creates Voucher audit entry in dm_dbo.fxa_voucher_audit
     * @param session
     * @param fxaVoucher
     * @param voucherAudits
     * @throws DfException
     * @throws ParseException
     */
    public void createVoucherAudit(IDfSession session, IDfSysObject fxaVoucher, List<VoucherAudit> voucherAudits) throws DfException, ParseException {

        if(voucherAudits == null)
        {
            return;
        }

        for(VoucherAudit voucherAudit: voucherAudits) {
            String preValue = voucherAudit.getPreValue();
            String postValue = voucherAudit.getPostValue();
            String operatorValue = voucherAudit.getOperator();
            if (preValue == null) {
                preValue = "";
            }
            if (postValue == null) {
                postValue = "";
            }
            if (operatorValue == null) {
                operatorValue = "";
            }
            if ((preValue.equalsIgnoreCase("true") || preValue.equalsIgnoreCase("false")) &&
                    (postValue.equalsIgnoreCase("true") || postValue.equalsIgnoreCase("false"))) {

                //Pre and Post values are boolean
                queryInsertToVoucherAuditPrePostBoolean(voucherAudit, preValue, postValue, operatorValue, fxaVoucher, session);

            } else if (postValue.equalsIgnoreCase("true") || postValue.equalsIgnoreCase("false")) {

                //pre value is null and post value is boolean
                queryInsertToVoucherAuditOnlyPostBoolean(voucherAudit, preValue, postValue, operatorValue, fxaVoucher, session);


            } else if (voucherAudit.getAttributeName().equalsIgnoreCase("timings")) {

                //subject area is timings, pre and post vale will be dates
                queryInsertToVoucherAuditDate(voucherAudit, preValue, postValue, operatorValue, fxaVoucher, session);

            } else {

                //pre and post values are string
                queryAndStoreVoucherAuditStringValues(voucherAudit, preValue, postValue, operatorValue, fxaVoucher, session);
            }
        }

    }

    /**
     *pre value is null and post value is boolean
     */
    private void queryInsertToVoucherAuditOnlyPostBoolean(VoucherAudit voucherAudit, String preValue, String postValue, String operatorValue, IDfSysObject fxaVoucher, IDfSession session) throws DfException {
        boolean postValueBol = Boolean.parseBoolean(postValue);
        String queryString = String.format(DocumentumQuery.INSERT_VOUCHER_AUDIT, fxaVoucher.getChronicleId(), voucherAudit.getSubjectArea(),
                voucherAudit.getAttributeName(), preValue, postValueBol ? 1 : 0, operatorValue);
//        LogUtil.log("Query for audit - " + queryString, LogUtil.DEBUG, null);
        new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);
    }


    /**
     *Pre and Post values are boolean
     */
    private void queryInsertToVoucherAuditPrePostBoolean(VoucherAudit voucherAudit, String preValue, String postValue, String operatorValue, IDfSysObject fxaVoucher, IDfSession session) throws DfException {
        boolean postValueBol = Boolean.parseBoolean(postValue);
        boolean preValueBol = Boolean.parseBoolean(preValue);
        String queryString = String.format(DocumentumQuery.INSERT_VOUCHER_AUDIT, fxaVoucher.getChronicleId(), voucherAudit.getSubjectArea(),
                voucherAudit.getAttributeName(), preValueBol ? 1 : 0, postValueBol ? 1 : 0, operatorValue);
//        LogUtil.log("Query for audit - " + queryString, LogUtil.DEBUG, null);
        new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);
    }


    /**
     *subject area is timings, pre and post vale will be dates
     */
    private void queryInsertToVoucherAuditDate(VoucherAudit voucherAudit, String preValue, String postValue, String operatorValue, IDfSysObject fxaVoucher, IDfSession session) throws ParseException, DfException {
        String queryString = String.format(DocumentumQuery.INSERT_VOUCHER_AUDIT, fxaVoucher.getChronicleId(), voucherAudit.getSubjectArea(),
                voucherAudit.getAttributeName(), preValue, postValue, operatorValue);
//        LogUtil.log("Query for audit - " + queryString, LogUtil.DEBUG, null);
        new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);
    }


    /**
     * pre and post values are string
     */
    private void queryAndStoreVoucherAuditStringValues(VoucherAudit voucherAudit,
                                                       String preValue,
                                                       String postValue,
                                                       String operatorValue,
                                                       IDfSysObject fxaVoucher,
                                                       IDfSession session) throws DfException {
        //In case of attribute_name as dips, prevalue and postvalue will be null
        if(voucherAudit.getSubjectArea().equalsIgnoreCase("dips")){

            queryInsertToVoucherAudit(voucherAudit, preValue, postValue, operatorValue, fxaVoucher, session);

        }
        else{
            if(!postValue.isEmpty()){
                queryInsertToVoucherAudit(voucherAudit, preValue, postValue, operatorValue, fxaVoucher, session);
            }
        }
    }


    /**
     *pre and post values are string
     */
    private void queryInsertToVoucherAudit(VoucherAudit voucherAudit, String preValue, String postValue, String operatorValue, IDfSysObject fxaVoucher, IDfSession session) throws DfException {
        String queryString = String.format(DocumentumQuery.INSERT_VOUCHER_AUDIT, fxaVoucher.getChronicleId(), voucherAudit.getSubjectArea(),
                voucherAudit.getAttributeName(), preValue, postValue, operatorValue);
//        LogUtil.log("Query for audit - "+queryString, LogUtil.DEBUG, null);

        new DfQuery(queryString).execute(session, IDfQuery.DF_QUERY);

    }

}
