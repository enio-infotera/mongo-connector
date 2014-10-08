package com.despegar.integration.mongo.query;

import java.util.HashMap;
import java.util.Map;

public class GroupQuery {

    private Map<String, Expression> operators = new HashMap<String, Expression>();
    private Object id;

    public GroupQuery id(Expression expression) {
        this.id = expression;

        return this;
    }

    public GroupQuery id(String property) {
        this.id = property;

        return this;
    }

    @SuppressWarnings("unchecked")
    public GroupQuery addToId(String name, Expression expression) {
        if (this.id == null || !(this.id instanceof Map)) {
            this.id = new HashMap<String, Object>();
        }

        Map<String, Object> map = (HashMap<String, Object>) this.id;
        map.put(name, expression);

        return this;
    }

    @SuppressWarnings("unchecked")
    public GroupQuery addToId(String name, String property) {
        if (this.id == null || !(this.id instanceof Map)) {
            this.id = new HashMap<String, Object>();
        }

        Map<String, Object> map = (HashMap<String, Object>) this.id;
        map.put(property, property);

        return this;
    }

    public GroupQuery put(String key, Expression expression) {
        this.operators.put(key, expression);

        return this;
    }

    public Map<String, Expression> getProperties() {
        return this.operators;
    }

    public Object getId() {
        return this.id;
    }

}
