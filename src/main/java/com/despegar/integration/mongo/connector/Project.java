package com.despegar.integration.mongo.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.despegar.integration.mongo.connector.Expression.Arithmetical;
import com.despegar.integration.mongo.connector.Expression.Array;
import com.despegar.integration.mongo.connector.Expression.Date;

public class Project
    implements Bson {

    private Map<String, Object> operators = new HashMap<String, Object>();
    private List<String> showProperties = new ArrayList<String>();
    private Boolean showId = Boolean.TRUE;

    public Project put(String name, Arithmetical expression) {
        this.addExpression(name, expression);
        return this;
    }

    public Project put(String name, Array expression) {
        this.addExpression(name, expression);
        return this;
    }

    public Project put(String name, Date expression) {
        this.addExpression(name, expression);
        return this;
    }

    public Project put(String name, String property) {
        if (name == null || property == null) {
            return this;
        }
        this.operators.put(name, property);

        return this;
    }

    public Project hideId() {
        this.showId = Boolean.FALSE;

        return this;
    }

    public Project show(String property) {
        if (property == null) {
            return this;
        }
        this.showProperties.add(property);

        return this;
    }

    private void addExpression(String name, Expression expression) {
        if (name == null || expression == null) {
            return;
        }
        this.operators.put(name, expression);
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

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
        // TODO Auto-generated method stub
        return null;
    }
}
