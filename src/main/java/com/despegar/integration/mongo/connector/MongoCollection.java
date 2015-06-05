package com.despegar.integration.mongo.connector;

import java.util.List;

import org.apache.commons.lang.mutable.MutableLong;

import com.despegar.integration.mongo.entities.BulkResult;
import com.despegar.integration.mongo.entities.GenericIdentifiableEntity;

public class MongoCollection<T extends GenericIdentifiableEntity<?>> {

    protected MongoDao<T> mongoDao;

    MongoCollection(MongoDao<T> mongoDao) {
        this.mongoDao = mongoDao;
    }

    static interface ForEachMethod<T> {

        void iterate(T entity);

    }

    public T findOne() {
        Query query = new Query();
        query.limit(1);

        return this.findOne(query);
    }

    public T findOne(final Query query) {
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

    public List<T> find(final Query query, MutableLong count) {

        if (query == null) {
            return this.mongoDao.find(null, null, null, null, null, count);
        }

        // TODO el cursor del find no esta devolviendo el metodo .count(), hasta que lo agreguen
        // esta es la unica forma de obtener la cantidad de elementos
        if (count != null) {
            count.setValue(this.mongoDao.collectionSize(query.match()));
        }

        return this.mongoDao.find(query.match(), null, query.sort(), null, null, count);
    }

    public void forEach(final Query query, final ForEachMethod<T> forEach) {
        this.mongoDao.forEach(query.match(), null, query.sort(), query.limit(), query.skip(), forEach);
    }

    public Long count(final Match query) {
        if (query == null) {
            return this.mongoDao.collectionSize();
        }

        return this.mongoDao.collectionSize(query);
    }

    public <X> X add(T t) {
        t.setId(null);
        return this.mongoDao.insert(t);
    }

    // FALSE = insert | TRUE = update
    public Boolean updateOrInsert(final Match query, final T entity) {
        return this.mongoDao.insertOrUpdate(query, entity);
    }

    public Boolean update(final Match query, final Update updateQuery) {
        Long count = this.update(query, updateQuery, Boolean.FALSE);
        return (count == 1);
    }

    public Boolean update(final Match query, final T entity) {
        Long count = this.update(query, entity, Boolean.FALSE);
        return (count == 1);
    }

    public Long update(final Match query, final Update updateQuery, final Boolean multi) {
        return this.mongoDao.update(query, updateQuery, multi);
    }

    public Long update(final Match query, final T entity, final Boolean multi) {
        return this.mongoDao.update(query, entity, multi);
    }

    public <X> Boolean update(final X id, final Update updateQuery) {
        return this.mongoDao.updateById(id, updateQuery);
    }

    public <X> Boolean update(final X id, final T entity) {
        return this.mongoDao.updateById(id, entity);
    }

    public <X> Boolean remove(final X id) {
        return this.mongoDao.deleteById(id);
    }

    public Boolean remove(final Match query) {
        Long count = this.remove(query, Boolean.FALSE);
        return (count == 1);
    }

    public Long remove(final Match query, final Boolean multi) {
        return this.mongoDao.delete(query, multi);
    }

    public Long removeAll() {
        return this.remove(null, Boolean.TRUE);
    }

    public List<T> aggregate(final Aggregate query) {
        return this.mongoDao.aggregate(query.piplines());
    }

    public <Y> List<Y> aggregate(final Aggregate query, final Class<Y> returnClazz) {
        return this.mongoDao.aggregate(query.piplines(), returnClazz);
    }

    public <Y> List<Y> distinct(final String property, final Class<Y> returnClazz) {
        return this.distinct(property, null, returnClazz);
    }

    public <Y> List<Y> distinct(final String property, final Match query, final Class<Y> returnClazz) {
        return this.mongoDao.distinct(property, query, returnClazz);
    }

    public Boolean exists(final Query query) {
        return this.mongoDao.exists(query.match());
    }

    public BulkResult bulk(final Bulk<T> bulk) {
        return this.mongoDao.bulk(bulk.operations(), bulk.isOrderRequired());
    }

}
