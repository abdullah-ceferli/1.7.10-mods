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

public class ParticleBlood
extends Particle {
    public static final ResourceLocation BLOOD1 = new ResourceLocation("srparasites", "particle/blood1");
    public static final ResourceLocation BLOOD2 = new ResourceLocation("srparasites", "particle/blood2");
    public static final ResourceLocation BLOOD_LAND = new ResourceLocation("srparasites", "particle/blood_land");
    private final int type;
    private final float mroew = (float)((Math.random() - Math.random()) * 0.05);

    protected ParticleBlood(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int r) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.type = r;
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(BLOOD1.toString()));
        this.particleScale = 2.0f;
        this.particleMaxAge = 40;
        this.particleAlpha = 0.9f;
        if (this.type == 1) {
            this.motionX = this.mroew;
            this.motionY = ySpeedIn;
            this.motionZ = this.mroew;
        }
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int age = this.particleAge++;
        this.move(this.motionX, this.motionY, this.motionZ);
        if (this.type == 0) {
            this.motionY -= 0.5;
        }
        if (this.type == 1) {
            this.setMaxAge(80);
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY = 0.31;
            this.motionX += (double)this.mroew * 0.5;
            this.motionZ += (double)this.mroew * 0.5;
            this.particleGravity = 0.04f;
        }
        if (age >= 4) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(BLOOD2.toString()));
        }
        if (this.onGround) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(BLOOD_LAND.toString()));
            this.motionX /= 10.0;
            this.motionZ /= 10.0;
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    public int getFXLayer() {
        return 1;
    }
}

