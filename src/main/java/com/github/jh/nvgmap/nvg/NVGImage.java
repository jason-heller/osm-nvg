package com.github.jh.nvgmap.nvg;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;

public class NVGImage implements NVGDrawable {
    private int image;
    private int x, y, width, height;
    private float cx, cy;
    private float alpha = 1f;
    private float angle;
    private NVGPaint imgPaint;

    public NVGImage(long ctx, int x, int y, String path) {
        setPosition(x, y);
        setImage(ctx, path);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setImage(long ctx, String path)
    {
        dispose(ctx);

        try {
            byte[] fileData = Files.readAllBytes(new File(path).toPath());
            ByteBuffer buf = MemoryUtil.memAlloc(fileData.length);
            buf.put(fileData);
            buf.flip();

            image = NanoVG.nvgCreateImageMem(ctx, 0, buf);
            MemoryUtil.memFree(buf);

            int[] w = new int[1];
            int[] h = new int[1];
            NanoVG.nvgImageSize(ctx, image, w, h);
            width = w[0];
            height = h[0];
            cx = x + width / 2f;
            cy = y + height / 2f;

            imgPaint = NVGPaint.create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(long ctx) {
        if (image == 0)
            return;

        NanoVG.nvgSave(ctx);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer imgw = stack.mallocInt(1), imgh = stack.mallocInt(1);
            NanoVG.nvgImageSize(ctx, image, imgw, imgh);

            NanoVG.nvgTranslate(ctx, cx, cy);  // Translate to the center
            NanoVG.nvgRotate(ctx, angle);      // Apply rotation
            NanoVG.nvgTranslate(ctx, -cx, -cy); // Translate back

            NanoVG.nvgImagePattern(ctx, x, y, width, height, 0f, image, alpha, imgPaint);
            NanoVG.nvgBeginPath(ctx);
            NanoVG.nvgRect(ctx, x, y, width, height);
            NanoVG.nvgFillPaint(ctx, imgPaint);
            NanoVG.nvgFill(ctx);
        }

        NanoVG.nvgRestore(ctx);
    }

    public void dispose(long ctx)
    {
        if (image == 0)
            return;

        NanoVG.nvgDeleteImage(ctx, image);
        imgPaint.free();
    }

    public void rotate(float deltaAngle) {
        angle += deltaAngle;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
