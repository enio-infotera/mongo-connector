package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.List;

public class AggregateQuery {

    static enum AggregateOperation {
        MATCH, GEO_NEAR, GROUP;
    }

    List<Aggregate> piplines = new ArrayList<Aggregate>();

    public List<Aggregate> getPiplines() {
        return this.piplines;
    }

    public AggregateQuery match(Query query) {
        if (query == null) {
            return this;
        }
        this.piplines.add(new MatchAggregate(query));
        return this;
    }

    public AggregateQuery geoNear(GeometrySpecifierQuery geoSpecifier) {
        if (geoSpecifier == null) {
            return this;
        }
        this.piplines.add(new GeoNearAggregate(geoSpecifier));
        return this;
    }

    public AggregateQuery group(GroupQuery groupQuery) {
        if (groupQuery == null) {
            return this;
        }
        this.piplines.add(new GroupAggregate(groupQuery));
        return this;
    }

    public static interface Aggregate {
        AggregateOperation getAggregationOperation();
    }

    public static class MatchAggregate
        implements Aggregate {
        private AggregateOperation aggregationOperation = AggregateOperation.MATCH;
        private Query query;

        public MatchAggregate(Query query) {
            this.query = query;
        }

        public AggregateOperation getAggregationOperation() {
            return this.aggregationOperation;
        }

        public Query getQuery() {
            return this.query;
        }
    }

    public static class GeoNearAggregate
        implements Aggregate {

        private AggregateOperation aggregationOperation = AggregateOperation.GEO_NEAR;
        private GeometrySpecifierQuery geometrySpecifier;

        public GeoNearAggregate(GeometrySpecifierQuery geometrySpecifier) {
            this.geometrySpecifier = geometrySpecifier;
        }

        public GeometrySpecifierQuery getGeometrySpecifier() {
            return this.geometrySpecifier;
        }

        public AggregateOperation getAggregationOperation() {
            return this.aggregationOperation;
        }
    }

    public static class GroupAggregate
        implements Aggregate {

        private AggregateOperation aggregationOperation = AggregateOperation.GROUP;
        private GroupQuery group;

        public GroupAggregate(GroupQuery group) {
            this.group = group;
        }

        public GroupQuery getGroup() {
            return this.group;
        }

        public AggregateOperation getAggregationOperation() {
            return this.aggregationOperation;
        }
    }


}
