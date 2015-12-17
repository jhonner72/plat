package com.fujixerox.aus.asset.impl.processor.inquiry.query;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.query.QueryHelper;
import com.fujixerox.aus.asset.api.dfc.session.IDfcSessionInvoker;
import com.fujixerox.aus.asset.api.mapping.IAttributeMapping;
import com.fujixerox.aus.asset.api.processor.IFallbackDataProvider;
import com.fujixerox.aus.asset.impl.processor.RequestQueryBuilder;
import com.fujixerox.aus.asset.model.beans.generated.request.IndexQuery;
import com.fujixerox.aus.asset.model.beans.generated.request.Inquiry;
import com.fujixerox.aus.asset.model.beans.generated.request.QField;
import com.fujixerox.aus.asset.model.beans.generated.request.ResultField;
import com.fujixerox.aus.asset.model.beans.generated.request.SortField;
import com.fujixerox.aus.asset.model.beans.generated.response.IndexQueryResult;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class IndexQueryInvoker implements IDfcSessionInvoker<Boolean> {

    private final Inquiry _request;

    private final IndexQuery _query;

    private final IndexQueryResult _queryResult;

    private final IAttributeMapping _attributeMapping;

    private final IFallbackDataProvider _fallbackImageProvider;

    private final int _limit;

    public IndexQueryInvoker(Inquiry request, IndexQuery query,
            IndexQueryResult queryResult, IAttributeMapping attributeMapping,
            IFallbackDataProvider fallbackDataProvider, int limit) {
        _request = request;
        _query = query;
        _queryResult = queryResult;
        _attributeMapping = attributeMapping;
        _fallbackImageProvider = fallbackDataProvider;
        _limit = limit;
    }

    @Override
    public Boolean invoke(IDfSession session) throws DfException {
        RequestQueryBuilder queryBuilder = getQueryBuilder(_request, _query);
        IDfCollectionHandler processor = new IDfCollectionHandler(_request,
                _query, _queryResult, queryBuilder, _fallbackImageProvider);
        return QueryHelper.selectQuery(session, queryBuilder.buildQuery(
                session, _request), _limit, processor);
    }

    private RequestQueryBuilder getQueryBuilder(Inquiry request,
            IndexQuery query) {
        RequestQueryBuilder queryBuilder = new RequestQueryBuilder(
                _attributeMapping);
        for (ResultField resultField : request.getResultfields()) {
            queryBuilder.addResultFiled(request, resultField);
        }
        for (QField qField : query.getQfields()) {
            queryBuilder.addRestriction(request, qField);
        }
        for (SortField sortField : request.getSortfields()) {
            queryBuilder.addOrdering(request, sortField);
        }
        return queryBuilder;
    }

}
