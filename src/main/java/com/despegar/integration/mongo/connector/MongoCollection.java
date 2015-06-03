package com.despegar.integration.mongo.connector;

import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;

import com.despegar.integration.mongo.entities.BulkResult;
import com.despegar.integration.mongo.entities.GenericIdentifiableEntity;

public class MongoCollection<T extends GenericIdentifiableEntity<?>> {

    protected MongoDao<T> mongoDao;

    MongoCollection(MongoDao<T> mongoDao) {
        this.mongoDao = mongoDao;
    }

    public T findOne() {
        Query query = new Query();
        query.limit(1);

        return this.findOne(query);
    }

    public T findOne(Query query) {
        return this.mongoDao.findOne(query.match(), query.sort(), query.limit(), query.skip());
    }

    public <X extends Object> T findOne(final X id) {
        return this.mongoDao.findOne(id);
    }

    public List<T> find() {
        return this.find(null);
    }

    public List<T> find(final Query query) {
        return this.find(query, null);
    }

    public List<T> find(final Query query, final MutableInt count) {

        if (query == null) {
            return this.mongoDao.find(null, null, null, null, null, null);
        }

        return this.mongoDao.find(query.match(), null, query.sort(), null, null, count);
    }

    public Long count(final Match query) {
        if (query == null) {
            return this.mongoDao.collectionSize();
        }

        // TODO dejar un query?
        return this.mongoDao.collectionSize(query);
    }

    public <X> X add(final T t) {
        t.setId(null);
        return this.mongoDao.insert(t);
    }

    // TODO no hay mas save, ni insertIfNotPresent

    public Long update(final Match query, final Update updateQuery) {
        return this.update(query, updateQuery, Boolean.FALSE);
    }

    public Long update(final Match query, final Update updateQuery, final Boolean multi) {
        return this.mongoDao.update(query, updateQuery, Boolean.FALSE, multi);
    }

    public <X> Boolean update(final X id, final Update updateQuery) {
        return this.mongoDao.updateById(id, updateQuery);
    }

    // TODO findAndModify no existe mas

    public <X> Boolean remove(final X id) {
        return this.mongoDao.delete(id);
    }

    public Boolean remove(Match query) {
        return this.mongoDao.delete(query);
    }

    // TODO no hay mas drop

    public List<T> aggregate(Aggregate query) {
        return this.mongoDao.aggregate(query.piplines());
    }

    public <Y> List<Y> aggregate(Aggregate query, Class<Y> returnClazz) {
        return this.mongoDao.aggregate(query.piplines(), returnClazz);
    }

    // TODO no hay mas aggregate options

    public <Y> List<Y> distinct(String property, Class<Y> returnClazz) {
        return this.distinct(property, null, returnClazz);
    }

    public <Y> List<Y> distinct(String property, Match query, Class<Y> returnClazz) {
        return this.mongoDao.distinct(property, query, returnClazz);
    }

    public Boolean exists(Query query) {
        return this.mongoDao.exists(query.match());
    }

    public BulkResult bulk(Bulk<T> bulk) {
        return this.mongoDao.bulk(bulk.operations(), bulk.isOrderRequired());
    }

}
