package com.despegar.integration.mongo.id;

import org.bson.types.ObjectId;

public class StringIdGenerator
    implements IdGenerator<String> {

    public String generateId(String collectionName) {
        return new ObjectId().toString();
    }

}
