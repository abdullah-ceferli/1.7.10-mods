/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.Vec3d
 */
package com.dhanantry.scapeandrunparasites.client.fx;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;

public final class ParticleVengeance {
    private static final Random RAND = new Random();

    private ParticleVengeance() {
    }

    private static WorldClient world() {
        return Minecraft.getMinecraft().theWorld;
    }

    public static void electricSparksAround(Vec3d center, float radius, int count) {
        WorldClient w = ParticleVengeance.world();
        if (w == null) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            double ang = RAND.nextDouble() * Math.PI * 2.0;
            double r = (double)radius * (0.2 + RAND.nextDouble() * 0.8);
            double px = center.xCoord + Math.cos(ang) * r + (RAND.nextDouble() - 0.5) * 0.2;
            double py = center.yCoord + 0.2 + RAND.nextDouble() * 1.4;
            double pz = center.zCoord + Math.sin(ang) * r + (RAND.nextDouble() - 0.5) * 0.2;
            double vx = (RAND.nextDouble() - 0.5) * 0.8;
            double vy = (RAND.nextDouble() - 0.5) * 0.3;
            double vz = (RAND.nextDouble() - 0.5) * 0.8;
            w.spawnParticle(EnumParticleTypes.SPELL_MOB, px, py, pz, 0.65, 0.1, 0.9, new int[0]);
            if (RAND.nextInt(3) == 0) {
                w.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, vx, vy, vz, new int[0]);
            }
            if (RAND.nextInt(4) != 0) continue;
            w.spawnParticle(EnumParticleTypes.END_ROD, px, py, pz, vx * 0.2, vy * 0.2, vz * 0.2, new int[0]);
        }
    }

    public static void impactDust(Vec3d hitPos, int count) {
        WorldClient w = ParticleVengeance.world();
        if (w == null) {
            return;
        }
        int blockStateId = 1;
        int[] args = new int[]{blockStateId};
        for (int i = 0; i < count; ++i) {
            double px = hitPos.xCoord + (RAND.nextDouble() - 0.5) * 0.4;
            double py = hitPos.yCoord + RAND.nextDouble() * 0.2;
            double pz = hitPos.zCoord + (RAND.nextDouble() - 0.5) * 0.4;
            double vx = (RAND.nextDouble() - 0.5) * 0.35;
            double vy = 0.1 + RAND.nextDouble() * 0.25;
            double vz = (RAND.nextDouble() - 0.5) * 0.35;
            w.spawnParticle(EnumParticleTypes.BLOCK_DUST, px, py, pz, vx, vy, vz, args);
            if (!RAND.nextBoolean()) continue;
            w.spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, px, py + 0.1, pz, 0.0, 0.05, 0.0, new int[0]);
        }
    }

    public static void heavyBleedAround(Vec3d center, int count) {
        WorldClient w = ParticleVengeance.world();
        if (w == null) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            double px = center.xCoord + (RAND.nextDouble() - 0.5) * 0.8;
            double py = center.yCoord + 0.3 + RAND.nextDouble() * 1.2;
            double pz = center.zCoord + (RAND.nextDouble() - 0.5) * 0.8;
            w.spawnParticle(EnumParticleTypes.SPELL_MOB, px, py, pz, 0.55, 0.02, 0.02, new int[0]);
            if (RAND.nextInt(4) != 0) continue;
            w.spawnParticle(EnumParticleTypes.DRIP_LAVA, px, py, pz, 0.0, 0.0, 0.0, new int[0]);
        }
    }

    public static void lightningExplosion(Vec3d pos) {
        double vz;
        double vy;
        double vx;
        int i;
        WorldClient w = ParticleVengeance.world();
        if (w == null) {
            return;
        }
        w.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, pos.xCoord, pos.yCoord + 0.5, pos.zCoord, 0.0, 0.0, 0.0, new int[0]);
        for (i = 0; i < 12; ++i) {
            vx = (RAND.nextDouble() - 0.5) * 0.6;
            vy = RAND.nextDouble() * 0.4;
            vz = (RAND.nextDouble() - 0.5) * 0.6;
            w.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.xCoord, pos.yCoord + 0.3, pos.zCoord, vx, vy, vz, new int[0]);
        }
        for (i = 0; i < 18; ++i) {
            vx = (RAND.nextDouble() - 0.5) * 1.1;
            vy = RAND.nextDouble() * 0.7;
            vz = (RAND.nextDouble() - 0.5) * 1.1;
            w.spawnParticle(EnumParticleTypes.CRIT_MAGIC, pos.xCoord, pos.yCoord + 0.6, pos.zCoord, vx, vy, vz, new int[0]);
        }
    }
}

