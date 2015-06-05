package com.despegar.integration.mongo.connector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class Group
    implements Bson {

    private Map<String, Object> operators = new HashMap<String, Object>();
    private Object id;

    public Group id(Expression expression) {
        this.id = expression;

        return this;
    }

    public Group id(String property) {
        this.id = "$" + property;

        return this;
    }

    @SuppressWarnings("unchecked")
    public Group addToId(String name, Expression expression) {
        if (this.id == null || !(this.id instanceof Map)) {
            this.id = new HashMap<String, Object>();
        }

        Map<String, Object> map = (HashMap<String, Object>) this.id;
        map.put(name, expression);

        return this;
    }

    @SuppressWarnings("unchecked")
    public Group addToId(String name, String property) {
        if (this.id == null || !(this.id instanceof Map)) {
            this.id = new HashMap<String, Object>();
        }

        Map<String, Object> map = (HashMap<String, Object>) this.id;
        map.put(name, "$" + property);

        return this;
    }

    public Group put(String key, Expression expression) {
        this.operators.put(key, expression);
        return this;
    }

    public Group put(String key, String property) {
        this.operators.put(key, "$" + property);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
        Document doc = new Document();
        if (this.id instanceof Map) {
            Document id = new Document();
            Map<String, Object> idMap = (Map<String, Object>) this.id;
            for (Entry<String, Object> entry : idMap.entrySet()) {
                if (entry.getValue() instanceof Expression) {
                    id.append(entry.getKey(), ((Expression) entry.getValue()).toBsonDocument(documentClass, codecRegistry));
                } else {
                    id.append(entry.getKey(), entry.getValue());
                }
            }
            doc.append("_id", id);
        } else if (this.id instanceof Expression) {
            doc.append("_id", ((Expression) this.id).toBsonDocument(documentClass, codecRegistry));
        } else {
            doc.append("_id", this.id);
        }

        if (this.operators.isEmpty()) {
            return doc.toBsonDocument(documentClass, codecRegistry);
        }

        for (Entry<String, Object> entry : this.operators.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Expression) {
                doc.append(key, ((Expression) value).toBsonDocument(documentClass, codecRegistry));
            } else {
                doc.append(key, value);
            }
        }

        return doc.toBsonDocument(documentClass, codecRegistry);
    }
}
