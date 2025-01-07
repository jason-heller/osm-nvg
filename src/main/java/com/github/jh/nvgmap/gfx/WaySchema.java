package com.github.jh.nvgmap.gfx;

import org.lwjgl.nanovg.NVGColor;

public class WaySchema {

    private final float width;
    private final NVGColor primary;
    private final NVGColor secondary;
    private final LineStyle lineStyle;
    private boolean isArea;

    private WaySchema(float width, NVGColor primary, NVGColor secondary, LineStyle lineStyle, boolean isArea) {
        this.width = width;
        this.primary = primary;
        this.secondary = secondary;
        this.lineStyle = lineStyle;
        this.isArea = isArea;
    }

    public static WaySchema asLine(float width, NVGColor color) {
        return new WaySchema(width, color, ColorUtil.darken(color, 50), LineStyle.SOLID, false);
    }

    public static WaySchema asLine(float width, NVGColor color, LineStyle lineStyle) {
        return new WaySchema(width, color, ColorUtil.darken(color, 50), lineStyle, false);
    }

    public static WaySchema asLine(float width, NVGColor primary, NVGColor secondary, LineStyle lineStyle) {
        return new WaySchema(width, primary, secondary, lineStyle, false);
    }

    public static WaySchema asArea(NVGColor color) {
        return new WaySchema(0f, color, ColorUtil.darken(color, 50), LineStyle.NONE, true);
    }

    public static WaySchema asArea(float width, NVGColor color, LineStyle lineStyle) {
        return new WaySchema(width, color, ColorUtil.darken(color, 50), lineStyle, true);
    }

    public float getWidth() {
        return width;
    }

    public NVGColor getPrimaryColor() {
        return primary;
    }

    public NVGColor getSecondaryColor() {
        return secondary;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public boolean isArea() {
        return isArea;
    }
}
