/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ParticleLava
extends Particle {
    private final float field_70586_a;

    protected ParticleLava(World world, double d, double d2, double d3) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187129_i *= (double)0.8f;
        this.field_187130_j *= (double)0.8f;
        this.field_187131_k *= (double)0.8f;
        this.field_187130_j = this.field_187136_p.nextFloat() * 0.4f + 0.05f;
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.field_70544_f *= this.field_187136_p.nextFloat() * 2.0f + 0.2f;
        this.field_70586_a = this.field_70544_f;
        this.field_70547_e = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.func_70536_a(49);
    }

    @Override
    public int func_189214_a(float f) {
        int n = super.func_189214_a(f);
        \u2603 = 240;
        \u2603 = n >> 16 & 0xFF;
        return 0xF0 | \u2603 << 16;
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e;
        this.field_70544_f = this.field_70586_a * (1.0f - \u2603 * \u2603);
        super.func_180434_a(bufferBuilder, entity, f, f2, f3, f4, f5, f6);
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_187112_i();
        }
        float f = (float)this.field_70546_d / (float)this.field_70547_e;
        if (this.field_187136_p.nextFloat() > f) {
            this.field_187122_b.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, this.field_187126_f, this.field_187127_g, this.field_187128_h, this.field_187129_i, this.field_187130_j, this.field_187131_k, new int[0]);
        }
        this.field_187130_j -= 0.03;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.999f;
        this.field_187130_j *= (double)0.999f;
        this.field_187131_k *= (double)0.999f;
        if (this.field_187132_l) {
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleLava(world, d, d2, d3);
        }
    }
}

