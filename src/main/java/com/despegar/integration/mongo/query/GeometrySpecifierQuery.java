package com.despegar.integration.mongo.query;


public class GeometrySpecifierQuery {
    private Double[] near;
    private String distanceField;
    private Integer limit;
    private Integer num;
    private Double maxDistance;
    private boolean spherical = true;
    private Double distanceMultiplier;
    private String includeLocs;
    private boolean uniqueDocs = true;

    public Double[] getNear() {
        return this.near;
    }

    public String getDistanceField() {
        return this.distanceField;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public Integer getNum() {
        return this.num;
    }

    public Double getMaxDistance() {
        return this.maxDistance;
    }

    public boolean isSpherical() {
        return this.spherical;
    }

    public Double getDistanceMultiplier() {
        return this.distanceMultiplier;
    }

    public String getIncludeLocs() {
        return this.includeLocs;
    }

    public boolean isUniqueDocs() {
        return this.uniqueDocs;
    }

    public GeometrySpecifierQuery setNear(Double[] coordinates) {
        this.near = coordinates;
        return this;
    }

    public GeometrySpecifierQuery setDistanceField(String distanceField) {
        this.distanceField = distanceField;
        return this;
    }

    public GeometrySpecifierQuery setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public GeometrySpecifierQuery setNum(Integer num) {
        this.num = num;
        return this;
    }

    public GeometrySpecifierQuery setMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
        return this;
    }

    public GeometrySpecifierQuery setSpherical(boolean spherical) {
        this.spherical = spherical;
        return this;
    }

    public GeometrySpecifierQuery setDistanceMultiplier(Double distanceMultiplier) {
        this.distanceMultiplier = distanceMultiplier;
        return this;
    }

    public GeometrySpecifierQuery setIncludeLocs(String includeLocs) {
        this.includeLocs = includeLocs;
        return this;
    }

    public GeometrySpecifierQuery setUniqueDocs(boolean uniqueDocs) {
        this.uniqueDocs = uniqueDocs;
        return this;
    }

    public static class Near {

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
}
