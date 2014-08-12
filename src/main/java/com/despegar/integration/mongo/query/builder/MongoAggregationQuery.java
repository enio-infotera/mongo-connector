package com.despegar.integration.mongo.query.builder;

import java.util.ArrayList;
import java.util.List;

import com.despegar.integration.mongo.query.AggregateQuery;
import com.despegar.integration.mongo.query.AggregateQuery.Aggregate;
import com.despegar.integration.mongo.query.AggregateQuery.AggregateOperation;
import com.despegar.integration.mongo.query.AggregateQuery.GeoNearAggregate;
import com.despegar.integration.mongo.query.AggregateQuery.MatchAggregate;
import com.despegar.integration.mongo.query.Query;
import com.despegar.integration.mongo.query.aggregation.GeometrySpecifierQuery;
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
        }
        return aggregationOperation;
    }

    private DBObject getAggregationQuery(final Aggregate aggregation) {
        switch (aggregation.getAggregationOperation()) {
        case GEO_NEAR:
            return this.getGeometrySpecifiers(((GeoNearAggregate) aggregation).getGeometrySpecifier());
        case MATCH:
            return this.getMatchQuery(((MatchAggregate) aggregation).getQuery());
        }

        return null;
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
