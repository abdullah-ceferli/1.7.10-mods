/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ParticleExplosionHuge
extends Particle {
    private int field_70579_a;
    private final int field_70580_aq;

    protected ParticleExplosionHuge(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_70580_aq = 8;
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
    }

    @Override
    public void func_189213_a() {
        for (int i = 0; i < 6; ++i) {
            double d = this.field_187126_f + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 4.0;
            \u2603 = this.field_187127_g + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 4.0;
            \u2603 = this.field_187128_h + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 4.0;
            this.field_187122_b.func_175688_a(EnumParticleTypes.EXPLOSION_LARGE, d, \u2603, \u2603, (float)this.field_70579_a / (float)this.field_70580_aq, 0.0, 0.0, new int[0]);
        }
        ++this.field_70579_a;
        if (this.field_70579_a == this.field_70580_aq) {
            this.func_187112_i();
        }
    }

    @Override
    public int func_70537_b() {
        return 1;
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleExplosionHuge(world, d, d2, d3, d4, d5, d6);
        }
    }
}

