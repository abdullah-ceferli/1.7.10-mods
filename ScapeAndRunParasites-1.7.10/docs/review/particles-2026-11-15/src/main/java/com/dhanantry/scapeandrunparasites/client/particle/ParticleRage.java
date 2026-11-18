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

public class ParticleRage
extends Particle {
    public static final ResourceLocation RAGE1 = new ResourceLocation("srparasites", "particle/rage1");
    public static final ResourceLocation RAGE2 = new ResourceLocation("srparasites", "particle/rage2");
    public static final ResourceLocation RAGE3 = new ResourceLocation("srparasites", "particle/rage3");

    protected ParticleRage(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RAGE1.toString()));
        if (Math.random() <= 0.5) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RAGE2.toString()));
        } else if (Math.random() <= 0.5) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RAGE3.toString()));
        }
        this.particleScale = 2.0f;
        this.particleMaxAge = 30;
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
        float mow = (float)(Math.random() - Math.random());
        float meow = (float)((Math.random() - Math.random()) * 0.1);
        float mreow = (float)(Math.random() * 0.2);
        this.particleAlpha -= 0.05f;
        this.particleScale -= 0.08f;
        this.particleRed = 1.0f - mreow;
        this.particleGreen = 1.0f - mreow;
        this.particleBlue = 1.0f - mreow;
        this.posY -= (double)(0.01f + meow);
        this.posX += (double)meow;
        this.posZ += (double)meow;
        this.particleAngle = 0.01f * (float)age * mow;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    public int getFXLayer() {
        return 1;
    }
}

