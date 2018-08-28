package br.com.technologgy.integracao.mongo.connector;

import com.mongodb.DB;

public interface DBConnection {

    DB getDB();
}
