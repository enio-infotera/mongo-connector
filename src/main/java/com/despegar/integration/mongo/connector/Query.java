package com.despegar.integration.mongo.connector;

import org.bson.conversions.Bson;

import com.despegar.integration.mongo.connector.Sort.SortDirection;

public class Query {

    private Match match = new Match();
    private Sort sort = new Sort();

    private Integer limit;
    private Integer skip;

    public Query equal(String property, Object value) {
        this.match.equal(property, value);
        return this;
    }

    public Query notEqual(String property, Object value) {
        this.match.notEqual(property, value);
        return this;
    }

    public Query in(String property, Iterable<Object> values) {
        this.match.in(property, values);
        return this;
    }

    public Query notIn(String property, Iterable<Object> values) {
        this.match.notIn(property, values);
        return this;
    }

    public Query all(String property, Iterable<Object> values) {
        this.match.all(property, values);
        return this;
    }

    public Query in(String property, Object... values) {
        this.match.in(property, values);
        return this;
    }

    public Query notIn(String property, Object... values) {
        this.match.notIn(property, values);
        return this;
    }

    public Query all(String property, Object... values) {
        this.match.all(property, values);
        return this;
    }

    public Query greatThan(String property, Object value) {
        this.match.greatThan(property, value);
        return this;
    }

    public Query greatThanEqual(String property, Object value) {
        this.match.greatThanEqual(property, value);
        return this;
    }

    public Query lowerThan(String property, Object value) {
        this.match.lowerThan(property, value);
        return this;
    }

    public Query lowerThanEqual(String property, Object value) {
        this.match.lowerThanEqual(property, value);
        return this;
    }

    public Query exists(String property) {
        this.match.exists(property);
        return this;
    }

    public Query addSort(String property, SortDirection direction) {
        this.sort.addSort(property, direction);
        return this;
    }

    public Query addSort(String property) {
        this.sort.addSort(property);
        return this;
    }

    // TODO mod, not, notExists, near, nearSphere, intersect, within, or, and
    // TODO new! sin soporte en mongo-connector regex, size, text
    // siguen sin soporte where y elemMatch


    public Query limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Query skip(Integer skip) {
        this.skip = skip;
        return this;
    }

    Bson match() {
        return this.match;
    }

    Bson sort() {
        return this.sort;
    }

    Integer skip() {
        return this.skip;
    }

    Integer limit() {
        return this.limit;
    }

}
