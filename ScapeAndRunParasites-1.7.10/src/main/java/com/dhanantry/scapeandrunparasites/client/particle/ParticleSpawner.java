/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.particle;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleBiomass;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleBlood;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleCoolerFog;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleDot;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleEen;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleFlash;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleFog;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleMultipleGore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleRGBSmoke;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleRHappy;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleRage;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleWind;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleSpawner {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static Particle spawnParticle(SRPEnumParticle type, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int r, int g, int b) {
        if (mc != null && mc.getRenderViewEntity() != null && ParticleSpawner.mc.effectRenderer != null) {
            int canSpawn = Minecraft.getMinecraft().gameSettings.particleSetting;
            if (canSpawn >= 1) {
                return null;
            }
            Particle part = null;
            if (type == SRPEnumParticle.FOG) {
                part = new ParticleFog((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r, g, b);
            } else if (type == SRPEnumParticle.SPORE) {
                part = new ParticleSpore((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.GCLOUD) {
                part = new ParticleRGBSmoke((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r, g, b);
            } else if (type == SRPEnumParticle.GSPLASH) {
                part = new ParticleMultipleGore((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r);
            } else if (type == SRPEnumParticle.RHAPPY) {
                part = new ParticleRHappy((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.BIOMASS) {
                part = new ParticleBiomass((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.EEN) {
                part = new ParticleEen((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.FLASH) {
                part = new ParticleFlash((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r, g, b);
            } else if (type == SRPEnumParticle.DOT) {
                part = new ParticleDot((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r, g, b);
            } else if (type == SRPEnumParticle.WIND) {
                part = new ParticleWind((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.COOLER_FOG) {
                part = new ParticleCoolerFog((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.RAGE) {
                part = new ParticleRage((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            }
            if (type == SRPEnumParticle.BLOOD) {
                part = new ParticleBlood((World)ParticleSpawner.mc.theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r);
            }
            if (part != null) {
                ParticleSpawner.mc.effectRenderer.addEffect(part);
            }
            return part;
        }
        return null;
    }
}

