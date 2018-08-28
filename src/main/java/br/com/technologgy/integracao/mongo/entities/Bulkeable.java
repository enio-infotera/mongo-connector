package br.com.technologgy.integracao.mongo.entities;

import br.com.technologgy.integracao.mongo.query.BulkOperation;

public interface Bulkeable {

    BulkOperation getOperation();

}
