package com.despegar.integration.mongo.id;


public interface IdGenerator<T extends Object> {

    T generateId(String collectionName);

}
