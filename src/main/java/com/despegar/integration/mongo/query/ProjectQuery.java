package com.despegar.integration.mongo.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectQuery {

    private Map<String, Object> operators = new HashMap<String, Object>();
    private List<String> showProperties = new ArrayList<String>();
    private Boolean showId = Boolean.TRUE;

    public ProjectQuery put(String name, Expression expression) {
        if (name == null || expression == null) {
            return this;
        }
        this.operators.put(name, expression);

        return this;
    }

    public ProjectQuery put(String name, String property) {
        if (name == null || property == null) {
            return this;
        }
        this.operators.put(name, property);

        return this;
    }

    public ProjectQuery show(String property) {
        if (property == null) {
            return this;
        }
        this.showProperties.add(property);

        return this;
    }

    public ProjectQuery hideId() {
        this.showId = Boolean.FALSE;

        return this;
    }

    public Map<String, Object> getOperators() {
        return this.operators;
    }

    public List<String> getShowProperties() {
        return this.showProperties;
    }

    public Boolean getShowId() {
        return this.showId;
    }
}
