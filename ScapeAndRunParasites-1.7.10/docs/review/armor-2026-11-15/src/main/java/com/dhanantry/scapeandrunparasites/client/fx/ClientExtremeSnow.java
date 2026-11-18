/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$FogMode
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity
 *  net.minecraftforge.client.event.EntityViewRenderEvent$RenderFogEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client.fx;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.client.fx.ParticleBlizzard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class ClientExtremeSnow {
    private static boolean enabled = false;
    private static float intensity = 1.0f;
    private static boolean forceAnywhere = true;
    private static float windDeg = 30.0f;
    private static float windSpeed = 0.5f;
    private static double windX = 0.0;
    private static double windZ = 0.0;

    private ClientExtremeSnow() {
    }

    public static void setState(boolean on, float i, boolean any, float deg, float spd) {
        enabled = on;
        intensity = MathHelper.clamp_float((float)i, (float)0.0f, (float)1.0f);
        forceAnywhere = any;
        windDeg = deg % 360.0f;
        windSpeed = MathHelper.clamp_float((float)spd, (float)0.0f, (float)1.0f);
        double rad = Math.toRadians(windDeg);
        double base = 0.15 + 0.55 * (double)windSpeed;
        windX = Math.cos(rad) * base;
        windZ = Math.sin(rad) * base;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            if (mc.renderGlobal != null) {
                mc.renderGlobal.loadRenderers();
            }
            if (mc.entityRenderer != null) {
                mc.entityRenderer.updateRenderer();
            }
        }
    }

    private static float exp2DensityFor(double targetBlocks) {
        double TH = 0.02;
        return (float)Math.min(0.999, Math.sqrt(Math.log(50.0)) / Math.max(1.0E-6, targetBlocks));
    }

    @SubscribeEvent
    public static void onFogDensity(EntityViewRenderEvent.FogDensity e) {
        if (!enabled) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (!ClientExtremeSnow.isOutdoors((World)mc.theWorld, mc.thePlayer)) {
            return;
        }
        float d = ClientExtremeSnow.exp2DensityFor(10.0);
        e.setDensity(d);
        e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors e) {
        if (!enabled) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (!ClientExtremeSnow.isOutdoors((World)mc.theWorld, mc.thePlayer)) {
            return;
        }
        float push = 0.75f;
        e.setRed(e.getRed() * (1.0f - push) + push);
        e.setGreen(e.getGreen() * (1.0f - push) + push);
        e.setBlue(e.getBlue() * (1.0f - push) + push);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (!enabled || e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient w = mc.theWorld;
        if (w == null) {
            return;
        }
        EntityPlayerSP p = mc.thePlayer;
        if (p == null) {
            return;
        }
        double radius = 12.0;
        int setting = mc.gameSettings.particleSetting;
        double budget = setting == 2 ? 0.25 : (setting == 1 ? 0.55 : 1.0);
        int base = 140 + (int)(360.0f * intensity);
        int count = (int)((double)base * 1.8 * budget);
        for (int i = 0; i < count; ++i) {
            int z;
            int x = (int)(p.posX + w.rand.nextGaussian() * radius);
            BlockPos ground = WorldAccess.getPrecipitationHeight(w, new BlockPos(x, (int)p.posY, z = (int)(p.posZ + w.rand.nextGaussian() * radius)));
            if (!WorldAccess.canSeeSky(w, ground) || !forceAnywhere && !WorldAccess.canSnowAt(w, ground, false)) continue;
            double spawnY = Math.max((double)(ground.getY() + 16 + w.rand.nextInt(8)), p.posY + 16.0 + (double)w.rand.nextInt(8));
            double sx = (double)x + 0.5 + (w.rand.nextDouble() - 0.5);
            double sz = (double)z + 0.5 + (w.rand.nextDouble() - 0.5);
            double jitter = 0.03;
            double vx = windX + (w.rand.nextDouble() - 0.5) * jitter;
            double vz = windZ + (w.rand.nextDouble() - 0.5) * jitter;
            double vy = -0.22 - 0.06 * (double)intensity - w.rand.nextDouble() * 0.04;
            mc.effectRenderer.addEffect((Particle)new ParticleBlizzard((World)w, sx, spawnY, sz, vx, vy, vz, p));
        }
    }

    private static float densityForVisibility(double targetBlocks) {
        double THRESHOLD = 0.02;
        double D = Math.sqrt(Math.log(50.0)) / Math.max(1.0E-6, targetBlocks);
        return (float)MathHelper.clamp_double((double)D, (double)0.0, (double)1.0);
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent e) {
        if (!enabled) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (!ClientExtremeSnow.isOutdoors((World)mc.theWorld, mc.thePlayer)) {
            return;
        }
        float d = ClientExtremeSnow.exp2DensityFor(10.0);
        GlStateManager.setFog((GlStateManager.FogMode)GlStateManager.FogMode.EXP2);
        GlStateManager.setFogDensity((float)d);
    }

    private static boolean isOutdoors(World w, EntityPlayerSP p) {
        BlockPos[] samples;
        for (BlockPos s : samples = new BlockPos[]{new BlockPos(p.posX, p.posY, p.posZ), new BlockPos(p.posX + 4.0, p.posY, p.posZ), new BlockPos(p.posX - 4.0, p.posY, p.posZ), new BlockPos(p.posX, p.posY, p.posZ + 4.0), new BlockPos(p.posX, p.posY, p.posZ - 4.0)}) {
            BlockPos h = WorldAccess.getPrecipitationHeight(w, s);
            if (!WorldAccess.canSeeSky(w, h)) continue;
            return true;
        }
        return false;
    }
}

