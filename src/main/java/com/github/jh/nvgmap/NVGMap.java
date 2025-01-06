package com.github.jh.nvgmap;

import com.github.jh.nvgmap.components.Way;
import com.github.jh.nvgmap.gfx.MapSchema;
import com.github.jh.nvgmap.gfx.OSMSchema;
import com.github.jh.nvgmap.gfx.WaySchema;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NVGMap {

    private int x, y, width, height;
    private MapSchema mapSchema = new OSMSchema();

    private MapData mapData = null;
    private MapRegion mapRegion;

    private final Map<Integer, NVGPath[]> pathMap = new TreeMap<>();

    public NVGMap(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /*public void setMapRegion(double south, double west, double north, double east) {
        mapRegion = new MapRegion(south, west, north, east);
        setMapRegion(mapRegion);
    }*/

    public void setMapRegion(MapRegion mapRegion) {

        mapRegion.shift(-.08,0.0);

        MapRequester requester = new MapRequester()
                .setTimeout(25)
                .setMapRegion(mapRegion)
                .addQuery("way->.ways;\n" +
                "rel[type=multipolygon]->.polys;\n" +
                "(.ways; .polys;)");

        this.mapData = requester.request();

        // Map failed to load
        if (mapData == null) {
            return;
        }

        // Grab graphical contents
        Map<Integer, List<Way>> wayMap = (HashMap<Integer, List<Way>>) mapData.getWays();
        final int nLayers = wayMap.keySet().size();

        for(int layer : wayMap.keySet()) {

            List<Way> wayList = wayMap.get(layer);
            final int nWays = wayList.size();

            NVGPath[] paths = new NVGPath[nWays];
            pathMap.put(layer, paths);

            for(int i = 0; i < nWays; i++) {

                Way way = wayList.get(i);
                NVGPoint[] points = new NVGPoint[way.getCoords().length];

                for(int j = 0; j < points.length; j++) {
                    points[j] = way.getCoords()[j].toMapPosition(x, y, width, height, mapRegion);
                }

                WaySchema pathSchema = mapSchema.getWaySchema(way.getWayType());
                paths[i] = new NVGPath(way, pathSchema, points);
            }
        }
    }

    public void draw(long ctx) {
        NanoVG.nvgScissor(ctx, x, y, width, height);
        NanoVG.nvgBeginPath(ctx);

        NanoVG.nvgRect(ctx, x, y, width, height);
        NanoVG.nvgFillColor(ctx, mapSchema.getBackgroundColor());
        NanoVG.nvgFill(ctx);

        for(int layer : pathMap.keySet()) {
            for(NVGPath path : pathMap.get(layer)) {
                path.draw(ctx);
            }
        }

        NanoVG.nvgResetScissor(ctx);
    }

    public MapData getMapData() {
        return mapData;
    }
}
