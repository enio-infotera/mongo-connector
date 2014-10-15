package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.List;

public class AggregateQuery {

    static enum AggregateOperation {
        MATCH, GEO_NEAR, GROUP, UNWIND, SORT, SKIP, LIMIT, PROJECT;
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

    public AggregateQuery unwind(String property) {
        if (property == null) {
            return this;
        }
        this.piplines.add(new UnwindAggregate(property));
        return this;
    }

    public AggregateQuery skip(Integer skip) {
        if (skip == null) {
            return this;
        }
        this.piplines.add(new SkipAggregate(skip));
        return this;
    }

    public AggregateQuery limit(Integer limit) {
        if (limit == null) {
            return this;
        }
        this.piplines.add(new LimitAggregate(limit));
        return this;
    }

    public AggregateQuery project(ProjectQuery project) {
        if (project == null) {
            return this;
        }
        this.piplines.add(new ProjectAggregate(project));
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

    public AggregateQuery sort(SortQuery sortQuery) {
        if (sortQuery == null) {
            return this;
        }
        this.piplines.add(new SortAggregate(sortQuery));
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

    public static class UnwindAggregate
        implements Aggregate {

        private AggregateOperation aggregationOperation = AggregateOperation.UNWIND;
        private String property;

        public UnwindAggregate(String property) {
            this.property = property;
        }

        public AggregateOperation getAggregationOperation() {
            return this.aggregationOperation;
        }

        public String getProperty() {
            return this.property;
        }
    }

    public static class SkipAggregate
        implements Aggregate {

        private AggregateOperation aggregationOperation = AggregateOperation.SKIP;
        private Integer skip;

        public SkipAggregate(Integer skip) {
            this.skip = skip;
        }

        public AggregateOperation getAggregationOperation() {
            return this.aggregationOperation;
        }

        public Integer getSkip() {
            return this.skip;
        }
    }

    public static class LimitAggregate
        implements Aggregate {

        private AggregateOperation aggregationOperation = AggregateOperation.LIMIT;
        private Integer limit;

        public LimitAggregate(Integer limit) {
            this.limit = limit;
        }

        public AggregateOperation getAggregationOperation() {
            return this.aggregationOperation;
        }

        public Integer getLimit() {
            return this.limit;
        }
    }

    public static class SortAggregate
        implements Aggregate {

        private AggregateOperation aggregationOperation = AggregateOperation.SORT;
        private SortQuery sortQuery;

        public SortAggregate(SortQuery query) {
            this.sortQuery = query;
        }

        public AggregateOperation getAggregationOperation() {
            return this.aggregationOperation;
        }

        public SortQuery getSortQuery() {
            return this.sortQuery;
        }

    }

    public static class ProjectAggregate
        implements Aggregate {

        private AggregateOperation aggregationOperation = AggregateOperation.PROJECT;
        private ProjectQuery projectQuery;

        public ProjectAggregate(ProjectQuery query) {
            this.projectQuery = query;
        }

        public AggregateOperation getAggregationOperation() {
            return this.aggregationOperation;
        }

        public ProjectQuery getProjectQuery() {
            return this.projectQuery;
        }

    }


}
