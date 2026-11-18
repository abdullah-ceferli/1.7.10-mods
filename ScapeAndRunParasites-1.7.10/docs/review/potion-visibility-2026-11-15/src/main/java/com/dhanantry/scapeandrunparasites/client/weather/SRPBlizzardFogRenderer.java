/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.client.weather;

import com.dhanantry.scapeandrunparasites.client.weather.SRPBlizzardDirectionClient;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;

public final class SRPBlizzardFogRenderer {
    private static final int SHELL_COUNT = 8;
    private static final int LONGITUDE_SEGMENTS = 32;
    private static final int LATITUDE_SEGMENTS = 14;
    private static boolean shaderLookupAttempted = false;
    private static Field currentShaderNameField = null;

    private SRPBlizzardFogRenderer() {
    }

    public static void render(Minecraft mc, float partialTicks, float intensity) {
        if (mc == null || mc.theWorld == null || mc.getRenderViewEntity() == null) {
            return;
        }
        if (!SRPBlizzardFogRenderer.isShaderPackActive()) {
            return;
        }
        if ((intensity = MathHelper.clamp_float((float)intensity, (float)0.0f, (float)1.0f)) <= 0.001f) {
            return;
        }
        float strength = SRPBlizzardFogRenderer.smoothStep(intensity);
        double nearRadius = SRPBlizzardFogRenderer.lerp(14.0, 5.5, (double)strength);
        double farRadius = SRPBlizzardFogRenderer.lerp(48.0, 20.0, (double)strength);
        float baseAlpha = 0.02f + strength * 0.14f;
        float blackBlend = MathHelper.clamp_float((float)SRPBlizzardDirectionClient.getBlackBlend(partialTicks), (float)0.0f, (float)1.0f);
        float red = SRPBlizzardFogRenderer.lerp(0.78f, 0.055f, blackBlend);
        float green = SRPBlizzardFogRenderer.lerp(0.82f, 0.06f, blackBlend);
        float blue = SRPBlizzardFogRenderer.lerp(0.86f, 0.07f, blackBlend);
        float daylight = mc.theWorld.getSunBrightness(partialTicks);
        float lightMultiplier = 0.78f + daylight * 0.22f;
        red *= lightMultiplier;
        green *= lightMultiplier;
        blue *= lightMultiplier;
        double time = (float)mc.theWorld.getTotalWorldTime() + partialTicks;
        GlStateManager.enableDepth();
        GlStateManager.depthFunc((int)515);
        GlStateManager.depthMask((boolean)false);
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        for (int shell = 7; shell >= 0; --shell) {
            float shellProgress = (float)shell / 7.0f;
            double radius = SRPBlizzardFogRenderer.lerp(nearRadius, farRadius, (double)shellProgress);
            float shellAlpha = baseAlpha * (0.55f + shellProgress * 0.45f);
            shellAlpha = MathHelper.clamp_float((float)shellAlpha, (float)0.0f, (float)0.22f);
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            SRPBlizzardFogRenderer.renderShell(buffer, radius, shell, time, red, green, blue, shellAlpha);
            tessellator.draw();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static void renderShell(BufferBuilder buffer, double radius, int shellIndex, double time, float red, float green, float blue, float alpha) {
        for (int latitude = 0; latitude < 14; ++latitude) {
            double latitude0 = -1.5707963267948966 + Math.PI * (double)latitude / 14.0;
            double latitude1 = -1.5707963267948966 + Math.PI * (double)(latitude + 1) / 14.0;
            for (int longitude = 0; longitude < 32; ++longitude) {
                double longitude0 = Math.PI * 2 * (double)longitude / 32.0;
                double longitude1 = Math.PI * 2 * (double)(longitude + 1) / 32.0;
                SRPBlizzardFogRenderer.addShellVertex(buffer, radius, longitude0, latitude0, shellIndex, time, red, green, blue, alpha);
                SRPBlizzardFogRenderer.addShellVertex(buffer, radius, longitude1, latitude0, shellIndex, time, red, green, blue, alpha);
                SRPBlizzardFogRenderer.addShellVertex(buffer, radius, longitude1, latitude1, shellIndex, time, red, green, blue, alpha);
                SRPBlizzardFogRenderer.addShellVertex(buffer, radius, longitude0, latitude1, shellIndex, time, red, green, blue, alpha);
            }
        }
    }

    private static void addShellVertex(BufferBuilder buffer, double radius, double longitude, double latitude, int shellIndex, double time, float red, float green, float blue, float alpha) {
        double waveA = Math.sin(longitude * 3.0 + time * 0.01 + (double)shellIndex * 1.73);
        double waveB = Math.cos(latitude * 4.0 - time * 0.006 + (double)shellIndex * 0.91);
        double deformation = 1.0 + waveA * waveB * 0.018;
        double actualRadius = radius * deformation;
        double cosLatitude = Math.cos(latitude);
        double x = Math.cos(longitude) * cosLatitude * actualRadius;
        double y = Math.sin(latitude) * actualRadius;
        double z = Math.sin(longitude) * cosLatitude * actualRadius;
        buffer.pos(x, y, z).color(red, green, blue, alpha).endVertex();
    }

    private static boolean isShaderPackActive() {
        if (!shaderLookupAttempted) {
            shaderLookupAttempted = true;
            try {
                Class<?> shadersClass = Class.forName("net.optifine.shaders.Shaders", false, SRPBlizzardFogRenderer.class.getClassLoader());
                currentShaderNameField = shadersClass.getDeclaredField("currentShaderName");
                currentShaderNameField.setAccessible(true);
            }
            catch (Throwable ignored) {
                currentShaderNameField = null;
            }
        }
        if (currentShaderNameField == null) {
            return false;
        }
        try {
            Object value = currentShaderNameField.get(null);
            if (!(value instanceof String)) {
                return false;
            }
            String shaderName = ((String)value).trim();
            if (shaderName.isEmpty()) {
                return false;
            }
            return !"OFF".equalsIgnoreCase(shaderName);
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static float smoothStep(float value) {
        value = MathHelper.clamp_float((float)value, (float)0.0f, (float)1.0f);
        return value * value * (3.0f - 2.0f * value);
    }

    private static float lerp(float from, float to, float amount) {
        return from + (to - from) * amount;
    }

    private static double lerp(double from, double to, double amount) {
        return from + (to - from) * amount;
    }
}

