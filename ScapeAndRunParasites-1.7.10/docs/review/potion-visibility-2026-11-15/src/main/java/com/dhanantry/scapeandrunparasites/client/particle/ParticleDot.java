/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ParticleDot
extends Particle {
    public static final ResourceLocation DOT = new ResourceLocation("srparasites", "particle/dot");

    protected ParticleDot(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float r, float g, float b) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(DOT.toString()));
        this.particleScale = 2.0f;
        this.particleMaxAge = 40;
        this.particleAlpha = 0.0f;
        this.particleRed = r / 255.0f;
        this.particleGreen = g / 255.0f;
        this.particleBlue = b / 255.0f;
        if (ySpeedIn != 0.0 || xSpeedIn != 0.0 || zSpeedIn != 0.0) {
            this.motionX = xSpeedIn;
            this.motionY = ySpeedIn;
            this.motionZ = zSpeedIn;
        }
    }

    public int getBrightnessForRender(float p_189214_1_) {
        float f = ((float)this.particleAge + p_189214_1_) / (float)this.particleMaxAge;
        f = MathHelper.clamp_float((float)f, (float)0.0f, (float)1.0f);
        int i = super.getBrightnessForRender(p_189214_1_);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((j += (int)(f * 15.0f * 16.0f)) > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int age = this.particleAge++;
        float maxAge = this.particleMaxAge;
        this.posY = this.prevPosY + (double)age * 0.01;
        this.particleAlpha = age > 20 ? (maxAge - (float)age) / maxAge : (age <= 5 & age != 1 ? ((float)age - maxAge) / maxAge : 1.0f);
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    public int getFXLayer() {
        return 1;
    }
}

