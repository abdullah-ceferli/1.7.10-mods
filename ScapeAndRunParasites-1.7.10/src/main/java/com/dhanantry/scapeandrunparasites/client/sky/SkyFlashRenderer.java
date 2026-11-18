/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client.sky;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class SkyFlashRenderer {
    private static final ResourceLocation TEX = new ResourceLocation("srparasites:textures/entity/monster/sky_flash.png");
    private static final List<Flash> FLASHES = new ArrayList<Flash>();

    public static void trigger(int durationTicks, double span, double altitude) {
        SkyFlashRenderer.trigger(durationTicks, span, altitude, -15.0f);
    }

    public static void trigger(int durationTicks, double span, double altitude, float bankDeg) {
        SkyFlashRenderer.triggerOffset(durationTicks, span, altitude, bankDeg, 0.0, 0.0, 0.0);
    }

    public static void triggerOffset(int durationTicks, double span, double altitude, float bankDeg, double yawOffsetDeg, double forwardOffset, double lateralOffset) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        Vec3d cam = new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        Vec3d upW = new Vec3d(0.0, 1.0, 0.0);
        Vec3d look = mc.thePlayer.getLook(1.0f).normalize();
        Vec3d right = upW.crossProduct(look).normalize();
        if (right.lengthSquared() < 1.0E-6) {
            right = new Vec3d(1.0, 0.0, 0.0);
        }
        Vec3d lookR = SkyFlashRenderer.rotateYaw(look, yawOffsetDeg);
        Vec3d rightR = SkyFlashRenderer.rotateYaw(right, yawOffsetDeg);
        double forwardDist = 260.0 + forwardOffset;
        Vec3d center = cam.add(lookR.scale(forwardDist)).addVector(0.0, altitude, 0.0).add(rightR.scale(lateralOffset));
        Vec3d start = center.subtract(rightR.scale(span * 0.5));
        Vec3d end = center.add(rightR.scale(span * 0.5));
        FLASHES.add(new Flash(mc.theWorld.getTotalWorldTime(), durationTicks, start, end, bankDeg));
    }

    private static Vec3d rotateYaw(Vec3d v, double deg) {
        double r = Math.toRadians(deg);
        double c = Math.cos(r);
        double s = Math.sin(r);
        return new Vec3d(v.xCoord * c - v.zCoord * s, v.yCoord, v.xCoord * s + v.zCoord * c);
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderWorldLastEvent e) {
        if (FLASHES.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            FLASHES.clear();
            return;
        }
        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((int)770, (int)1);
        mc.getTextureManager().bindTexture(TEX);
        float pt = e.getPartialTicks();
        Iterator<Flash> it = FLASHES.iterator();
        while (it.hasNext()) {
            Flash f = it.next();
            double now = (float)mc.theWorld.getTotalWorldTime() + pt;
            float t = (float)((now - f.startTick) / f.durationTicks);
            if (t >= 1.0f) {
                it.remove();
                continue;
            }
            if (t < 0.0f) continue;
            Vec3d pos = SkyFlashRenderer.lerp(f.start, f.end, t);
            Vec3d upW = new Vec3d(0.0, 1.0, 0.0);
            Vec3d tangent = f.end.subtract(f.start).normalize();
            Vec3d n = upW.crossProduct(tangent);
            if (n.lengthSquared() < 1.0E-6) {
                n = new Vec3d(1.0, 0.0, 0.0).crossProduct(tangent);
            }
            n = n.normalize();
            Vec3d b = tangent.crossProduct(n).normalize();
            if (Math.abs(f.bankDeg) > 0.001f) {
                double r = Math.toRadians(f.bankDeg);
                double cs = Math.cos(r);
                double sn = Math.sin(r);
                Vec3d nR = n.scale(cs).add(b.scale(sn));
                Vec3d bR = b.scale(cs).subtract(n.scale(sn));
                n = nR;
                b = bR;
            }
            float halfLen = 24.0f;
            float halfThick = 6.0f;
            int alpha = Math.max(0, Math.min(255, (int)(255.0f * (1.0f - t))));
            SkyFlashRenderer.drawRibbonQuad(pos, tangent, n, b, 0.0, halfLen, halfThick, alpha, camX, camY, camZ);
            SkyFlashRenderer.drawRibbonQuad(pos, tangent, n, b, 60.0, halfLen, halfThick, alpha, camX, camY, camZ);
            SkyFlashRenderer.drawRibbonQuad(pos, tangent, n, b, 120.0, halfLen, halfThick, alpha, camX, camY, camZ);
        }
        GlStateManager.depthMask((boolean)true);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static void drawRibbonQuad(Vec3d pos, Vec3d tangent, Vec3d n, Vec3d b, double phiDeg, float halfLen, float halfThick, int alpha, double camX, double camY, double camZ) {
        double r = Math.toRadians(phiDeg);
        double cs = Math.cos(r);
        double sn = Math.sin(r);
        Vec3d u = n.scale(cs).add(b.scale(sn));
        Vec3d p0 = pos.add(tangent.scale((double)(-halfLen))).add(u.scale((double)(-halfThick)));
        Vec3d p1 = pos.add(tangent.scale((double)halfLen)).add(u.scale((double)(-halfThick)));
        Vec3d p2 = pos.add(tangent.scale((double)halfLen)).add(u.scale((double)halfThick));
        Vec3d p3 = pos.add(tangent.scale((double)(-halfLen))).add(u.scale((double)halfThick));
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(p0.xCoord - camX, p0.yCoord - camY, p0.zCoord - camZ).tex(0.0, 1.0).color(255, 255, 255, alpha).endVertex();
        buf.pos(p1.xCoord - camX, p1.yCoord - camY, p1.zCoord - camZ).tex(1.0, 1.0).color(255, 255, 255, alpha).endVertex();
        buf.pos(p2.xCoord - camX, p2.yCoord - camY, p2.zCoord - camZ).tex(1.0, 0.0).color(255, 255, 255, alpha).endVertex();
        buf.pos(p3.xCoord - camX, p3.yCoord - camY, p3.zCoord - camZ).tex(0.0, 0.0).color(255, 255, 255, alpha).endVertex();
        tess.draw();
    }

    private static Vec3d lerp(Vec3d a, Vec3d b, float t) {
        return a.scale((double)(1.0f - t)).add(b.scale((double)t));
    }

    private SkyFlashRenderer() {
    }

    private static final class Flash {
        final double startTick;
        final double durationTicks;
        final Vec3d start;
        final Vec3d end;
        final float bankDeg;

        Flash(double startTick, double durationTicks, Vec3d start, Vec3d end, float bankDeg) {
            this.startTick = startTick;
            this.durationTicks = durationTicks;
            this.start = start;
            this.end = end;
            this.bankDeg = bankDeg;
        }
    }
}

