package com.despegar.integration.mongo.query.aggregation;

import java.util.HashMap;
import java.util.Map;

public class GroupQuery {

    public static enum GroupOperation {
        SUM, AVG, FIRST, LAST, MAX, MIN, PUSH, ADD_TO_SET;
    }

    private Map<String, OperationWithFunction> operators = new HashMap<String, OperationWithFunction>();
    private Map<String, String> id = new HashMap<String, String>();

    public GroupQuery addId(String key, String value) {
        this.id.put(key, value);

        return this;
    }

    public GroupQuery put(String key, GroupOperation operator, Object value) {
        this.operators.put(key, new OperationWithFunction(operator, value));

        return this;
    }

    public static class OperationWithFunction {
        private GroupOperation operation;
        private Object value;

        public OperationWithFunction(GroupOperation operation, Object value) {
            super();
            this.operation = operation;
            this.value = value;
        }

        public GroupOperation getGroupOperation() {
            return this.operation;
        }

        public Object getValue() {
            return this.value;
        }

    }

    public Map<String, OperationWithFunction> getOperators() {
        return this.operators;
    }

    public Map<String, String> getId() {
        return this.id;
    }

}
