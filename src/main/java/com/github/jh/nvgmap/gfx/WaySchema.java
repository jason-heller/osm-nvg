package com.github.jh.nvgmap.gfx;

import org.lwjgl.nanovg.NVGColor;

public class WaySchema {

    private final float width;
    private final NVGColor fillColor;
    private final NVGColor borderColor;
    private final LineStyle lineStyle;
    private boolean isArea;

    private WaySchema(float width, NVGColor color, LineStyle lineStyle, boolean isArea) {
        this.width = width;
        this.fillColor = color;
        this.borderColor = ColorUtil.darken(color, 50);
        this.lineStyle = lineStyle;
        this.isArea = isArea;
    }

    public static WaySchema asLine(float width, NVGColor color) {
        return new WaySchema(width, color, LineStyle.SOLID, false);
    }

    public static WaySchema asLine(float width, NVGColor color, LineStyle lineStyle) {
        return new WaySchema(width, color, lineStyle, false);
    }

    public static WaySchema asArea(NVGColor color) {
        return new WaySchema(0f, color, LineStyle.NONE, true);
    }

    public static WaySchema asArea(float width, NVGColor color, LineStyle lineStyle) {
        return new WaySchema(width, color, lineStyle, true);
    }

    public float getWidth() {
        return width;
    }

    public NVGColor getFillColor() {
        return fillColor;
    }

    public NVGColor getBorderColor() {
        return borderColor;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public boolean isArea() {
        return isArea;
    }
}
