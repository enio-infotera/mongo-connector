package com.despegar.integration.mongo.connector;

import java.net.UnknownHostException;

import com.despegar.integration.mongo.entities.GenericIdentificableEntity;
import com.despegar.integration.mongo.id.IdGenerator;
import com.despegar.integration.mongo.id.StringIdGenerator;
import com.despegar.integration.mongo.support.IdWithUnderscoreStrategy;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoCollectionFactory {

    public MongoCollectionFactory(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;

        this.mapper.setPropertyNamingStrategy(new IdWithUnderscoreStrategy());
        this.mapper.setSerializationInclusion(Include.NON_NULL);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private IdGenerator<?> idGenerator = new StringIdGenerator();
    private ObjectMapper mapper = new ObjectMapper();
    private MongoDBConnection mongoDBConnection;

    public <T extends GenericIdentificableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz) throws UnknownHostException {
        MongoDao<T> mongoDao = new MongoDao<T>(this.mongoDBConnection.getDB(), collection, this.mapper, clazz,
            this.idGenerator);
        return new MongoCollection<T>(collection, clazz, mongoDao);
    }

    public static <T extends GenericIdentificableEntity<?>> MongoCollection<T> buildMongoCollection(String collection,
        Class<T> clazz, MongoDBConnection mongoDBConnection, IdGenerator<?> idGenerator, ObjectMapper mapper)
        throws UnknownHostException {

        mapper.setPropertyNamingStrategy(new IdWithUnderscoreStrategy());
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
