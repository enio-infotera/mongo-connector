package com.despegar.integration.mongo.connector;

import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;

import com.despegar.integration.mongo.entities.GenericIdentificableEntity;
import com.despegar.integration.mongo.query.AggregateQuery;
import com.despegar.integration.mongo.query.Query;
import com.despegar.integration.mongo.query.Update;
import com.despegar.integration.mongo.query.builder.MongoAggregationQuery;
import com.despegar.integration.mongo.query.builder.MongoQuery;
import com.despegar.integration.mongo.query.builder.MongoUpdate;
import com.mongodb.ReadPreference;

public class MongoCollection<T extends GenericIdentificableEntity<?>> {

    protected Class<T> clazz;
    private String collectionName;
    protected MongoDao<T> mongoDao;

    MongoCollection(String collectionName, Class<T> collectionClass, MongoDao<T> mongoDao) {
        this.collectionName = collectionName;
        this.clazz = collectionClass;
        this.mongoDao = mongoDao;
    }

    public <X extends Object> T get(final X id) {
        return this.mongoDao.findOne(id);
    }

    public T getOne() {
        Query query = new Query();
        query.setLimit(1);

        return this.getOne(query);
    }

    public T getOne(final Query query) {
        final MongoQuery mhq = new MongoQuery(query);

        return this.mongoDao.findOne(mhq.getQuery(), mhq.getSortInfo(), mhq.getQueryPage());
    }

    public List<T> getAll() {
        return this.getAll(null);
    }

    public List<T> getAll(final Query query) {
        return this.getAll(query, null);
    }

    public List<T> getAll(final Query query, final MutableInt count) {

        if (query == null) {
            return this.mongoDao.find();
        }

        final MongoQuery mongoQuery = new MongoQuery(query);

        return this.mongoDao.find(mongoQuery.getQuery(), null, mongoQuery.getSortInfo(), mongoQuery.getQueryPage(), count,
            this.isCrucialDataIntegration(query));
    }

    public List<T> getAll(Query query, MutableInt count, Integer pagingOffset, Integer pagingLimit) {
        query.setSkip(pagingOffset);
        query.setLimit(pagingLimit);
        return this.getAll(query, count);
    }

    public Integer count(final Query query) {
        if (query == null) {
            return this.mongoDao.getTotalObjectsInCollection(this.collectionName);
        }

        final MongoQuery mongoQuery = new MongoQuery(query);
        return this.mongoDao.getTotalObjectsInCollection(this.collectionName, mongoQuery.getQuery());
    }

    public <X extends Object> X add(final T t) {
        t.setId(null);
        return this.mongoDao.insert(t);
    }

    public <X extends Object> X insertIfNotPresent(final T t) {
        return this.mongoDao.insert(t);
    }

    public <X extends Object> X save(final T t) {
        return this.mongoDao.updateOrInsert(t);
    }

    @SuppressWarnings("unchecked")
    public <X extends Object> X update(final Query query, final Update updateQuery) {
        final MongoQuery mongoQuery = new MongoQuery(query);
        final MongoUpdate mongoUpdateQuery = new MongoUpdate(updateQuery);
        Object[] res = (Object[]) this.mongoDao.update(mongoQuery.getQuery(), mongoUpdateQuery.getUpdate(), false);
        if (res.length == 1) {
            return (X) res[0];
        }
        return null;
    }

    public <X extends Object> void remove(final X id) {
        this.mongoDao.delete(this.collectionName, id);
    }

    public void remove(Query query) {
        final MongoQuery mongoQuery = new MongoQuery(query);
        this.mongoDao.delete(this.collectionName, mongoQuery.getQuery());
    }

    public void removeAll() {
        this.mongoDao.dropCollection(this.collectionName);
    }

    public List<T> aggregate(AggregateQuery query) {
        MongoAggregationQuery mongoHandlerAggregationQuery = new MongoAggregationQuery(query);
        return this.mongoDao.aggregate(mongoHandlerAggregationQuery.getQuery());
    }

    public <Y extends Object> List<Y> aggregate(AggregateQuery query, Class<Y> returnClazz) {
        MongoAggregationQuery mongoHandlerAggregationQuery = new MongoAggregationQuery(query);
        return this.mongoDao.aggregate(mongoHandlerAggregationQuery.getQuery(), returnClazz);
    }

    public List<?> distinct(String key) {
        return this.mongoDao.distinct(key);
    }

    public List<?> distinct(String key, Query query) {
        MongoQuery q = new MongoQuery(query);
        return this.mongoDao.distinct(key, q.getQuery());
    }

    private ReadPreference isCrucialDataIntegration(Query query) {
        if (query.isCrucialDataIntegration()) {
            return ReadPreference.primary();
        }

        return null;
    }

    public void setCollectionName(final String collectionName) {
        this.collectionName = collectionName;
    }

    public void setClazz(final Class<T> clazz) {
        this.clazz = clazz;
    }

}
