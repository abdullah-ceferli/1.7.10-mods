/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.particle;

import com.dhanantry.scapeandrunparasites.client.particle.SRPParticleRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ParticlePure
extends Particle {
    public static final int KIND_WAVE = 0;
    public static final int KIND_PULSE = 1;
    private final double cx;
    private final double cy;
    private final double cz;
    private final int kind;
    private double angle;
    private double radius;
    private double omega;
    private double up;
    private double out;
    private float startScale;

    public ParticlePure(World world, double x, double y, double z, int kind, TextureAtlasSprite sprite) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.cx = x;
        this.cy = y;
        this.cz = z;
        this.kind = kind;
        this.particleMaxAge = 22 + this.rand.nextInt(14);
        this.canCollide = false;
        this.particleGravity = 0.0f;
        this.particleAlpha = 0.95f;
        this.angle = this.rand.nextDouble() * Math.PI * 2.0;
        this.radius = 0.15 + this.rand.nextDouble() * 0.25;
        this.omega = 0.2 + this.rand.nextDouble() * 0.2;
        this.up = 0.01 + this.rand.nextDouble() * 0.02;
        this.out = 0.07 + this.rand.nextDouble() * 0.05;
        this.startScale = 0.9f + this.rand.nextFloat() * 0.5f;
        this.particleScale = 0.0f;
        this.setParticleTexture(sprite);
        this.posX += (this.rand.nextDouble() - 0.5) * 0.05;
        this.posY += this.rand.nextDouble() * 0.03;
        this.posZ += (this.rand.nextDouble() - 0.5) * 0.05;
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
        TextureAtlasSprite alt;
        int age;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if ((age = this.particleAge++) >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        float t = (float)age / (float)this.particleMaxAge;
        float easeIn = (float)Math.min(1.0, (double)t * 8.0);
        float easeOut = (float)(1.0 - Math.max(0.0, (double)(t - 0.5f) * 2.0));
        this.particleScale = this.startScale * easeIn * easeOut;
        this.particleAlpha = 0.95f * easeOut + 0.05f;
        if (age % 4 == 0 && this.rand.nextBoolean() && (alt = SRPParticleRegistry.randPureSprite(this.rand)) != null) {
            this.setParticleTexture(alt);
        }
        if (this.kind == 0) {
            this.angle += this.omega;
            this.radius *= 0.992;
            double sx = Math.cos(this.angle) * this.radius;
            double sz = Math.sin(this.angle) * this.radius;
            double wob = Math.sin((double)age * 0.2 + this.angle) * 0.01;
            this.posX = this.cx + sx + wob;
            this.posZ = this.cz + sz - wob;
            this.posY += this.up;
        } else {
            double theta = this.angle;
            double r = (double)age * this.out;
            double curl = Math.sin((double)age * 0.25) * 0.06;
            double sx = Math.cos(theta) * r - Math.sin(theta) * curl;
            double sz = Math.sin(theta) * r + Math.cos(theta) * curl;
            this.posX = this.cx + sx;
            this.posZ = this.cz + sz;
            this.posY = this.cy + Math.sin((double)age * 0.25) * 0.03;
        }
    }

    public int getFXLayer() {
        return 1;
    }
}

