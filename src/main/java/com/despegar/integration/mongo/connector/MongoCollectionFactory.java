package com.despegar.integration.mongo.connector;

import java.net.UnknownHostException;

import com.despegar.integration.mongo.entities.GenericIdentifiableEntity;
import com.despegar.integration.mongo.id.IdGenerator;
import com.despegar.integration.mongo.id.StringIdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoCollectionFactory {

    public MongoCollectionFactory(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;

        this.idGenerator = new StringIdGenerator();
        this.mapper = new ObjectMapper();
    }

    private IdGenerator<?> idGenerator;
    private ObjectMapper mapper;
    private MongoDBConnection mongoDBConnection;

    public <T extends GenericIdentifiableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz) throws UnknownHostException {
        MongoDao<T> mongoDao = new MongoDao<T>(this.mongoDBConnection.getDB(), collection, this.mapper, clazz,
            this.idGenerator);
        return new MongoCollection<T>(collection, clazz, mongoDao);
    }

    public static <T extends GenericIdentifiableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz, MongoDBConnection mongoDBConnection, IdGenerator<?> idGenerator, ObjectMapper mapper)
        throws UnknownHostException {

        MongoDao<T> mongoDao = new MongoDao<T>(mongoDBConnection.getDB(), collection, mapper, clazz, idGenerator);
        return new MongoCollection<T>(collection, clazz, mongoDao);
    }

    public void setIdGenerator(IdGenerator<?> idGenerator) {
        this.idGenerator = idGenerator;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

}
