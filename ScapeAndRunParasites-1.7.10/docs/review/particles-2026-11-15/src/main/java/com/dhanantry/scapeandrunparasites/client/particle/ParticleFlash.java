/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleFlash
extends Particle {
    public static final ResourceLocation FLASH = new ResourceLocation("srparasites", "particle/flash");
    private final int R;
    private final int G;
    private final int B;

    protected ParticleFlash(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int r, int g, int b) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, (double)r, (double)g, (double)b);
        this.R = r;
        this.G = g;
        this.B = b;
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FLASH.toString()));
        this.particleScale = 6.0f;
        this.particleMaxAge = 8;
        if (ySpeedIn != 0.0 || xSpeedIn != 0.0 || zSpeedIn != 0.0) {
            this.motionX = xSpeedIn;
            this.motionY = ySpeedIn;
            this.motionZ = zSpeedIn;
        }
    }

    public int getBrightnessForRender(float p_189214_1_) {
        int i = super.getBrightnessForRender(p_189214_1_);
        return 0xF0 | i << 16;
    }

    public void onUpdate() {
        int age = this.particleAge++;
        float rot = 20.0f;
        if (age >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.particleScale = 8.0f / ((float)age * 0.5f);
        this.particleAlpha = (float)age / 0.1f;
        this.particleAngle = (float)(Math.sin(rot * (float)age) * Math.cos(rot * (float)age));
        this.particleRed = age * this.R;
        this.particleBlue = age * this.B;
        this.particleGreen = age * this.G;
    }

    public int getFXLayer() {
        return 1;
    }
}

