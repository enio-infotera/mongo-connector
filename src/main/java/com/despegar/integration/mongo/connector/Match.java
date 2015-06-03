package com.despegar.integration.mongo.connector;

import java.util.ArrayList;
import java.util.Collection;

import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

public class Match
    implements Bson {

    private Collection<Bson> filters = new ArrayList<Bson>();

    public static class Point {
        private Double latitude;
        private Double longitude;

        public Point(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return this.latitude;
        }

        public Double getLongitude() {
            return this.longitude;
        }
    }

    public Match equal(String property, Object value) {
        this.filters.add(Filters.eq(property, value));
        return this;
    }

    public Match notEqual(String property, Object value) {
        this.filters.add(Filters.nor(Filters.eq(property, value)));
        return this;
    }

    public Match in(String property, Iterable<Object> values) {
        this.filters.add(Filters.in(property, values));
        return this;
    }

    public Match notIn(String property, Iterable<Object> values) {
        this.filters.add(Filters.nin(property, values));
        return this;
    }

    public Match all(String property, Iterable<Object> values) {
        this.filters.add(Filters.all(property, values));
        return this;
    }

    public Match in(String property, Object... values) {
        this.filters.add(Filters.in(property, values));
        return this;
    }

    public Match notIn(String property, Object... values) {
        this.filters.add(Filters.nin(property, values));
        return this;
    }

    public Match all(String property, Object... values) {
        this.filters.add(Filters.all(property, values));
        return this;
    }

    public Match greatThan(String property, Object value) {
        this.filters.add(Filters.gt(property, value));
        return this;
    }

    public Match greatThanEqual(String property, Object value) {
        this.filters.add(Filters.gte(property, value));
        return this;
    }

    public Match lowerThan(String property, Object value) {
        this.filters.add(Filters.lt(property, value));
        return this;
    }

    public Match lowerThanEqual(String property, Object value) {
        this.filters.add(Filters.lte(property, value));
        return this;
    }

    public Match exists(String property) {
        this.filters.add(Filters.exists(property));
        return this;
    }

    public Match near(String property, Point point, Double maxDistance) {
        this.near(property, point, maxDistance, null);
        return this;
    }

    public Match near(String property, Point point, Double maxDistance, Double minDistance) {
        this.filters.add(MyFilters.near(property, point, maxDistance, minDistance));
        return this;
    }


    // TODO mod, not, notExists, nearSphere, intersect, within, or, and
    // TODO new! sin soporte en mongo-connector regex, size, text
    // siguen sin soporte where y elemMatch

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
        if (this.filters.isEmpty()) {
            return null;
        }

        return Filters.and(this.filters.toArray(new Bson[] {})).toBsonDocument(documentClass, codecRegistry);
    }
}
