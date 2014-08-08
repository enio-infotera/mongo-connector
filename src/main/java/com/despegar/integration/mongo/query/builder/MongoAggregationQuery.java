package com.despegar.integration.mongo.query.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.despegar.integration.mongo.query.AggregationQuery;
import com.despegar.integration.mongo.query.AggregationQuery.Aggregation;
import com.despegar.integration.mongo.query.AggregationQuery.AggregationOperation;
import com.despegar.integration.mongo.query.AggregationQuery.GeometryAggregationSpecifier;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoAggregationQuery {

    private final AggregationQuery handlerAggregationQuery;

    public MongoAggregationQuery(final AggregationQuery query) {
        this.handlerAggregationQuery = query;
    }

    public List<DBObject> getQuery() {
        List<DBObject> res = new ArrayList<DBObject>();
        List<Aggregation> aggregationList = this.handlerAggregationQuery.getAggregations();
        for (Aggregation aggregation : aggregationList) {
            res.add(this.appendAggregationOperations(aggregation));
        }
        return res;
    }

    private DBObject appendAggregationOperations(Aggregation aggregation) {
        DBObject dbQuery = new BasicDBObject();
        String key = this.getAggregationOperation(aggregation.getAggregationOperation());
        dbQuery.put(key, this.getAggregationQuery(aggregation));
        return dbQuery;
    }

    private DBObject getAggregationQuery(Aggregation aggregation) {
        Assert.notNull(
            AggregationOperation.GEO_NEAR.equals(aggregation.getAggregationOperation())
                && aggregation.getGeometrySpecifiers() == null, "Specifiers for geometry operations are required.");

        MongoQuery mongoHandlerQuery = new MongoQuery(aggregation.getQuery());

        if (aggregation.getGeometrySpecifiers() != null) {
            DBObject geometrySpecifiers = this.getGeometrySpecifiers(aggregation.getGeometrySpecifiers());
            geometrySpecifiers.put("query", mongoHandlerQuery.getQuery());
            return geometrySpecifiers;
        } else {
            return mongoHandlerQuery.getQuery();
        }
    }

    private DBObject getGeometrySpecifiers(GeometryAggregationSpecifier geometrySpecifiers) {
        DBObject specifierProperties = new BasicDBObject();
        specifierProperties.put("near", geometrySpecifiers.getNear());
        specifierProperties.put("distanceField", geometrySpecifiers.getDistanceField());
        specifierProperties.put("limit", geometrySpecifiers.getLimit());
        specifierProperties.put("num", geometrySpecifiers.getNum());
        specifierProperties.put("maxDistance", geometrySpecifiers.getMaxDistance());
        specifierProperties.put("spherical", geometrySpecifiers.isSpherical());
        specifierProperties.put("distanceMultiplier", geometrySpecifiers.getDistanceMultiplier());
        specifierProperties.put("includeLocs", geometrySpecifiers.getIncludeLocs());
        specifierProperties.put("uniqueDocs", geometrySpecifiers.isUniqueDocs());
        return specifierProperties;
    }

    private String getAggregationOperation(final AggregationOperation operation) {
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

}
