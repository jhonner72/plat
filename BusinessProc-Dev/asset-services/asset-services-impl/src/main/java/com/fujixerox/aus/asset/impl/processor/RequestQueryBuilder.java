package com.fujixerox.aus.asset.impl.processor;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.mapping.IAttributeMapping;
import com.fujixerox.aus.asset.impl.constants.RequestConstants;
import com.fujixerox.aus.asset.impl.query.AbstractQueryBuilder;
import com.fujixerox.aus.asset.model.beans.generated.request.Inquiry;
import com.fujixerox.aus.asset.model.beans.generated.request.ObjectType;
import com.fujixerox.aus.asset.model.beans.generated.request.QField;
import com.fujixerox.aus.asset.model.beans.generated.request.ResultField;
import com.fujixerox.aus.asset.model.beans.generated.request.SortDirection;
import com.fujixerox.aus.asset.model.beans.generated.request.SortField;
import com.fujixerox.aus.asset.model.beans.generated.request.YNBool;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class RequestQueryBuilder extends AbstractQueryBuilder {

    public RequestQueryBuilder(IAttributeMapping attributeMapping) {
        super(attributeMapping);
    }

    @Override
    protected String getObjectHandleAttributeName() {
        return RequestConstants.OBJECT_HANDLE_FIELD;
    }

    public void addResultFiled(Inquiry request, ResultField field) {
        addResultFiled(getObjectname(request), field.getName());
    }

    public void addRestriction(Inquiry request, QField field) {
        addRestriction(getObjectname(request), field.getName(), field.getLow(),
                field.getHigh(), field.getMatchCase() != YNBool.N);
    }

    public void addOrdering(Inquiry request, SortField sortField) {
        addOrdering(getObjectname(request), sortField.getName(), sortField
                .getOrder() == SortDirection.DSC);
    }

    public String getColumn(Inquiry request, ResultField field) {
        return getColumnAliasByIncoming(getObjectname(request), field.getName());
    }

    public String buildQuery(IDfSession session, Inquiry request)
        throws DfException {
        return buildQuery(session, getObjectname(request),
                request.getCount() == YNBool.Y,
                request.getMediastatus() == YNBool.Y
                        || request.getImages() == YNBool.Y);
    }

    private String getObjectname(Inquiry request) {
        ObjectType objectType = request.getObject();
        if (objectType != null) {
            return objectType.getName();
        }
        return null;
    }

    public String getObjectType(Inquiry request) {
        return getObjectType(getObjectname(request));
    }

}
