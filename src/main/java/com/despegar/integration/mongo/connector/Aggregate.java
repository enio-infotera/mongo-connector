package com.despegar.integration.mongo.connector;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class Aggregate {

    static enum AggregateOperationType {
        MATCH("$match"), GEO_NEAR("$geoNear"), GROUP("$group"), UNWIND("$unwind"), SORT("$sort"), SKIP("$skip"), LIMIT(
                        "$limit"), PROJECT("$project");

        private String operator;

        AggregateOperationType(String operator) {
            this.operator = operator;
        }
    }

    private List<Bson> piplines = new ArrayList<Bson>();

    List<Bson> piplines() {
        return this.piplines;
    }

    public Aggregate addMatch(Match query) {
        if (query == null) {
            return this;
        }
        this.piplines.add(new MatchAggregate(query));
        return this;
    }

    public Aggregate addUnwind(String filePath) {
        if (filePath == null) {
            return this;
        }
        this.piplines.add(new UnwindAggregate(filePath));
        return this;
    }

    public Aggregate addSkip(Integer skip) {
        if (skip == null) {
            return this;
        }
        this.piplines.add(new SkipAggregate(skip));
        return this;
    }

    public Aggregate addLimit(Integer limit) {
        if (limit == null) {
            return this;
        }
        this.piplines.add(new LimitAggregate(limit));
        return this;
    }

    // TODO implementar
    // public Aggregate addProject(Project project) {
    // if (project == null) {
    // return this;
    // }
    // this.piplines.add(new ProjectAggregate(project));
    // return this;
    // }

    // TODO implementar
    // public Aggregate addGeoNear(GeometrySpecifier geoSpecifier) {
    // if (geoSpecifier == null) {
    // return this;
    // }
    // this.piplines.add(new GeoNearAggregate(geoSpecifier));
    // return this;
    // }

    public Aggregate addGroup(Group groupQuery) {
        if (groupQuery == null) {
            return this;
        }
        this.piplines.add(new GroupAggregate(groupQuery));
        return this;
    }

    public Aggregate addSort(Sort sortQuery) {
        if (sortQuery == null) {
            return this;
        }
        this.piplines.add(new SortAggregate(sortQuery));
        return this;
    }

    static abstract class AggregateOperation
        implements Bson {
        protected AggregateOperationType type;

        AggregateOperation(AggregateOperationType type) {
            this.type = type;
        }
    }

    static class MatchAggregate
        extends AggregateOperation {
        private Match query;

        public MatchAggregate(Match query) {
            super(AggregateOperationType.MATCH);
            this.query = query;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            return new BsonDocument(this.type.operator, this.query.toBsonDocument(documentClass, codecRegistry));
        }
    }

    static class GeoNearAggregate
        extends AggregateOperation {

        private GeometrySpecifier geometrySpecifier;

        public GeoNearAggregate(GeometrySpecifier geometrySpecifier) {
            super(AggregateOperationType.GEO_NEAR);
            this.geometrySpecifier = geometrySpecifier;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            // TODO hacer el bson
            return null;
        }
    }

    static class GroupAggregate
        extends AggregateOperation {

        private Group group;

        public GroupAggregate(Group group) {
            super(AggregateOperationType.GROUP);
            this.group = group;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            return new Document(this.type.operator, this.group.toBsonDocument(documentClass, codecRegistry)).toBsonDocument(
                documentClass, codecRegistry);
        }
    }

    static class UnwindAggregate
        extends AggregateOperation {

        private String filePath;

        public UnwindAggregate(String filePath) {
            super(AggregateOperationType.UNWIND);
            this.filePath = filePath;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            return new Document(this.type.operator, this.filePath).toBsonDocument(documentClass, codecRegistry);
        }
    }

    static class SkipAggregate
        extends AggregateOperation {

        private Integer skip;

        public SkipAggregate(Integer skip) {
            super(AggregateOperationType.SKIP);
            this.skip = skip;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            return new Document(this.type.operator, this.skip).toBsonDocument(documentClass, codecRegistry);
        }
    }

    static class LimitAggregate
        extends AggregateOperation {

        private Integer limit;

        public LimitAggregate(Integer limit) {
            super(AggregateOperationType.LIMIT);
            this.limit = limit;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            return new Document(this.type.operator, this.limit).toBsonDocument(documentClass, codecRegistry);
        }
    }

    static class SortAggregate
        extends AggregateOperation {

        private Sort sortQuery;

        public SortAggregate(Sort query) {
            super(AggregateOperationType.SORT);
            this.sortQuery = query;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            return new Document(this.type.operator, this.sortQuery).toBsonDocument(documentClass, codecRegistry);
        }
    }

    static class ProjectAggregate
        extends AggregateOperation {

        private Project projectQuery;

        public ProjectAggregate(Project query) {
            super(AggregateOperationType.PROJECT);
            this.projectQuery = query;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
