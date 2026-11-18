/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeAbstract;
import java.util.Random;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteTreeThin
extends WorldGenParasiteTreeAbstract {
    public WorldGenParasiteTreeThin(boolean notify) {
        super(notify);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        BlockPos current;
        int i = 16;
        boolean flag = true;
        if (BlockStates.get(worldIn, position.down()).getBlock() == Blocks.air) {
            return false;
        }
        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;
                if (j == position.getY()) {
                    k = 0;
                }
                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < worldIn.getHeight()) {
                            if (this.isReplaceable(worldIn, (BlockPos)blockpos$mutableblockpos.setPos(l, j, i1))) continue;
                            flag = false;
                            continue;
                        }
                        flag = false;
                    }
                }
            }
            if (!flag) {
                return false;
            }
            current = position;
            while (!BlockStates.get(worldIn, current.down()).isFullBlock() && current.down().getY() >= 1) {
                current = current.down();
                this.placeTrunk(worldIn, current);
            }
        } else {
            return false;
        }
        current = position;
        current = this.placeColumn(worldIn, current, 3, rand, 0.0, 4);
        this.grow(worldIn, current.north(), rand, 1);
        this.grow(worldIn, current.east(), rand, 2);
        this.grow(worldIn, current.south(), rand, 3);
        this.grow(worldIn, current.west(), rand, 4);
        return true;
    }

    private void placeThin(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteThin));
    }

    private void placeCanister(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteCanister).withProperty(BlockParasiteCanister.VARIANT, (Comparable)((Object)BlockParasiteCanister.EnumType.LUMP)));
    }

    private void placeTrunk(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteTrunk).withProperty(BlockParasiteTrunk.VARIANT, (Comparable)((Object)BlockParasiteTrunk.EnumType.TREE)));
    }

    private BlockPos placeColumn(World worldIn, BlockPos pos, int times, Random rand, double extraChance, int minimum) {
        int current;
        int atm = current = pos.getY();
        BlockPos newPos = pos;
        times = rand.nextInt(times) + minimum;
        if (BlockStates.get(worldIn, pos.down()).getBlock() == Blocks.air && rand.nextDouble() <= extraChance) {
            this.placeCanister(worldIn, pos.down());
        }
        while (current < atm + times && minimum != 1) {
            this.placeThin(worldIn, newPos);
            newPos = newPos.up();
            ++current;
        }
        this.placeThin(worldIn, newPos);
        return newPos;
    }

    private void grow(World worldIn, BlockPos pos, Random rand, int direction) {
        pos = this.placeColumn(worldIn, pos, 2, rand, 0.3, 2);
        if (rand.nextInt(3) == 0) {
            pos = this.directionToGrow(pos, direction);
            pos = this.placeColumn(worldIn, pos, 3, rand, 0.5, 2);
            if (rand.nextInt(5) == 0) {
                pos = this.directionToGrow(pos, direction);
                pos = this.placeColumn(worldIn, pos, 3, rand, 0.7, 2);
            } else {
                pos = this.directionToGrow(pos, direction);
                pos = this.placeColumn(worldIn, pos, 1, rand, 0.7, 1);
            }
        } else {
            pos = this.directionToGrow(pos, direction);
            pos = this.placeColumn(worldIn, pos, 1, rand, 0.5, 1);
        }
    }

    private BlockPos directionToGrow(BlockPos pos, int choice) {
        switch (choice) {
            case 1: {
                return pos.north();
            }
            case 2: {
                return pos.east();
            }
            case 3: {
                return pos.south();
            }
        }
        return pos.west();
    }
}

