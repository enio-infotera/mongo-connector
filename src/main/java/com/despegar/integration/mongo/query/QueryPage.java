package com.despegar.integration.mongo.query;

import java.io.Serializable;

public class QueryPage
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private int offset;
    private int limit;

    public QueryPage(Integer offset, Integer limit) {
        if (offset == null) {
            offset = 0;
        }

        if (limit == null) {
            limit = 30;
        }

        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
