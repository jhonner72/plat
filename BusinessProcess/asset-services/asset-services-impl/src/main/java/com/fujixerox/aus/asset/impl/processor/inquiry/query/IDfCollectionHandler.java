package com.fujixerox.aus.asset.impl.processor.inquiry.query;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.util.StorageUtils;
import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.processor.IFallbackDataProvider;
import com.fujixerox.aus.asset.impl.constants.DocumentumConstants;
import com.fujixerox.aus.asset.impl.mapping.Attribute;
import com.fujixerox.aus.asset.impl.processor.RequestQueryBuilder;
import com.fujixerox.aus.asset.impl.processor.inquiry.imaging.ImageProcessor;
import com.fujixerox.aus.asset.model.beans.generated.request.IndexQuery;
import com.fujixerox.aus.asset.model.beans.generated.request.Inquiry;
import com.fujixerox.aus.asset.model.beans.generated.request.ResultField;
import com.fujixerox.aus.asset.model.beans.generated.request.YNBool;
import com.fujixerox.aus.asset.model.beans.generated.response.IndexQueryResult;
import com.fujixerox.aus.asset.model.beans.generated.response.Item;
import com.fujixerox.aus.asset.model.beans.generated.response.RField;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class IDfCollectionHandler extends AbstractCollectionHandler {

    private final Inquiry _request;

    private final IndexQueryResult _queryResult;

    private final RequestQueryBuilder _queryBuilder;

    private final ImageProcessor _imageProcessor;

    private final Map<String, IAttribute> _attributeMap = new LinkedHashMap<String, IAttribute>();

    public IDfCollectionHandler(Inquiry request, IndexQuery query,
            IndexQueryResult queryResult, RequestQueryBuilder queryBuilder,
            IFallbackDataProvider fallbackDataProvider) {
        super();
        _request = request;
        _queryResult = queryResult;
        _queryBuilder = queryBuilder;
        if (_request.getImages() == YNBool.Y) {
            _imageProcessor = new ImageProcessor(_request, query,
                    fallbackDataProvider);
        } else {
            _imageProcessor = null;
        }
    }

    @Override
    public void handleMetadata(IDfCollection row) throws DfException {
        // this is count query, no need to process attributes
        if (_request.getCount() == YNBool.Y) {
            return;
        }
        // request does not contain result fields, using default
        if (_request.getResultfields().isEmpty()) {
            for (int i = 0, n = row.getAttrCount(); i < n; i++) {
                String columnName = row.getAttr(i).getName();
                IAttribute attribute = _queryBuilder
                        .getInputAttribute(columnName);
                if (attribute != null) {
                    _attributeMap.put(columnName, new Attribute(attribute,
                            _queryBuilder.getAttributeHandler(attribute)));
                }
            }
            return;
        }
        // request contains result fields, using them
        for (ResultField resultField : _request.getResultfields()) {
            String columnName = _queryBuilder.getColumn(_request, resultField);
            if (columnName == null) {
                continue;
            }
            if (!row.hasAttr(columnName)) {
                continue;
            }
            IAttribute attribute = _queryBuilder.getInputAttribute(columnName);
            if (attribute != null) {
                _attributeMap.put(columnName, new Attribute(attribute,
                        _queryBuilder.getAttributeHandler(attribute)));
            }
        }
    }

    @Override
    public void handleRow(IDfCollection row, int position) throws DfException {
        // count query
        if (_request.getCount() == YNBool.Y) {
            _queryResult.setRetCount(row.getInt(row.getAttr(0).getName()));
            return;
        }

        // select query
        Item item = new Item();
        for (Map.Entry<String, IAttribute> e : _attributeMap.entrySet()) {
            String attrName = e.getKey();
            IAttribute attribute = e.getValue();
            RField field = new RField();
            field.setName(attribute.getName());
            String value = getValue(row.getValue(attrName), attribute
                    .getHandler());
            field.setValue(value);
            item.getRfields().add(field);
        }
        handleObjectName(row, item);
        if (_request.getMediastatus() == YNBool.Y) {
            handleMediaStatus(row, item);
        }
        if (_request.getImages() == YNBool.Y) {
            handleImages(row, item);
        }
        _queryResult.withItems(item);
    }

    private void handleObjectName(IDfCollection row, Item item) {
        item.setObjectname(_queryBuilder.getObjectType(_request));
    }

    private void handleMediaStatus(IDfCollection row, Item item)
        throws DfException {
        if (!row.hasAttr(DocumentumConstants.R_CONTENT_SIZE)) {
            return;
        }
        if (!row.hasAttr(DocumentumConstants.A_STORAGE_TYPE)) {
            return;
        }
        int size = row.getInt(DocumentumConstants.R_CONTENT_SIZE);
        if (size <= 0) {
            item.setMediastatus("");
            return;
        }
        String storage = row.getString(DocumentumConstants.A_STORAGE_TYPE);
        if (StringUtils.isEmpty(storage)) {
            item.setMediastatus("");
            return;
        }
        IDfSession session = row.getSession();
        if (!StorageUtils.isStoreAvailable(session, storage)) {
            item.setMediastatus("");
            return;
        }
        item.setMediastatus("m");
    }

    private void handleImages(IDfCollection row, Item item) throws DfException {
        if (_imageProcessor != null) {
            _imageProcessor.processImages(row, item);
        }
    }

}
