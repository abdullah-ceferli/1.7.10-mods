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

public class ParticleMultipleGore
extends Particle {
    public static final ResourceLocation ASSIMILATED1 = new ResourceLocation("srparasites", "particle/gore/flesh_assimilated1");
    public static final ResourceLocation ASSIMILATED2 = new ResourceLocation("srparasites", "particle/gore/flesh_assimilated2");
    public static final ResourceLocation ASSIMILATED3 = new ResourceLocation("srparasites", "particle/gore/flesh_assimilated3");
    public static final ResourceLocation PURE1 = new ResourceLocation("srparasites", "particle/gore/flesh_pure1");
    public static final ResourceLocation PURE2 = new ResourceLocation("srparasites", "particle/gore/flesh_pure2");
    public static final ResourceLocation PURE3 = new ResourceLocation("srparasites", "particle/gore/flesh_pure3");
    public static final ResourceLocation PRIMITIVE1 = new ResourceLocation("srparasites", "particle/gore/flesh_primitive1");
    public static final ResourceLocation PRIMITIVE2 = new ResourceLocation("srparasites", "particle/gore/flesh_primitive2");
    public static final ResourceLocation PRIMITIVE3 = new ResourceLocation("srparasites", "particle/gore/flesh_primitive3");
    public static final ResourceLocation ADAPTED1 = new ResourceLocation("srparasites", "particle/gore/flesh_adapted1");
    public static final ResourceLocation ADAPTED2 = new ResourceLocation("srparasites", "particle/gore/flesh_adapted2");
    public static final ResourceLocation ADAPTED3 = new ResourceLocation("srparasites", "particle/gore/flesh_adapted3");
    public static final ResourceLocation VOMIT1 = new ResourceLocation("srparasites", "particle/vomit/vomit1");
    public static final ResourceLocation VOMIT2 = new ResourceLocation("srparasites", "particle/vomit/vomit2");
    public static final ResourceLocation VOMIT3 = new ResourceLocation("srparasites", "particle/vomit/vomit3");
    public static final ResourceLocation VOMIT4 = new ResourceLocation("srparasites", "particle/vomit/vomit4");
    public static final ResourceLocation VOMIT5 = new ResourceLocation("srparasites", "particle/vomit/vomit5");
    public static final ResourceLocation VOMIT6 = new ResourceLocation("srparasites", "particle/vomit/vomit6");

    protected ParticleMultipleGore(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int texture) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
        if (texture == 0) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ASSIMILATED1.toString()));
            if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ASSIMILATED2.toString()));
            } else if (Math.random() <= 0.25) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ASSIMILATED3.toString()));
            }
        } else if (texture == 1) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PURE1.toString()));
            if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PURE2.toString()));
            } else if (Math.random() <= 0.25) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PURE3.toString()));
            }
        } else if (texture == 2) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PRIMITIVE1.toString()));
            if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PRIMITIVE2.toString()));
            } else if (Math.random() <= 0.25) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PRIMITIVE3.toString()));
            }
        } else if (texture == 3) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ADAPTED1.toString()));
            if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ADAPTED2.toString()));
            } else if (Math.random() <= 0.25) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ADAPTED3.toString()));
            }
        } else if (texture == 4) {
            this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(VOMIT1.toString()));
            if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(VOMIT2.toString()));
            } else if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(VOMIT3.toString()));
            } else if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(VOMIT4.toString()));
            } else if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(VOMIT5.toString()));
            } else if (Math.random() <= 0.5) {
                this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(VOMIT6.toString()));
            }
        }
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.setSize(0.01f, 0.01f);
        this.particleGravity = 0.04f;
        this.particleMaxAge = 20 * (this.rand.nextInt(3) + 1);
    }

    public void onUpdate() {
        int age = this.particleAge++;
        float maxAge = this.particleMaxAge;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= (double)this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)0.98f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)0.98f;
        if (maxAge - (float)age <= 5.0f & this.particleAlpha >= 0.0f) {
            this.particleAlpha -= 0.05f;
        }
        if (this.particleMaxAge-- <= 0) {
            this.setExpired();
        }
        if (this.onGround) {
            this.motionX *= (double)0.7f;
            this.motionZ *= (double)0.7f;
        }
    }

    public int getFXLayer() {
        return 1;
    }
}

