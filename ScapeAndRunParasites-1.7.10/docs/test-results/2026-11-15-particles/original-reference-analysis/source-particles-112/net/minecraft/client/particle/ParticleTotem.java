/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.world.World;

public class ParticleTotem
extends ParticleSimpleAnimated {
    public ParticleTotem(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 176, 8, -0.05f);
        this.field_187129_i = d4;
        this.field_187130_j = d5;
        this.field_187131_k = d6;
        this.field_70544_f *= 0.75f;
        this.field_70547_e = 60 + this.field_187136_p.nextInt(12);
        if (this.field_187136_p.nextInt(4) == 0) {
            this.func_70538_b(0.6f + this.field_187136_p.nextFloat() * 0.2f, 0.6f + this.field_187136_p.nextFloat() * 0.3f, this.field_187136_p.nextFloat() * 0.2f);
        } else {
            this.func_70538_b(0.1f + this.field_187136_p.nextFloat() * 0.2f, 0.4f + this.field_187136_p.nextFloat() * 0.3f, this.field_187136_p.nextFloat() * 0.2f);
        }
        this.func_191238_f(0.6f);
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleTotem(world, d, d2, d3, d4, d5, d6);
        }
    }
}

