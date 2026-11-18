/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleBubble
extends Particle {
    protected ParticleBubble(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.func_70536_a(32);
        this.func_187115_a(0.02f, 0.02f);
        this.field_70544_f *= this.field_187136_p.nextFloat() * 0.6f + 0.2f;
        this.field_187129_i = d4 * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.field_187130_j = d5 * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.field_187131_k = d6 * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        this.field_187130_j += 0.002;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.85f;
        this.field_187130_j *= (double)0.85f;
        this.field_187131_k *= (double)0.85f;
        if (this.field_187122_b.func_180495_p(new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h)).func_185904_a() != Material.field_151586_h) {
            this.func_187112_i();
        }
        if (this.field_70547_e-- <= 0) {
            this.func_187112_i();
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleBubble(world, d, d2, d3, d4, d5, d6);
        }
    }
}

