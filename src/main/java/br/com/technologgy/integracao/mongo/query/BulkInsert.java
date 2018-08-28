package br.com.technologgy.integracao.mongo.query;

import br.com.technologgy.integracao.mongo.entities.Bulkeable;
import br.com.technologgy.integracao.mongo.entities.GenericIdentifiableEntity;
import br.com.technologgy.integracao.mongo.query.BulkOperation;

public class BulkInsert<T extends GenericIdentifiableEntity<?>> implements Bulkeable {

    private T entity;

    public BulkInsert(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public BulkOperation getOperation() {
        return BulkOperation.INSERT;
    }

}
