package com.fujixerox.aus.asset.impl.processor.inquiry;

import java.util.List;

import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.api.mapping.IAttributeMapping;
import com.fujixerox.aus.asset.api.processor.IFallbackDataProvider;
import com.fujixerox.aus.asset.impl.constants.RequestConstants;
import com.fujixerox.aus.asset.impl.processor.AbstractRequestProcessor;
import com.fujixerox.aus.asset.model.beans.generated.request.IndexQuery;
import com.fujixerox.aus.asset.model.beans.generated.request.Inquiry;
import com.fujixerox.aus.asset.model.beans.generated.request.QField;
import com.fujixerox.aus.asset.model.beans.generated.request.ResultField;
import com.fujixerox.aus.asset.model.beans.generated.response.Inquiryresult;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractInquiryProcessor extends
        AbstractRequestProcessor<Inquiry, Inquiryresult> {

    private final IAttributeMapping _attributeMapping;

    private final IFallbackDataProvider _fallbackDataProvider;

    protected AbstractInquiryProcessor(ICredentialManager credentialManager,
            boolean authenticationRequired, IAttributeMapping attributeMapping,
            IFallbackDataProvider fallbackDataProvider) {
        super(credentialManager, authenticationRequired);
        _attributeMapping = attributeMapping;
        _fallbackDataProvider = fallbackDataProvider;
    }

    protected IAttributeMapping getAttributeMapping() {
        return _attributeMapping;
    }

    protected IFallbackDataProvider getFallbackDataProvider() {
        return _fallbackDataProvider;
    }

    protected final boolean isViewRequest(Inquiry request, IndexQuery query) {
        List<ResultField> resultFieldList = request.getResultfields();
        if (resultFieldList == null || resultFieldList.isEmpty()) {
            return false;
        }
        if (resultFieldList.size() > 1) {
            return false;
        }
        if (!RequestConstants.OBJECT_HANDLE_FIELD.equals(resultFieldList.get(0)
                .getName())) {
            return false;
        }
        List<QField> qFields = query.getQfields();
        if (qFields == null || qFields.isEmpty()) {
            return false;
        }
        return RequestConstants.OBJECT_HANDLE_FIELD.equals(qFields.get(0)
                .getName());
    }

    @Override
    public final Class<Inquiry> getRequestClass() {
        return Inquiry.class;
    }

    @Override
    public final Class<Inquiryresult> getResponseClass() {
        return Inquiryresult.class;
    }

}
