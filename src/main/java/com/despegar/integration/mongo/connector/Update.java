package com.despegar.integration.mongo.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class Update
    implements Bson {

    private Collection<UpdateOperation> operations = new ArrayList<Update.UpdateOperation>();

    private enum UpdateOperationType {
        SET("$set"), UNSET("$unset"), INC("$inc"), RENAME("$rename"), ADD_TO_SET("$addToSet"), POP("$pop"), PULL_ALL(
                        "$pullAll"), PULL("$pull"), PUSH("$push");

        private String operator;

        UpdateOperationType(String operator) {
            this.operator = operator;
        }
    }

    abstract static class UpdateOperation
        implements Bson {

        protected UpdateOperationType type;

        UpdateOperation(UpdateOperationType type) {
            this.type = type;
        }
    }

    public static class Set
        extends UpdateOperation {
        Set() {
            super(UpdateOperationType.SET);
        }

        private Map<String, Object> properties = new HashMap<String, Object>();

        public Set put(String property, Object value) {
            return this;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {

            // TODO pasar a new document, es mas facil de leer
            BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());

            writer.writeStartDocument();
            writer.writeName(this.type.operator);
            for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
                writer.writeStartDocument();
                writer.writeName(entry.getKey());
                encodeValue(writer, entry.getValue(), codecRegistry);
                writer.writeEndDocument();
            }
            writer.writeEndDocument();

            return writer.getDocument();
        }
    }

    public static Set set() {
        return new Set();
    }

    public Update addOperation(UpdateOperation uo) {
        this.operations.add(uo);
        return this;
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
        BsonArray updOps = new BsonArray();

        for (UpdateOperation uo : this.operations) {
            updOps.add(uo.toBsonDocument(documentClass, codecRegistry));
        }

        return updOps.asDocument();
    }

    @SuppressWarnings("unchecked")
    private static <TItem> void encodeValue(final BsonDocumentWriter writer, final TItem value,
        final CodecRegistry codecRegistry) {
        if (value == null) {
            writer.writeNull();
        } else if (value instanceof Bson) {
            ((Encoder) codecRegistry.get(BsonDocument.class)).encode(writer,
                ((Bson) value).toBsonDocument(BsonDocument.class, codecRegistry), EncoderContext.builder().build());
        } else {
            ((Encoder) codecRegistry.get(value.getClass())).encode(writer, value, EncoderContext.builder().build());
        }
    }
}
