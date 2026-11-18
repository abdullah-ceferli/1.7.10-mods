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

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import java.util.Random;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteBall
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteBall(boolean notify) {
        super(notify);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i = 12;
        boolean flag = true;
        if (BlockStates.get(worldIn, position.down()).getBlock() == Blocks.air) {
            return false;
        }
        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 4;
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
            int extra = rand.nextInt(4);
            BlockPos current = position.up(2 + extra);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure("ball"), new Random(), worldIn, current, 3, 0, 3);
            BlockPos center = new BlockPos(position.getX(), position.getY(), position.getZ());
            boolean skip = true;
            for (int z = 1; z <= 4; ++z) {
                if (rand.nextDouble() <= 0.5 && skip) {
                    skip = false;
                    continue;
                }
                BlockPos root = center.up(3 + extra);
                root = this.getDirectionRoot(root, z, 3);
                this.placeThin(worldIn, root);
                root = root.down();
                this.placeThin(worldIn, root);
                root = this.getDirectionRoot(root, z, 1);
                this.placeThin(worldIn, root);
                while (!BlockStates.get(worldIn, root.down()).isFullBlock()) {
                    root = root.down();
                    this.placeThin(worldIn, root);
                }
                for (int j = 1; j <= 4; ++j) {
                    BlockPos roots = this.getDirectionRoot(root, j, 1);
                    if (BlockStates.get(worldIn, roots).isFullBlock()) continue;
                    this.placeThin(worldIn, roots);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private void placeThin(World worldIn, BlockPos pos) {
        if (BlockStates.get(worldIn, pos).getBlock() == Blocks.air) {
            this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteThin));
        } else {
            this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteTrunk).withProperty(BlockParasiteTrunk.VARIANT, (Comparable)((Object)BlockParasiteTrunk.EnumType.TREE)));
        }
    }

    private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
        switch (direction) {
            case 1: {
                return center.north(times);
            }
            case 2: {
                return center.east(times);
            }
            case 3: {
                return center.south(times);
            }
        }
        return center.west(times);
    }
}

