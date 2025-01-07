package com.github.jh.nvgmap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.github.jh.nvgmap.components.MapCoordinate;
import com.github.jh.nvgmap.components.Way;
import com.github.jh.nvgmap.components.WayType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class MapRequester {
    private final String OVERPASS_URL = "https://overpass-api.de/api/interpreter?data=";

    private List<String> queries = new ArrayList<>();

    private final String format = "json";
    private int timeout = 25;
    private MapRegion mapRegion;
    private MapData mapData;
    public MapRequester() {
    }

    public MapData retrieveMapData()
    {
        return retrieveMapData(buildQuery());
    }
    public MapData retrieveMapData(String query) {
        MapData mapData = null;

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            URL url = new URI(OVERPASS_URL + encodedQuery).toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();
/*
            // Debug
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            System.out.println(result);
            //
*/
            mapData = parseRequest(inputStream);

            inputStream.close();

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        if (mapData == null)
            System.err.println("Failed to query OpenStreetMap.");

        return mapData;
    }

    private MapData parseRequest(InputStream inputStream) {
        mapData = new MapData(mapRegion);

        try {

            Object obj = new JSONParser().parse(new InputStreamReader(inputStream));
            JSONObject jsonObj = (JSONObject) obj;

            JSONArray elementArray = (JSONArray) jsonObj.get("elements");

            Iterator iter = elementArray.iterator();

            while(iter.hasNext()) {
                JSONObject element = (JSONObject) iter.next();
                JSONObject tags = (JSONObject) element.get("tags");

                parseNode(element, tags);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

        return mapData;
    }

    private void parseNode(JSONObject element, JSONObject tags) {
        switch(element.get("type").toString()) {
            case "way":
                if (tags == null)
                    break;

                Way newWay = parseWay(tags, element, null);

                if (newWay != null)
                    mapData.addWay(newWay);
                break;

            case "relation":
                parseRelation(tags, element);
                break;

            default:
                System.out.println("ignored: " + element.get("type").toString());
        }
    }

    private void parseRelation(JSONObject tags, JSONObject element) {
        JSONArray membersArray = (JSONArray)element.get("members");

        Way parentWay = null;

        Iterator memberIter = membersArray.iterator();
        while(memberIter.hasNext()) {
            JSONObject member = (JSONObject)memberIter.next();

            if (member.get("type").toString().equals("way")) {
                if (parentWay == null) {
                    parentWay = parseWay(tags, member, null);
                    continue;
                }

                Way newWay = parseWay(tags, member, parentWay.getWayType());

                if (newWay != null && !parentWay.attemptJoin(newWay)) {
                    mapData.addWay(newWay);
                }
            } else {
                System.out.println("ignored: " + element.get("type").toString());
            }
        }

        if (parentWay != null)
            mapData.addWay(parentWay);
    }

    private Way parseWay(JSONObject tags, JSONObject obj, WayType inheritedType) {
        // Ensure this is a way and not a multipoly

        JSONArray geomArray = (JSONArray)obj.get("geometry");

        final int nCoords = geomArray.size();

        final MapCoordinate[] coords = new MapCoordinate[nCoords];

        String layerTag = (String) tags.get("layer");

        // Sometimes 'level' is incorrectly used in place of 'layer' in OSM
        // We check this tag to account for this common error
        if (layerTag == null) {
            layerTag = (String) tags.get("level");
            if (layerTag != null && layerTag.contains(";")) {
                String[] levels = layerTag.split(";");
                layerTag = levels[levels.length - 1];
            }
        }

        int layer = (layerTag == null) ? 0 : Integer.parseInt(layerTag);

        Iterator iter = geomArray.iterator();
        for(int i = 0; i < nCoords; i++) {
            JSONObject latLongPair = (JSONObject) iter.next();
            double lat = (double) latLongPair.get("lat");
            double lon = (double) latLongPair.get("lon");

            coords[i] = new MapCoordinate(lat, lon);
        }

        if (inheritedType != null) {
            layer += inheritedType.getPriority();
            return new Way(null, inheritedType, coords, layer);
        }

        String name = (String)tags.get("name");
        WayType wayType = getWayType(tags);

        if (wayType == null)
            return null;

        layer *= 100000; // Ensure layer is more significant than z-order of nodes
        layer += wayType.getPriority();

        return new Way(name, wayType, coords, layer);
    }

    private WayType getWayType(JSONObject tags) {
        WayType wayType = null;

        String naturalTag = (String)tags.get("natural");

        // Natural areas
        if (naturalTag != null) {
            wayType = switch (naturalTag) {
                case "wetland" -> WayType.WETLAND;
                case "water" -> WayType.RIVER;
                case "wood" -> WayType.WOOD;
                case "sand" -> WayType.SAND;
                default -> wayType;
            };

            if (wayType == null)
                System.out.println("Ignored natural tag: " + naturalTag);
        }

        String landuseTag = (String)tags.get("landuse");
        if (landuseTag != null) {
            wayType = switch (landuseTag) {
                case "commercial" -> WayType.COMMERCIAL_ZONING;
                case "construction" -> WayType.CONSTRUCTION_ZONING;
                case "education" -> WayType.EDUCATION_ZONING;
                case "industrial" -> WayType.INDUSTRIAL_ZONING;
                case "residential" -> WayType.RESIDENTIAL_ZONING;
                case "retail" -> WayType.RETAIL_ZONING;

                case "farmland" -> WayType.FARMLAND;
                case "farmyard" -> WayType.FARMYARD;
                case "forest" -> WayType.WOOD;

                case "grass" -> WayType.GARDEN_ZONING;

                case "basin" -> WayType.RIVER;
                default -> wayType;
            };

            if (wayType == null)
                System.out.println("Ignored landuse tag: " + naturalTag);
        }

        if (wayType != null)
            return wayType;

        String leisureTag = (String)tags.get("leisure");

        if (leisureTag != null) {
            wayType = switch (leisureTag) {
                case "park" -> WayType.PARK_ZONING;
                case "garden" -> WayType.GARDEN_ZONING;
                case "swimming pool" -> WayType.SWIMMING_POOL_ZONING;
                default -> wayType;
            };
        }

        if (wayType != null)
            return wayType;

        String buildingTag = (String)tags.get("building");
        if (buildingTag != null) {
            return WayType.BUILDING;
        }

        String wayTypeTag = (String)tags.get("highway");

        if (wayTypeTag == null)
            wayTypeTag = (String)tags.get("railway");

        if (wayTypeTag == null)
            wayTypeTag = (String)tags.get("power");

        if (wayTypeTag == null)
            wayTypeTag = (String)tags.get("waterway");

        if (wayTypeTag != null) {
            for (WayType h : WayType.values()) {
                if (h.name().equals(wayTypeTag.toUpperCase())) {
                    wayType = h;
                    break;
                }
            }
        }

        /*if (wayTypeTag == null) {
            for(Object key : tags.keySet()) {
                System.out.println((String)key + ": " + (String)(tags.get(key)));
            }System.out.println();
        }*/

        return wayType;
    }

    private String buildQuery() {
        StringBuilder sb = new StringBuilder();

        if (mapRegion != null)
            sb.append("[bbox:").append(mapRegion.toString()).append("]");

        sb.append("[out:").append(format).append("]");
        sb.append("[timeout:").append(timeout).append("];\n");

        for(String query : queries)
            sb.append(query).append(";\n");

        sb.append("out geom;");

        return sb.toString();
    }

    public int getTimeout() {
        return timeout;
    }

    public MapRequester setTimeout(int timeout) {
        assert timeout > 0;

        this.timeout = timeout;
        return this;
    }

    public MapRegion getMapRegion() {
        return mapRegion;
    }

    public MapRequester setMapRegion(MapRegion mapRegion) {
        this.mapRegion = mapRegion;
        return this;
    }

    public MapRequester query(String query) {
        queries.add(query);
        return this;
    }

    public MapRequester clearQueries() {
        queries.clear();
        return this;
    }
}
