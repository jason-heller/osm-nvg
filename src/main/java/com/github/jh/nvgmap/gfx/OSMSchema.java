package com.github.jh.nvgmap.gfx;

import org.lwjgl.nanovg.NVGColor;

import static com.github.jh.nvgmap.gfx.ColorUtil. rgb;

public class OSMSchema extends MapSchema {

    private static final NVGColor buildingColor =  rgb(217, 208, 201);
    private static final NVGColor waterColor =  rgb(170, 211, 223);
    private static final NVGColor forestColor =  rgb(172, 210, 156);
    private static final NVGColor grassColor =  rgb(205, 247, 201);
    private static final NVGColor wetlandColor =  rgb(205, 235, 176);
    private static final NVGColor beachColor =  rgb(255, 241, 186);
    private static final NVGColor sandColor =  rgb(245, 233, 198);  // Apparently this is a different color than beaches on OSM.. alright
    private static final NVGColor backgroundColor =  rgb(242, 239, 233);

    private static final WaySchema MOTORWAY =  WaySchema.asLine(10,  rgb(232, 146, 162));
    private static final WaySchema PRIMARY =  WaySchema.asLine(10,  rgb(252, 214, 164));
    private static final WaySchema SECONDARY =  WaySchema.asLine(10,  rgb(248, 260, 189));
    private static final WaySchema TERTIARY =  WaySchema.asLine(10,  ColorUtil.WHITE);
    private static final WaySchema DEFAULT =  WaySchema.asLine(1, ColorUtil.MAGENTA);
    private static final WaySchema RESIDENTIAL =  WaySchema.asLine(5, ColorUtil.WHITE);
    private static final WaySchema PATH =  WaySchema.asLine(1,  rgb(232, 146, 162), LineStyle.DASHED);
    private static final WaySchema STEPS =  WaySchema.asLine(1,  rgb(232, 146, 162), LineStyle.DOTTED);
    private static final WaySchema RAIL =  WaySchema.asLine(3, ColorUtil.WHITE, rgb(112, 112, 112), LineStyle.DASH_SOLID);
    private static final WaySchema STREAM =  WaySchema.asLine(2, waterColor);
    private static final WaySchema POWER_LINE =  WaySchema.asLine(1,  rgb(151, 151, 151));
    private static final WaySchema SERVICE =  WaySchema.asLine(3, ColorUtil.WHITE);

    private static final WaySchema WATER =  WaySchema.asArea(waterColor);
    private static final WaySchema WOOD =  WaySchema.asArea(forestColor);
    private static final WaySchema WETLAND =  WaySchema.asArea(wetlandColor);
    private static final WaySchema BEACH =  WaySchema.asArea(beachColor);
    private static final WaySchema SAND =  WaySchema.asArea(sandColor);
    private static final WaySchema PLATFORM =  WaySchema.asArea(rgb(187, 187, 187));

    public OSMSchema() {
        super();
    }

    @Override
    protected WaySchema grassSchema() {
        return WaySchema.asArea(grassColor);
    }

    @Override
    protected WaySchema farmLandSchema() {
        return WaySchema.asArea(rgb(239, 240,214));
    }

    @Override
    protected WaySchema farmYardSchema() {
        return WaySchema.asArea(rgb(234, 294,164));
    }

    @Override
    protected WaySchema retailZoneSchema() {
        return WaySchema.asArea(rgb(254, 202,197));
    }

    @Override
    protected WaySchema residentialZoneSchema() {
        return WaySchema.asArea(rgb(218, 218,218));
    }

    @Override
    protected WaySchema industrialZoneSchema() {
        return WaySchema.asArea(rgb(230, 209, 227));
    }

    @Override
    protected WaySchema educationZoneSchema() {
        return WaySchema.asArea(ColorUtil.WHITE);
    }


    @Override
    protected WaySchema buildingZoneSchema() {
        return WaySchema.asArea(buildingColor);
    }

    @Override
    protected WaySchema constructionZoneSchema() {
        return WaySchema.asArea(rgb(199, 199, 180));
    }

    @Override
    protected WaySchema commercialZoneSchema() {
        return WaySchema.asArea(rgb(238, 207, 207));
    }

    @Override
    protected WaySchema waterWaySchema() {
        return WATER;
    }
    @Override
    protected WaySchema woodedWaySchema() {
        return WOOD;
    }
    @Override
    protected WaySchema beachWaySchema() {
        return BEACH;
    }
    @Override
    protected WaySchema sandWaySchema() {
        return SAND;
    }

    @Override
    protected WaySchema wetlandWaySchema() {
        return WETLAND;
    }

    @Override
    protected NVGColor backgroundColor() {
        return backgroundColor;
    }

    @Override
    protected WaySchema motorwayWaySchema() {
        return MOTORWAY;
    }

    @Override
    protected WaySchema primaryWaySchema() {
        return PRIMARY;
    }

    @Override
    protected WaySchema secondaryWaySchema() {
        return SECONDARY;
    }

    @Override
    protected WaySchema tetriaryWaySchema() {
        return TERTIARY;
    }
    @Override
    protected WaySchema stepWaySchema() {
        return STEPS;
    }

    @Override
    protected WaySchema residentialWaySchema() {
        return RESIDENTIAL;
    }

    @Override
    protected WaySchema footWaySchema() {
        return PATH;
    }

    @Override
    protected WaySchema serviceWaySchema() {
        return SERVICE;
    }

    @Override
    protected WaySchema railWaySchema() {
        return RAIL;
    }

    @Override
    protected WaySchema defaultWaySchema() {
        return DEFAULT;
    }

    @Override
    protected WaySchema streamWaySchema() {
        return STREAM;
    }

    @Override
    protected WaySchema powerLineWaySchema() {
        return POWER_LINE;
    }

    @Override
    protected WaySchema platformWaySchema() {
        return PLATFORM;
    }
}
