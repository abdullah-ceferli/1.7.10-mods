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
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.client.weather;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.client.weather.SRPBlizzardDirectionClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;

public final class SRPBlizzardRenderer {
    private static final ResourceLocation SNOW_TEXTURE = new ResourceLocation("textures/environment/snow.png");

    private SRPBlizzardRenderer() {
    }

    public static void render(Minecraft mc, float partialTicks, float intensity) {
        if (mc.theWorld == null) {
            return;
        }
        Entity camera = mc.getRenderViewEntity();
        if (camera == null) {
            return;
        }
        if ((intensity = MathHelper.clamp_float((float)intensity, (float)0.0f, (float)1.0f)) <= 0.001f) {
            return;
        }
        double cameraX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * (double)partialTicks;
        double cameraY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * (double)partialTicks;
        double cameraZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * (double)partialTicks;
        float cameraYaw = camera.prevRotationYaw + MathHelper.wrapAngleTo180_float((float)(camera.rotationYaw - camera.prevRotationYaw)) * partialTicks;
        double cameraYawRadians = Math.toRadians(cameraYaw);
        double cameraRightX = Math.cos(cameraYawRadians);
        double cameraRightZ = Math.sin(cameraYawRadians);
        float blackBlend = SRPBlizzardDirectionClient.getBlackBlend(partialTicks);
        double time = (float)mc.theWorld.getTotalWorldTime() + partialTicks;
        int radius = 8 + MathHelper.floor_float((float)(intensity * 6.0f));
        int minX = MathHelper.floor_double((double)cameraX) - radius;
        int maxX = MathHelper.floor_double((double)cameraX) + radius;
        int minZ = MathHelper.floor_double((double)cameraZ) - radius;
        int maxZ = MathHelper.floor_double((double)cameraZ) + radius;
        double windAngle = time * 0.0012 + Math.sin(time * 3.7E-4) * 0.45;
        double gust = 0.72 + Math.sin(time * 0.065) * 0.18 + Math.sin(time * 0.017) * 0.1;
        double windStrength = (0.8 + (double)intensity * 2.2) * gust;
        double windX = Math.cos(windAngle) * windStrength;
        double windZ = Math.sin(windAngle) * windStrength;
        mc.getTextureManager().bindTexture(SNOW_TEXTURE);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc((int)515);
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc((int)516, (float)0.05f);
        GlStateManager.depthMask((boolean)false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        float daylight = mc.theWorld.getSunBrightness(partialTicks);
        float brightness = 0.86f + daylight * 0.14f;
        int minRenderY = MathHelper.floor_double((double)cameraY) - 10;
        int maxRenderY = MathHelper.floor_double((double)cameraY) + 14;
        double snowTime = SRPBlizzardDirectionClient.getMotionPhase(partialTicks);
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                BlockPos precipitationPos;
                int groundY;
                int startY;
                double dx = (double)x + 0.5 - cameraX;
                double dz = (double)z + 0.5 - cameraZ;
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > (double)radius || (startY = Math.max(groundY = (precipitationPos = WorldAccess.getPrecipitationHeight(mc.theWorld, new BlockPos(x, 0, z))).getY(), minRenderY)) >= maxRenderY) continue;
                float distanceFade = 1.0f - MathHelper.clamp_float((float)((float)(distance / (double)radius)), (float)0.0f, (float)1.0f);
                int laneCount = intensity > 0.7f ? 3 : 2;
                for (int lane = 0; lane < laneCount; ++lane) {
                    float randomA = SRPBlizzardRenderer.hash01(x, z, lane * 11);
                    float randomB = SRPBlizzardRenderer.hash01(x, z, lane * 11 + 1);
                    float randomC = SRPBlizzardRenderer.hash01(x, z, lane * 11 + 2);
                    float randomD = SRPBlizzardRenderer.hash01(x, z, lane * 11 + 3);
                    float randomE = SRPBlizzardRenderer.hash01(x, z, lane * 11 + 4);
                    float randomF = SRPBlizzardRenderer.hash01(x, z, lane * 11 + 5);
                    int baseSpacing = intensity > 0.55f ? 1 : 2;
                    int laneSpacing = baseSpacing + (randomD > 0.82f ? 1 : 0);
                    int phase = MathHelper.floor_float((float)(randomC * (float)(laneSpacing + 1)));
                    for (int y = startY + phase; y < maxRenderY; y += laneSpacing) {
                        double endSurfaceY;
                        if (randomF > 0.84f) continue;
                        double segmentHeight = 1.8 + (double)randomD * 2.1;
                        double fallValue = snowTime * (0.16 + (double)intensity * 0.18) + (double)randomC * 37.0 + (double)lane * 1.73;
                        double fall = SRPBlizzardRenderer.positiveModulo(fallValue, (double)laneSpacing + 0.35);
                        double yJitter = ((double)randomE - 0.5) * 0.9;
                        double bottomWorldY = (double)y + yJitter - fall;
                        double topWorldY = bottomWorldY + segmentHeight;
                        double windPhase = time * 0.045 + (double)y * 0.13 + (double)randomA * 8.0 + (double)lane * 0.91;
                        double sideways = Math.sin(windPhase) * 0.14 * (double)intensity;
                        double perpendicularWindX = -windZ;
                        double perpendicularWindZ = windX;
                        double perpendicularLength = Math.sqrt(perpendicularWindX * perpendicularWindX + perpendicularWindZ * perpendicularWindZ);
                        if (perpendicularLength > 0.001) {
                            perpendicularWindX /= perpendicularLength;
                            perpendicularWindZ /= perpendicularLength;
                        }
                        double jitterX = ((double)randomA - 0.5) * 0.92 + perpendicularWindX * sideways + (double)lane * perpendicularWindX * 0.22;
                        double jitterZ = ((double)randomB - 0.5) * 0.92 + perpendicularWindZ * sideways + (double)lane * perpendicularWindZ * 0.22;
                        double baseX = (double)x + 0.5 + jitterX;
                        double baseZ = (double)z + 0.5 + jitterZ;
                        double streakX = -windX * (0.16 + (double)intensity * 0.24);
                        double streakZ = -windZ * (0.16 + (double)intensity * 0.24);
                        double endWorldX = baseX + streakX;
                        double endWorldZ = baseZ + streakZ;
                        double baseSurfaceY = SRPBlizzardRenderer.getPrecipitationSurfaceY(mc, baseX, baseZ);
                        double surfaceY = Math.max(baseSurfaceY, endSurfaceY = SRPBlizzardRenderer.getPrecipitationSurfaceY(mc, endWorldX, endWorldZ));
                        if (topWorldY <= surfaceY + 0.02) continue;
                        if (bottomWorldY < surfaceY + 0.02) {
                            bottomWorldY = surfaceY + 0.02;
                        }
                        double middleX = baseX + streakX * 0.5;
                        double middleY = (bottomWorldY + topWorldY) * 0.5;
                        double middleZ = baseZ + streakZ * 0.5;
                        double halfWidth = 0.11 + (double)randomB * 0.12 + (double)intensity * 0.03;
                        double widthX = cameraRightX * halfWidth;
                        double widthZ = cameraRightZ * halfWidth;
                        float alpha = intensity * (0.34f + distanceFade * 0.58f) * (0.74f + randomA * 0.26f);
                        alpha = Math.min(alpha, 0.96f);
                        if (distance < 2.0) {
                            alpha *= 0.92f;
                        }
                        double x1 = baseX - cameraX;
                        double z1 = baseZ - cameraZ;
                        double x2 = endWorldX - cameraX;
                        double z2 = endWorldZ - cameraZ;
                        double y1 = bottomWorldY - cameraY;
                        double y2 = topWorldY - cameraY;
                        double textureOffset = time * 0.09 + (double)randomC * 8.0 + (double)lane * 0.37;
                        SRPBlizzardRenderer.addQuad(buffer, x1, y1, z1, x2, y2, z2, widthX, widthZ, textureOffset, brightness, blackBlend, alpha);
                    }
                }
            }
        }
        tessellator.draw();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.alphaFunc((int)516, (float)0.1f);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static double positiveModulo(double value, double modulus) {
        return value - Math.floor(value / modulus) * modulus;
    }

    private static void addQuad(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, double widthX, double widthZ, double textureOffset, float brightness, float blackBlend, float alpha) {
        float color = brightness * (1.0f - blackBlend);
        buffer.pos(x1 - widthX, y1, z1 - widthZ).tex(0.0, textureOffset).color(color, color, color, alpha).endVertex();
        buffer.pos(x1 + widthX, y1, z1 + widthZ).tex(1.0, textureOffset).color(color, color, color, alpha).endVertex();
        buffer.pos(x2 + widthX, y2, z2 + widthZ).tex(1.0, textureOffset + 1.0).color(color, color, color, alpha).endVertex();
        buffer.pos(x2 - widthX, y2, z2 - widthZ).tex(0.0, textureOffset + 1.0).color(color, color, color, alpha).endVertex();
    }

    private static double getPrecipitationSurfaceY(Minecraft mc, double x, double z) {
        BlockPos column = new BlockPos(MathHelper.floor_double((double)x), 0, MathHelper.floor_double((double)z));
        return WorldAccess.getPrecipitationHeight(mc.theWorld, column).getY();
    }

    private static float hash01(int x, int z, int salt) {
        long value = (long)x * 341873128712L + (long)z * 132897987541L + (long)salt * 42317861L;
        value ^= value >>> 13;
        value *= 1274126177L;
        value ^= value >>> 16;
        return (float)(value & 0xFFFFFFL) / 1.6777215E7f;
    }
}

