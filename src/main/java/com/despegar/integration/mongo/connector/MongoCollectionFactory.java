package com.despegar.integration.mongo.connector;

import java.net.UnknownHostException;

import com.despegar.integration.mongo.entities.GenericIdentifiableEntity;
import com.despegar.integration.mongo.id.IdGenerator;
import com.despegar.integration.mongo.id.StringIdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoCollectionFactory {

    public MongoCollectionFactory(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;
        this.mapper = new ObjectMapper();
        this.idGenerator = new StringIdGenerator();
    }

    private IdGenerator<?> idGenerator;
    private MongoDBConnection mongoDBConnection;
    private ObjectMapper mapper;

    public <T extends GenericIdentifiableEntity<?>> MongoCollection<T> buildMongoCollection(String collection, Class<T> clazz)
        throws UnknownHostException {
        MongoDao<T> mongoDao = new MongoDao<T>(this.mongoDBConnection.getDB(), collection, clazz, this.mapper,
            this.idGenerator);
        return new MongoCollection<T>(mongoDao);
    }

    public static <T extends GenericIdentifiableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz, MongoDBConnection mongoDBConnection, IdGenerator<?> idGenerator, ObjectMapper mapper)
        throws UnknownHostException {

        MongoDao<T> mongoDao = new MongoDao<T>(mongoDBConnection.getDB(), collection, clazz, mapper, idGenerator);
        return new MongoCollection<T>(mongoDao);
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setIdGenerator(IdGenerator<?> idGenerator) {
        this.idGenerator = idGenerator;
    }

}
