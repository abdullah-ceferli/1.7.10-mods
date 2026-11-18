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

public class ParticleHeart
extends Particle {
    float field_70575_a;

    protected ParticleHeart(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        this(world, d, d2, d3, d4, d5, d6, 2.0f);
    }

    protected ParticleHeart(World world, double d, double d2, double d3, double d4, double d5, double d6, float f) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187129_i *= (double)0.01f;
        this.field_187130_j *= (double)0.01f;
        this.field_187131_k *= (double)0.01f;
        this.field_187130_j += 0.1;
        this.field_70544_f *= 0.75f;
        this.field_70544_f *= f;
        this.field_70575_a = this.field_70544_f;
        this.field_70547_e = 16;
        this.func_70536_a(80);
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e * 32.0f;
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        this.field_70544_f = this.field_70575_a * \u2603;
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
        if (this.field_187127_g == this.field_187124_d) {
            this.field_187129_i *= 1.1;
            this.field_187131_k *= 1.1;
        }
        this.field_187129_i *= (double)0.86f;
        this.field_187130_j *= (double)0.86f;
        this.field_187131_k *= (double)0.86f;
        if (this.field_187132_l) {
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
    }

    public static class AngryVillagerFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            ParticleHeart particleHeart = new ParticleHeart(world, d, d2 + 0.5, d3, d4, d5, d6);
            particleHeart.func_70536_a(81);
            particleHeart.func_70538_b(1.0f, 1.0f, 1.0f);
            return particleHeart;
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleHeart(world, d, d2, d3, d4, d5, d6);
        }
    }
}

