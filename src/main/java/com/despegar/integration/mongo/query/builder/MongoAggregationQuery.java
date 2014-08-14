package com.despegar.integration.mongo.query.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.despegar.integration.mongo.query.AggregateQuery;
import com.despegar.integration.mongo.query.AggregateQuery.Aggregate;
import com.despegar.integration.mongo.query.AggregateQuery.AggregateOperation;
import com.despegar.integration.mongo.query.AggregateQuery.GeoNearAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.GroupAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.MatchAggregate;
import com.despegar.integration.mongo.query.Query;
import com.despegar.integration.mongo.query.aggregation.GeometrySpecifierQuery;
import com.despegar.integration.mongo.query.aggregation.GroupQuery;
import com.despegar.integration.mongo.query.aggregation.GroupQuery.GroupOperation;
import com.despegar.integration.mongo.query.aggregation.GroupQuery.OperationWithFunction;
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
        }
        return aggregationOperation;
    }

    private String getGroupOperation(final GroupOperation operation) {
        String groupOperation = null;
        switch (operation) {
        case SUM:
            groupOperation = "$sum";
            break;
        case AVG:
            groupOperation = "$avg";
            break;
        case MIN:
            groupOperation = "$min";
            break;
        case MAX:
            groupOperation = "$max";
            break;
        case PUSH:
            groupOperation = "$push";
            break;
        case LAST:
            groupOperation = "$last";
            break;
        case FIRST:
            groupOperation = "$first";
            break;
        case ADD_TO_SET:
            groupOperation = "$addToSet";
            break;
        }
        return groupOperation;
    }

    private DBObject getAggregationQuery(final Aggregate aggregation) {
        switch (aggregation.getAggregationOperation()) {
        case GEO_NEAR:
            return this.getGeometrySpecifiers(((GeoNearAggregate) aggregation).getGeometrySpecifier());
        case MATCH:
            return this.getMatchQuery(((MatchAggregate) aggregation).getQuery());
        case GROUP:
            return this.getGroupQuery(((GroupAggregate) aggregation).getGroup());
        }

        return null;
    }

    private DBObject getGroupQuery(GroupQuery group) {
        DBObject specifierProperties = new BasicDBObject();
        for (Map.Entry<String, OperationWithFunction> entry : group.getOperators().entrySet()) {
            final String key = entry.getKey();
            final OperationWithFunction operationWithfunction = entry.getValue();
            final String groupOperation = this.getGroupOperation(operationWithfunction.getGroupOperation());

            DBObject function = new BasicDBObject(groupOperation, operationWithfunction.getValue());
            specifierProperties.put(key, function);
        }

        DBObject id = new BasicDBObject();
        for (Map.Entry<String, String> entry : group.getId().entrySet()) {
            id.put(entry.getKey(), entry.getValue());
        }
        specifierProperties.put("_id", id);
        return specifierProperties;
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


}
