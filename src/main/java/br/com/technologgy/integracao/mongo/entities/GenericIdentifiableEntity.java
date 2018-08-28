package br.com.technologgy.integracao.mongo.entities;

public interface GenericIdentifiableEntity<Type> {

    Type getId();

    void setId(Type id);

}
