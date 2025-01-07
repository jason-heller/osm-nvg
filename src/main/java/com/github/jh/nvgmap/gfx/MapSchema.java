package com.github.jh.nvgmap.gfx;

import com.github.jh.nvgmap.components.WayType;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.nanovg.NVGColor;

public abstract class MapSchema {
    private final NVGColor backgroundColor;
    private final WaySchema[] waySchemas = new WaySchema[WayType.values().length];

    public MapSchema() {
        backgroundColor = backgroundColor();

        assignWaySchema(WayType.MOTORWAY, motorwayWaySchema());
        assignWaySchema(WayType.MOTORWAY_LINK, motorwayWaySchema());
        assignWaySchema(WayType.PRIMARY, primaryWaySchema());
        assignWaySchema(WayType.SECONDARY, secondaryWaySchema());
        assignWaySchema(WayType.TERTIARY, tetriaryWaySchema());
        assignWaySchema(WayType.RESIDENTIAL, residentialWaySchema());
        assignWaySchema(WayType.PATH, footWaySchema());
        assignWaySchema(WayType.FOOTWAY, footWaySchema());
        assignWaySchema(WayType.RAIL, railWaySchema());
        assignWaySchema(WayType.STEPS, stepWaySchema());
        assignWaySchema(WayType.STREAM, streamWaySchema());
        assignWaySchema(WayType.LINE, powerLineWaySchema());
        assignWaySchema(WayType.SERVICE, serviceWaySchema());
        assignWaySchema(WayType.DEFAULT, defaultWaySchema());

        assignWaySchema(WayType.RIVER, waterWaySchema());
        assignWaySchema(WayType.WOOD, woodedWaySchema());
        assignWaySchema(WayType.WETLAND, wetlandWaySchema());
        assignWaySchema(WayType.BEACH, beachWaySchema());
        assignWaySchema(WayType.SAND, sandWaySchema());
        assignWaySchema(WayType.PLATFORM, platformWaySchema());

        assignWaySchema(WayType.COMMERCIAL_ZONING, commercialZoneSchema());
        assignWaySchema(WayType.CONSTRUCTION_ZONING, constructionZoneSchema());
        assignWaySchema(WayType.EDUCATION_ZONING, educationZoneSchema());
        assignWaySchema(WayType.INDUSTRIAL_ZONING, industrialZoneSchema());
        assignWaySchema(WayType.RESIDENTIAL_ZONING, residentialZoneSchema());
        assignWaySchema(WayType.RETAIL_ZONING, retailZoneSchema());
        assignWaySchema(WayType.BUILDING, buildingZoneSchema());

        assignWaySchema(WayType.GARDEN_ZONING, grassSchema());
        assignWaySchema(WayType.PARK_ZONING, grassSchema());
        assignWaySchema(WayType.SWIMMING_POOL_ZONING, waterWaySchema());

        assignWaySchema(WayType.FARMLAND, farmLandSchema());
        assignWaySchema(WayType.FARMYARD, farmYardSchema());
    }
    protected abstract WaySchema grassSchema();

    protected abstract WaySchema farmLandSchema();

    protected abstract WaySchema farmYardSchema();
    protected abstract WaySchema buildingZoneSchema();

    protected abstract WaySchema retailZoneSchema();

    protected abstract WaySchema residentialZoneSchema();

    protected abstract WaySchema industrialZoneSchema();

    protected abstract WaySchema educationZoneSchema();

    protected abstract WaySchema constructionZoneSchema();

    protected abstract WaySchema commercialZoneSchema();

    private void assignWaySchema(WayType wayType, WaySchema waySchema) {
        waySchemas[wayType.ordinal()] = waySchema;
    }

    public NVGColor getBackgroundColor() {
        return backgroundColor();
    }

    protected abstract WaySchema waterWaySchema();
    protected abstract WaySchema woodedWaySchema();
    protected abstract WaySchema beachWaySchema();
    protected abstract WaySchema sandWaySchema();
    protected abstract WaySchema wetlandWaySchema();
    protected abstract NVGColor backgroundColor();
    protected abstract WaySchema motorwayWaySchema();
    protected abstract WaySchema primaryWaySchema();
    protected abstract WaySchema secondaryWaySchema();
    protected abstract WaySchema tetriaryWaySchema();
    protected abstract WaySchema residentialWaySchema();
    protected abstract WaySchema footWaySchema();
    protected abstract WaySchema stepWaySchema();
    protected abstract WaySchema serviceWaySchema();
    protected abstract WaySchema railWaySchema();
    protected abstract WaySchema defaultWaySchema();
    protected abstract WaySchema streamWaySchema();
    protected abstract WaySchema powerLineWaySchema();
    protected abstract WaySchema platformWaySchema();
    public WaySchema getWaySchema(@NotNull WayType wayType) {
        return waySchemas[wayType.ordinal()];
    }
}
