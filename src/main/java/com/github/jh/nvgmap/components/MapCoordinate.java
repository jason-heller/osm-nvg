package com.github.jh.nvgmap.components;

import com.github.jh.nvgmap.MapRegion;
import com.github.jh.nvgmap.NVGMap;
import com.github.jh.nvgmap.NVGPoint;

public record MapCoordinate(double latitude, double longitude) {

    public MapCoordinate {
        assert longitude >= -90.0 && longitude <= 90.0;
    }
    public NVGPoint toMapPosition(float mapX, float mapY, float mapWidth, float mapHeight, MapRegion region) {
        double coordWidth = region.getEast() - region.getWest();
        double coordHeight = region.getNorth() - region.getSouth();

        double latRatio = (latitude - region.getSouth()) / coordHeight;
        double lonRatio = (longitude - region.getWest()) / coordWidth;

        float x = mapWidth * (float)lonRatio;
        float y = mapHeight * (float)(1.0 - latRatio);

        return new NVGPoint(mapX + x, mapY + y);
    }
}
