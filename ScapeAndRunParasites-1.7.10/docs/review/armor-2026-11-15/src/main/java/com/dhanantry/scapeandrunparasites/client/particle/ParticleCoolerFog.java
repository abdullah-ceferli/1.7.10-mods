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

public class ParticleCoolerFog
extends Particle {
    private float speedHor;
    private float speedVer;
    private float ang;
    public static final ResourceLocation FOG_INTRO1 = new ResourceLocation("srparasites", "particle/fog/fog_intro1");
    public static final ResourceLocation FOG_INTRO2 = new ResourceLocation("srparasites", "particle/fog/fog_intro2");
    public static final ResourceLocation FOG_INTRO3 = new ResourceLocation("srparasites", "particle/fog/fog_intro3");
    public static final ResourceLocation FOG_INTRO4 = new ResourceLocation("srparasites", "particle/fog/fog_intro4");
    public static final ResourceLocation FOG_INTRO5 = new ResourceLocation("srparasites", "particle/fog/fog_intro5");
    public static final ResourceLocation FOG1 = new ResourceLocation("srparasites", "particle/fog/fog1");
    public static final ResourceLocation FOG2 = new ResourceLocation("srparasites", "particle/fog/fog2");
    public static final ResourceLocation FOG3 = new ResourceLocation("srparasites", "particle/fog/fog3");
    public static final ResourceLocation FOG4 = new ResourceLocation("srparasites", "particle/fog/fog4");

    protected ParticleCoolerFog(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO1.toString()));
        this.particleScale = (float)(10.0 + Math.random() * 0.5);
        this.particleMaxAge = 144;
        this.particleAlpha = 0.9f;
        float sXZ = 0.01f;
        this.speedHor = (float)(Math.random() * (double)sXZ - Math.random() * (double)sXZ);
        float sY = 0.005f;
        this.speedVer = (float)(Math.random() * (double)sY - Math.random() * (double)sY);
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int age = this.particleAge++;
        this.posY -= (double)this.speedVer;
        this.posX += (double)this.speedHor;
        this.posZ += (double)this.speedHor;
        if (age >= 8) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO2.toString()));
        }
        if (age >= 16) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO3.toString()));
        }
        if (age >= 24) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO4.toString()));
        }
        if (age >= 32) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO5.toString()));
        }
        if (age >= 40) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG1.toString()));
        }
        if (age >= 48) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG2.toString()));
        }
        if (age >= 56) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG3.toString()));
        }
        if (age >= 64) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG4.toString()));
        }
        if (age >= 72) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG1.toString()));
        }
        if (age >= 80) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG2.toString()));
        }
        if (age >= 88) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG3.toString()));
        }
        if (age >= 96) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG4.toString()));
        }
        if (age >= 104) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO5.toString()));
        }
        if (age >= 112) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO4.toString()));
        }
        if (age >= 120) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO3.toString()));
        }
        if (age >= 128) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO2.toString()));
        }
        if (age >= 136) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FOG_INTRO1.toString()));
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    public int getFXLayer() {
        return 1;
    }
}

