package com.despegar.integration.mongo.query;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;

public class SortQuery {

    public static enum SortDirection {
        ASC, DESC
    }

    private OrderedMap sortMap = new ListOrderedMap();

    @SuppressWarnings("unchecked")
    public SortQuery addSort(String property, SortDirection direction) {
        if (property == null || direction == null) {
            return this;
        }
        this.sortMap.put(property, direction);
        return this;
    }

    public SortQuery addSort(String property) {
        return this.addSort(property, SortDirection.ASC);
    }

    public OrderedMap getSortMap() {
        return this.sortMap;
    }

}
