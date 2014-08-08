package com.despegar.integration.mongo.support;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;

public class IdWithUnderscoreStrategy
    extends PropertyNamingStrategyBase {

    private static final long serialVersionUID = 1L;

    @Override
    public String translate(final String propertyName) {
        if (propertyName == null) {
            return propertyName; // garbage in, garbage out
        }

        if (propertyName.equals("id")) {
            return "_id";
        } else {
            return propertyName;
        }
    }

}
