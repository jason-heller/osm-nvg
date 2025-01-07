package com.github.jh.nvgmap.components;

public enum WayType {

    MOTORWAY(380),
    MOTORWAY_LINK(240),
    PRIMARY(360),
    SECONDARY(350),
    TERTIARY(340),
    RESIDENTIAL(330),
    SERVICE(150),
    FOOTWAY(100),
    RAIL(440),
    //SUBWAY(420),
    //LIGHT_RAIL(420),
    //NARROW_GUAGE(420),
    //MONORAIL(420),
    //FUNICULAR(420),
    //TRAM(410),
    STREAM,
    LINE(500),
    STEPS(90),
    PATH(100),
    DEFAULT,

    PLATFORM(90),
    WETLAND(-1000),
    RIVER(-900),
    WOOD(-1000),
    BEACH(-1000),
    SAND(-1000),

    COMMERCIAL_ZONING(-900),
    CONSTRUCTION_ZONING(-900),
    EDUCATION_ZONING(-900),
    INDUSTRIAL_ZONING(-900),
    RESIDENTIAL_ZONING(-900),
    RETAIL_ZONING(-900),
    PARK_ZONING(-1000),
    SWIMMING_POOL_ZONING(-900),
    GARDEN_ZONING(-1000),
    FARMLAND(-900),
    FARMYARD(-900),
    BUILDING(500);

    private final int priority;

    WayType() {
        this(0);
    }

    WayType(int priority) {
        assert (priority >> 16) == 0;

        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
