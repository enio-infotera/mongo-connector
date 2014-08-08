package com.despegar.integration.mongo.entities;

public class Point {

    private Double[] coordinates;
    @SuppressWarnings("unused")
    // TODO: chequear si no se puede hacer cercania a algo que no sea un punto para proximas versiones
    private String type = "Point";

    public Point(Double[] coordinates) {
        this.coordinates = coordinates;
    }

    public Double[] getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(Double[] coordinates) {
        this.coordinates = coordinates;
    }
}
