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

public class ParticleWind
extends Particle {
    public static final ResourceLocation WIND1 = new ResourceLocation("srparasites", "particle/wind/wind1");
    public static final ResourceLocation WIND2 = new ResourceLocation("srparasites", "particle/wind/wind2");
    public static final ResourceLocation WIND3 = new ResourceLocation("srparasites", "particle/wind/wind3");
    public static final ResourceLocation WIND4 = new ResourceLocation("srparasites", "particle/wind/wind4");
    public static final ResourceLocation WIND5 = new ResourceLocation("srparasites", "particle/wind/wind5");
    public static final ResourceLocation WIND6 = new ResourceLocation("srparasites", "particle/wind/wind6");
    public static final ResourceLocation WIND7 = new ResourceLocation("srparasites", "particle/wind/wind7");
    public static final ResourceLocation WIND8 = new ResourceLocation("srparasites", "particle/wind/wind8");
    public static final ResourceLocation WIND9 = new ResourceLocation("srparasites", "particle/wind/wind9");
    public static final ResourceLocation WIND10 = new ResourceLocation("srparasites", "particle/wind/wind10");
    public static final ResourceLocation WIND11 = new ResourceLocation("srparasites", "particle/wind/wind11");
    public static final ResourceLocation WIND12 = new ResourceLocation("srparasites", "particle/wind/wind12");
    public static final ResourceLocation WIND13 = new ResourceLocation("srparasites", "particle/wind/wind13");

    protected ParticleWind(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND1.toString()));
        this.particleScale = 2.0f;
        this.particleMaxAge = 52;
        this.canCollide = true;
        this.particleAlpha = 0.5f;
        if (ySpeedIn != 0.0 || xSpeedIn != 0.0 || zSpeedIn != 0.0) {
            this.motionX = xSpeedIn;
            this.motionY = ySpeedIn;
            this.motionZ = zSpeedIn;
        }
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        int age = this.particleAge++;
        this.posY += (double)age * this.motionY;
        this.posX += this.motionX + (Math.random() * ((double)age * 0.005) - Math.random() * ((double)age * 0.005));
        this.posZ += this.motionZ + (Math.random() * ((double)age * 0.005) - Math.random() * ((double)age * 0.005));
        if (age >= 4) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND2.toString()));
        }
        if (age >= 8) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND3.toString()));
        }
        if (age >= 12) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND4.toString()));
        }
        if (age >= 16) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND5.toString()));
        }
        if (age >= 20) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND6.toString()));
        }
        if (age >= 24) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND7.toString()));
        }
        if (age >= 28) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND8.toString()));
        }
        if (age >= 32) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND9.toString()));
        }
        if (age >= 36) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND10.toString()));
        }
        if (age >= 40) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND11.toString()));
        }
        if (age >= 44) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND12.toString()));
        }
        if (age >= 48) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WIND13.toString()));
        }
    }

    public int getFXLayer() {
        return 1;
    }
}

