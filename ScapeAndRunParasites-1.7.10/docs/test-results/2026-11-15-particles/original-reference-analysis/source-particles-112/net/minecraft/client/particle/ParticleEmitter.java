/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ParticleEmitter
extends Particle {
    private final Entity field_174851_a;
    private int field_174852_ax;
    private final int field_174850_ay;
    private final EnumParticleTypes field_174849_az;

    public ParticleEmitter(World world, Entity entity, EnumParticleTypes enumParticleTypes) {
        this(world, entity, enumParticleTypes, 3);
    }

    public ParticleEmitter(World world, Entity entity, EnumParticleTypes enumParticleTypes, int n) {
        super(world, entity.field_70165_t, entity.func_174813_aQ().field_72338_b + (double)(entity.field_70131_O / 2.0f), entity.field_70161_v, entity.field_70159_w, entity.field_70181_x, entity.field_70179_y);
        this.field_174851_a = entity;
        this.field_174850_ay = n;
        this.field_174849_az = enumParticleTypes;
        this.func_189213_a();
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
    }

    @Override
    public void func_189213_a() {
        for (int i = 0; i < 16; ++i) {
            double d = this.field_187136_p.nextFloat() * 2.0f - 1.0f;
            if (d * d + (\u2603 = (double)(this.field_187136_p.nextFloat() * 2.0f - 1.0f)) * \u2603 + (\u2603 = (double)(this.field_187136_p.nextFloat() * 2.0f - 1.0f)) * \u2603 > 1.0) continue;
            \u2603 = this.field_174851_a.field_70165_t + d * (double)this.field_174851_a.field_70130_N / 4.0;
            \u2603 = this.field_174851_a.func_174813_aQ().field_72338_b + (double)(this.field_174851_a.field_70131_O / 2.0f) + \u2603 * (double)this.field_174851_a.field_70131_O / 4.0;
            \u2603 = this.field_174851_a.field_70161_v + \u2603 * (double)this.field_174851_a.field_70130_N / 4.0;
            this.field_187122_b.func_175682_a(this.field_174849_az, false, \u2603, \u2603, \u2603, d, \u2603 + 0.2, \u2603, new int[0]);
        }
        ++this.field_174852_ax;
        if (this.field_174852_ax >= this.field_174850_ay) {
            this.func_187112_i();
        }
    }

    @Override
    public int func_70537_b() {
        return 3;
    }
}

