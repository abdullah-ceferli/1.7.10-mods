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

public class ParticleRedstone
extends Particle {
    float field_70570_a;

    protected ParticleRedstone(World world, double d, double d2, double d3, float f, float f2, float f3) {
        this(world, d, d2, d3, 1.0f, f, f2, f3);
    }

    protected ParticleRedstone(World world, double d, double d2, double d3, float f, float f2, float f3, float f4) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187129_i *= (double)0.1f;
        this.field_187130_j *= (double)0.1f;
        this.field_187131_k *= (double)0.1f;
        if (f2 == 0.0f) {
            f2 = 1.0f;
        }
        \u2603 = (float)Math.random() * 0.4f + 0.6f;
        this.field_70552_h = ((float)(Math.random() * (double)0.2f) + 0.8f) * f2 * \u2603;
        this.field_70553_i = ((float)(Math.random() * (double)0.2f) + 0.8f) * f3 * \u2603;
        this.field_70551_j = ((float)(Math.random() * (double)0.2f) + 0.8f) * f4 * \u2603;
        this.field_70544_f *= 0.75f;
        this.field_70544_f *= f;
        this.field_70570_a = this.field_70544_f;
        this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.field_70547_e = (int)((float)this.field_70547_e * f);
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e * 32.0f;
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        this.field_70544_f = this.field_70570_a * \u2603;
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
        this.func_70536_a(7 - this.field_70546_d * 8 / this.field_70547_e);
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        if (this.field_187127_g == this.field_187124_d) {
            this.field_187129_i *= 1.1;
            this.field_187131_k *= 1.1;
        }
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
            return new ParticleRedstone(world, d, d2, d3, (float)d4, (float)d5, (float)d6);
        }
    }
}

