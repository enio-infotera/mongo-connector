package com.despegar.integration.mongo.connector;

import java.net.UnknownHostException;

import com.despegar.integration.domain.api.GenericIdentificableEntity;
import com.despegar.integration.mongo.id.IdGenerator;
import com.despegar.integration.mongo.id.StringIdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoCollectionFactory {

    private MongoCollectionFactory() {
    }

    public static <T extends GenericIdentificableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz, MongoDBConnection mongoDBConnection) throws UnknownHostException {
        return buildMongoCollection(collection, clazz, mongoDBConnection, new StringIdGenerator(), new ObjectMapper());
    }

    public static <T extends GenericIdentificableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz, MongoDBConnection mongoDBConnection, IdGenerator<?> idGenerator) throws UnknownHostException {
        return buildMongoCollection(collection, clazz, mongoDBConnection, idGenerator, new ObjectMapper());
    }

    public static <T extends GenericIdentificableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz, MongoDBConnection mongoDBConnection, ObjectMapper mapper) throws UnknownHostException {
        return buildMongoCollection(collection, clazz, mongoDBConnection, new StringIdGenerator(), mapper);
    }

    public static <T extends GenericIdentificableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz, MongoDBConnection mongoDBConnection, IdGenerator<?> idGenerator, ObjectMapper mapper)
        throws UnknownHostException {
        MongoDao<T> mongoDao = new MongoDao<T>(mongoDBConnection.getDB(), collection, mapper, clazz, idGenerator);
        return new MongoCollection<T>(collection, clazz, mongoDao);
    }

}
