package br.com.technologgy.integracao.mongo.connector;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.technologgy.integracao.mongo.exception.MongoAuthenticationException;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;

public class MongoDBConnection implements DBConnection {

    private String dbName;
    private String replicaSet;
    private MongoClientOptions mongoOptions;
    private MongoCredential credential;

    public MongoDBConnection(String dbName, String replicaSet) throws UnknownHostException {
        this.dbName = dbName;
        this.replicaSet = replicaSet;
        this.instanceDB();
    }

    public MongoDBConnection(String dbName, String replicaSet, MongoClientOptions mongoOptions) throws UnknownHostException {
        this.dbName = dbName;
        this.replicaSet = replicaSet;
        this.mongoOptions = mongoOptions;
        this.instanceDB();
    }

    public MongoDBConnection(String dbName, String replicaSet, String userName, String password,
            MongoClientOptions mongoOptions) throws UnknownHostException, MongoAuthenticationException {
        this.dbName = dbName;
        this.replicaSet = replicaSet;
        this.credential = MongoCredential.createCredential(userName, dbName, password.toCharArray());
        this.mongoOptions = mongoOptions;
        this.instanceDB();
    }

    public MongoDBConnection(String dbName, String replicaSet, String userName, String password)
            throws UnknownHostException, MongoAuthenticationException {
        this(dbName, replicaSet, userName, password, null);
    }

    private DB db;

    @SuppressWarnings("deprecation")
    private void instanceDB() throws UnknownHostException {
        if (this.mongoOptions == null) {
            this.mongoOptions = MongoClientOptions.builder().build();
        }

        String[] addresses = this.replicaSet.split(",");
        List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
        ServerAddress serverAddress;
        for (String address : addresses) {
            String[] split = address.split(":");
            serverAddress = new ServerAddress(split[0], new Integer(split[1]));
            serverAddresses.add(serverAddress);
        }

        MongoClient mongo;
        if (this.credential != null) {
            mongo = new MongoClient(serverAddresses, Arrays.asList(this.credential), this.mongoOptions);
            try {
                mongo.getDB(this.dbName).command("ping");
            } catch (MongoTimeoutException e) {
                throw new MongoAuthenticationException(this.credential);
            }
        } else {
            mongo = new MongoClient(serverAddresses, this.mongoOptions);
        }
        this.db = mongo.getDB(this.dbName);

    }

    public DB getDB() {
        return this.db;
    }
}
