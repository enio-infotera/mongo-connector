package com.despegar.integration.mongo.connector;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Sorts;

public class Sort
    implements Bson {

    private List<Bson> sorts = new ArrayList<Bson>();

    public static enum SortDirection {
        ASC, DESC
    }

    public Sort addSort(String property, SortDirection direction) {
        if (property == null || direction == null) {
            return this;
        }
        this.sorts.add(direction.equals(SortDirection.ASC) ? Sorts.ascending(property) : Sorts.descending(property));
        return this;
    }

    public Sort addSort(String property) {
        return this.addSort(property, SortDirection.ASC);
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
        if (this.sorts.isEmpty()) {
            return null;
        }
        return Sorts.orderBy(this.sorts).toBsonDocument(documentClass, codecRegistry);
    }
}
