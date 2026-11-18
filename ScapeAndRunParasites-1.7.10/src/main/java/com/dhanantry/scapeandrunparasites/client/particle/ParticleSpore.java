/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ParticleSpore
extends Particle {
    public static final ResourceLocation WHITE1 = new ResourceLocation("srparasites", "particle/spore/spore_white1");
    public static final ResourceLocation WHITE2 = new ResourceLocation("srparasites", "particle/spore/spore_white2");
    public static final ResourceLocation WHITE3 = new ResourceLocation("srparasites", "particle/spore/spore_white3");
    public static final ResourceLocation WHITE4 = new ResourceLocation("srparasites", "particle/spore/spore_white4");

    public ParticleSpore(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn + 0.0, yCoordIn, zCoordIn + 0.0, xSpeedIn, ySpeedIn, zSpeedIn);
        this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WHITE1.toString()));
        if (Math.random() <= 0.5) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WHITE2.toString()));
        } else if (Math.random() <= 0.5) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WHITE3.toString()));
        } else if (Math.random() <= 0.5) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(WHITE4.toString()));
        }
        this.motionX = xSpeedIn * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.motionY = ySpeedIn * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.motionZ = zSpeedIn * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.particleScale = 0.0f;
        this.particleMaxAge = 200;
        this.particleGravity = 0.0f;
        float color = (float)(1.0 - Math.random() * 0.5);
        this.particleAlpha = 0.6f;
        this.particleRed = color;
        this.particleGreen = color;
        this.particleBlue = color;
    }

    public Particle multiplyVelocity(float multiplier) {
        this.motionX *= (double)multiplier;
        this.motionY *= (double)multiplier;
        this.motionZ *= (double)multiplier;
        return this;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if ((double)this.particleScale < 0.7 && this.particleAge < 175) {
            this.particleScale += 0.05f;
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        if (this.particleAge >= 175) {
            this.particleScale -= 0.0278f;
            this.particleAlpha -= 0.01f;
        }
        this.motionY -= 0.04 * (double)this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)0.98f;
        this.motionY += 1.0E-16;
        this.motionZ *= (double)0.98f;
        if (this.onGround) {
            this.motionX *= (double)0.7f;
            this.motionZ *= (double)0.7f;
            this.motionY += 1.0E-5;
        }
    }

    public int getFXLayer() {
        return 1;
    }

    public boolean shouldDisableDepth() {
        return false;
    }
}

