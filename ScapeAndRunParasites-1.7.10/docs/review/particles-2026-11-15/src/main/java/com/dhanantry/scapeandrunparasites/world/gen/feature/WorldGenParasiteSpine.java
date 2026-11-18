/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockSlab$EnumBlockHalf
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.BlockStairs$EnumHalf
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import java.util.Random;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteSpine
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteSpine(boolean notify) {
        super(notify);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int atmm;
        BlockPos twoCurrent;
        int partner;
        BlockPos current;
        int i = 28;
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
            current = position;
            partner = rand.nextInt(4);
            twoCurrent = this.directionToGrow(current, partner, false);
            atmm = (partner + 1) % 4;
            while (!BlockStates.get(worldIn, current.down()).isFullBlock() && current.down().getY() >= 1) {
                current = current.down();
                twoCurrent = twoCurrent.down();
                this.placeTrunk(worldIn, current);
                this.placeTrunk(worldIn, twoCurrent);
                for (int z = 0; z <= 3; ++z) {
                    this.placeTrunk(worldIn, this.directionToGrow(current, z, false));
                    this.placeTrunk(worldIn, this.directionToGrow(twoCurrent, z, false));
                }
            }
        } else {
            return false;
        }
        current = position;
        twoCurrent = this.directionToGrow(current, partner, false);
        this.placeStair(worldIn, current, atmm, true, 1, false);
        current = this.placeColumn(worldIn, current, 4, rand, partner, 27, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        this.placeColumn(worldIn, this.directionToGrow(position, (atmm + 2) % 4, false), 5, rand, partner, -1, false);
        this.placeStair(worldIn, twoCurrent, atmm, true, 1, false);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, 4, rand, (partner + 2) % 4, 27, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        this.placeColumn(worldIn, this.directionToGrow(this.directionToGrow(position, partner, false), (atmm + 2) % 4, false), 5, rand, partner, -1, false);
        this.placeStair(worldIn, twoCurrent.down(4), partner, true, 1, false);
        this.placeStair(worldIn, current.down(4), (partner + 2) % 4, true, 1, false);
        current = this.directionToGrow(current.up(), atmm, true);
        current = this.placeColumn(worldIn, current, 2, rand, partner, 14, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        twoCurrent = this.directionToGrow(twoCurrent.up(), atmm, true);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, 2, rand, (partner + 2) % 4, 14, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        int randomG = rand.nextInt(4) + 3;
        current = this.directionToGrow(current.up(), atmm, true);
        this.placeStair(worldIn, current.down(), (atmm + 2) % 4, false, 0, false);
        current = this.placeColumn(worldIn, current, randomG, rand, partner, 7, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        twoCurrent = this.directionToGrow(twoCurrent.up(), atmm, true);
        this.placeStair(worldIn, twoCurrent.down(), (atmm + 2) % 4, false, 0, false);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 7, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        randomG = rand.nextInt(5) + 6;
        current = this.directionToGrow(current.up(), atmm, true);
        this.placeStair(worldIn, current.down(), (atmm + 2) % 4, false, 0, false);
        current = this.placeColumn(worldIn, current, randomG, rand, partner, 2, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        twoCurrent = this.directionToGrow(twoCurrent.up(), atmm, true);
        this.placeStair(worldIn, twoCurrent.down(), (atmm + 2) % 4, false, 0, false);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 2, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        randomG = rand.nextInt(5) + 6;
        current = this.directionToGrow(current.up(), atmm, true);
        this.placeStair(worldIn, current.down(), (atmm + 2) % 4, false, 0, false);
        current = this.placeColumn(worldIn, current, randomG, rand, partner, 0, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        twoCurrent = this.directionToGrow(twoCurrent.up(), atmm, true);
        this.placeStair(worldIn, twoCurrent.down(), (atmm + 2) % 4, false, 0, false);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 0, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        return true;
    }

    private void placeStair(World worldIn, BlockPos pos, int direction, boolean bottom, int times, boolean slab) {
        switch (direction) {
            case 0: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.north(times), BlockStates.defaultState(SRPBlocks.ParasiteRubbleBoneStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.SOUTH).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM));
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.north(times), BlockStates.defaultState(SRPBlocks.ParasiteRubbleBoneStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.SOUTH).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.TOP));
                if (!slab) break;
                this.setBlockAndNotifyAdequately(worldIn, pos.north(times + 1), BlockStates.defaultState(SRPBlocks.ParasiteRubbleSlabHalf).withProperty((IProperty)BlockSlab.HALF, (Comparable)BlockSlab.EnumBlockHalf.TOP));
                break;
            }
            case 1: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.east(times), BlockStates.defaultState(SRPBlocks.ParasiteRubbleBoneStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.WEST).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM));
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.east(times), BlockStates.defaultState(SRPBlocks.ParasiteRubbleBoneStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.WEST).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.TOP));
                if (!slab) break;
                this.setBlockAndNotifyAdequately(worldIn, pos.east(times + 1), BlockStates.defaultState(SRPBlocks.ParasiteRubbleSlabHalf).withProperty((IProperty)BlockSlab.HALF, (Comparable)BlockSlab.EnumBlockHalf.TOP));
                break;
            }
            case 3: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.west(times), BlockStates.defaultState(SRPBlocks.ParasiteRubbleBoneStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.EAST).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM));
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.west(times), BlockStates.defaultState(SRPBlocks.ParasiteRubbleBoneStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.EAST).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.TOP));
                if (!slab) break;
                this.setBlockAndNotifyAdequately(worldIn, pos.west(times + 1), BlockStates.defaultState(SRPBlocks.ParasiteRubbleSlabHalf).withProperty((IProperty)BlockSlab.HALF, (Comparable)BlockSlab.EnumBlockHalf.TOP));
                break;
            }
            default: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.south(times), BlockStates.defaultState(SRPBlocks.ParasiteRubbleBoneStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.NORTH).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM));
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.south(times), BlockStates.defaultState(SRPBlocks.ParasiteRubbleBoneStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.NORTH).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.TOP));
                if (!slab) break;
                this.setBlockAndNotifyAdequately(worldIn, pos.south(times + 1), BlockStates.defaultState(SRPBlocks.ParasiteRubbleSlabHalf).withProperty((IProperty)BlockSlab.HALF, (Comparable)BlockSlab.EnumBlockHalf.TOP));
            }
        }
    }

    private void placeTrunk(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteRubble));
    }

    private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, int direction, int slabs, boolean stair) {
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in) {
            if (times % 2 == 0 && stair) {
                if (slabs > 20) {
                    this.placeStair(worldIn, newPos, direction, false, 2, false);
                    slabs -= 10;
                } else if (slabs > 10) {
                    this.placeStair(worldIn, newPos, direction, false, 2, false);
                    this.placeStair(worldIn, newPos.up(), (direction + 2) % 4, true, -2, false);
                    this.placeStair(worldIn, newPos.up(), direction, false, 3, (slabs -= 10) > 5);
                } else {
                    this.placeStair(worldIn, newPos, direction, false, 2, slabs > 0);
                    --slabs;
                }
            }
            this.placeTrunk(worldIn, newPos);
            newPos = newPos.up();
            ++current;
            ++times;
        }
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

