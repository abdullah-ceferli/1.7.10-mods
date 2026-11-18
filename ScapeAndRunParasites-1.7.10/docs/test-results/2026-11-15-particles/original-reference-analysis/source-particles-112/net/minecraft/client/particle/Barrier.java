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
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Barrier
extends Particle {
    protected Barrier(World world, double d, double d2, double d3, Item item) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.func_187117_a(Minecraft.func_71410_x().func_175599_af().func_175037_a().func_178082_a(item));
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.field_187129_i = 0.0;
        this.field_187130_j = 0.0;
        this.field_187131_k = 0.0;
        this.field_70545_g = 0.0f;
        this.field_70547_e = 80;
    }

    @Override
    public int func_70537_b() {
        return 1;
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = this.field_187119_C.func_94209_e();
        \u2603 = this.field_187119_C.func_94212_f();
        \u2603 = this.field_187119_C.func_94206_g();
        \u2603 = this.field_187119_C.func_94210_h();
        \u2603 = 0.5f;
        \u2603 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)f - field_70556_an);
        \u2603 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)f - field_70554_ao);
        \u2603 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)f - field_70555_ap);
        int n = this.func_189214_a(f);
        \u2603 = n >> 16 & 0xFFFF;
        \u2603 = n & 0xFFFF;
        bufferBuilder.func_181662_b(\u2603 - f2 * 0.5f - f5 * 0.5f, \u2603 - f3 * 0.5f, \u2603 - f4 * 0.5f - f6 * 0.5f).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 - f2 * 0.5f + f5 * 0.5f, \u2603 + f3 * 0.5f, \u2603 - f4 * 0.5f + f6 * 0.5f).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 + f2 * 0.5f + f5 * 0.5f, \u2603 + f3 * 0.5f, \u2603 + f4 * 0.5f + f6 * 0.5f).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 + f2 * 0.5f - f5 * 0.5f, \u2603 - f3 * 0.5f, \u2603 + f4 * 0.5f - f6 * 0.5f).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(\u2603, \u2603).func_181675_d();
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new Barrier(world, d, d2, d3, Item.func_150898_a(Blocks.field_180401_cv));
        }
    }
}

