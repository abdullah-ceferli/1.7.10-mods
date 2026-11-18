/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleKirinWarning
extends Particle {
    private static final ResourceLocation TEX = new ResourceLocation("srparasites", "textures/particle/kirin_warning.png");
    private final float sizeBlocks;
    private final float yaw;
    private final int max;

    public ParticleKirinWarning(World world, double x, double y, double z, float sizeBlocks, float yawRad, int maxAgeTicks) {
        super(world, x, y, z);
        this.sizeBlocks = sizeBlocks;
        this.yaw = yawRad;
        this.particleMaxAge = this.max = Math.max(1, maxAgeTicks);
        this.canCollide = false;
        this.particleGravity = 0.0f;
        this.motionZ = 0.0;
        this.motionY = 0.0;
        this.motionX = 0.0;
        this.particleAlpha = 1.0f;
        this.setRBGColorF(1.0f, 1.0f, 1.0f);
    }

    public int getFXLayer() {
        return 3;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEX);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
        GlStateManager.disableCull();
        double x = this.posX - interpPosX;
        double y = this.posY - interpPosY;
        double z = this.posZ - interpPosZ;
        GlStateManager.translate((double)x, (double)y, (double)z);
        GlStateManager.rotate((float)((float)Math.toDegrees(this.yaw)), (float)0.0f, (float)1.0f, (float)0.0f);
        float half = this.sizeBlocks * 0.5f;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos((double)(-half), 0.0, (double)(-half)).tex(0.0, 0.0).color(255, 255, 255, (int)(255.0f * this.particleAlpha)).endVertex();
        buf.pos((double)(-half), 0.0, (double)half).tex(0.0, 1.0).color(255, 255, 255, (int)(255.0f * this.particleAlpha)).endVertex();
        buf.pos((double)half, 0.0, (double)half).tex(1.0, 1.0).color(255, 255, 255, (int)(255.0f * this.particleAlpha)).endVertex();
        buf.pos((double)half, 0.0, (double)(-half)).tex(1.0, 0.0).color(255, 255, 255, (int)(255.0f * this.particleAlpha)).endVertex();
        tess.draw();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}

