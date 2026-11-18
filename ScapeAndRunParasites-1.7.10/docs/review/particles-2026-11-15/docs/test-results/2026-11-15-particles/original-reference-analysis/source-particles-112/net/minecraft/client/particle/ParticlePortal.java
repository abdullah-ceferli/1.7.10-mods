/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticlePortal
extends Particle {
    private final float field_70571_a;
    private final double field_70574_aq;
    private final double field_70573_ar;
    private final double field_70572_as;

    protected ParticlePortal(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_187129_i = d4;
        this.field_187130_j = d5;
        this.field_187131_k = d6;
        this.field_187126_f = d;
        this.field_187127_g = d2;
        this.field_187128_h = d3;
        this.field_70574_aq = this.field_187126_f;
        this.field_70573_ar = this.field_187127_g;
        this.field_70572_as = this.field_187128_h;
        float f = this.field_187136_p.nextFloat() * 0.6f + 0.4f;
        this.field_70571_a = this.field_70544_f = this.field_187136_p.nextFloat() * 0.2f + 0.5f;
        this.field_70552_h = f * 0.9f;
        this.field_70553_i = f * 0.3f;
        this.field_70551_j = f;
        this.field_70547_e = (int)(Math.random() * 10.0) + 40;
        this.func_70536_a((int)(Math.random() * 8.0));
    }

    @Override
    public void func_187110_a(double d, double d2, double d3) {
        this.func_187108_a(this.func_187116_l().func_72317_d(d, d2, d3));
        this.func_187118_j();
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e;
        \u2603 = 1.0f - \u2603;
        \u2603 *= \u2603;
        \u2603 = 1.0f - \u2603;
        this.field_70544_f = this.field_70571_a * \u2603;
        super.func_180434_a(bufferBuilder, entity, f, f2, f3, f4, f5, f6);
    }

    @Override
    public int func_189214_a(float f) {
        int n = super.func_189214_a(f);
        float \u26032 = (float)this.field_70546_d / (float)this.field_70547_e;
        \u26032 *= \u26032;
        \u26032 *= \u26032;
        \u2603 = n & 0xFF;
        \u2603 = n >> 16 & 0xFF;
        if ((\u2603 += (int)(\u26032 * 15.0f * 16.0f)) > 240) {
            \u2603 = 240;
        }
        return \u2603 | \u2603 << 16;
    }

    @Override
    public void func_189213_a() {
        float f;
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        \u2603 = f = (float)this.field_70546_d / (float)this.field_70547_e;
        f = -f + f * f * 2.0f;
        f = 1.0f - f;
        this.field_187126_f = this.field_70574_aq + this.field_187129_i * (double)f;
        this.field_187127_g = this.field_70573_ar + this.field_187130_j * (double)f + (double)(1.0f - \u2603);
        this.field_187128_h = this.field_70572_as + this.field_187131_k * (double)f;
        if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_187112_i();
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticlePortal(world, d, d2, d3, d4, d5, d6);
        }
    }
}

