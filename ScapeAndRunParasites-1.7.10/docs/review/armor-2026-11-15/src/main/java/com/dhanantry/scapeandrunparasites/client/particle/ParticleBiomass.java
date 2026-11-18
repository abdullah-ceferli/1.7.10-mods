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

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ParticleBiomass
extends Particle {
    public static final ResourceLocation BIOMASS1 = new ResourceLocation("srparasites", "particle/biomass1");
    public static final ResourceLocation BIOMASS2 = new ResourceLocation("srparasites", "particle/biomass2");
    public static final ResourceLocation BIOMASS3 = new ResourceLocation("srparasites", "particle/biomass3");
    public static final ResourceLocation BIOMASS4 = new ResourceLocation("srparasites", "particle/biomass4");

    protected ParticleBiomass(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        ParticleSpawner.spawnParticle(SRPEnumParticle.DOT, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0, 165, 255, 0);
        ParticleSpawner.spawnParticle(SRPEnumParticle.DOT, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0, 60, 229, 27);
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(BIOMASS1.toString()));
        if (Math.random() <= 0.5) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(BIOMASS2.toString()));
        } else if (Math.random() <= 0.5) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(BIOMASS3.toString()));
        } else if (Math.random() <= 0.5) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(BIOMASS4.toString()));
        }
        this.particleScale = 3.0f;
        this.particleMaxAge = 45;
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
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
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        if (this.particleAge >= 25) {
            this.particleScale -= 0.1f;
            this.particleRed -= 0.03f;
            this.particleGreen -= 0.02f;
            this.particleBlue -= 0.025f;
        }
        this.motionY += 0.005;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)0.98f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)0.98f;
        if (this.onGround) {
            this.motionX *= (double)0.7f;
            this.motionZ *= (double)0.7f;
        }
    }

    public int getFXLayer() {
        return 1;
    }
}

