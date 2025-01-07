package com.github.jh.nvgmap.gfx;

public enum LineStyle {
    SOLID(), DASHED(4.0), DOTTED(2.0), DASH_DOTTED(4.0, 2.0), DASH_SOLID(8.0), NONE();

    private final double[] segments;

    LineStyle(double... segments) {
        this.segments = segments;
    }

    public boolean isSegmented() {
        return segments.length > 0;
    }

    public double getSegmentSize(int index) {
        return segments[index % segments.length];
    }
}
