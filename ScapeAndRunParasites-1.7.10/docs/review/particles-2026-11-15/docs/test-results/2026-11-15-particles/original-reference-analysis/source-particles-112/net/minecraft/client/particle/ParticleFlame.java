/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleFlame
extends Particle {
    private final float field_70562_a;

    protected ParticleFlame(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_187129_i = this.field_187129_i * (double)0.01f + d4;
        this.field_187130_j = this.field_187130_j * (double)0.01f + d5;
        this.field_187131_k = this.field_187131_k * (double)0.01f + d6;
        this.field_187126_f += (double)((this.field_187136_p.nextFloat() - this.field_187136_p.nextFloat()) * 0.05f);
        this.field_187127_g += (double)((this.field_187136_p.nextFloat() - this.field_187136_p.nextFloat()) * 0.05f);
        this.field_187128_h += (double)((this.field_187136_p.nextFloat() - this.field_187136_p.nextFloat()) * 0.05f);
        this.field_70562_a = this.field_70544_f;
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
        this.func_70536_a(48);
    }

    @Override
    public void func_187110_a(double d, double d2, double d3) {
        this.func_187108_a(this.func_187116_l().func_72317_d(d, d2, d3));
        this.func_187118_j();
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e;
        this.field_70544_f = this.field_70562_a * (1.0f - \u2603 * \u2603 * 0.5f);
        super.func_180434_a(bufferBuilder, entity, f, f2, f3, f4, f5, f6);
    }

    @Override
    public int func_189214_a(float f) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e;
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        int n = super.func_189214_a(f);
        \u2603 = n & 0xFF;
        \u2603 = n >> 16 & 0xFF;
        if ((\u2603 += (int)(\u2603 * 15.0f * 16.0f)) > 240) {
            \u2603 = 240;
        }
        return \u2603 | \u2603 << 16;
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_187112_i();
        }
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.96f;
        this.field_187130_j *= (double)0.96f;
        this.field_187131_k *= (double)0.96f;
        if (this.field_187132_l) {
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleFlame(world, d, d2, d3, d4, d5, d6);
        }
    }
}

