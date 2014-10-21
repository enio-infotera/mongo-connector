package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.OrderedMapIterator;

import com.despegar.integration.mongo.query.AggregateQuery.Aggregate;
import com.despegar.integration.mongo.query.AggregateQuery.AggregateOperation;
import com.despegar.integration.mongo.query.AggregateQuery.GeoNearAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.GroupAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.LimitAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.MatchAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.ProjectAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.SkipAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.SortAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.UnwindAggregate;
import com.despegar.integration.mongo.query.Query.OrderDirection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoAggregationQuery {

    private final AggregateQuery handlerAggregationQuery;

    public MongoAggregationQuery(final AggregateQuery query) {
        this.handlerAggregationQuery = query;
    }

    public List<DBObject> getQuery() {
        List<DBObject> res = new ArrayList<DBObject>();
        for (Aggregate aggregation : this.handlerAggregationQuery.getPiplines()) {
            String key = this.getAggregationOperation(aggregation.getAggregationOperation());
            Object query = this.getAggregationQuery(aggregation);
            if (query == null) {
                continue;
            }

            res.add(new BasicDBObject(key, query));
        }
        return res;
    }

    private String getAggregationOperation(final AggregateOperation operation) {
        String aggregationOperation = null;
        switch (operation) {
        case GEO_NEAR:
            aggregationOperation = "$geoNear";
            break;
        case MATCH:
            aggregationOperation = "$match";
            break;
        case GROUP:
            aggregationOperation = "$group";
            break;
        case LIMIT:
            aggregationOperation = "$limit";
            break;
        case SKIP:
            aggregationOperation = "$skip";
            break;
        case PROJECT:
            aggregationOperation = "$project";
            break;
        case SORT:
            aggregationOperation = "$sort";
            break;
        case UNWIND:
            aggregationOperation = "$unwind";
            break;
        }
        return aggregationOperation;
    }

    private Object getAggregationQuery(final Aggregate aggregation) {
        switch (aggregation.getAggregationOperation()) {
        case GEO_NEAR:
            return this.getGeometrySpecifiers(((GeoNearAggregate) aggregation).getGeometrySpecifier());
        case MATCH:
            return this.getMatchQuery(((MatchAggregate) aggregation).getQuery());
        case GROUP:
            return this.getGroupQuery(((GroupAggregate) aggregation).getGroup());
        case LIMIT:
            return ((LimitAggregate) aggregation).getLimit();
        case SKIP:
            return ((SkipAggregate) aggregation).getSkip();
        case UNWIND:
            return ((UnwindAggregate) aggregation).getProperty();
        case SORT:
            return this.getSortQuery(((SortAggregate) aggregation).getSortQuery());
        case PROJECT:
            return this.getProjectQuery(((ProjectAggregate) aggregation).getProjectQuery());
        }

        return null;
    }

    private DBObject getSortQuery(SortQuery sort) {
        final DBObject sortInfo = new BasicDBObject(sort.getSortMap().size());
        final OrderedMapIterator orderedMapIterator = sort.getSortMap().orderedMapIterator();
        while (orderedMapIterator.hasNext()) {
            final String key = (String) orderedMapIterator.next();
            final OrderDirection orderDir = (OrderDirection) orderedMapIterator.getValue();
            sortInfo.put(key, this.getOrderDir(orderDir));
        }

        return sortInfo;
    }

    private DBObject getProjectQuery(ProjectQuery project) {
        final DBObject projectInfo = new BasicDBObject();

        projectInfo.put("_id", project.getShowId());
        for (String property : project.getShowProperties()) {
            projectInfo.put(property, 1);
        }

        for (Map.Entry<String, Object> entry : project.getOperators().entrySet()) {
            projectInfo.put(entry.getKey(), MongoExpression.resolveObjects(entry.getKey()));
        }

        return projectInfo;
    }

    private DBObject getGroupQuery(GroupQuery group) {
        DBObject specifierProperties = new BasicDBObject();
        this.setGroupId(group, specifierProperties);
        for (Map.Entry<String, Expression> entry : group.getProperties().entrySet()) {
            final String key = entry.getKey();
            specifierProperties.put(key, MongoExpression.getExpressionDBObject(entry.getValue()));
        }

        return specifierProperties;
    }

    @SuppressWarnings("unchecked")
    private void setGroupId(GroupQuery group, DBObject specifierProperties) {
        if (group.getId() == null) {
            return;
        }

        if (group.getId() instanceof Map) {
            DBObject dbObject = new BasicDBObject();
            Map<String, Object> map = (HashMap<String, Object>) group.getId();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                dbObject.put(entry.getKey(), MongoExpression.resolveObjects(entry.getValue()));
            }

            specifierProperties.put("_id", dbObject);
            return;
        }

        specifierProperties.put("_id", MongoExpression.resolveObjects(group.getId()));
    }

    private DBObject getMatchQuery(Query query) {
        MongoQuery mongoQuery = new MongoQuery(query);
        return mongoQuery.getQuery();
    }

    private DBObject getGeometrySpecifiers(GeometrySpecifierQuery geometrySpecifiers) {
        DBObject specifierProperties = new BasicDBObject();
        this.putIfNotNull(geometrySpecifiers.getNear(), "near", specifierProperties);
        this.putIfNotNull(geometrySpecifiers.getDistanceField(), "distanceField", specifierProperties);
        this.putIfNotNull(geometrySpecifiers.getLimit(), "limit", specifierProperties);
        this.putIfNotNull(geometrySpecifiers.getNum(), "num", specifierProperties);
        this.putIfNotNull(geometrySpecifiers.getMaxDistance(), "maxDistance", specifierProperties);
        this.putIfNotNull(geometrySpecifiers.isSpherical(), "spherical", specifierProperties);
        this.putIfNotNull(geometrySpecifiers.getDistanceMultiplier(), "distanceMultiplier", specifierProperties);
        this.putIfNotNull(geometrySpecifiers.getIncludeLocs(), "includeLocs", specifierProperties);
        this.putIfNotNull(geometrySpecifiers.isUniqueDocs(), "uniqueDocs", specifierProperties);
        return specifierProperties;
    }

    private void putIfNotNull(Object value, String property, DBObject specifierProperites) {
        if (value != null) {
            specifierProperites.put(property, value);
        }
    }

    private int getOrderDir(final OrderDirection orderDir) {

        if (Query.OrderDirection.ASC.equals(orderDir)) {
            return 1;
        }

        if (Query.OrderDirection.DESC.equals(orderDir)) {
            return -1;
        }

        return 0;
    }


}
