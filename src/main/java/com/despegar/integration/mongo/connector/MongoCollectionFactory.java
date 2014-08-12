package com.despegar.integration.mongo.connector;

import java.net.UnknownHostException;

import com.despegar.integration.mongo.entities.GenericIdentificableEntity;
import com.despegar.integration.mongo.id.IdGenerator;
import com.despegar.integration.mongo.id.StringIdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoCollectionFactory {

    public MongoCollectionFactory(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;
    }

    private IdGenerator<?> idGenerator = new StringIdGenerator();
    private ObjectMapper mapper = new ObjectMapper();
    private MongoDBConnection mongoDBConnection;

    public <T extends GenericIdentificableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz) throws UnknownHostException {
        return MongoCollectionFactory.buildMongoCollection(collection, clazz, this.mongoDBConnection, this.idGenerator,
            this.mapper);
    }

    public static <T extends GenericIdentificableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
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
