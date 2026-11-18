/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.world.World;

public class ParticleEndRod
extends ParticleSimpleAnimated {
    public ParticleEndRod(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 176, 8, -5.0E-4f);
        this.field_187129_i = d4;
        this.field_187130_j = d5;
        this.field_187131_k = d6;
        this.field_70544_f *= 0.75f;
        this.field_70547_e = 60 + this.field_187136_p.nextInt(12);
        this.func_187145_d(15916745);
    }

    @Override
    public void func_187110_a(double d, double d2, double d3) {
        this.func_187108_a(this.func_187116_l().func_72317_d(d, d2, d3));
        this.func_187118_j();
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleEndRod(world, d, d2, d3, d4, d5, d6);
        }
    }
}

