package com.despegar.integration.mongo.connector;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.mutable.MutableLong;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.despegar.integration.mongo.connector.Bulk.BulkOperation;
import com.despegar.integration.mongo.connector.MongoCollection.ForEachMethod;
import com.despegar.integration.mongo.entities.BulkResult;
import com.despegar.integration.mongo.entities.GenericIdentifiableEntity;
import com.despegar.integration.mongo.id.IdGenerator;
import com.despegar.integration.mongo.support.DateJsonDeserializer;
import com.despegar.integration.mongo.support.DateJsonSerializer;
import com.despegar.integration.mongo.support.IdWithUnderscoreStrategy;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.BasicDBObject;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

@SuppressWarnings("rawtypes")
class MongoDao<T extends GenericIdentifiableEntity> {

    private com.mongodb.client.MongoCollection<Document> coll;
    private IdGenerator idGenerator;
    private Class<T> clazz;
    private ObjectMapper mapper;

    MongoDao(MongoDatabase mongoDb, String collection, Class<T> clazz, ObjectMapper mapper, IdGenerator idGenerator)
        throws UnknownHostException {
        this.idGenerator = idGenerator;
        this.clazz = clazz;
        this.mapper = mapper;

        this.mapper.setPropertyNamingStrategy(new IdWithUnderscoreStrategy());
        this.mapper.setSerializationInclusion(Include.NON_NULL);
        this.mapper.registerModule(getDateModule());

        this.coll = mongoDb.getCollection(collection);
    }

