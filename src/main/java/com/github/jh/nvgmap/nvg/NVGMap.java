package com.github.jh.nvgmap.nvg;

import com.github.jh.nvgmap.MapData;
import com.github.jh.nvgmap.MapRegion;
import com.github.jh.nvgmap.MapRequester;
import com.github.jh.nvgmap.MapState;
import com.github.jh.nvgmap.components.Way;
import com.github.jh.nvgmap.gfx.MapSchema;
import com.github.jh.nvgmap.gfx.OSMSchema;
import com.github.jh.nvgmap.gfx.WaySchema;
import com.github.jh.nvgmap.nvg.NVGPath;
import com.github.jh.nvgmap.nvg.NVGPoint;
import org.lwjgl.nanovg.NanoVG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NVGMap {

    private static final double COORD_PX_RATIO = 10000.0;
    private int x, y, width, height;
    private double xScale, yScale;
    private MapSchema mapSchema = new OSMSchema();

    private MapData mapData = null;
    private MapRegion mapRegion;

    private final Map<Integer, NVGPath[]> pathMap = new TreeMap<>();

    private MapState mapState = MapState.NO_CONTENT;

    private NVGImage waitingImage, failImage;

    public NVGMap(long ctx, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        int iconX = (width / 2) - 16;
        int iconY = (height / 2) - 16;
        waitingImage = new NVGImage(ctx, x + iconX, y + iconY, "src/main/resources/map_wait.png");
        failImage = new NVGImage(ctx, x + iconX, y + iconY, "src/main/resources/map_fail.png");
    }

    public void create(double south, double west, double north, double east) {
        mapRegion = new MapRegion(south, west, north, east);
        MapRequester requester = new MapRequester()
                .setTimeout(25)
                .setMapRegion(mapRegion)
                .query("way->.ways;\n" +
                        "rel[type=multipolygon]->.polys;\n" +
                        "(.ways; .polys;)");

        MapData mapData = requester.retrieveMapData();

        if (mapData == null) {
            System.err.println("Failed to query OpenStreetMap.");
            return;
        }

        create(mapData);
    }

    public void create(MapData mapData) {

        if (mapData == null) {
            setState(MapState.FAILED);
            return;
        }

        this.mapData = mapData;

        mapRegion = mapData.getRegion();
        double latitudeSpan = mapRegion.getEast() - mapRegion.getWest();
        double longitudeSpan = mapRegion.getNorth() - mapRegion.getSouth();

        xScale = width / latitudeSpan;
        yScale = height / longitudeSpan;

        float lineScale = (float) (Math.min(xScale, yScale) / COORD_PX_RATIO);

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
                paths[i] = new NVGPath(way, pathSchema, points, lineScale);
            }
        }

        setState(MapState.ACTIVE);
    }

    public void draw(long ctx) {

        NanoVG.nvgScissor(ctx, x, y, width, height);
        NanoVG.nvgBeginPath(ctx);

        NanoVG.nvgRect(ctx, x, y, width, height);
        NanoVG.nvgFillColor(ctx, mapSchema.getBackgroundColor());
        NanoVG.nvgFill(ctx);

        if (mapData == null) {
            switch(mapState) {
                case FAILED:
                    failImage.draw(ctx);
                    break;
                case WAITING:
                    waitingImage.setAngle(((System.currentTimeMillis() % 1000) / 1000f) * 6.28318f);
                    waitingImage.draw(ctx);
                    break;
                case NO_CONTENT:
                    break;
            }

            NanoVG.nvgResetScissor(ctx);
            return;
        }

        for(int layer : pathMap.keySet()) {
            for(NVGPath path : pathMap.get(layer)) {
                path.draw(ctx);
            }
        }

        NanoVG.nvgResetScissor(ctx);
    }

    public void resize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        create(this.getMapData());
    }

    public MapData getMapData() {
        return mapData;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setState(MapState mapState) {
        this.mapState = mapState;
    }

    public void dispose(long ctx) {
        waitingImage.dispose(ctx);
        failImage.dispose(ctx);
    }
}
