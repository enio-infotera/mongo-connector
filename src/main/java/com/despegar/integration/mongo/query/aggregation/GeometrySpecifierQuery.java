package com.despegar.integration.mongo.query.aggregation;



public class GeometrySpecifierQuery {
    private Near near;
    private String distanceField;
    private Integer limit;
    private Integer num;
    private Double maxDistance;
    private boolean spherical = true;
    private Double distanceMultiplier;
    private String includeLocs;
    private boolean uniqueDocs = true;

    public Near getNear() {
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

    public GeometrySpecifierQuery setNear(Near near) {
        this.near = near;
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
}
