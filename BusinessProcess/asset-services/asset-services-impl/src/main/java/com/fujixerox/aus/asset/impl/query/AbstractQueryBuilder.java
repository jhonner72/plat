package com.fujixerox.aus.asset.impl.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.query.ReservedWords;
import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.mapping.IAttributeMapping;
import com.fujixerox.aus.asset.api.query.IQueryBuilder;
import com.fujixerox.aus.asset.api.query.IQueryCondition;
import com.fujixerox.aus.asset.api.query.IQueryOrdering;
import com.fujixerox.aus.asset.api.query.IQueryRestriction;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;
import com.fujixerox.aus.asset.api.util.logger.Logger;
import com.fujixerox.aus.asset.impl.constants.DocumentumConstants;
import com.fujixerox.aus.asset.impl.mapping.Attribute;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractQueryBuilder implements IQueryBuilder {

    private static final String[] MEDIA_ATTRIBUTES = new String[] {
        DocumentumConstants.R_OBJECT_ID, DocumentumConstants.R_CONTENT_SIZE,
        DocumentumConstants.A_STORAGE_TYPE, };

    private final Map<IAttribute, IAttribute> _queryAttributes = new LinkedHashMap<>();

    private final List<IQueryRestriction> _queryRestrictions = SetUniqueList
            .setUniqueList(new ArrayList<IQueryRestriction>());

    private final List<IQueryOrdering> _queryOrderings = SetUniqueList
            .setUniqueList(new ArrayList<IQueryOrdering>());

    private final Map<String, String> _tableAliases = new HashMap<String, String>();

    private final Map<IAttribute, String> _columnAliases = new HashMap<IAttribute, String>();

    private final IAttributeMapping _attributeMapping;

    protected AbstractQueryBuilder(IAttributeMapping attributeMapping) {
        _attributeMapping = attributeMapping;
    }

    protected abstract String getObjectHandleAttributeName();

    @Override
    public final void addResultFiled(String objectType, String attributeName) {
        IAttribute incoming = _attributeMapping.getIncomingAttribute(
                objectType, attributeName);
        if (incoming == null) {
            Logger.debug("Unable to find mapping for attribute {0}:{1}",
                    objectType, attributeName);
            return;
        }

        IAttribute outgoing = _attributeMapping
                .getOutgoingAttributeByIncoming(incoming);
        if (outgoing == null) {
            Logger.debug(
                    "Unable to find corresponding attribute for attribute {0}:{1}",
                    objectType, attributeName);
            return;
        }

        String specification = buildColumnSpecification(incoming, outgoing);
        Logger.debug("Result field {0}:{1} is mapped to {2}", objectType,
                attributeName, specification);
        _queryAttributes.put(incoming, outgoing);
    }

    @Override
    public final void addRestriction(String objectType, String attributeName,
            String lowValue, String highValue) {
        addRestriction(objectType, attributeName, lowValue, highValue, true);
    }

    @Override
    public final void addRestriction(String objectType, String attributeName,
            String lowValue, String highValue, boolean matchCase) {
        addRestriction(objectType, attributeName, new QueryCondition(lowValue,
                highValue, matchCase));
    }

    @Override
    public final void addRestriction(String objectType, String attributeName,
            IQueryCondition condition) {
        IAttribute incoming = _attributeMapping.getIncomingAttribute(
                objectType, attributeName);
        if (incoming == null) {
            Logger.debug("Unable to find mapping for attribute {0}:{1}",
                    objectType, attributeName);
            return;
        }

        IAttribute outgoing = _attributeMapping
                .getOutgoingAttributeByIncoming(incoming);
        if (outgoing == null) {
            Logger.debug(
                    "Unable to find corresponding attribute for attribute {0}:{1}",
                    objectType, attributeName);
            return;
        }

        IQueryRestriction restriction = new QueryRestriction(outgoing,
                condition);

        String qualification = buildColumnQualification(outgoing);
        Logger.debug("Query field {0}:{1} is mapped to {2}", objectType,
                attributeName, qualification);
        _queryRestrictions.add(restriction);
    }

    @Override
    public final void addOrdering(String objectType, String attributeName,
            boolean descending) {
        IAttribute incoming = _attributeMapping.getIncomingAttribute(
                objectType, attributeName);
        if (incoming == null) {
            Logger.debug("Unable to find mapping for attribute {0}:{1}",
                    objectType, attributeName);
            return;
        }

        IAttribute outgoing = _attributeMapping
                .getOutgoingAttributeByIncoming(incoming);
        if (outgoing == null) {
            Logger.debug(
                    "Unable to find corresponding attribute for attribute {0}:{1}",
                    objectType, attributeName);
            return;
        }

        QueryOrdering ordering = new QueryOrdering(outgoing, descending);
        String qualification = buildColumnQualification(outgoing);
        Logger.debug("Ordering field {0}:{1} is mapped to {2}", objectType,
                attributeName, qualification);
        _queryOrderings.add(ordering);
    }

    @Override
    public final String buildQuery(IDfSession session, String objectType)
        throws DfException {
        return buildQuery(session, objectType, false);
    }

    @Override
    public final String buildQuery(IDfSession session, String objectType,
            boolean countQuery) throws DfException {
        return buildQuery(session, objectType, countQuery, false);
    }

    @Override
    public final String buildQuery(IDfSession session, String objectType,
            boolean countQuery, boolean addMediaAttributes) throws DfException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(buildProjection(session, objectType, countQuery,
                addMediaAttributes));
        String restriction = buildRestriction(session);
        if (StringUtils.isNotBlank(restriction)) {
            query.append(" WHERE ").append(restriction);
        }
        if (!countQuery) {
            String ordering = buildOrdering(session);
            if (StringUtils.isNotBlank(ordering)) {
                query.append(" ORDER BY ").append(ordering);
            }
        }
        Logger.debug("Query is: {0}", query.toString());
        return query.toString();
    }

    @Override
    public final String buildProjection(IDfSession session, String objectType) {
        return buildProjection(session, objectType, false);
    }

    @Override
    public final String buildProjection(IDfSession session, String objectType,
            boolean countQuery) {
        return buildProjection(session, objectType, countQuery, false);
    }

    @Override
    public final String buildProjection(IDfSession session, String objectType,
            boolean countQuery, boolean addMediaAttributes) {
        StringBuilder projection = new StringBuilder();
        if (countQuery) {
            projection.append("COUNT(*)");
        } else {
            if (_queryAttributes.isEmpty()) {
                for (IAttribute attribute : _attributeMapping
                        .getAllIncomingAttributes(objectType)) {
                    addResultFiled(attribute.getObjectType(),
                            attribute.getName());
                }
            }

            for (Map.Entry<IAttribute, IAttribute> e : _queryAttributes
                    .entrySet()) {
                String alias = buildColumnSpecification(e.getKey(),
                        e.getValue());
                if (StringUtils.isNotBlank(alias)) {
                    if (projection.length() > 0) {
                        projection.append(", ");
                    }
                    projection.append(alias);
                }
            }

            if (addMediaAttributes) {
                for (String attr : MEDIA_ATTRIBUTES) {
                    if (projection.length() > 0) {
                        projection.append(", ");
                    }
                    projection.append(attr);
                }
            }

            if (projection.length() == 0) {
                projection.append('*');
            }
        }
        projection.append(" FROM ").append(buildTables());
        return projection.toString();
    }

    @Override
    public final String buildRestriction(IDfSession session) throws DfException {
        StringBuilder restriction = new StringBuilder();
        Set<IAttributeHandler> handlers = new HashSet<IAttributeHandler>();
        try {
            for (IQueryRestriction queryRestriction : _queryRestrictions) {
                String qualification = buildColumnQualification(queryRestriction
                        .getAttribute());
                String condition = null;
                IAttributeHandler handler = queryRestriction.getHandler();
                if (handler != null) {
                    handlers.add(handler);
                    condition = handler.getRestriction(session, qualification,
                            queryRestriction);
                }
                if (StringUtils.isBlank(condition)) {
                    continue;
                }
                if (restriction.length() > 0) {
                    restriction.append(" AND ");
                }
                restriction.append('(').append(condition).append(')');
            }
        } finally {
            for (IAttributeHandler handler : handlers) {
                handler.dispose();
            }
        }
        return restriction.toString();
    }

    @Override
    public final String buildOrdering(IDfSession session) {
        StringBuilder ordering = new StringBuilder();
        for (IQueryOrdering queryOrdering : _queryOrderings) {
            String qualification = buildColumnQualification(queryOrdering
                    .getAttribute());
            if (StringUtils.isBlank(qualification)) {
                continue;
            }
            if (ordering.length() > 0) {
                ordering.append(", ");
            }
            ordering.append(qualification);
            if (queryOrdering.isDescending()) {
                ordering.append(" DESC");
            }
        }
        return ordering.toString();
    }

    @Override
    public final IAttribute getInputAttribute(String columnName) {
        IAttribute attribute = null;
        for (Map.Entry<IAttribute, String> entry : _columnAliases.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(columnName)) {
                attribute = entry.getKey();
                break;
            }
        }
        return attribute;
    }

    public final IAttributeHandler getAttributeHandler(IAttribute inputAttribute) {
        IAttributeHandler handler = inputAttribute.getHandler();
        if (handler != null) {
            return handler;
        }
        IAttribute outgoingAttribute = _attributeMapping
                .getOutgoingAttributeByIncoming(inputAttribute);
        if (outgoingAttribute != null) {
            return outgoingAttribute.getHandler();
        }
        return null;
    }

    private String buildTables() {
        StringBuilder tables = new StringBuilder();
        for (Map.Entry<String, String> entry : _tableAliases.entrySet()) {
            if (tables.length() > 0) {
                tables.append(", ");
            }
            tables.append(entry.getKey()).append(" ").append(entry.getValue());
        }
        return tables.toString();
    }

    private String buildTableAlias(String tableName) {
        if (_tableAliases.containsKey(tableName)) {
            return _tableAliases.get(tableName);
        }
        String alias = "t" + (_tableAliases.size() + 1);
        _tableAliases.put(tableName, alias);
        return alias;
    }

    private String buildColumnQualification(IAttribute outgoing) {
        String qualification = outgoing.getName();
        // dealing with existing attribute
        if (StringUtils.isNotBlank(qualification)) {
            String tableAlias = buildTableAlias(outgoing.getObjectType());
            if (ReservedWords.isReserved(qualification)) {
                qualification = "\"" + qualification + "\"";
            }
            return tableAlias + "." + qualification;
        }
        return null;
    }

    private String buildColumnSpecification(IAttribute incoming,
            IAttribute outgoing) {
        if (!_columnAliases.containsKey(incoming)) {
            String alias = "attr" + (_columnAliases.size() + 1);
            _columnAliases.put(incoming, alias);
        }
        String qualification = buildColumnQualification(outgoing);
        // dealing with existing attribute
        if (StringUtils.isNotBlank(qualification)) {
            return qualification + " AS " + _columnAliases.get(incoming);
        }
        String literal = null;
        IAttributeHandler handler = outgoing.getHandler();
        if (handler == null) {
            handler = incoming.getHandler();
        }
        literal = handler.getDefaultValue();
        if (StringUtils.isBlank(literal)) {
            literal = "0";
        }
        return "'" + literal + "' AS " + _columnAliases.get(incoming);
    }

    @Override
    public final String getColumnAliasByIncoming(IAttribute incoming) {
        return _columnAliases.get(incoming);
    }

    @Override
    public final String getColumnAliasByIncoming(String objectType,
            String attributeName) {
        return getColumnAliasByIncoming(Attribute.of(getObjectType(objectType),
                attributeName));
    }

    @Override
    public String getObjectType(String objectType) {
        return _attributeMapping.getObjectType(objectType);
    }

}
