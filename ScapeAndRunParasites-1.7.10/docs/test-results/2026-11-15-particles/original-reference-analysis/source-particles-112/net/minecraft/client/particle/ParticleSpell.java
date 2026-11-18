/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleSpell
extends Particle {
    private static final Random field_174848_a = new Random();
    private int field_70590_a = 128;

    protected ParticleSpell(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.5 - field_174848_a.nextDouble(), d5, 0.5 - field_174848_a.nextDouble());
        this.field_187130_j *= (double)0.2f;
        if (d4 == 0.0 && d6 == 0.0) {
            this.field_187129_i *= (double)0.1f;
            this.field_187131_k *= (double)0.1f;
        }
        this.field_70544_f *= 0.75f;
        this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public boolean func_187111_c() {
        return true;
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_187112_i();
        }
        this.func_70536_a(this.field_70590_a + (7 - this.field_70546_d * 8 / this.field_70547_e));
        this.field_187130_j += 0.004;
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

    public void func_70589_b(int n) {
        this.field_70590_a = n;
    }

    public static class InstantFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            ParticleSpell particleSpell = new ParticleSpell(world, d, d2, d3, d4, d5, d6);
            particleSpell.func_70589_b(144);
            return particleSpell;
        }
    }

    public static class WitchFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            ParticleSpell particleSpell = new ParticleSpell(world, d, d2, d3, d4, d5, d6);
            particleSpell.func_70589_b(144);
            float \u26032 = world.field_73012_v.nextFloat() * 0.5f + 0.35f;
            particleSpell.func_70538_b(1.0f * \u26032, 0.0f * \u26032, 1.0f * \u26032);
            return particleSpell;
        }
    }

    public static class AmbientMobFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            ParticleSpell particleSpell = new ParticleSpell(world, d, d2, d3, d4, d5, d6);
            particleSpell.func_82338_g(0.15f);
            particleSpell.func_70538_b((float)d4, (float)d5, (float)d6);
            return particleSpell;
        }
    }

    public static class MobFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            ParticleSpell particleSpell = new ParticleSpell(world, d, d2, d3, d4, d5, d6);
            particleSpell.func_70538_b((float)d4, (float)d5, (float)d6);
            return particleSpell;
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleSpell(world, d, d2, d3, d4, d5, d6);
        }
    }
}

