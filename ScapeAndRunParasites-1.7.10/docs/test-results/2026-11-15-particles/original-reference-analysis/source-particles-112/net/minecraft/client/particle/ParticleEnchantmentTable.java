/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleEnchantmentTable
extends Particle {
    private final float field_70565_a;
    private final double field_70568_aq;
    private final double field_70567_ar;
    private final double field_70566_as;

    protected ParticleEnchantmentTable(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_187129_i = d4;
        this.field_187130_j = d5;
        this.field_187131_k = d6;
        this.field_70568_aq = d;
        this.field_70567_ar = d2;
        this.field_70566_as = d3;
        this.field_187123_c = d + d4;
        this.field_187124_d = d2 + d5;
        this.field_187125_e = d3 + d6;
        this.field_187126_f = this.field_187123_c;
        this.field_187127_g = this.field_187124_d;
        this.field_187128_h = this.field_187125_e;
        float f = this.field_187136_p.nextFloat() * 0.6f + 0.4f;
        this.field_70565_a = this.field_70544_f = this.field_187136_p.nextFloat() * 0.5f + 0.2f;
        this.field_70552_h = 0.9f * f;
        this.field_70553_i = 0.9f * f;
        this.field_70551_j = f;
        this.field_70547_e = (int)(Math.random() * 10.0) + 30;
        this.func_70536_a((int)(Math.random() * 26.0 + 1.0 + 224.0));
    }

    @Override
    public void func_187110_a(double d, double d2, double d3) {
        this.func_187108_a(this.func_187116_l().func_72317_d(d, d2, d3));
        this.func_187118_j();
    }

    @Override
    public int func_189214_a(float f) {
        int n = super.func_189214_a(f);
        float \u26032 = (float)this.field_70546_d / (float)this.field_70547_e;
        \u26032 *= \u26032;
        \u26032 *= \u26032;
        \u2603 = n & 0xFF;
        \u2603 = n >> 16 & 0xFF;
        if ((\u2603 += (int)(\u26032 * 15.0f * 16.0f)) > 240) {
            \u2603 = 240;
        }
        return \u2603 | \u2603 << 16;
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        float f = (float)this.field_70546_d / (float)this.field_70547_e;
        f = 1.0f - f;
        \u2603 = 1.0f - f;
        \u2603 *= \u2603;
        \u2603 *= \u2603;
        this.field_187126_f = this.field_70568_aq + this.field_187129_i * (double)f;
        this.field_187127_g = this.field_70567_ar + this.field_187130_j * (double)f - (double)(\u2603 * 1.2f);
        this.field_187128_h = this.field_70566_as + this.field_187131_k * (double)f;
        if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_187112_i();
        }
    }

    public static class EnchantmentTable
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleEnchantmentTable(world, d, d2, d3, d4, d5, d6);
        }
    }
}

