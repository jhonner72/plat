package com.fujixerox.aus.asset.impl.query;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.query.IQueryBuilder;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;
import com.fujixerox.aus.asset.impl.mapping.Attribute;
import com.fujixerox.aus.asset.impl.mapping.AttributeMapping;
import com.fujixerox.aus.asset.impl.query.handlers.DateAttributeHandler;
import com.fujixerox.aus.asset.impl.query.handlers.IntegerAttributeHandler;
import com.fujixerox.aus.asset.impl.query.handlers.MapStringAttributeHandler;
import com.fujixerox.aus.asset.impl.query.handlers.StringAttributeHandler;
import com.fujixerox.aus.asset.impl.query.handlers.StringListAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class BaseQueryBuilderTest {

    private AttributeMapping _attributeMapping;

    private IAttribute _imPostingDateAttribute;

    private IAttribute _imObjectHandleAttribute;

    private IAttribute _imDeletedAttribute;

    private IAttribute _imPagesAttribute;

    private IAttribute _imNameAttribute;

    private IAttribute _imStringEnumAttribute1;

    private IAttribute _imStringEnumAttribute2;

    private IAttribute _dctmObjectNameAttribute;

    private IAttribute _dctmCreationDateAttribute;

    private IAttribute _dctmObjectIdAttribute;

    private IAttribute _dctmDeletedAttribute;

    private IAttribute _dctmPageCountAttribute;

    private IAttribute _dctmEnum1Attribute;

    private IAttribute _dctmEnum2Attribute;

    @Before
    public void setUp() {
        Map<IAttribute, IAttribute> attributeMap = new HashMap<IAttribute, IAttribute>();
        IAttributeHandler dateAttributeHandler = new DateAttributeHandler(
                "yyyyMMdd");
        IAttributeHandler keyAttributeHandler = new StringAttributeHandler(
                false, false);
        IAttributeHandler intAttributeHandler = new IntegerAttributeHandler();
        IAttributeHandler wildCardStringHandler = new StringAttributeHandler(
                true, true);
        IAttributeHandler listHandler = new StringListAttributeHandler(true,
                Arrays.asList("1", "2", "3", "4"));
        IAttributeHandler mapHandler = new MapStringAttributeHandler(true,
                Arrays.asList("1", "2", "3", "4"), Arrays.asList("a", "b", "c"));

        // im attributes
        _imPostingDateAttribute = new Attribute("vol2", "PostingDate");
        _imObjectHandleAttribute = new Attribute("vol2", "objecthandle");
        _imDeletedAttribute = new Attribute("vol2", "Deleted");
        _imPagesAttribute = new Attribute("vol2", "Pages");
        _imNameAttribute = new Attribute("vol2", "Name");
        _imStringEnumAttribute1 = new Attribute("vol2", "imEnum1");
        _imStringEnumAttribute2 = new Attribute("vol2", "imEnum2");

        // dctm attributes
        _dctmObjectNameAttribute = new Attribute("dm_document", "object_name",
                wildCardStringHandler);
        _dctmCreationDateAttribute = new Attribute("dm_document",
                "r_creation_date", dateAttributeHandler);
        _dctmObjectIdAttribute = new Attribute("dm_document", "r_object_id",
                keyAttributeHandler);
        _dctmDeletedAttribute = new Attribute("dm_document", "deleted",
                intAttributeHandler);
        _dctmPageCountAttribute = new Attribute("dm_document", "r_page_cnt",
                intAttributeHandler);
        _dctmEnum1Attribute = new Attribute("dm_document", "dctmEnum1",
                listHandler);
        _dctmEnum2Attribute = new Attribute("dm_document", "dctmEnum2",
                mapHandler);

        // mapping
        attributeMap.put(_imNameAttribute, _dctmObjectNameAttribute);
        attributeMap.put(_imPostingDateAttribute, _dctmCreationDateAttribute);
        attributeMap.put(_imObjectHandleAttribute, _dctmObjectIdAttribute);
        attributeMap.put(_imDeletedAttribute, _dctmDeletedAttribute);
        attributeMap.put(_imPagesAttribute, _dctmPageCountAttribute);
        attributeMap.put(_imStringEnumAttribute1, _dctmEnum1Attribute);
        attributeMap.put(_imStringEnumAttribute2, _dctmEnum2Attribute);
        _attributeMapping = new AttributeMapping("vol2", attributeMap);
    }

    @Test
    public void testProjection() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addResultFiled(_imPostingDateAttribute.getObjectType(),
                _imPostingDateAttribute.getName());
        queryBuilder.addResultFiled(_imObjectHandleAttribute.getObjectType(),
                _imObjectHandleAttribute.getName());
        assertEquals(
                "r_creation_date AS attr1, r_object_id AS attr2 FROM dm_document",
                queryBuilder.buildProjection(null, null, false));
        queryBuilder.addResultFiled(_imDeletedAttribute.getObjectType(),
                _imDeletedAttribute.getName());
        assertEquals(
                "r_creation_date AS attr1, r_object_id AS attr2, \"deleted\" AS attr3 FROM dm_document",
                queryBuilder.buildProjection(null, null, false));
    }

    @Test
    public void testMapping() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addResultFiled(_imPostingDateAttribute.getObjectType(),
                _imPostingDateAttribute.getName());
        queryBuilder.addResultFiled(_imObjectHandleAttribute.getObjectType(),
                _imObjectHandleAttribute.getName());
        queryBuilder.addResultFiled(_imPostingDateAttribute.getObjectType(),
                _imPostingDateAttribute.getName());
        queryBuilder.addResultFiled(_imDeletedAttribute.getObjectType(),
                _imDeletedAttribute.getName());
        queryBuilder.buildQuery(null, null);
        assertEquals("attr1", queryBuilder
                .getColumnAliasByIncoming(_imPostingDateAttribute));
        assertEquals("attr2", queryBuilder
                .getColumnAliasByIncoming(_imObjectHandleAttribute));
        assertEquals("attr3", queryBuilder
                .getColumnAliasByIncoming(_imDeletedAttribute));
        Assert.assertNull(queryBuilder
                .getColumnAliasByIncoming(_imNameAttribute));
        assertEquals(_imPostingDateAttribute, queryBuilder
                .getInputAttribute("attr1"));
        assertEquals(_imObjectHandleAttribute, queryBuilder
                .getInputAttribute("attr2"));
        assertEquals(_imDeletedAttribute, queryBuilder
                .getInputAttribute("attr3"));
        Assert.assertNull(queryBuilder.getInputAttribute("attr4"));
    }

    @Test
    public void testOrdering() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addResultFiled(_imPostingDateAttribute.getObjectType(),
                _imPostingDateAttribute.getName());
        queryBuilder.addResultFiled(_imObjectHandleAttribute.getObjectType(),
                _imObjectHandleAttribute.getName());
        queryBuilder.addOrdering(_imPostingDateAttribute.getObjectType(),
                _imPostingDateAttribute.getName(), false);
        assertEquals("r_creation_date", queryBuilder.buildOrdering(null));
        queryBuilder.addOrdering(_imObjectHandleAttribute.getObjectType(),
                _imObjectHandleAttribute.getName(), true);
        assertEquals("r_creation_date, r_object_id DESC", queryBuilder
                .buildOrdering(null));
    }

    @Test
    public void testIntLowHighRestriction() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Pages", "1", "2");
        assertEquals("(r_page_cnt >= 1 AND r_page_cnt <= 2)",
                queryBuilder.buildRestriction(null));
    }

    @Test
    public void testIntSameRestriction() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Pages", "1", "1");
        assertEquals("(r_page_cnt = 1)", queryBuilder.buildRestriction(null));
    }

    @Test
    public void testIntLowRestriction() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Pages", "1", "");
        assertEquals("(r_page_cnt >= 1)", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testHighLowRestriction() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Pages", "", "2");
        assertEquals("(r_page_cnt <= 2)", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testDateLowHighRestriction() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "PostingDate", "20150413",
                "20150416");
        assertEquals(
                "(r_creation_date >= DATE('2015/04/13 00:00:00', 'yyyy/mm/dd hh:mi:ss') AND r_creation_date <= DATE('2015/04/16 23:59:59', 'yyyy/mm/dd hh:mi:ss'))",
                queryBuilder.buildRestriction(null));
    }

    @Test
    public void testDateLowRestriction() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "PostingDate", "20150413", "");
        assertEquals(
                "(r_creation_date >= DATE('2015/04/13 00:00:00', 'yyyy/mm/dd hh:mi:ss'))",
                queryBuilder.buildRestriction(null));
    }

    @Test
    public void testDateHighRestriction() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "PostingDate", "", "20150416");
        assertEquals(
                "(r_creation_date <= DATE('2015/04/16 23:59:59', 'yyyy/mm/dd hh:mi:ss'))",
                queryBuilder.buildRestriction(null));
    }

    @Test
    public void testEscapeSingleBackSlash() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Name", "\\", "\\");
        assertEquals("(object_name = '\\')", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testEscapeDoubleBackSlash() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Name", "\\\\", "\\\\");
        assertEquals("(object_name = '\\\\')", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testEscapeSingleWildcard() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Name", "*", "*");
        assertEquals("(object_name LIKE '%' ESCAPE '\\')", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testEscapedSingleWildcard() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Name", "\\*", "\\*");
        assertEquals("(object_name = '\\*')", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testEscapedBackSlashAndSingleWildcard() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Name", "\\\\*", "\\\\*");
        assertEquals("(object_name LIKE '\\\\%' ESCAPE '\\')", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testEscapedAndNonWildcard() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Name", "\\a", "\\a");
        assertEquals("(object_name = '\\a')", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testWildcardsAndEscapedWildcard() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "Name", "*\\**", "*\\**");
        assertEquals("(object_name LIKE '%*%' ESCAPE '\\')", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testSimpleEnum() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "imEnum1", "2", "3");
        assertEquals("(dctmEnum1 IN ('2','3'))", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testSimpleEnumNoHighBoundary() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "imEnum1", "2", "");
        assertEquals("(dctmEnum1 IN ('2','3','4'))", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testSimpleEnumNoLowBoundary() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "imEnum1", "", "3");
        assertEquals("(dctmEnum1 IN ('1','2','3'))", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testSimpleEnumNoMatchHighBoundary() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "imEnum1", "1", "6");
        assertEquals("(dctmEnum1 IN ('1','2','3','4'))", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testSimpleEnumNoMatchLowBoundary() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "imEnum1", "0", "");
        assertEquals("(1<>1)", queryBuilder.buildRestriction(null));
    }

    @Test
    public void testMutualEnum() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "imEnum2", "2", "3");
        assertEquals("(dctmEnum2 IN ('b','c'))", queryBuilder
                .buildRestriction(null));
    }

    @Test
    public void testMutualEnumEmpty() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "imEnum2", "4", "4");
        assertEquals("(1<>1)", queryBuilder.buildRestriction(null));
    }

    @Test
    public void testMutualEnumEquals() throws Exception {
        IQueryBuilder queryBuilder = getQueryBuilder();
        queryBuilder.addRestriction("vol2", "imEnum2", "3", "3");
        assertEquals("(dctmEnum2 = 'c')", queryBuilder
                .buildRestriction(null));
    }

    protected IQueryBuilder getQueryBuilder() {
        return new AbstractQueryBuilder(_attributeMapping) {
            @Override
            protected String getObjectHandleAttributeName() {
                return "objecthanlde";
            }
        };
    }

}
