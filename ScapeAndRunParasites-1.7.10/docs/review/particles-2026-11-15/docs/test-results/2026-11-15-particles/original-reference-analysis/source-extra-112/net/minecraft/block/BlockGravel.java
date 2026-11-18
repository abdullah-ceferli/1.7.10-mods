/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockGravel
extends BlockFalling {
    @Override
    public Item func_180660_a(IBlockState iBlockState, Random random, int n) {
        if (n > 3) {
            n = 3;
        }
        if (random.nextInt(10 - n * 3) == 0) {
            return Items.field_151145_ak;
        }
        return super.func_180660_a(iBlockState, random, n);
    }

    @Override
    public MapColor func_180659_g(IBlockState iBlockState, IBlockAccess iBlockAccess, BlockPos blockPos) {
        return MapColor.field_151665_m;
    }

    @Override
    public int func_189876_x(IBlockState iBlockState) {
        return -8356741;
    }
}

