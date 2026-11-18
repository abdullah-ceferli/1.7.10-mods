/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleExplosion
extends Particle {
    protected ParticleExplosion(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_187129_i = d4 + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.field_187130_j = d5 + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.field_187131_k = d6 + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.field_70552_h = \u2603 = this.field_187136_p.nextFloat() * 0.3f + 0.7f;
        this.field_70553_i = \u2603;
        this.field_70551_j = \u2603;
        this.field_70544_f = this.field_187136_p.nextFloat() * this.field_187136_p.nextFloat() * 6.0f + 1.0f;
        this.field_70547_e = (int)(16.0 / ((double)this.field_187136_p.nextFloat() * 0.8 + 0.2)) + 2;
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
        this.field_187130_j += 0.004;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.9f;
        this.field_187130_j *= (double)0.9f;
        this.field_187131_k *= (double)0.9f;
        if (this.field_187132_l) {
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleExplosion(world, d, d2, d3, d4, d5, d6);
        }
    }
}

