package com.github.jh.nvgmap.nvg;

import com.github.jh.nvgmap.components.Way;
import com.github.jh.nvgmap.gfx.LineStyle;
import com.github.jh.nvgmap.gfx.WaySchema;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.util.ArrayList;
import java.util.List;

public class NVGPath implements NVGDrawable {

    private NVGPoint[] points;
    private final WaySchema schema;

    private Way way;
    private int secondaryPtIndex = -1;

    private float scale;

    public NVGPath(Way way, WaySchema schema, NVGPoint[] points, float scale) {
        this.way = way;
        this.schema = schema;
        this.points = points;
        this.scale = (float)Math.min(scale, 1f);

        LineStyle lineStyle = schema.getLineStyle();

        if (lineStyle.isSegmented()) {
            int nCoords = points.length;

            List<NVGPoint> newPoints = new ArrayList<>(nCoords);
            double lenSqr = 0.0;
            double dx, dy;
            double dashSize;

            NVGPoint current, last;
            last = points[0];

            newPoints.add(last);

            for(int i = 1; i < nCoords; i++) {
                current = points[i];

                dx = (current.x() - last.x());
                dy = (current.y() - last.y());
                lenSqr += dx * dx + dy * dy;

                dashSize = lineStyle.getSegmentSize(newPoints.size());

                if (lenSqr >= dashSize * dashSize) {

                    double len = Math.sqrt(lenSqr);

                    dx = (dx / len) * dashSize;
                    dy = (dy / len) * dashSize;

                    float newX = last.x() + (float)dx;
                    float newY = last.y() + (float)dy;

                    NVGPoint interPoint = new NVGPoint(newX, newY);
                    newPoints.add(interPoint);
                    lenSqr = 0.0;

                    last = interPoint;
                    i--;
                }
            }

            if (lineStyle == LineStyle.DASH_SOLID) {
                secondaryPtIndex = newPoints.size();

                for(int i = 0; i < nCoords; i++)
                    newPoints.add(points[i]);
            }

            this.points = new NVGPoint[newPoints.size()];
            newPoints.toArray(this.points);
        }
    }

    @Override
    public void draw(long ctx) {
        if (schema.isArea()) {
            drawArea(ctx);
        }

        if (schema.getLineStyle() != LineStyle.NONE)
            drawLine(ctx);
    }

    private void drawLine(long ctx) {
        final NVGColor border = schema.getPrimaryColor();
        final NVGColor fill = schema.getSecondaryColor();

        if (secondaryPtIndex > 0) {
            drawSegment(ctx, secondaryPtIndex, points.length, fill, fill);
            drawSegment(ctx, 0, secondaryPtIndex, fill, border);
        } else {
            drawSegment(ctx, 0, points.length, fill, border);
        }
    }

    private void drawSegment(long ctx, int start, int length, NVGColor fillColor, NVGColor borderColor) {
        NanoVG.nvgBeginPath(ctx);
        NanoVG.nvgMoveTo(ctx, points[start].x(), points[start].y());

        for(int i = start + 1; i < length; i++) {
            if (schema.getLineStyle().isSegmented()) {

                if (i % 2 == 0) {
                    drawStroke(ctx, fillColor, borderColor);
                    NanoVG.nvgBeginPath(ctx);
                    NanoVG.nvgMoveTo(ctx, points[i].x(), points[i].y());
                }
            }

            NanoVG.nvgLineTo(ctx, points[i].x(), points[i].y());

        }

        drawStroke(ctx, fillColor, borderColor);
    }

    private void drawStroke(long ctx, NVGColor fillColor, NVGColor borderColor) {
        NanoVG.nvgStrokeColor(ctx, fillColor);
        NanoVG.nvgStrokeWidth(ctx, schema.getWidth() * scale);
        NanoVG.nvgStroke(ctx);

        NanoVG.nvgStrokeColor(ctx, borderColor);
        NanoVG.nvgStrokeWidth(ctx, schema.getWidth() * scale - 1f);
        NanoVG.nvgStroke(ctx);
    }

    private void drawArea(long ctx) {
        int nCoords = points.length;

        NanoVG.nvgBeginPath(ctx);
        NanoVG.nvgMoveTo(ctx, points[0].x(), points[0].y());

        for (int i = 1; i < nCoords; i++) {
            NanoVG.nvgLineTo(ctx, points[i].x(), points[i].y());
        }

        NanoVG.nvgClosePath(ctx);
        NanoVG.nvgFillColor(ctx, schema.getPrimaryColor());
        NanoVG.nvgFill(ctx);
    }
}
