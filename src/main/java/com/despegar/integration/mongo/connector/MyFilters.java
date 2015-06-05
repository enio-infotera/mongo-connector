package com.despegar.integration.mongo.connector;

import static com.mongodb.assertions.Assertions.notNull;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.Document;
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

            Document near = new Document();

            Document geometry = new Document();
            geometry.append("type", "Point");
            List<BsonDouble> points = new ArrayList<BsonDouble>();
            points.add(new BsonDouble(this.point.getLongitude()));
            points.add(new BsonDouble(this.point.getLatitude()));
            geometry.append("coordinates", new BsonArray(points));

            near.append("$geometry", geometry);

            if (this.minDistance != null) {
                near.append("$minDistance", this.minDistance);
            }
            if (this.maxDistance != null) {
                near.append("$maxDistance", this.maxDistance);
            }

            Document query = new Document(this.operatorName, near);

            return new Document(this.fieldName, query).toBsonDocument(documentClass, codecRegistry);
        }
    }

}
