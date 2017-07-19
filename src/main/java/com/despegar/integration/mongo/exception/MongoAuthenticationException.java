package com.despegar.integration.mongo.exception;

import com.mongodb.MongoCredential;

public class MongoAuthenticationException
    extends RuntimeException {
    private static final long serialVersionUID = 7586365409320107087L;

    private MongoCredential credential;

    public MongoAuthenticationException(MongoCredential credential) {
        this.credential = credential;
    }

    @Override
    public String getMessage() {
        if (this.credential != null) {
            return "Could not authenticate with " + this.credential.toString();
        }
        return "Could not authenticate without credentials";
    }


}
