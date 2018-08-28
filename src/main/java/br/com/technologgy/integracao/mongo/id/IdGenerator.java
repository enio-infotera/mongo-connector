package br.com.technologgy.integracao.mongo.id;

public interface IdGenerator<T extends Object> {

    T generateId(String collectionName);

    Boolean validateId(Object id);

    void updateId(String collectionName, Object id);

}
