/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleSuspendedTown
extends Particle {
    protected ParticleSuspendedTown(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_70552_h = \u2603 = this.field_187136_p.nextFloat() * 0.1f + 0.2f;
        this.field_70553_i = \u2603;
        this.field_70551_j = \u2603;
        this.func_70536_a(0);
        this.func_187115_a(0.02f, 0.02f);
        this.field_70544_f *= this.field_187136_p.nextFloat() * 0.6f + 0.5f;
        this.field_187129_i *= (double)0.02f;
        this.field_187130_j *= (double)0.02f;
        this.field_187131_k *= (double)0.02f;
        this.field_70547_e = (int)(20.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void func_187110_a(double d, double d2, double d3) {
        this.func_187108_a(this.func_187116_l().func_72317_d(d, d2, d3));
        this.func_187118_j();
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= 0.99;
        this.field_187130_j *= 0.99;
        this.field_187131_k *= 0.99;
        if (this.field_70547_e-- <= 0) {
            this.func_187112_i();
        }
    }

    public static class HappyVillagerFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            ParticleSuspendedTown particleSuspendedTown = new ParticleSuspendedTown(world, d, d2, d3, d4, d5, d6);
            particleSuspendedTown.func_70536_a(82);
            particleSuspendedTown.func_70538_b(1.0f, 1.0f, 1.0f);
            return particleSuspendedTown;
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleSuspendedTown(world, d, d2, d3, d4, d5, d6);
        }
    }
}

