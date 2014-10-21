package com.despegar.integration.mongo.id;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

public class StringIdGenerator
    implements IdGenerator<String> {

    public String generateId(String collectionName) {
        return new ObjectId().toString();
    }

    public Boolean validateId(Object id) {
        return id != null ? !StringUtils.isEmpty(id.toString()) : Boolean.FALSE;
    }

    public void updateId(String collectionName, Object id) {
        // nothing to do
    }

}
