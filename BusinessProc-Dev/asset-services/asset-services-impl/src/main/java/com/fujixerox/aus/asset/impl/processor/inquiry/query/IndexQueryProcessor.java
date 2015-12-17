package com.fujixerox.aus.asset.impl.processor.inquiry.query;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.api.mapping.IAttributeMapping;
import com.fujixerox.aus.asset.api.processor.IFallbackDataProvider;
import com.fujixerox.aus.asset.api.util.logger.Logger;
import com.fujixerox.aus.asset.impl.constants.ResponseConstants.GenericCode;
import com.fujixerox.aus.asset.impl.processor.inquiry.AbstractInquiryProcessor;
import com.fujixerox.aus.asset.impl.processor.inquiry.imaging.ImageProcessor;
import com.fujixerox.aus.asset.model.beans.generated.request.IndexQuery;
import com.fujixerox.aus.asset.model.beans.generated.request.Inquiry;
import com.fujixerox.aus.asset.model.beans.generated.request.ResultField;
import com.fujixerox.aus.asset.model.beans.generated.request.YNBool;
import com.fujixerox.aus.asset.model.beans.generated.response.IndexQueryResult;
import com.fujixerox.aus.asset.model.beans.generated.response.Inquiryresult;
import com.fujixerox.aus.asset.model.beans.generated.response.Item;
import com.fujixerox.aus.asset.model.beans.generated.response.LimitReached;
import com.fujixerox.aus.asset.model.beans.generated.response.RField;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class IndexQueryProcessor extends AbstractInquiryProcessor {

    private static final int MAX_ROWS_DEFAULT = 10000;

    private final int _maxRows;

    private final boolean _performFallback;

    public IndexQueryProcessor(ICredentialManager credentialManager,
            boolean authenticationRequired, IAttributeMapping attributeMapping,
            IFallbackDataProvider fallbackDataProvider) {
        this(credentialManager, authenticationRequired, attributeMapping,
                fallbackDataProvider, MAX_ROWS_DEFAULT);
    }

    public IndexQueryProcessor(ICredentialManager credentialManager,
            boolean authenticationRequired, IAttributeMapping attributeMapping,
            IFallbackDataProvider fallbackDataProvider, int maxRows) {
        super(credentialManager, authenticationRequired, attributeMapping,
                fallbackDataProvider);
        _maxRows = maxRows;
        _performFallback = fallbackDataProvider != null;
    }

    @Override
    protected boolean doCanProcess(Inquiry request) {
        if (request.getIndexqueries() == null) {
            return false;
        }

        return !request.getIndexqueries().isEmpty();
    }

    @Override
    protected Inquiryresult doProcess(Inquiry request, String userName) {
        Inquiryresult result = new Inquiryresult();
        result.setSessionname(request.getSessionname());
        for (IndexQuery query : request.getIndexqueries()) {
            result.getIndexqueryresults().add(processSearchRequest(request, query));
        }

        boolean hasData = false;
        boolean countQuery = request.getCount() == YNBool.Y;
        for (IndexQueryResult indexQueryResult : result.getIndexqueryresults()) {
            if (countQuery) {
                Integer retCount = indexQueryResult.getRetCount();
                hasData = retCount != null && retCount > 0;
            } else {
                List<Item> items = indexQueryResult.getItems();
                hasData = items != null && !items.isEmpty();
            }
            if (hasData) {
                break;
            }
        }

        if (!hasData && _performFallback) {
            Logger.debug("Query result contains no data, adding fake data");
            IndexQuery query = request.getIndexqueries().get(0);
            IndexQueryResult indexQueryResult = result.getIndexqueryresults()
                    .get(0);
            if (countQuery) {
                indexQueryResult.setRetCount(1);
            } else {
                addFallBackResult(request, query, indexQueryResult);
            }
        }

        return result;
    }

    private IndexQueryResult processSearchRequest(Inquiry request, IndexQuery query) {
        IndexQueryResult result = new IndexQueryResult().withQueryname(query.getQueryname());

        try {
            int maxRows = _maxRows;
            if (request.getMaxrows() != null) {
                int requestRows = request.getMaxrows();
                if (requestRows != 0 && requestRows < maxRows) {
                    maxRows = requestRows;
                }
            }

            boolean hasMore = invokeInSession(request.getSecurityToken(),
                new IndexQueryInvoker(request, query, result, getAttributeMapping(), getFallbackDataProvider(), maxRows));

            if (hasMore) {
                Logger.debug("Result contains more than " + maxRows
                        + " rows, adding limit reached flag");
                result.setLimitreached(new LimitReached());
            }
        } catch (DfException ex) {
            Logger.error(ex.getMessage(), ex);
            handleException(ex, result);
        }
        return result;
    }

    private void addFallBackResult(Inquiry request, IndexQuery query, IndexQueryResult result) {
        try {
            Set<String> attributes = new HashSet<>();
            for (ResultField resultField : request.getResultfields()) {
                attributes.add(resultField.getName());
            }
            Map<String, String> noData = getFallbackDataProvider()
                    .getNoResultData(attributes);
            if (noData == null || noData.isEmpty()) {
                return;
            }

            Item item = new Item();
            for (ResultField resultField : request.getResultfields()) {
                String attrName = resultField.getName();
                String value = noData.get(resultField.getName());
                RField rField = new RField().withName(attrName)
                        .withValue(value);
                item.withRfields(rField);
            }
            item.setObjectname(getAttributeMapping().getDefaultType());
            item.setMediastatus("m");

            if (request.getImages() == YNBool.Y) {
                ImageProcessor imageProcessor = new ImageProcessor(request,
                        query, getFallbackDataProvider());
                imageProcessor.processNoResultImage(item);
            }

            result.withItems(item);
        } catch (DfException ex) {
            Logger.error(ex.getMessage(), ex);
            handleException(ex, result);
        }
    }

    private void handleException(DfException exception, IndexQueryResult result) {
        result.setResponseCode(GenericCode.SYNTAX_ERROR);
    }

}
