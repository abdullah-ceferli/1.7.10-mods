/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleDigging
extends Particle {
    private final IBlockState field_174847_a;
    private BlockPos field_181019_az;

    protected ParticleDigging(World world, double d, double d2, double d3, double d4, double d5, double d6, IBlockState iBlockState) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_174847_a = iBlockState;
        this.func_187117_a(Minecraft.func_71410_x().func_175602_ab().func_175023_a().func_178122_a(iBlockState));
        this.field_70545_g = iBlockState.func_177230_c().field_149763_I;
        this.field_70552_h = 0.6f;
        this.field_70553_i = 0.6f;
        this.field_70551_j = 0.6f;
        this.field_70544_f /= 2.0f;
    }

    public ParticleDigging func_174846_a(BlockPos blockPos) {
        this.field_181019_az = blockPos;
        if (this.field_174847_a.func_177230_c() == Blocks.field_150349_c) {
            return this;
        }
        this.func_187154_b(blockPos);
        return this;
    }

    public ParticleDigging func_174845_l() {
        this.field_181019_az = new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h);
        Block block = this.field_174847_a.func_177230_c();
        if (block == Blocks.field_150349_c) {
            return this;
        }
        this.func_187154_b(this.field_181019_az);
        return this;
    }

    protected void func_187154_b(@Nullable BlockPos blockPos) {
        int n = Minecraft.func_71410_x().func_184125_al().func_186724_a(this.field_174847_a, this.field_187122_b, blockPos, 0);
        this.field_70552_h *= (float)(n >> 16 & 0xFF) / 255.0f;
        this.field_70553_i *= (float)(n >> 8 & 0xFF) / 255.0f;
        this.field_70551_j *= (float)(n & 0xFF) / 255.0f;
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

    @Override
    public int func_189214_a(float f) {
        int n = super.func_189214_a(f);
        \u2603 = 0;
        if (this.field_187122_b.func_175667_e(this.field_181019_az)) {
            \u2603 = this.field_187122_b.func_175626_b(this.field_181019_az, 0);
        }
        return n == 0 ? \u2603 : n;
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleDigging(world, d, d2, d3, d4, d5, d6, Block.func_176220_d(nArray[0])).func_174845_l();
        }
    }
}

