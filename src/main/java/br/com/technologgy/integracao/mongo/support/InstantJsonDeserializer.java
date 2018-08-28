package br.com.technologgy.integracao.mongo.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class InstantJsonDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Date date = (Date) jp.getEmbeddedObject();
            return date.toInstant();
        } else {
            throw new RuntimeException("Invalid date format. ISODate was expected.");
        }
    }
}
