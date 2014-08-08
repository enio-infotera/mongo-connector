package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.List;

import com.despegar.integration.mongo.entities.Point;

public class AggregationQuery {

    public static enum AggregationOperation {
        GEO_NEAR, MATCH, GROUP
    }

    public static enum GeometrySpecifiers {
        NEAR, DISTANCE_FIELD, LIMIT, NUM, MAX_DISTANCE, SPHERICAL, DISTANCE_MULTIPLIER, INCLUDE_LOCS, UNIQUE_DOCS
    }

    private List<Aggregation> aggregations = new ArrayList<AggregationQuery.Aggregation>();

    public void addAggregation(Aggregation aggregation) {
        this.aggregations.add(aggregation);
    }

    public List<Aggregation> getAggregations() {
        return this.aggregations;
    }

    public static class Aggregation {
        private AggregationOperation aggregationOperation;
        private GeometryAggregationSpecifier geometrySpecifiers;
        private Query query;

        public Aggregation(AggregationOperation aggregationOperation, GeometryAggregationSpecifier geometrySpecifiers,
            Query query) {
            super();
            this.aggregationOperation = aggregationOperation;
            this.geometrySpecifiers = geometrySpecifiers;
            this.query = query;
        }

        public AggregationOperation getAggregationOperation() {
            return this.aggregationOperation;
        }

        public GeometryAggregationSpecifier getGeometrySpecifiers() {
            return this.geometrySpecifiers;
        }

        public Query getQuery() {
            return this.query;
        }
    }

    public static class GeometryAggregationSpecifier {
        private Point near;
        private String distanceField;
        private Integer limit;
        private Integer num;
        private Double maxDistance;
        private boolean spherical = true;
        private Double distanceMultiplier;
        private String includeLocs;
        private boolean uniqueDocs = true;

        public GeometryAggregationSpecifier(Double[] point, String distanceField) {
            this.near = new Point(point);
            this.distanceField = distanceField;
        }

        public GeometryAggregationSpecifier(Double[] point, String distanceField, Integer limit, Integer num,
            Double maxDistance, boolean spherical, Double distanceMultiplier, String includeLocs, boolean uniqueDocs) {
            this.near = new Point(point);
            this.distanceField = distanceField;
            this.limit = limit;
            this.num = num;
            this.maxDistance = maxDistance;
            this.spherical = spherical;
            this.distanceMultiplier = distanceMultiplier;
            this.includeLocs = includeLocs;
            this.uniqueDocs = uniqueDocs;
        }

        public Point getNear() {
            return this.near;
        }

        public String getDistanceField() {
            return this.distanceField;
        }

        public Integer getLimit() {
            return this.limit;
        }

        public Integer getNum() {
            return this.num;
        }

        public Double getMaxDistance() {
            return this.maxDistance;
        }

        public boolean isSpherical() {
            return this.spherical;
        }

        public Double getDistanceMultiplier() {
            return this.distanceMultiplier;
        }

        public String getIncludeLocs() {
            return this.includeLocs;
        }

        public boolean isUniqueDocs() {
            return this.uniqueDocs;
        }
    }
}
