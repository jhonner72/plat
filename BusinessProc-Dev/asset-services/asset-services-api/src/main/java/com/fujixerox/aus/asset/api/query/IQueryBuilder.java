package com.fujixerox.aus.asset.api.query;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IQueryBuilder {

    void addResultFiled(String objectType, String attributeName);

    void addRestriction(String typeName, String attributeName, String lowValue,
            String highValue);

    void addRestriction(String objectType, String attributeName,
            String lowValue, String highValue, boolean matchCase);

    void addRestriction(String objectType, String attributeName,
            IQueryCondition condition);

    void addOrdering(String objectType, String attributeName, boolean descending);

    String buildQuery(IDfSession session, String objectType) throws DfException;

    String buildQuery(IDfSession session, String objectType, boolean countQuery)
        throws DfException;

    String buildQuery(IDfSession session, String objectType,
            boolean countQuery, boolean addMediaAttributes) throws DfException;

    String buildProjection(IDfSession session, String objectType);

    String buildProjection(IDfSession session, String objectType,
            boolean countQuery);

    String buildProjection(IDfSession session, String objectType,
            boolean countQuery, boolean addMediaAttributes);

    String buildRestriction(IDfSession session) throws DfException;

    String buildOrdering(IDfSession session);

    IAttribute getInputAttribute(String columnName);

    IAttributeHandler getAttributeHandler(IAttribute inputAttribute);

    String getColumnAliasByIncoming(IAttribute incoming);

    String getColumnAliasByIncoming(String objectType, String attributeName);

    String getObjectType(String objectType);

}
