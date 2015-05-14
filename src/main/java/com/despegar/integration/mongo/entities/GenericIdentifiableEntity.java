package com.despegar.integration.mongo.entities;

public interface GenericIdentifiableEntity<Type> extends Bulkeable {

    Type getId();

    void setId(Type id);

}
