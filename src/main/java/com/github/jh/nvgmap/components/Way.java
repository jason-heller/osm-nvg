package com.github.jh.nvgmap.components;

public class Way {

    private String name;
    private int layer;
    private WayType wayType;
    private MapCoordinate[] coords;

    public Way(String name, WayType wayType, MapCoordinate[] coords, int layer) {
        this.name = name;
        this.wayType = wayType;
        this.coords = coords;
        this.layer = layer;
    }

    public String getName() {
        return name;
    }

    public WayType getWayType() {
        return wayType;
    }

    public MapCoordinate[] getCoords() {
        return coords;
    }

    public boolean attemptJoin(Way way) {
        if (way.getWayType() != this.getWayType())
            return false;

        final MapCoordinate[] otherCoords = way.getCoords();

        final int nCoordsThis = coords.length;
        final int nCoordsThat = otherCoords.length;

        final MapCoordinate[] newCoords = new MapCoordinate[nCoordsThis + nCoordsThat];

        int coordId = 0;

        for (MapCoordinate otherCoord : otherCoords)
            newCoords[coordId++] = otherCoord;

        for (MapCoordinate coord : coords)
            newCoords[coordId++] = coord;

        coords = newCoords;
        return true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setWayType(WayType wayType) {
        this.wayType = wayType;
    }
}
