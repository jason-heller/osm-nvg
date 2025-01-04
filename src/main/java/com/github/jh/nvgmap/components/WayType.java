package com.github.jh.nvgmap.components;

public enum WayType {
    MOTORWAY, MOTORWAY_LINK, PRIMARY, SECONDARY, TERTIARY, RESIDENTIAL, SERVICE, FOOTWAY, RAIL, STREAM, LINE, STEPS, PATH, DEFAULT,

    WETLAND(-1), RIVER(-1), WOOD(-1), BEACH(-1), SAND(-1);

    private final int priority;

    WayType() {
        this(0);
    }

    WayType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
