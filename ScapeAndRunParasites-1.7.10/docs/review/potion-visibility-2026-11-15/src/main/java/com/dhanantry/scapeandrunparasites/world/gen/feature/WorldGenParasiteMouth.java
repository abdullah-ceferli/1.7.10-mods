/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.BlockStairs$EnumHalf
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import java.util.Random;
import net.minecraft.block.BlockStairs;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteMouth
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteMouth(boolean notify) {
        super(notify);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        this.genMouth(worldIn, rand, position);
        int rana = rand.nextInt(7) + 1;
        if (rand.nextBoolean()) {
            rana *= -1;
        }
        int x = rana + position.getX();
        int z = position.getZ();
        if (x == 7) {
            rana = rand.nextInt(7) + 1;
            if (rand.nextBoolean()) {
                rana *= -1;
            }
            z += rana;
        } else {
            rana = 7;
            if (rand.nextBoolean()) {
                rana *= -1;
            }
            z += rana;
        }
        BlockPos newp = new BlockPos(x, position.getY(), z);
        newp = ParasiteEventEntity.getFloor(worldIn, newp, 5);
        if (newp != null) {
            this.genMouth(worldIn, rand, newp);
        }
        return true;
    }

    private void genMouth(World worldIn, Random rand, BlockPos position) {
        for (int yyy = 0; yyy <= 2; ++yyy) {
            for (int xs = -2; xs <= 2; ++xs) {
                for (int zs = -2; zs <= 2; ++zs) {
                    if (BlockStates.get(worldIn, new BlockPos(position.getX() + xs, position.getY() + yyy, position.getZ() + zs)).getBlock() == Blocks.air) continue;
                    return;
                }
            }
        }
        BlockPos helper = position;
        this.placePeri(worldIn, position, 1, 2);
        this.placePeri(worldIn, position, 2, 1);
        BlockPos current = position;
        int i = 7;
        while (!(BlockStates.get(worldIn, current.down()).isFullBlock() && i < 0 || current.down().getY() <= 2)) {
            --i;
            this.setBlockAndNotifyAdequately(worldIn, current, BlockStates.defaultState(Blocks.air));
            current = current.down();
            this.placePeri(worldIn, current, 5, 1);
        }
        this.placeFlesh(worldIn, current);
    }

    private void placeMouth(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteMouth));
    }

    private void placeFlesh(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FLESH)));
    }

    private void placeFleshStair(World worldIn, BlockPos pos, int direction, boolean bottom, int times) {
        switch (direction) {
            case 0: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.north(times), BlockStates.defaultState(SRPBlocks.ParasiteStainFleshStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.SOUTH).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM));
                    this.placeColumn(worldIn, pos.north(times).down(), 2, null, 0.0);
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.north(times), BlockStates.defaultState(SRPBlocks.ParasiteStainFleshStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.SOUTH).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.TOP));
                break;
            }
            case 1: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.east(times), BlockStates.defaultState(SRPBlocks.ParasiteStainFleshStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.WEST).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM));
                    this.placeColumn(worldIn, pos.east(times).down(), 2, null, 0.0);
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.east(times), BlockStates.defaultState(SRPBlocks.ParasiteStainFleshStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.WEST).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.TOP));
                break;
            }
            case 3: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.west(times), BlockStates.defaultState(SRPBlocks.ParasiteStainFleshStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.EAST).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM));
                    this.placeColumn(worldIn, pos.west(times).down(), 2, null, 0.0);
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.west(times), BlockStates.defaultState(SRPBlocks.ParasiteStainFleshStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.EAST).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.TOP));
                break;
            }
            default: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.south(times), BlockStates.defaultState(SRPBlocks.ParasiteStainFleshStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.NORTH).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM));
                    this.placeColumn(worldIn, pos.south(times).down(), 2, null, 0.0);
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.south(times), BlockStates.defaultState(SRPBlocks.ParasiteStainFleshStair).withProperty((IProperty)BlockStairs.FACING, (Comparable)EnumFacing.NORTH).withProperty((IProperty)BlockStairs.HALF, (Comparable)BlockStairs.EnumHalf.TOP));
            }
        }
    }

    private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
        switch (direction) {
            case 0: {
                return center.north(times);
            }
            case 1: {
                return center.east(times);
            }
            case 2: {
                return center.south(times);
            }
        }
        return center.west(times);
    }

    private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, double extraChance) {
        int current = pos.getY();
        int currentY = pos.getY();
        int atm = current;
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in && currentY > 2) {
            --currentY;
            this.placeFlesh(worldIn, newPos);
            newPos = newPos.down();
            ++current;
            ++times;
        }
        return newPos;
    }

    private void placePeri(World worldIn, BlockPos position, int type, int area) {
        BlockPos helper = position;
        for (int i = 0; i <= 3; ++i) {
            helper = this.getDirectionRoot(position, i, area);
            this.placeColumn(worldIn, helper.down(), 2, null, 0.0);
            if (type == 5) {
                this.placeFlesh(worldIn, helper);
            } else if (type == 1) {
                this.placeFleshStair(worldIn, helper, i, true, 0);
            } else {
                this.placeMouth(worldIn, helper);
            }
            BlockPos rootH = helper;
            for (int o = 0; o < 2; ++o) {
                helper = o == 0 ? this.directionToGrow(rootH, (i + 1) % 4, false) : this.directionToGrow(rootH, (i + 3) % 4, false);
                this.placeColumn(worldIn, helper.down(), 2, null, 0.0);
                if (type == 5) {
                    this.placeFlesh(worldIn, helper);
                    continue;
                }
                if (type == 1) {
                    this.placeFleshStair(worldIn, helper, i, true, 0);
                    continue;
                }
                this.placeMouth(worldIn, helper);
            }
        }
    }

    private BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
        if (sideCurse) {
            switch (choice) {
                case 0: {
                    atm = atm.north();
                    atm = atm.east();
                    break;
                }
                case 1: {
                    atm = atm.north();
                    atm = atm.west();
                    break;
                }
                case 10: {
                    atm = atm.east();
                    atm = atm.south();
                    break;
                }
                case 11: {
                    atm = atm.east();
                    atm = atm.north();
                    break;
                }
                case 20: {
                    atm = atm.south();
                    atm = atm.west();
                    break;
                }
                case 21: {
                    atm = atm.south();
                    atm = atm.east();
                    break;
                }
                case 30: {
                    atm = atm.west();
                    atm = atm.north();
                    break;
                }
                default: {
                    atm = atm.west();
                    atm = atm.south();
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

    private void positionSides(int choice) {
    }
}

