package com.despegar.integration.mongo.connector;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.despegar.integration.mongo.entities.GenericIdentifiableEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.DeleteManyModel;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;

public class Bulk<T extends GenericIdentifiableEntity<?>> {

    private List<BulkOperation> operations = new ArrayList<BulkOperation>();
    private Boolean orderRequired;

    public Bulk(Boolean orderRequired) {
        this.orderRequired = orderRequired;
    }

    static abstract class BulkOperation {
        public abstract WriteModel<Document> getWriteModel(ObjectMapper mapper);
    }

    static class BulkInsert<T>
        extends BulkOperation {

        private T entity;

        BulkInsert(T entity) {
            this.entity = entity;
        }

        @Override
        public WriteModel<Document> getWriteModel(ObjectMapper mapper) {
            return new InsertOneModel<Document>(mapper.convertValue(this.entity, Document.class));
        }
    }

    static class BulkReplace<T>
        extends BulkOperation {

        private Match match;
        private T entity;

        BulkReplace(Match match, T entity) {
            this.match = match;
            this.entity = entity;
        }

        @Override
        public WriteModel<Document> getWriteModel(ObjectMapper mapper) {
            return new ReplaceOneModel<Document>(this.match, mapper.convertValue(this.entity, Document.class));
        }
    }

    static class BulkUpdateOne
        extends BulkOperation {

        private Match query;
        private Update update;

        BulkUpdateOne(Match query, Update update) {
            this.query = query;
            this.update = update;
        }

        @Override
        public WriteModel<Document> getWriteModel(ObjectMapper mapper) {
            return new UpdateOneModel<Document>(this.query, this.update);
        }
    }

    static class BulkUpdateMany
        extends BulkOperation {
        private Match query;
        private Update update;

        BulkUpdateMany(Match query, Update update) {
            this.query = query;
            this.update = update;
        }

        @Override
        public WriteModel<Document> getWriteModel(ObjectMapper mapper) {
            return new UpdateManyModel<Document>(this.query, this.update);
        }
    }

    static class BulkRemoveMany
        extends BulkOperation {
        private Match query;

        BulkRemoveMany(Match query) {
            this.query = query;
        }

        @Override
        public WriteModel<Document> getWriteModel(ObjectMapper mapper) {
            return new DeleteManyModel<Document>(this.query);
        }
    }

    static class BulkRemoveOne
        extends BulkOperation {
        private Match query;

        BulkRemoveOne(Match query) {
            this.query = query;
        }

        @Override
        public WriteModel<Document> getWriteModel(ObjectMapper mapper) {
            return new DeleteOneModel<Document>(this.query);
        }
    }

    public Bulk<T> insert(List<T> entities) {
        for (T entity : entities) {
            this.operations.add(new BulkInsert<T>(entity));
        }
        return this;
    }

    public Bulk<T> insert(T entity) {
        this.operations.add(new BulkInsert<T>(entity));
        return this;
    }

    public Bulk<T> updateOne(Match query, Update update) {
        this.operations.add(new BulkUpdateOne(query, update));
        return this;
    }

    public Bulk<T> updateMany(Match query, Update update) {
        this.operations.add(new BulkUpdateMany(query, update));
        return this;
    }

    public Bulk<T> removeOne(Match query) {
        this.operations.add(new BulkRemoveOne(query));
        return this;
    }

    public Bulk<T> removeMany(Match query) {
        this.operations.add(new BulkRemoveMany(query));
        return this;
    }

    public Bulk<T> replaceOne(Match query, T entity) {
        this.operations.add(new BulkReplace<T>(query, entity));
        return this;
    }

    Boolean isOrderRequired() {
        return this.orderRequired;
    }

    List<BulkOperation> operations() {
        return this.operations;
    }
}
