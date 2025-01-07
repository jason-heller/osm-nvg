package com.github.jh.nvgmap.nvg;

import com.github.jh.nvgmap.MapData;
import com.github.jh.nvgmap.MapRegion;
import com.github.jh.nvgmap.MapRequester;
import com.github.jh.nvgmap.MapState;
import com.github.jh.nvgmap.components.Way;
import com.github.jh.nvgmap.gfx.MapSchema;
import com.github.jh.nvgmap.gfx.OSMSchema;
import com.github.jh.nvgmap.gfx.WaySchema;
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

    /**
     * Creates a map using the given latitude and longitude boundaries. Intended as a convenient way to
     * quickly query and get a map for use. Will set map to a fail state if unable to query the OSM api.
     *
     * @param south The southern longitudinal boundary of the map
     * @param west The western latitudinal boundary of the map
     * @param north The northern longitudinal boundary of the map
     * @param east The eastern latitudinal boundary of the map
     */
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
            setState(MapState.FAILED);
            return;
        }

        create(mapData);
    }

    /**
     * Creates a map using the given MapData, generally created from a MapRequester object.
     * Will set map to a fail state if unable to query the OSM api.
     *
     * @param mapData The map data to use for this map.
     */
    public void create(MapData mapData) {

        if (mapData == null) {
            System.err.println("Failed to query OpenStreetMap.");
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

    /**
     * Renders the map. Make sure to put this with your other NVG render calls.
     *
     * @param ctx The handle for the NanoVG context
     */
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

    /**
     * Sets the map's position and size.
     *
     * @param x The map's X position in pixels
     * @param y The map's Y position in pixels
     * @param width The map's width in pixels
     * @param height The map's height in pixels
     */
    public void setBoundary(int x, int y, int width, int height) {
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

    /**
     * Sets the maps state. Used to indicate how the map should display
     * within the window.
     *
     * @param mapState the state of the map
     */
    public void setState(MapState mapState) {
        this.mapState = mapState;
    }

    /**
     * Frees resources created by the map. Should be called before terminating the program.
     *
     * @param ctx The handle for the NanoVG context
     */
    public void dispose(long ctx) {
        waitingImage.dispose(ctx);
        failImage.dispose(ctx);
    }
}
