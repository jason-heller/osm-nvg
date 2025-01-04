package com.github.jh.nvgmap.gfx;

import org.lwjgl.nanovg.NVGColor;

public class ColorUtil {

    public static final NVGColor WHITE = rgb(255, 255, 255);
    public static final NVGColor LIGHT_SILVER = rgb(220, 220, 220);
    public static final NVGColor SILVER = rgb(128, 128, 128);
    public static final NVGColor GREY = rgb(64, 64, 64);
    public static final NVGColor DARK_GREY = rgb(32, 32, 32);;
    public static final NVGColor BLACK = rgb(0, 0, 0);

    public static final NVGColor RED = rgb(255, 0, 0);
    public static final NVGColor ORANGE = rgb(255, 106, 0);
    public static final NVGColor YELLOW = rgb(255, 216, 0);
    public static final NVGColor LIME = rgb(182, 255, 0);
    public static final NVGColor GREEN = rgb(76, 255, 0);
    public static final NVGColor AQUA = rgb(0, 255, 255);
    public static final NVGColor TEAL = rgb(0, 148, 255);
    public static final NVGColor BLUE = rgb(0, 38, 255);
    public static final NVGColor INDIGO = rgb(72, 0, 255);
    public static final NVGColor VIOLET = rgb(178, 0, 255);
    public static final NVGColor MAGENTA = rgb(255, 0, 255);

    public static NVGColor rgb(int r, int g, int b) {
        return rgb(r, g, b, 255);
    }

    public static NVGColor rgb(int r, int g, int b, int a) {
        NVGColor nvgColor = NVGColor.create();

        nvgColor.r(r / 255f);
        nvgColor.g(g / 255f);
        nvgColor.b(b / 255f);
        nvgColor.a(a / 255f);

        return nvgColor;
    }

    public static NVGColor rgb(String hex)
    {
        String hexr = hex.substring(1, 2);
        hexr += hexr;
        String hexg = hex.substring(2, 3);
        hexg += hexg;
        String hexb = hex.substring(3, 4);
        hexb += hexb;

        return rgb(Integer.valueOf(hexr, 16), Integer.valueOf(hexg, 16), Integer.valueOf(hexb, 16), 255);
    }

    public static NVGColor darken(NVGColor color, int amount) {
        NVGColor outColor = NVGColor.create();

        float amountScaled = amount / 255f;

        outColor.r(Math.max(color.r() - amountScaled, 0f));
        outColor.g(Math.max(color.g() - amountScaled, 0f));
        outColor.b(Math.max(color.b() - amountScaled, 0f));
        outColor.a(color.a());

        return outColor;
    }
}
