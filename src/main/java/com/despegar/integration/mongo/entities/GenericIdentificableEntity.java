package com.despegar.integration.mongo.entities;

public interface GenericIdentificableEntity<Type> {

    Type getId();

    void setId(Type id);

}
