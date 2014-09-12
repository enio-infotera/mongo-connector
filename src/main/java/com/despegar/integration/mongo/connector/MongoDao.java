package com.despegar.integration.mongo.connector;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;

import com.despegar.integration.mongo.entities.GenericIdentificableEntity;
import com.despegar.integration.mongo.id.IdGenerator;
import com.despegar.integration.mongo.query.QueryPage;
import com.despegar.integration.mongo.support.DateJsonDeserializer;
import com.despegar.integration.mongo.support.DateJsonSerializer;
import com.despegar.integration.mongo.support.IdWithUnderscoreStrategy;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.AggregationOptions;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

@SuppressWarnings("rawtypes")
class MongoDao<T extends GenericIdentificableEntity> {

    private DB mongoDb;
    private Class<T> clazz;
    private DBCollection coll;
    private IdGenerator idGenerator;
    private ObjectMapper mapper;

    MongoDao(DB mongoDb, String collection, ObjectMapper mapper, Class<T> clazz, IdGenerator idGenerator)
        throws UnknownHostException {
        this.mongoDb = mongoDb;
        this.clazz = clazz;
        this.idGenerator = idGenerator;
        this.mapper = new ObjectMapper();

        this.mapper.setPropertyNamingStrategy(new IdWithUnderscoreStrategy());
        this.mapper.setSerializationInclusion(Include.NON_NULL);
        this.mapper.registerModule(getDateModule());

        this.coll = this.mongoDb.getCollection(collection);
    }

    public T findOne() {
        return this.findOne(new BasicDBObject());
    }

    public T findOne(ReadPreference readPreference) {
        return this.findOne(new BasicDBObject(), readPreference);
    }

    public T findOne(DBObject query) {
        return this.findOne(query, new BasicDBObject(), new QueryPage(0, 1));
    }

    public T findOne(DBObject query, ReadPreference readPreference) {
        return this.findOne(query, new BasicDBObject(), new QueryPage(0, 1), readPreference);
    }

