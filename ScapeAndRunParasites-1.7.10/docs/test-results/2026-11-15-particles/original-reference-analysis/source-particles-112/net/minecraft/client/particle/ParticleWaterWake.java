/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleWaterWake
extends Particle {
    protected ParticleWaterWake(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187129_i *= (double)0.3f;
        this.field_187130_j = Math.random() * (double)0.2f + (double)0.1f;
        this.field_187131_k *= (double)0.3f;
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.func_70536_a(19);
        this.func_187115_a(0.01f, 0.01f);
        this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.field_70545_g = 0.0f;
        this.field_187129_i = d4;
        this.field_187130_j = d5;
        this.field_187131_k = d6;
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        this.field_187130_j -= (double)this.field_70545_g;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.98f;
        this.field_187130_j *= (double)0.98f;
        this.field_187131_k *= (double)0.98f;
        int n = 60 - this.field_70547_e;
        float \u26032 = (float)n * 0.001f;
        this.func_187115_a(\u26032, \u26032);
        this.func_70536_a(19 + n % 4);
        if (this.field_70547_e-- <= 0) {
            this.func_187112_i();
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleWaterWake(world, d, d2, d3, d4, d5, d6);
        }
    }
}

