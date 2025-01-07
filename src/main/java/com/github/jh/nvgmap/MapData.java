package com.github.jh.nvgmap;

import com.github.jh.nvgmap.components.Way;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapData {
    private final MapRegion mapRegion;

    private Map<Integer, List<Way>> ways = new HashMap<>();
    public MapData(MapRegion mapRegion) {
        this.mapRegion = mapRegion;
    }
    public void addWay(Way way) {
        int layer = way.getLayer();

        List<Way> wayList = ways.computeIfAbsent(layer, k -> new LinkedList<>());

        // Binary sort into
        wayList.add(way);
    }

    public MapRegion getRegion() {
        return mapRegion;
    }

    public Map<Integer, List<Way>> getWays()
    {
        return ways;
    }
}
