package com.despegar.integration.mongo.connector;

import static com.mongodb.assertions.Assertions.notNull;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.despegar.integration.mongo.connector.Match.Point;


public class MyFilters {

    public static Bson near(String property, Point point, Double maxDistance, Double minDistance) {
        return new NearFilter("$near", property, point, maxDistance, minDistance);
    }

    static final class NearFilter
        implements Bson {
        private final String operatorName;
        private final String fieldName;
        private final Double maxDistance;
        private final Double minDistance;
        private final Point point;

        NearFilter(String operatorName, String property, Point point, Double maxDistance, Double minDistance) {
            this.operatorName = notNull("operatorName", this.operatorName);
            this.fieldName = notNull("fieldName", this.fieldName);
            this.point = point;
            this.maxDistance = maxDistance;
            this.minDistance = minDistance;
        }

        @Override
        public <TDocument> BsonDocument toBsonDocument(final Class<TDocument> documentClass,
            final CodecRegistry codecRegistry) {

            // TODO pasar a new Document, es mas facil de leer
            BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());

            writer.writeStartDocument();
            writer.writeName(this.fieldName);
            writer.writeStartDocument();
            writer.writeName(this.operatorName);
            writer.writeStartDocument();
            writer.writeName("$geometry");
            writer.writeStartDocument();
            writer.writeString("type", "Point");
            writer.writeStartArray("coordinates");
            writer.writeDouble(this.point.getLatitude());
            writer.writeDouble(this.point.getLongitude());
            writer.writeEndArray();
            writer.writeEndDocument();
            if (this.minDistance != null) {
                writer.writeDouble("$minDistance", this.minDistance);
            }
            if (this.maxDistance != null) {
                writer.writeDouble("$maxDistance", this.maxDistance);
            }
            writer.writeEndDocument();
            writer.writeEndDocument();

            return writer.getDocument();
        }

    }

}
