package br.com.technologgy.integracao.mongo.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.io.IOException;
import java.util.Date;

public class DateJsonSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        TokenBuffer buffer = (TokenBuffer) jgen;
        ObjectCodec codec = buffer.getCodec();
        buffer.setCodec(null);

        buffer.writeObject(value);

        buffer.setCodec(codec);
    }
}
