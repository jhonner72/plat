package com.fujixerox.aus.asset.impl.query.handlers;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfValue;
import com.fujixerox.aus.asset.api.util.logger.Logger;
import com.fujixerox.aus.asset.impl.constants.DocumentumConstants;
import com.fujixerox.aus.asset.impl.dfc.IntToDfIdConverter;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */

public class AssociatedQueryAttributeDataHandler extends
        AbstractAttributeHandler {

    private static final ThreadLocal<int[]> PARTS = new ThreadLocal<int[]>() {
        @Override
        protected int[] initialValue() {
            return new int[] {-1, -1, -1, -1, };
        }
    };

    private final int _position;

    public AssociatedQueryAttributeDataHandler(int position) {
        super();
        _position = position;
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue)
        throws DfException {

        int[] parts = PARTS.get();

        if (!isEqualBoundaries(lowValue, highValue)) {
            return EMPTY_CONDITION;
        }

        try {
            parts[_position] = Integer.valueOf(lowValue);
        } catch (NumberFormatException ex) {
            Logger.error(ex);
            return EMPTY_CONDITION;
        }

        for (int integer : parts) {
            if (integer < 0) {
                return null;
            }
        }

        PARTS.remove();

        IDfId objectId = IntToDfIdConverter.unpack(DfId.DM_DOCUMENT, Long
                .valueOf(session.getDocbaseId()), parts);
        IDfPersistentObject object = session.getObject(objectId);

        // new vouchers
        if (object.hasAttr(DocumentumConstants.FXA_TRAN_LINK_NO)
                && object.hasAttr(DocumentumConstants.FXA_BATCH_NUMBER)) {
            String tranLinkNo = object
                    .getString(DocumentumConstants.FXA_TRAN_LINK_NO);
            String batchNumber = object
                    .getString(DocumentumConstants.FXA_BATCH_NUMBER);
            if (StringUtils.isNotBlank(tranLinkNo)
                    && StringUtils.isNotBlank(batchNumber)) {
                return buildTranLinkRestriction(batchNumber, tranLinkNo);
            }
        }

        // migrated
        if (object.hasAttr(DocumentumConstants.FXA_M_CR_DRN)) {
            String drn = object.getString(DocumentumConstants.FXA_M_CR_DRN);
            if (StringUtils.isNotBlank(drn)) {
                return buildDrnRestriction(drn);
            }
            if (object.hasAttr(DocumentumConstants.FXA_DRN)) {
                drn = object.getString(DocumentumConstants.FXA_DRN);
                if (StringUtils.isNotBlank(drn)) {
                    return buildDrnRestriction(drn);
                }
            }
        }

        // returning the same voucher
        return equal(qualification, toQuotedString(objectId.getId()));
    }

    @Override
    public String getStringValue(IDfValue value) {
        return String.valueOf(IntToDfIdConverter.pack(value.asId())[_position]);
    }

    private String buildDrnRestriction(String drn) {
        return "(" + DocumentumConstants.FXA_M_CR_DRN + " = "
                + toQuotedString(drn) + " OR " + DocumentumConstants.FXA_DRN
                + " = " + toQuotedString(drn) + ")";
    }

    private String buildTranLinkRestriction(String batchNumber,
            String trankLinkNo) {
        return "(" + DocumentumConstants.FXA_TRAN_LINK_NO + " = "
                + toQuotedString(trankLinkNo) + " AND "
                + DocumentumConstants.FXA_BATCH_NUMBER + " = "
                + toQuotedString(batchNumber) + ")";
    }

    @Override
    public void dispose() {
        try {
            int[] parts = PARTS.get();
            for (int integer : parts) {
                if (integer >= 0) {
                    throw new IllegalStateException(
                            "Not all arguments were provided");
                }
            }
        } finally {
            PARTS.remove();
        }
    }

}
