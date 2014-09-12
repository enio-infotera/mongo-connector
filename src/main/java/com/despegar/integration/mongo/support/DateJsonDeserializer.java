package com.despegar.integration.mongo.support;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateJsonDeserializer
    extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Date date = (Date) jp.getEmbeddedObject();
            return date;
        } else {
            throw new RuntimeException("Invalid date format. ISODate was expected.");
        }
    }
}
