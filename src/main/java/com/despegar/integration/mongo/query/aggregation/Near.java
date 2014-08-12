package com.despegar.integration.mongo.query.aggregation;

public class Near {

    public static enum NearType {
        POINT("Point");

        private String value;

        private NearType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    private Double[] coordinates;
    private String type;

    public Near(NearType type, Double[] coordinates) {
        this.coordinates = coordinates;
        this.type = type.getValue();
    }

    public Double[] getCoordinates() {
        return this.coordinates;
    }

    public String getType() {
        return this.type;
    }
}
