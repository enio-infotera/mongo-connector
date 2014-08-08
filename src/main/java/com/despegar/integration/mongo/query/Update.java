package com.despegar.integration.mongo.query;

import java.util.HashMap;
import java.util.Map;

public class Update {

    public static enum UpdateOperation {
        SET, UNSET, INC, RENAME, ADD_TO_SET, POP, PULL_ALL, PULL, PUSH
    }

    private Map<String, Object> properties = new HashMap<String, Object>();
    private UpdateOperation updateOperation = null;

    public UpdateOperation getUpdateOperation() {
        return this.updateOperation;
    }

    public Update put(String key, Object value) {

        if (key != null) {
            this.getProperties().put(key, value);
        }

        return this;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setUpdateOperation(UpdateOperation updateOperation) {
        this.updateOperation = updateOperation;
    }

}
