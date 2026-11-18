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

public class ParticleCrit
extends Particle {
    float field_174839_a;

    protected ParticleCrit(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        this(world, d, d2, d3, d4, d5, d6, 1.0f);
    }

    protected ParticleCrit(World world, double d, double d2, double d3, double d4, double d5, double d6, float f) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187129_i *= (double)0.1f;
        this.field_187130_j *= (double)0.1f;
        this.field_187131_k *= (double)0.1f;
        this.field_187129_i += d4 * 0.4;
        this.field_187130_j += d5 * 0.4;
        this.field_187131_k += d6 * 0.4;
        this.field_70552_h = \u2603 = (float)(Math.random() * (double)0.3f + (double)0.6f);
        this.field_70553_i = \u2603;
        this.field_70551_j = \u2603;
        this.field_70544_f *= 0.75f;
        this.field_70544_f *= f;
        this.field_174839_a = this.field_70544_f;
        this.field_70547_e = (int)(6.0 / (Math.random() * 0.8 + 0.6));
        this.field_70547_e = (int)((float)this.field_70547_e * f);
        this.func_70536_a(65);
        this.func_189213_a();
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e * 32.0f;
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        this.field_70544_f = this.field_174839_a * \u2603;
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
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_70553_i = (float)((double)this.field_70553_i * 0.96);
        this.field_70551_j = (float)((double)this.field_70551_j * 0.9);
        this.field_187129_i *= (double)0.7f;
        this.field_187130_j *= (double)0.7f;
        this.field_187131_k *= (double)0.7f;
        this.field_187130_j -= (double)0.02f;
        if (this.field_187132_l) {
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
    }

    public static class DamageIndicatorFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            ParticleCrit particleCrit = new ParticleCrit(world, d, d2, d3, d4, d5 + 1.0, d6, 1.0f);
            particleCrit.func_187114_a(20);
            particleCrit.func_70536_a(67);
            return particleCrit;
        }
    }

    public static class MagicFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            ParticleCrit particleCrit = new ParticleCrit(world, d, d2, d3, d4, d5, d6);
            particleCrit.func_70538_b(particleCrit.func_70534_d() * 0.3f, particleCrit.func_70542_f() * 0.8f, particleCrit.func_70535_g());
            particleCrit.func_94053_h();
            return particleCrit;
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleCrit(world, d, d2, d3, d4, d5, d6);
        }
    }
}