    public T findOne(DBObject query, DBObject sortInfo, QueryPage page) {
        List<T> list = this.find(query, new BasicDBObject(), sortInfo, page);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public T findOne(DBObject query, DBObject sortInfo, QueryPage page, ReadPreference readPreference) {
        List<T> list = this.find(query, new BasicDBObject(), sortInfo, page, readPreference);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public <X extends Object> T findOne(X id) {
        BasicDBObject o = new BasicDBObject();
        o.append("_id", id);
        DBObject dbObject = this.coll.findOne(o);
        return this.serialize(dbObject);
    }

    public List<T> find() {
        return this.find(new BasicDBObject());
    }

    public List<T> find(ReadPreference readPreference) {
        return this.find(new BasicDBObject(), readPreference);
    }

    public List<T> find(DBObject query) {
        return this.find(query, new BasicDBObject());
    }

    public List<T> find(DBObject query, ReadPreference readPreference) {
        return this.find(query, new BasicDBObject(), readPreference);
    }

    public List<T> find(DBObject query, DBObject fields) {
        return this.findUsingSortInfo(query, fields, null);
    }

    public List<T> find(DBObject query, DBObject fields, ReadPreference readPreference) {
        return this.findUsingSortInfo(query, fields, null, readPreference);
    }

    public List<T> findUsingSortInfo(DBObject query, DBObject fields, DBObject sortInfo) {
        return this.find(query, fields, sortInfo, null);
    }

    public List<T> findUsingSortInfo(DBObject query, DBObject fields, DBObject sortInfo, ReadPreference readPreference) {
        return this.find(query, fields, sortInfo, null, readPreference);
    }

    public List<T> findUsingPage(DBObject query, DBObject fields, QueryPage page) {
        return this.find(query, fields, null, page);
    }

    public List<T> findUsingPage(DBObject query, DBObject fields, QueryPage page, ReadPreference readPreference) {
        return this.find(query, fields, null, page, readPreference);
    }

    public List<T> find(DBObject query, DBObject fields, DBObject sortInfo, QueryPage page) {
        return this.find(query, fields, sortInfo, page, null, this.coll.getReadPreference());
    }

    public List<T> find(DBObject query, DBObject fields, DBObject sortInfo, QueryPage page, ReadPreference readPreference) {
        return this.find(query, fields, sortInfo, page, null, readPreference);
    }

    public List<T> find(DBObject query, DBObject fields, DBObject sortInfo, QueryPage page, MutableInt count) {
        return this.find(query, fields, sortInfo, page, count, this.coll.getReadPreference());
    }

    public List<T> find(DBObject query, DBObject fields, DBObject sortInfo, QueryPage page, MutableInt count,
        ReadPreference readPreference) {
        DBCursor cursor = this.coll.find(query, fields);

        if (count != null) {
            count.setValue(cursor.count());
        }

        if (sortInfo != null) {
            cursor = cursor.sort(sortInfo);
        }

        if (readPreference != null) {
            cursor.setReadPreference(readPreference);
        }

        if (page != null) {
            cursor = cursor.skip(page.getOffset());
            cursor = cursor.limit(page.getLimit());
        }

        List<T> ret = new ArrayList<T>();
        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            ret.add(this.serialize(next));
        }

        return ret;
    }

    public T findAndModify(DBObject query, DBObject fields, DBObject sort, boolean remove, DBObject update,
        boolean returnNew, boolean upsert) {
        return this.serialize(this.coll.findAndModify(query, fields, sort, remove, update, returnNew, upsert));
    }

    public List<?> distinct(String key) {
        return this.coll.distinct(key);
    }

    public List<?> distinct(String key, DBObject query) {
        return this.coll.distinct(key, query);
    }

    public <X extends Object> X insert(T value) {
        return this.insert(value, WriteConcern.NORMAL);
    }

    @SuppressWarnings("unchecked")
    public <X extends Object> X insert(T value, WriteConcern concern) {
        if (!this.idGenerator.validateId(value.getId())) {
            value.setId(this.idGenerator.generateId(this.coll.getName()));
        }

        this.coll.insert(this.deserialize(value), concern);

        return (X) value.getId();
    }

    public Integer update(DBObject query, DBObject value, boolean upsert, boolean multi, WriteConcern concern) {
        WriteResult update = this.coll.update(query, value, upsert, multi, concern);

        return update.getN();
    }

    public Integer update(BasicDBObject query, BasicDBObject value, boolean upsert) {
        return this.update(query, value, upsert, WriteConcern.SAFE);
    }

    public Integer update(DBObject query, DBObject value, boolean upsert, WriteConcern concern) {
        return this.update(query, value, upsert, false, concern);
    }

    public Integer update(DBObject query, DBObject value, WriteConcern concern) {
        return this.update(query, value, false, false, concern);
    }

    public Integer update(T query, T value, boolean upsert, boolean multi, WriteConcern concern) {
        WriteResult update = this.coll.update(this.deserialize(query), this.deserialize(value), upsert, multi, concern);

        return update.getN();
    }

    public Integer update(T query, T value, boolean upsert) {
        return this.update(query, value, upsert, WriteConcern.SAFE);
    }

    public Integer update(T query, T value, boolean upsert, WriteConcern concern) {
        return this.update(query, value, upsert, false, concern);
    }

    public Integer update(T query, T value, WriteConcern concern) {
        return this.update(query, value, false, false, concern);
    }

    private <X extends Object> X updateById(X id, T value) {
        DBObject o = new BasicDBObject();
        o.put("_id", id);
        this.coll.update(o, this.deserialize(value));

        return id;
    }

    public <X extends Object> X updateOrInsert(T value) {
        return this.updateOrInsert(value, WriteConcern.SAFE);
    }

    @SuppressWarnings("unchecked")
    public <X extends Object> X updateOrInsert(T value, WriteConcern concern) {
        X ret = null;
        if (!this.idGenerator.validateId(value.getId()) || this.findOne(value.getId()) == null) {
            if (value.getId() != null) {
                this.idGenerator.updateId(this.coll.getName(), value.getId());
            }
            ret = this.insert(value, concern);

        } else {
            ret = (X) this.updateById(value.getId(), value);
        }

        return ret;
    }

    public int getTotalObjectsInCollection(String collection) {
        return this.getTotalObjectsInCollection(collection, new BasicDBObject());
    }

    public int getTotalObjectsInCollection(String collection, DBObject key) {
        return this.coll.find(key).count();
    }

    public Set<String> getCollectionNames() {
        return this.mongoDb.getCollectionNames();
    }

    public void dropCollection(String collection) {
        DBCollection coll = this.mongoDb.getCollection(collection);
        coll.drop();
    }

    public void renameCollection(String collection, String newName) {
        this.renameCollection(collection, newName, false);
    }

    public void renameCollection(String collection, String newName, Boolean dropTarget) {
        DBCollection coll = this.mongoDb.getCollection(collection);
        coll.rename(newName, dropTarget);
    }

    public boolean delete(String collection, DBObject query) {
        DBCollection coll = this.mongoDb.getCollection(collection);
        return coll.remove(query).isUpdateOfExisting();
    }

    public <X extends Object> boolean delete(String collection, X id) {
        return this.delete(collection, new BasicDBObject("_id", id));
    }

    public void ensureIndex(String collection, DBObject index) {
        DBCollection coll = this.mongoDb.getCollection(collection);
        coll.createIndex(index);
    }

    public boolean exists(DBObject query) {
        DBCursor cursor = this.coll.find(query).limit(1);
        return cursor.hasNext();
    }

    public <X extends Object> List<X> aggregate(List<DBObject> pipeline, Class<X> resultClazz) {
        return this.aggregate(pipeline, this.coll.getReadPreference(), resultClazz);
    }

    public List<T> aggregate(List<DBObject> pipeline) {
        return this.aggregate(pipeline, this.coll.getReadPreference(), this.clazz);
    }

    public <X extends Object> List<X> aggregate(List<DBObject> pipeline, AggregationOptions options, Class<X> resultClazz) {
        return this.aggregate(pipeline, options, this.coll.getReadPreference(), resultClazz);
    }

    public <X extends Object> List<X> aggregate(List<DBObject> pipeline, ReadPreference readPreference, Class<X> resultClazz) {
        AggregationOutput aggregationOutput = this.coll.aggregate(pipeline, readPreference);
        Iterable<DBObject> results = aggregationOutput.results();
        Iterator<DBObject> iterator = results.iterator();
        List<X> ret = new ArrayList<X>();
        while (iterator.hasNext()) {
            ret.add(this.serialize(iterator.next(), resultClazz));
        }
        return ret;
    }

    public <X extends Object> List<X> aggregate(List<DBObject> pipeline, AggregationOptions options,
        ReadPreference readPreference, Class<X> resultClazz) {
        Cursor cursor = this.coll.aggregate(pipeline, options, readPreference);

        List<X> ret = new ArrayList<X>();
        while (cursor.hasNext()) {
            ret.add(this.serialize(cursor.next(), resultClazz));
        }

        return ret;
    }

    private T serialize(DBObject o) {
        return this.serialize(o, this.clazz);
    }

    private DBObject deserialize(T o) {
        return this.mapper.convertValue(o, BasicDBObject.class);
    }

    @SuppressWarnings("unchecked")
    private <X extends Object> X serialize(DBObject o, Class<X> resultClazz) {
        JavaType constructType = this.mapper.constructType(resultClazz);
        Object convertValue = this.mapper.convertValue(o, constructType);
        return (X) convertValue;
    }

    private static SimpleModule getDateModule() {
        // Register custom serializers
        SimpleModule module = new SimpleModule("DateModule", new Version(0, 0, 1, null, null, null));

        // Java Date
        module.addSerializer(Date.class, new DateJsonSerializer());
        module.addDeserializer(Date.class, new DateJsonDeserializer());

        return module;
    }
}
