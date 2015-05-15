package com.despegar.integration.mongo.entities;

public interface GenericIdentifiableEntity<Type>{

    Type getId();

    void setId(Type id);

}
