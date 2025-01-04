package com.github.jh.nvgmap;

import com.github.jh.nvgmap.components.Way;
import com.github.jh.nvgmap.gfx.LineStyle;
import com.github.jh.nvgmap.gfx.WaySchema;
import org.lwjgl.nanovg.NanoVG;

import java.util.ArrayList;
import java.util.List;

public class NVGPath implements NVGDrawable {

    private NVGPoint[] points;
    private final WaySchema schema;

    private Way way;

    public NVGPath(Way way, WaySchema schema, NVGPoint[] points) {
        this.way = way;
        this.schema = schema;
        this.points = points;

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
        int nCoords = points.length;

        NanoVG.nvgBeginPath(ctx);
        NanoVG.nvgMoveTo(ctx, points[0].x(), points[0].y());

        for(int i = 1; i < nCoords; i++) {
            if (schema.getLineStyle().isSegmented()) {

                if (i % 2 == 0) {
                    drawStroke(ctx);
                    NanoVG.nvgBeginPath(ctx);
                    NanoVG.nvgMoveTo(ctx, points[i].x(), points[i].y());
                }
            }

            NanoVG.nvgLineTo(ctx, points[i].x(), points[i].y());

        }

        drawStroke(ctx);
    }

    private void drawStroke(long ctx) {
        NanoVG.nvgStrokeColor(ctx, schema.getBorderColor());
        NanoVG.nvgStrokeWidth(ctx, schema.getWidth());
        NanoVG.nvgStroke(ctx);

        NanoVG.nvgStrokeColor(ctx, schema.getFillColor());
        NanoVG.nvgStrokeWidth(ctx, schema.getWidth() - 1f);
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
        NanoVG.nvgFillColor(ctx, schema.getFillColor());
        NanoVG.nvgFill(ctx);
    }
}
