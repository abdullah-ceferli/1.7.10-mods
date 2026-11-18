/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.world.World;

public class ParticleSpit
extends ParticleExplosion {
    protected ParticleSpit(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_70545_g = 0.5f;
    }

    @Override
    public void func_189213_a() {
        super.func_189213_a();
        this.field_187130_j -= 0.004 + 0.04 * (double)this.field_70545_g;
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleSpit(world, d, d2, d3, d4, d5, d6);
        }
    }
}

