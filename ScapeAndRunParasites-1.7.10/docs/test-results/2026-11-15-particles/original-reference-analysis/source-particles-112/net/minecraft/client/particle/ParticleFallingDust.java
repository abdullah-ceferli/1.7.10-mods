/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleFallingDust
extends Particle {
    float field_190018_a;
    final float field_190019_b;

    protected ParticleFallingDust(World world, double d, double d2, double d3, float f, float f2, float f3) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187129_i = 0.0;
        this.field_187130_j = 0.0;
        this.field_187131_k = 0.0;
        this.field_70552_h = f;
        this.field_70553_i = f2;
        this.field_70551_j = f3;
        \u2603 = 0.9f;
        this.field_70544_f *= 0.75f;
        this.field_70544_f *= 0.9f;
        this.field_190018_a = this.field_70544_f;
        this.field_70547_e = (int)(32.0 / (Math.random() * 0.8 + 0.2));
        this.field_70547_e = (int)((float)this.field_70547_e * 0.9f);
        this.field_190019_b = ((float)Math.random() - 0.5f) * 0.1f;
        this.field_190014_F = (float)Math.random() * ((float)Math.PI * 2);
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70546_d + f) / (float)this.field_70547_e * 32.0f;
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        this.field_70544_f = this.field_190018_a * \u2603;
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
        this.field_190015_G = this.field_190014_F;
        this.field_190014_F += (float)Math.PI * this.field_190019_b * 2.0f;
        if (this.field_187132_l) {
            this.field_190014_F = 0.0f;
            this.field_190015_G = 0.0f;
        }
        this.func_70536_a(7 - this.field_70546_d * 8 / this.field_70547_e);
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187130_j -= (double)0.003f;
        this.field_187130_j = Math.max(this.field_187130_j, (double)-0.14f);
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        @Nullable
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            IBlockState iBlockState = Block.func_176220_d(nArray[0]);
            if (iBlockState.func_177230_c() != Blocks.field_150350_a && iBlockState.func_185901_i() == EnumBlockRenderType.INVISIBLE) {
                return null;
            }
            int \u26032 = Minecraft.func_71410_x().func_184125_al().func_189991_a(iBlockState, world, new BlockPos(d, d2, d3));
            if (iBlockState.func_177230_c() instanceof BlockFalling) {
                \u26032 = ((BlockFalling)iBlockState.func_177230_c()).func_189876_x(iBlockState);
            }
            float \u26033 = (float)(\u26032 >> 16 & 0xFF) / 255.0f;
            float \u26034 = (float)(\u26032 >> 8 & 0xFF) / 255.0f;
            float \u26035 = (float)(\u26032 & 0xFF) / 255.0f;
            return new ParticleFallingDust(world, d, d2, d3, \u26033, \u26034, \u26035);
        }
    }
}

