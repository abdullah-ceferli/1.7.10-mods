/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ParticleBreaking
extends Particle {
    protected ParticleBreaking(World world, double d, double d2, double d3, Item item) {
        this(world, d, d2, d3, item, 0);
    }

    protected ParticleBreaking(World world, double d, double d2, double d3, double d4, double d5, double d6, Item item, int n) {
        this(world, d, d2, d3, item, n);
        this.field_187129_i *= (double)0.1f;
        this.field_187130_j *= (double)0.1f;
        this.field_187131_k *= (double)0.1f;
        this.field_187129_i += d4;
        this.field_187130_j += d5;
        this.field_187131_k += d6;
    }

    protected ParticleBreaking(World world, double d, double d2, double d3, Item item, int n) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.func_187117_a(Minecraft.func_71410_x().func_175599_af().func_175037_a().func_178087_a(item, n));
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.field_70545_g = Blocks.field_150433_aE.field_149763_I;
        this.field_70544_f /= 2.0f;
    }

    @Override
    public int func_70537_b() {
        return 1;
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_94054_b + this.field_70548_b / 4.0f) / 16.0f;
        \u2603 = \u2603 + 0.015609375f;
        \u2603 = ((float)this.field_94055_c + this.field_70549_c / 4.0f) / 16.0f;
        \u2603 = \u2603 + 0.015609375f;
        \u2603 = 0.1f * this.field_70544_f;
        if (this.field_187119_C != null) {
            \u2603 = this.field_187119_C.func_94214_a(this.field_70548_b / 4.0f * 16.0f);
            \u2603 = this.field_187119_C.func_94214_a((this.field_70548_b + 1.0f) / 4.0f * 16.0f);
            \u2603 = this.field_187119_C.func_94207_b(this.field_70549_c / 4.0f * 16.0f);
            \u2603 = this.field_187119_C.func_94207_b((this.field_70549_c + 1.0f) / 4.0f * 16.0f);
        }
        \u2603 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)f - field_70556_an);
        \u2603 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)f - field_70554_ao);
        \u2603 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)f - field_70555_ap);
        int n = this.func_189214_a(f);
        \u2603 = n >> 16 & 0xFFFF;
        \u2603 = n & 0xFFFF;
        bufferBuilder.func_181662_b(\u2603 - f2 * \u2603 - f5 * \u2603, \u2603 - f3 * \u2603, \u2603 - f4 * \u2603 - f6 * \u2603).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 - f2 * \u2603 + f5 * \u2603, \u2603 + f3 * \u2603, \u2603 - f4 * \u2603 + f6 * \u2603).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 + f2 * \u2603 + f5 * \u2603, \u2603 + f3 * \u2603, \u2603 + f4 * \u2603 + f6 * \u2603).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 + f2 * \u2603 - f5 * \u2603, \u2603 - f3 * \u2603, \u2603 + f4 * \u2603 - f6 * \u2603).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(\u2603, \u2603).func_181675_d();
    }

    public static class SnowballFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleBreaking(world, d, d2, d3, Items.field_151126_ay);
        }
    }

    public static class SlimeFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleBreaking(world, d, d2, d3, Items.field_151123_aH);
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            int n2 = nArray.length > 1 ? nArray[1] : 0;
            return new ParticleBreaking(world, d, d2, d3, d4, d5, d6, Item.func_150899_d(nArray[0]), n2);
        }
    }
}

