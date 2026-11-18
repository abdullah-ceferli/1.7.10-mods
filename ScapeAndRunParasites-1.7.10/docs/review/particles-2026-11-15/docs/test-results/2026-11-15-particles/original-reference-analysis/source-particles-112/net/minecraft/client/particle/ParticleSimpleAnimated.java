/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleSimpleAnimated
extends Particle {
    private final int field_187147_a;
    private final int field_187148_G;
    private final float field_187149_H;
    private float field_191239_M = 0.91f;
    private float field_187150_I;
    private float field_187151_J;
    private float field_187152_K;
    private boolean field_187153_L;

    public ParticleSimpleAnimated(World world, double d, double d2, double d3, int n, int n2, float f) {
        super(world, d, d2, d3);
        this.field_187147_a = n;
        this.field_187148_G = n2;
        this.field_187149_H = f;
    }

    public void func_187146_c(int n) {
        float f = (float)((n & 0xFF0000) >> 16) / 255.0f;
        \u2603 = (float)((n & 0xFF00) >> 8) / 255.0f;
        \u2603 = (float)((n & 0xFF) >> 0) / 255.0f;
        \u2603 = 1.0f;
        this.func_70538_b(f * 1.0f, \u2603 * 1.0f, \u2603 * 1.0f);
    }

    public void func_187145_d(int n) {
        this.field_187150_I = (float)((n & 0xFF0000) >> 16) / 255.0f;
        this.field_187151_J = (float)((n & 0xFF00) >> 8) / 255.0f;
        this.field_187152_K = (float)((n & 0xFF) >> 0) / 255.0f;
        this.field_187153_L = true;
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
        if (this.field_70546_d > this.field_70547_e / 2) {
            this.func_82338_g(1.0f - ((float)this.field_70546_d - (float)(this.field_70547_e / 2)) / (float)this.field_70547_e);
            if (this.field_187153_L) {
                this.field_70552_h += (this.field_187150_I - this.field_70552_h) * 0.2f;
                this.field_70553_i += (this.field_187151_J - this.field_70553_i) * 0.2f;
                this.field_70551_j += (this.field_187152_K - this.field_70551_j) * 0.2f;
            }
        }
        this.func_70536_a(this.field_187147_a + (this.field_187148_G - 1 - this.field_70546_d * this.field_187148_G / this.field_70547_e));
        this.field_187130_j += (double)this.field_187149_H;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)this.field_191239_M;
        this.field_187130_j *= (double)this.field_191239_M;
        this.field_187131_k *= (double)this.field_191239_M;
        if (this.field_187132_l) {
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
    }

    @Override
    public int func_189214_a(float f) {
        return 0xF000F0;
    }

    protected void func_191238_f(float f) {
        this.field_191239_M = f;
    }
}