    public T findOne(Bson query, Bson sortInfo, Integer limit, Integer skip) {
        List<T> list = this.find(query, null, sortInfo, limit, skip, null);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public <X extends Object> T findOne(X id) {
        BasicDBObject o = new BasicDBObject();
        o.append("_id", id);
        return this.serialize(this.coll.find(Filters.eq("_id", id)).first());
    }

    public void forEach(Bson query, Bson fields, Bson sortInfo, Integer limit, Integer skip, ForEachMethod<T> forEach) {
        FindIterable<Document> cursor = this.getFindCursor(query, fields, sortInfo, limit, skip, null);
        MongoCursor<Document> iterator = cursor.iterator();
        try {
            while (iterator.hasNext()) {
                T next = this.serialize(iterator.next());
                forEach.iterate(next);
            }
        } finally {
            iterator.close();
        }
    }

    public List<T> find(Bson query, Bson fields, Bson sortInfo, Integer limit, Integer skip, MutableLong count) {
        FindIterable<Document> cursor = this.getFindCursor(query, fields, sortInfo, limit, skip, count);
        List<T> ret = new ArrayList<T>();

        MongoCursor<Document> iterator = cursor.iterator();
        try {
            while (iterator.hasNext()) {
                T next = this.serialize(iterator.next());
                ret.add(next);
            }
        } finally {
            iterator.close();
        }

        return ret;
    }

    public <X> List<X> distinct(String key, Bson query, Class<X> clazz) {
        DistinctIterable<X> iterable = this.coll.distinct(key, clazz);
        if (query != null) {
            iterable.filter(query);
        }
        this.coll.find(Filters.and(Filters.eq("name", "algo"), Filters.exists("pepe"))).skip(10).limit(10);

        List<X> ret = new ArrayList<X>();
        MongoCursor<X> iterator = iterable.iterator();
        try {
            while (iterator.hasNext()) {
                X next = iterator.next();
                ret.add(next);
            }
        } finally {
            iterator.close();
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public <X extends Object> X insert(T value) {
        value.setId(this.idGenerator.generateId(this.getCollectionName()));
        this.coll.insertOne(this.deserialize(value));
        return (X) value.getId();
    }

    public Boolean insertOrUpdate(Bson query, T value) {
        Long count = this.update(query, value, Boolean.FALSE);
        if (count == 0) {
            this.insert(value);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public Long update(Bson query, Bson value, Boolean multi) {
        UpdateOptions up = new UpdateOptions();
        up.upsert(Boolean.FALSE);

        UpdateResult ur = null;
        if (multi) {
            ur = this.coll.updateMany(query, value, up);
        } else {
            ur = this.coll.updateOne(query, value, up);
        }

        return ur.getModifiedCount();
    }

    public Long update(Bson query, T value, Boolean multi) {
        UpdateOptions up = new UpdateOptions();
        up.upsert(Boolean.FALSE);

        UpdateResult ur = null;
        if (multi) {
            ur = this.coll.updateMany(query, this.deserialize(value), up);
        } else {
            ur = this.coll.updateOne(query, this.deserialize(value), up);
        }

        return ur.getModifiedCount();
    }

    public <X extends Object> Boolean updateById(X id, Bson value) {
        UpdateResult ur = this.coll.updateOne(Filters.eq("_id", id), value);
        return (ur.getModifiedCount() == 1L);
    }

    public <X extends Object> Boolean updateById(X id, T entity) {
        UpdateResult ur = this.coll.updateOne(Filters.eq("_id", id), this.deserialize(entity));
        return (ur.getModifiedCount() == 1L);
    }

    public Long collectionSize() {
        return this.coll.count();
    }

    public Long collectionSize(Bson query) {
        return this.coll.count(query);
    }

    public Long delete(Bson query, Boolean multi) {
        DeleteResult dr = null;
        if (multi) {
            dr = this.coll.deleteMany(query);
        } else {
            dr = this.coll.deleteOne(query);
        }
        return dr.getDeletedCount();
    }

    public <X extends Object> Boolean deleteById(X id) {
        DeleteResult dr = this.coll.deleteOne(Filters.eq("_id", id));
        return (dr.getDeletedCount() == 1);
    }

    public Boolean exists(Bson query) {
        FindIterable<Document> iterable = this.coll.find(query).limit(1);
        return (iterable.first() != null);
    }

    public <X extends Object> List<X> aggregate(List<Bson> pipeline, Class<X> resultClazz) {
        MongoCursor<Document> cursor = this.coll.aggregate(pipeline).useCursor(Boolean.TRUE).iterator();
        List<X> ret = new ArrayList<X>();
        while (cursor.hasNext()) {
            ret.add(this.serialize(cursor.next(), resultClazz));
        }

        return ret;
    }

    public List<T> aggregate(List<Bson> pipeline) {
        MongoCursor<Document> cursor = this.coll.aggregate(pipeline).useCursor(Boolean.TRUE).iterator();
        List<T> ret = new ArrayList<T>();
        while (cursor.hasNext()) {
            ret.add(this.serialize(cursor.next()));
        }

        return ret;
    }

    public BulkResult bulk(List<BulkOperation> operations, Boolean isOrderRequired) {
        BulkWriteOptions bo = new BulkWriteOptions();
        bo.ordered(isOrderRequired);
        List<WriteModel<Document>> writes = new ArrayList<WriteModel<Document>>();
        for (BulkOperation op : operations) {
            writes.add(op.getWriteModel(this.mapper));
        }
        BulkWriteResult br = this.coll.bulkWrite(writes, bo);

        BulkResult response = new BulkResult(br.getModifiedCount(), br.getDeletedCount(), br.getInsertedCount());
        return response;
    }

    private FindIterable<Document> getFindCursor(Bson query, Bson fields, Bson sortInfo, Integer limit, Integer skip,
        MutableLong count) {
        FindIterable<Document> cursor = this.coll.find();

        if (query != null) {
            cursor.filter(query);
        }

        if (fields != null) {
            cursor.projection(fields);
        }

        // TODO y el size?
        // if (count != null) {
        // count.setValue(cursor.);
        // }

        if (sortInfo != null) {
            cursor = cursor.sort(sortInfo);
        }

        if (skip != null) {
            cursor = cursor.skip(skip);
        }

        if (limit != null) {
            cursor = cursor.limit(limit);
        }

        return cursor;
    }

    private T serialize(Document o) {
        return this.serialize(o, this.clazz);
    }

    private Document deserialize(T o) {
        return this.mapper.convertValue(o, Document.class);
    }

    @SuppressWarnings("unchecked")
    private <X extends Object> X serialize(Document o, Class<X> resultClazz) {
        JavaType constructType = this.mapper.constructType(resultClazz);
        Object convertValue = this.mapper.convertValue(o, constructType);
        return (X) convertValue;
    }

    private static SimpleModule getDateModule() {
        SimpleModule module = new SimpleModule("DateModule", new Version(0, 0, 1, null, null, null));
        module.addSerializer(Date.class, new DateJsonSerializer());
        module.addDeserializer(Date.class, new DateJsonDeserializer());

        return module;
    }

    private String getCollectionName() {
        return this.coll.getNamespace().getCollectionName();
    }

}
