/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFalling
extends Block {
    public static boolean field_149832_M;

    public BlockFalling() {
        super(Material.field_151595_p);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    public BlockFalling(Material material) {
        super(material);
    }

    @Override
    public void func_176213_c(World world, BlockPos blockPos, IBlockState iBlockState) {
        world.func_175684_a(blockPos, this, this.func_149738_a(world));
    }

    @Override
    public void func_189540_a(IBlockState iBlockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
        world.func_175684_a(blockPos, this, this.func_149738_a(world));
    }

    @Override
    public void func_180650_b(World world, BlockPos blockPos, IBlockState iBlockState, Random random) {
        if (!world.field_72995_K) {
            this.func_176503_e(world, blockPos);
        }
    }

    private void func_176503_e(World world2, BlockPos blockPos) {
        World world2;
        if (!BlockFalling.func_185759_i(world2.func_180495_p(blockPos.func_177977_b())) || blockPos.func_177956_o() < 0) {
            return;
        }
        int n = 32;
        if (field_149832_M || !world2.func_175707_a(blockPos.func_177982_a(-32, -32, -32), blockPos.func_177982_a(32, 32, 32))) {
            world2.func_175698_g(blockPos);
            BlockPos blockPos2 = blockPos.func_177977_b();
            while (BlockFalling.func_185759_i(world2.func_180495_p(blockPos2)) && blockPos2.func_177956_o() > 0) {
                blockPos2 = blockPos2.func_177977_b();
            }
            if (blockPos2.func_177956_o() > 0) {
                world2.func_175656_a(blockPos2.func_177984_a(), this.func_176223_P());
            }
        } else if (!world2.field_72995_K) {
            EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world2, (double)blockPos.func_177958_n() + 0.5, blockPos.func_177956_o(), (double)blockPos.func_177952_p() + 0.5, world2.func_180495_p(blockPos));
            this.func_149829_a(entityFallingBlock);
            world2.func_72838_d(entityFallingBlock);
        }
    }

    protected void func_149829_a(EntityFallingBlock entityFallingBlock) {
    }

    @Override
    public int func_149738_a(World world) {
        return 2;
    }

    public static boolean func_185759_i(IBlockState iBlockState) {
        Block block = iBlockState.func_177230_c();
        Material \u26032 = iBlockState.func_185904_a();
        return block == Blocks.field_150480_ab || \u26032 == Material.field_151579_a || \u26032 == Material.field_151586_h || \u26032 == Material.field_151587_i;
    }

    public void func_176502_a_(World world, BlockPos blockPos, IBlockState iBlockState, IBlockState iBlockState2) {
    }

    public void func_190974_b(World world, BlockPos blockPos) {
    }

    @Override
    public void func_180655_c(IBlockState iBlockState, World world, BlockPos blockPos, Random random) {
        if (random.nextInt(16) == 0 && BlockFalling.func_185759_i(world.func_180495_p(\u2603 = blockPos.func_177977_b()))) {
            double d = (float)blockPos.func_177958_n() + random.nextFloat();
            \u2603 = (double)blockPos.func_177956_o() - 0.05;
            \u2603 = (float)blockPos.func_177952_p() + random.nextFloat();
            world.func_175688_a(EnumParticleTypes.FALLING_DUST, d, \u2603, \u2603, 0.0, 0.0, 0.0, Block.func_176210_f(iBlockState));
        }
    }

    public int func_189876_x(IBlockState iBlockState) {
        return -16777216;
    }
}

