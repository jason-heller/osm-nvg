package com.github.jh.nvgmap;

public class MapRegion {

    private double south, west, north, east;

    public MapRegion(double south, double west, double north, double east) {
        setLocation(south, west, north, east);
    }

    public void setLocation(double south, double west, double north, double east) {
        this.south = south;
        this.west = west;
        this.north = north;
        this.east = east;
    }

    public double getSouth() {
        return south;
    }

    public double getWest() {
        return west;
    }

    public double getNorth() {
        return north;
    }

    public double getEast() {
        return east;
    }

    public void setSouth(double south) {
        this.south = south;
    }

    public void setWest(double west) {
        this.west = west;
    }

    public void setNorth(double north) {
        this.north = north;
    }

    public void setEast(double east) {
        this.east = east;
    }

    public void shift(double deltaLat, double deltaLon) {
        west += deltaLat;
        east += deltaLat;
        north += deltaLon;
        south += deltaLon;
    }
    @Override
    public String toString() {
        return south + "," + west + "," + north + "," + east;
    }
}