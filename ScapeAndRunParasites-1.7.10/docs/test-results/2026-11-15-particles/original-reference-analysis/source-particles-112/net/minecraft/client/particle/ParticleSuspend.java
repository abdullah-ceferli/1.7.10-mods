/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleSuspend
extends Particle {
    protected ParticleSuspend(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2 - 0.125, d3, d4, d5, d6);
        this.field_70552_h = 0.4f;
        this.field_70553_i = 0.4f;
        this.field_70551_j = 0.7f;
        this.func_70536_a(0);
        this.func_187115_a(0.01f, 0.01f);
        this.field_70544_f *= this.field_187136_p.nextFloat() * 0.6f + 0.2f;
        this.field_187129_i = d4 * 0.0;
        this.field_187130_j = d5 * 0.0;
        this.field_187131_k = d6 * 0.0;
        this.field_70547_e = (int)(16.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
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
            return new ParticleSuspend(world, d, d2, d3, d4, d5, d6);
        }
    }
}

