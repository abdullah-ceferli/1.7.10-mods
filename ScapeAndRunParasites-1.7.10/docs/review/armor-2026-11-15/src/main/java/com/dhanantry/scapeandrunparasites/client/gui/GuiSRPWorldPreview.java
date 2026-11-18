/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSRPWorldPreview
extends Gui {
    private static final ResourceLocation EARTH_EASY = new ResourceLocation("srparasites", "textures/gui/worldsettings/earth_easy.png");
    private static final ResourceLocation EARTH_NORMAL = new ResourceLocation("srparasites", "textures/gui/worldsettings/earth_normal.png");
    private static final ResourceLocation EARTH_HARD = new ResourceLocation("srparasites", "textures/gui/worldsettings/earth_hard.png");
    private static final ResourceLocation EARTH_IMPOSSIBLE = new ResourceLocation("srparasites", "textures/gui/worldsettings/earth_impossible.png");
    private static final ResourceLocation MOON = new ResourceLocation("srparasites", "textures/gui/worldsettings/moon.png");
    private static final ResourceLocation METEOR = new ResourceLocation("srparasites", "textures/gui/worldsettings/meteor_orbit.png");
    private static final ResourceLocation EARTH_COLD = new ResourceLocation("srparasites", "textures/gui/worldsettings/earth_cold.png");
    private static final ResourceLocation EARTH_WARM = new ResourceLocation("srparasites", "textures/gui/worldsettings/earth_warm.png");
    private static final Random RAND = new Random(923847L);

    public static void draw(Minecraft mc, int x, int y, int width, int height, int difficulty, boolean meteorEnabled, int starType, float partialTicks) {
        long timeMs = Minecraft.getSystemTime();
        float time = (float)timeMs / 1000.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiSRPWorldPreview.drawRect((int)x, (int)y, (int)(x + width), (int)(y + height), (int)-16513262);
        GuiSRPWorldPreview.drawRect((int)(x - 1), (int)(y - 1), (int)(x + width + 1), (int)y, (int)-11511957);
        GuiSRPWorldPreview.drawRect((int)(x - 1), (int)(y + height), (int)(x + width + 1), (int)(y + height + 1), (int)-15394525);
        GuiSRPWorldPreview.drawRect((int)(x - 1), (int)y, (int)x, (int)(y + height), (int)-11511957);
        GuiSRPWorldPreview.drawRect((int)(x + width), (int)y, (int)(x + width + 1), (int)(y + height), (int)-15394525);
        GuiSRPWorldPreview.enableScissor(mc, x, y, width, height);
        GuiSRPWorldPreview.drawStars(x, y, width, height, time);
        GuiSRPWorldPreview.drawPlanetSystem(mc, x, y, width, height, difficulty, meteorEnabled, starType, time, partialTicks);
        GL11.glDisable((int)3089);
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.popMatrix();
    }

    private static void drawStars(int x, int y, int width, int height, float time) {
        int baseY;
        int baseX;
        int i;
        RAND.setSeed(923847L);
        for (i = 0; i < 70; ++i) {
            baseX = RAND.nextInt(Math.max(1, width));
            baseY = RAND.nextInt(Math.max(1, height));
            float depth = 0.35f + RAND.nextFloat() * 0.65f;
            float speed = 0.05f + depth * 0.18f;
            float phase = RAND.nextFloat() * ((float)Math.PI * 2);
            boolean big = RAND.nextInt(12) == 0;
            int brightness = 185 + RAND.nextInt(45);
            float twinkle = 0.6f + 0.4f * (float)Math.sin(time * (0.2f + depth * 0.18f) + phase);
            int alpha = (int)(6.0f + depth * 12.0f + twinkle * 14.0f);
            int sx = x + GuiSRPWorldPreview.wrap((int)((float)baseX - time * speed * 3.0f), width);
            int sy = y + baseY;
            GuiSRPWorldPreview.drawPrettyStar(sx, sy, brightness, alpha, big);
        }
        for (i = 0; i < 3; ++i) {
            baseX = RAND.nextInt(Math.max(1, width));
            baseY = RAND.nextInt(Math.max(1, height));
            float speed = 0.2f + RAND.nextFloat() * 0.15f;
            int sx = x + GuiSRPWorldPreview.wrap((int)((float)baseX - time * speed * 4.0f), width);
            int sy = y + baseY;
            int alpha = 4 + RAND.nextInt(4);
            int len = 6 + RAND.nextInt(5);
            GuiSRPWorldPreview.drawRect((int)(sx - len / 2), (int)sy, (int)(sx + len / 2), (int)(sy + 1), (int)GuiSRPWorldPreview.argb(alpha, 190, 205, 235));
        }
    }

    private static void drawPrettyStar(int x, int y, int brightness, int alpha, boolean big) {
        int outerAlpha = Math.max(2, alpha / 6);
        int glowAlpha = Math.max(4, alpha / 3);
        int coreAlpha = Math.min(255, alpha);
        int outer = GuiSRPWorldPreview.argb(outerAlpha, brightness, brightness, 255);
        int glow = GuiSRPWorldPreview.argb(glowAlpha, brightness, brightness, 255);
        int core = GuiSRPWorldPreview.argb(coreAlpha, 255, 255, 255);
        GuiSRPWorldPreview.drawRect((int)(x - 1), (int)y, (int)(x + 2), (int)(y + 1), (int)outer);
        GuiSRPWorldPreview.drawRect((int)x, (int)(y - 1), (int)(x + 1), (int)(y + 2), (int)outer);
        if (big) {
            GuiSRPWorldPreview.drawRect((int)(x - 1), (int)(y - 1), (int)(x + 2), (int)(y + 2), (int)glow);
        }
        GuiSRPWorldPreview.drawRect((int)x, (int)y, (int)(x + 1), (int)(y + 1), (int)core);
    }

    private static void drawTexturedWithAlpha(Minecraft mc, ResourceLocation tex, int x, int y, int w, int h, float alpha) {
        mc.getTextureManager().bindTexture(tex);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        GuiSRPWorldPreview.drawModalRectWithCustomSizedTexture((int)x, (int)y, (float)0.0f, (float)0.0f, (int)w, (int)h, (float)w, (float)h);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableAlpha();
    }

    private static void drawTexturedWithAlphaRotated(Minecraft mc, ResourceLocation tex, float centerX, float centerY, int w, int h, float alpha, float angleDeg) {
        mc.getTextureManager().bindTexture(tex);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)centerX, (float)centerY, (float)0.0f);
        GlStateManager.rotate((float)angleDeg, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((float)((float)(-w) / 2.0f), (float)((float)(-h) / 2.0f), (float)0.0f);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        GuiSRPWorldPreview.drawModalRectWithCustomSizedTexture((int)0, (int)0, (float)0.0f, (float)0.0f, (int)w, (int)h, (float)w, (float)h);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    private static int argb(int a, int r, int g, int b) {
        a = GuiSRPWorldPreview.clamp(a);
        r = GuiSRPWorldPreview.clamp(r);
        g = GuiSRPWorldPreview.clamp(g);
        b = GuiSRPWorldPreview.clamp(b);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static int clamp(int v) {
        if (v < 0) {
            return 0;
        }
        if (v > 255) {
            return 255;
        }
        return v;
    }

    private static void drawPlanetSystem(Minecraft mc, int x, int y, int width, int height, int difficulty, boolean meteorEnabled, int starType, float time, float partialTicks) {
        float cx = (float)x + (float)width / 2.0f;
        float cy = (float)y + (float)height / 2.0f + 4.0f;
        int earthSize = Math.min(width, height) / 2;
        earthSize = Math.max(44, Math.min(earthSize, 70));
        ResourceLocation earth = GuiSRPWorldPreview.getEarthTexture(difficulty, starType);
        GuiSRPWorldPreview.drawTexturedWithAlpha(mc, earth, (int)(cx - (float)earthSize / 2.0f), (int)(cy - (float)earthSize / 2.0f), earthSize, earthSize, 0.96f);
        float moonAngle = time * 0.65f;
        float moonOrbitX = (float)earthSize / 2.0f + 22.0f;
        float moonOrbitY = (float)earthSize / 3.0f + 12.0f;
        float moonX = cx + (float)Math.cos(moonAngle) * moonOrbitX;
        float moonY = cy + (float)Math.sin(moonAngle) * moonOrbitY;
        int moonSize = Math.max(10, earthSize / 5);
        float moonRotationDeg = (float)Math.toDegrees(Math.atan2(cy - moonY, cx - moonX));
        GuiSRPWorldPreview.drawTexturedWithAlphaRotated(mc, MOON, moonX, moonY, moonSize, moonSize, 0.82f, moonRotationDeg);
        if (meteorEnabled) {
            float meteorAngle = time * -1.15f + 1.7f;
            float meteorOrbitX = (float)earthSize / 2.0f + 34.0f;
            float meteorOrbitY = (float)earthSize / 3.0f + 20.0f;
            float meteorX = cx + (float)Math.cos(meteorAngle) * meteorOrbitX;
            float meteorY = cy + (float)Math.sin(meteorAngle) * meteorOrbitY;
            int meteorSize = Math.max(8, earthSize / 7);
            float meteorRotationDeg = (float)Math.toDegrees(Math.atan2(cy - meteorY, cx - meteorX));
            GuiSRPWorldPreview.drawTexturedWithAlphaRotated(mc, METEOR, meteorX, meteorY, meteorSize, meteorSize, 0.72f, meteorRotationDeg);
        }
    }

    private static ResourceLocation getEarthTexture(int difficulty, int starType) {
        if (starType == 1) {
            return EARTH_COLD;
        }
        if (starType == 2) {
            return EARTH_WARM;
        }
        switch (difficulty) {
            case 0: {
                return EARTH_EASY;
            }
            case 2: {
                return EARTH_HARD;
            }
            case 3: {
                return EARTH_IMPOSSIBLE;
            }
        }
        return EARTH_NORMAL;
    }

    private static int wrap(int value, int max) {
        if (max <= 0) {
            return 0;
        }
        if ((value %= max) < 0) {
            value += max;
        }
        return value;
    }

    private static void enableScissor(Minecraft mc, int x, int y, int width, int height) {
        ScaledResolution scaled = new ScaledResolution(mc);
        int scale = scaled.getScaleFactor();
        int scissorX = x * scale;
        int scissorY = mc.displayHeight - (y + height) * scale;
        int scissorW = width * scale;
        int scissorH = height * scale;
        GL11.glEnable((int)3089);
        GL11.glScissor((int)scissorX, (int)scissorY, (int)scissorW, (int)scissorH);
    }
}

