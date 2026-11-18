/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleCloud
extends Particle {
    float field_70569_a;

    protected ParticleCloud(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        float f = 2.5f;
        this.field_187129_i *= (double)0.1f;
        this.field_187130_j *= (double)0.1f;
        this.field_187131_k *= (double)0.1f;
        this.field_187129_i += d4;
        this.field_187130_j += d5;
        this.field_187131_k += d6;
        this.field_70552_h = \u2603 = 1.0f - (float)(Math.random() * (double)0.3f);
        this.field_70553_i = \u2603;
        this.field_70551_j = \u2603;
        this.field_70544_f *= 0.75f;
        this.field_70544_f *= 2.5f;
        this.field_70569_a = this.field_70544_f;
        this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.field_70547_e = (int)((float)this.field_70547_e * 2.5f);
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e * 32.0f;
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        this.field_70544_f = this.field_70569_a * \u2603;
        super.func_180434_a(bufferBuilder, entity, f, f2, f3, f4, f5, f6);
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_187112_i();
        }
        this.func_70536_a(7 - this.field_70546_d * 8 / this.field_70547_e);
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.96f;
        this.field_187130_j *= (double)0.96f;
        this.field_187131_k *= (double)0.96f;
        EntityPlayer entityPlayer = this.field_187122_b.func_184137_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, 2.0, false);
        if (entityPlayer != null) {
            AxisAlignedBB axisAlignedBB = entityPlayer.func_174813_aQ();
            if (this.field_187127_g > axisAlignedBB.field_72338_b) {
                this.field_187127_g += (axisAlignedBB.field_72338_b - this.field_187127_g) * 0.2;
                this.field_187130_j += (entityPlayer.field_70181_x - this.field_187130_j) * 0.2;
                this.func_187109_b(this.field_187126_f, this.field_187127_g, this.field_187128_h);
            }
        }
        if (this.field_187132_l) {
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleCloud(world, d, d2, d3, d4, d5, d6);
        }
    }
}

