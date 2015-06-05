package com.despegar.integration.mongo.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

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
        this.filters.add(Filters.not(Filters.eq(property, value)));
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

    public Match notExists(String property) {
        this.filters.add(Filters.exists(property, Boolean.FALSE));
        return this;
    }

    public Match size(String property, Integer size) {
        this.filters.add(Filters.size(property, size));
        return this;
    }

    public Match regex(String property, Pattern pattern) {
        this.filters.add(Filters.regex(property, pattern));
        return this;
    }

    public Match not(Match notMatch) {
        this.filters.add(Filters.not(notMatch));
        return this;
    }

    public Match or(Match... orMatchs) {
        this.filters.add(Filters.or(orMatchs));
        return this;
    }

    public Match and(Match... andMatchs) {
        this.filters.add(Filters.and(andMatchs));
        return this;
    }

    public Match text(String textToSearch) {
        this.filters.add(Filters.text(textToSearch));
        return this;
    }

    public Match text(String textToSearch, String language) {
        this.filters.add(Filters.text(textToSearch, language));
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

    // TODO mod, nearSphere, intersect, within
    // siguen sin soporte where y elemMatch

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
        if (this.filters.isEmpty()) {
            return null;
        }

        return Filters.and(this.filters.toArray(new Bson[] {})).toBsonDocument(documentClass, codecRegistry);
    }
}
