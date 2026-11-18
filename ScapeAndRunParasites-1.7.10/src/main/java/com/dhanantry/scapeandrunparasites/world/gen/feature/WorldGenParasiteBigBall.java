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

public class WorldGenParasiteBigBall
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteBigBall(boolean notify) {
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
            int extra = rand.nextInt(6) + 2;
            BlockPos current = position.up(9 + extra);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure("ballbig"), new Random(), worldIn, current, 6, 0, 6);
            BlockPos center = new BlockPos(position.getX(), position.getY(), position.getZ());
            boolean skip = true;
            for (int z = 0; z <= 3; ++z) {
                if (rand.nextDouble() <= 0.15 && skip) {
                    skip = false;
                    continue;
                }
                BlockPos root = center.up(12 + extra);
                root = this.getDirectionRoot(root, z, 4);
                root = this.placeColumn(worldIn, root, 3, rand, 0.0);
                int direction = rand.nextInt(4);
                boolean glag = true;
                boolean side = false;
                while (glag) {
                    int grow = rand.nextInt(4) + 2;
                    root = this.directionToGrow(root.down(), direction, side);
                    if (rand.nextDouble() <= 0.2) {
                        BlockPos atm = this.directionToGrow(root, direction, !side);
                        this.placeColumn(worldIn, this.directionToGrow(atm, direction, !side), grow, rand, 0.0);
                    }
                    root = this.placeColumn(worldIn, root, grow, rand, 0.0);
                    root = this.directionToGrow(root.down(), direction, !side);
                    root = this.placeColumn(worldIn, root, rand.nextInt(2) + 2, rand, 0.0);
                    side = !side;
                    glag = BlockStates.get(worldIn, root).isFullBlock();
                    direction = rand.nextInt(4);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private void placeTrunk(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteTrunk).withProperty(BlockParasiteTrunk.VARIANT, (Comparable)((Object)BlockParasiteTrunk.EnumType.TREE)));
    }

    private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
        switch (direction) {
            case 0: {
                return center.north(times);
            }
            case 1: {
                return center.east(times);
            }
            case 3: {
                return center.west(times);
            }
        }
        return center.south(times);
    }

    private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, double extraChance) {
        int current;
        --in;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        while (current > atm - in) {
            this.placeTrunk(worldIn, newPos);
            newPos = newPos.down();
            --current;
            ++times;
        }
        this.placeTrunk(worldIn, newPos);
        return newPos;
    }

    private BlockPos directionToGrow(BlockPos atm, int choice, boolean reverse) {
        if (reverse) {
            switch (choice) {
                case 0: {
                    atm = atm.south();
                    break;
                }
                case 1: {
                    atm = atm.west();
                    break;
                }
                case 3: {
                    atm = atm.east();
                    break;
                }
                default: {
                    atm = atm.north();
                }
            }
            return atm;
        }
        switch (choice) {
            case 0: {
                atm = atm.north();
                break;
            }
            case 1: {
                atm = atm.east();
                break;
            }
            case 3: {
                atm = atm.west();
                break;
            }
            default: {
                atm = atm.south();
            }
        }
        return atm;
    }
}

