package com.despegar.integration.mongo.connector;

import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;

import com.despegar.integration.mongo.entities.GenericIdentificableEntity;
import com.despegar.integration.mongo.query.AggregateQuery;
import com.despegar.integration.mongo.query.MongoAggregationQuery;
import com.despegar.integration.mongo.query.MongoQuery;
import com.despegar.integration.mongo.query.MongoUpdate;
import com.despegar.integration.mongo.query.Query;
import com.despegar.integration.mongo.query.Update;
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

    @Deprecated
    public T getOne() {
        return this.findOne();
    }

    @Deprecated
    public T getOne(final Query query) {
        return this.findOne(query);
    }

    public T findOne() {
        Query query = new Query();
        query.limit(1);

        return this.findOne(query);
    }

    public T findOne(final Query query) {
        final MongoQuery mhq = new MongoQuery(query);

        return this.mongoDao.findOne(mhq.getQuery(), mhq.getSortInfo(), mhq.getQueryPage());
    }

    @Deprecated
    public List<T> getAll() {
        return this.find();
    }

    @Deprecated
    public List<T> getAll(final Query query) {
        return this.find(query);
    }

    @Deprecated
    public List<T> getAll(final Query query, final MutableInt count) {
        return this.find(query, count);
    }

    @Deprecated
    public List<T> getAll(Query query, MutableInt count, Integer pagingOffset, Integer pagingLimit) {
        query.skip(pagingOffset);
        query.limit(pagingLimit);
        return this.find(query, count);
    }

    public List<T> find() {
        return this.find(null);
    }

    public List<T> find(final Query query) {
        return this.find(query, null);
    }

    public List<T> find(final Query query, final MutableInt count) {

        if (query == null) {
            return this.mongoDao.find();
        }

        final MongoQuery mongoQuery = new MongoQuery(query);

        return this.mongoDao.find(mongoQuery.getQuery(), null, mongoQuery.getSortInfo(), mongoQuery.getQueryPage(), count,
            this.isCrucialDataIntegration(query));
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

    public T getAndUpdate(final Query query, boolean remove, final Update updateQuery, boolean returnNew) {
        MongoQuery mhq = new MongoQuery(query);
        MongoUpdate mhqUpdate = new MongoUpdate(updateQuery);

        return this.mongoDao.findAndModify(mhq.getQuery(), null, mhq.getSortInfo(), remove, mhqUpdate.getUpdate(),
            returnNew, false);
    }

    public <X extends Object> boolean remove(final X id) {
        return this.mongoDao.delete(this.collectionName, id);
    }

    public boolean remove(Query query) {
        final MongoQuery mongoQuery = new MongoQuery(query);
        return this.mongoDao.delete(this.collectionName, mongoQuery.getQuery());
    }

    public void removeAll() {
        this.mongoDao.dropCollection(this.collectionName);
    }

    /*
     * BETA! as Tusam said "this can fail", and we know how Tusam finish. We are working to find the best solution to
     * this framework, but you can test this
     */
    public List<T> aggregate(AggregateQuery query) {
        MongoAggregationQuery mongoHandlerAggregationQuery = new MongoAggregationQuery(query);
        return this.mongoDao.aggregate(mongoHandlerAggregationQuery.getQuery());
    }

    /*
     * BETA! as Tusam said "this can fail", and we know how Tusam finish. We are working to find the best solution to
     * this framework, but you can test this
     */
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

    public Boolean exists(Query query) {
        MongoQuery q = new MongoQuery(query);
        return this.mongoDao.exists(q.getQuery());
    }

    private ReadPreference isCrucialDataIntegration(Query query) {
        if (query.isCrucialDataIntegration()) {
            return ReadPreference.primary();
        }

        return null;
    }

}
