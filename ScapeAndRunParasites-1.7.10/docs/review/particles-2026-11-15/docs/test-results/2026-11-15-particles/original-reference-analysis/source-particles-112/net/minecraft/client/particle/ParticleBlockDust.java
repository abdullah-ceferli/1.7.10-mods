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
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

public class ParticleBlockDust
extends ParticleDigging {
    protected ParticleBlockDust(World world, double d, double d2, double d3, double d4, double d5, double d6, IBlockState iBlockState) {
        super(world, d, d2, d3, d4, d5, d6, iBlockState);
        this.field_187129_i = d4;
        this.field_187130_j = d5;
        this.field_187131_k = d6;
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        @Nullable
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            IBlockState iBlockState = Block.func_176220_d(nArray[0]);
            if (iBlockState.func_185901_i() == EnumBlockRenderType.INVISIBLE) {
                return null;
            }
            return new ParticleBlockDust(world, d, d2, d3, d4, d5, d6, iBlockState).func_174845_l();
        }
    }
}

