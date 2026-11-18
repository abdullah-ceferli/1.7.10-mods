/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.fx;

import com.dhanantry.scapeandrunparasites.client.fx.ClientSRPParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleInfestedLeaf
extends Particle {
    private float yaw;
    private float pitch;
    private float rollVel;
    private float sway;
    private float swaySpeed;
    private float swayAmp;

    public ParticleInfestedLeaf(World w, double x, double y, double z) {
        super(w, x, y, z);
        int idx = w.rand.nextInt(ClientSRPParticles.INFESTED_LEAF_SPRITES.length);
        this.setParticleTexture(ClientSRPParticles.INFESTED_LEAF_SPRITES[idx]);
        this.particleScale = 5.6f + this.rand.nextFloat() * 0.6f;
        this.particleMaxAge = 80 + this.rand.nextInt(80);
        this.particleGravity = 0.02f;
        this.motionX = (this.rand.nextDouble() - 0.5) * 0.01;
        this.motionZ = (this.rand.nextDouble() - 0.5) * 0.01;
        this.motionY = -0.01 - this.rand.nextDouble() * 0.01;
        this.yaw = (float)(this.rand.nextDouble() * Math.PI * 2.0);
        this.pitch = (float)((this.rand.nextDouble() * 0.7 - 0.35) * Math.PI * 0.25);
        this.rollVel = (this.rand.nextFloat() - 0.5f) * 0.12f;
        this.sway = this.rand.nextFloat() * (float)Math.PI * 2.0f;
        this.swaySpeed = 0.15f + this.rand.nextFloat() * 0.15f;
        this.swayAmp = 0.02f + this.rand.nextFloat() * 0.02f;
        this.canCollide = true;
    }

    public void onUpdate() {
        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += this.rollVel;
        this.sway += this.swaySpeed;
        double flutterX = Math.cos(this.sway) * (double)this.swayAmp;
        double flutterZ = Math.sin(this.sway * 1.3f) * ((double)this.swayAmp * 0.8);
        this.motionX += flutterX * 0.2;
        this.motionZ += flutterZ * 0.2;
        this.yaw += (float)(Math.sin((double)this.sway * 0.7) * 0.003);
        this.pitch += (float)(Math.cos((double)this.sway * 0.9) * 0.003);
        this.motionY -= 0.002 + 0.001 * Math.sin((double)this.sway * 0.5);
        if (this.motionY < -0.06) {
            this.motionY = -0.06;
        }
        super.onUpdate();
        if (this.isExpired || this.onGround) {
            this.setExpired();
            return;
        }
        this.motionX *= 0.96;
        this.motionZ *= 0.96;
    }

    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.particleTexture == null) {
            return;
        }
        double x = this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX;
        double y = this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY;
        double z = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ;
        float s = 0.1f * this.particleScale * 0.5f;
        float roll = this.prevParticleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
        float cy = (float)Math.cos(this.yaw);
        float sy = (float)Math.sin(this.yaw);
        float cp = (float)Math.cos(this.pitch);
        float sp = (float)Math.sin(this.pitch);
        float cr = (float)Math.cos(roll);
        float sr = (float)Math.sin(roll);
        float rx = cr * cy + sr * sp * sy;
        float ry = sr * cp;
        float rz = -sr * sy + cr * sp * cy;
        float ux = -sr * cy + cr * sp * sy;
        float uy = cr * cp;
        float uz = sr * sy + cr * sp * cy;
        double x1 = x - (double)(rx *= s) - (double)(ux *= s);
        double y1 = y - (double)(ry *= s) - (double)(uy *= s);
        double z1 = z - (double)(rz *= s) - (double)(uz *= s);
        double x2 = x - (double)rx + (double)ux;
        double y2 = y - (double)ry + (double)uy;
        double z2 = z - (double)rz + (double)uz;
        double x3 = x + (double)rx + (double)ux;
        double y3 = y + (double)ry + (double)uy;
        double z3 = z + (double)rz + (double)uz;
        double x4 = x + (double)rx - (double)ux;
        double y4 = y + (double)ry - (double)uy;
        double z4 = z + (double)rz - (double)uz;
        float u0 = this.particleTexture.getMinU();
        float u1 = this.particleTexture.getMaxU();
        float v0 = this.particleTexture.getMinV();
        float v1 = this.particleTexture.getMaxV();
        int light = this.getBrightnessForRender(partialTicks);
        int lightU = light >> 16 & 0xFFFF;
        int lightV = light & 0xFFFF;
        int a = (int)(this.particleAlpha * 255.0f);
        int r = (int)(this.particleRed * 255.0f);
        int g = (int)(this.particleGreen * 255.0f);
        int b = (int)(this.particleBlue * 255.0f);
        buffer.pos(x1, y1, z1).tex((double)u1, (double)v1).color(r, g, b, a).lightmap(lightU, lightV).endVertex();
        buffer.pos(x2, y2, z2).tex((double)u1, (double)v0).color(r, g, b, a).lightmap(lightU, lightV).endVertex();
        buffer.pos(x3, y3, z3).tex((double)u0, (double)v0).color(r, g, b, a).lightmap(lightU, lightV).endVertex();
        buffer.pos(x4, y4, z4).tex((double)u0, (double)v1).color(r, g, b, a).lightmap(lightU, lightV).endVertex();
    }

    public int getFXLayer() {
        return 1;
    }
}

