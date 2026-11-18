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

import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import java.util.Random;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteTenFlower
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteTenFlower(boolean notify) {
        super(notify);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i = 12;
        boolean flag = true;
        if (BlockStates.get(worldIn, position.down()).getBlock() == Blocks.air) {
            return false;
        }
        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
            int dir;
            int zs;
            int xs;
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
            int lag = 0;
            BlockPos current = position;
            while (!BlockStates.get(worldIn, current.down()).isFullBlock() && current.down().getY() >= 1) {
                current = current.down();
                for (int yyy = 0; yyy <= lag; ++yyy) {
                    for (xs = -1; xs <= 1; ++xs) {
                        for (zs = -1; zs <= 1; ++zs) {
                            this.placeTrunk(worldIn, new BlockPos(current.getX() + xs, current.getY(), current.getZ() + zs));
                        }
                    }
                }
            }
            current = position;
            lag = rand.nextInt(2) + 1;
            for (int yyy = 0; yyy <= lag; ++yyy) {
                for (xs = -1; xs <= 1; ++xs) {
                    for (zs = -1; zs <= 1; ++zs) {
                        this.placeTrunk(worldIn, new BlockPos(current.getX() + xs, current.getY(), current.getZ() + zs));
                    }
                }
                current = current.up();
            }
            BlockPos helper = current = current.down();
            int times = 0;
            int dirHelper = 0;
            for (dir = 0; dir <= 3; ++dir) {
                helper = current;
                helper = this.getDirectionRoot(helper, dir, 2);
                this.placeTrunk(worldIn, helper);
            }
            current = current.up();
            for (dir = 0; dir <= 3; ++dir) {
                helper = current;
                lag = rand.nextInt(2) + 3;
                for (times = 1; times <= lag; ++times) {
                    helper = this.getDirectionRoot(helper, dir, 1);
                    this.placeTrunk(worldIn, helper);
                }
                helper = helper.up();
                this.placeTrunk(worldIn, helper);
                helper = this.getDirectionRoot(helper, dir, 1);
                this.placeTrunk(worldIn, helper);
                BlockPos posHelper0 = helper;
                for (int help = 1; help <= 3; help += 2) {
                    BlockPos posHelper1;
                    int firrr;
                    helper = posHelper0;
                    dirHelper = (dir + help) % 4;
                    helper = this.getDirectionRoot(helper, dirHelper, 1);
                    helper = helper.up();
                    BlockPos posHelper2 = helper = this.placeColumn(worldIn, helper, rand.nextInt(3) + 1, rand, 0.0);
                    int n = firrr = help == 1 && (dir == 0 || dir == 3) ? 0 : 1;
                    if (dir == 2 && help == 3) {
                        firrr = 0;
                    }
                    if (dir == 1 && help == 3) {
                        firrr = 0;
                    }
                    helper = posHelper2;
                    helper = this.directionToGrow(helper.down(), dir * 10 + firrr, true);
                    helper = posHelper1 = (helper = this.placeColumn(worldIn, helper, rand.nextInt(3) + 1, rand, 0.0));
                    helper = this.directionToGrow(helper, dir * 10 + firrr, true);
                    helper = this.placeColumn(worldIn, helper.down(), rand.nextInt(3) + 1, rand, 0.0);
                }
            }
            current = this.placeColumn(worldIn, current, rand.nextInt(4) + 4, rand, 0.0);
            for (dir = 0; dir <= 3; ++dir) {
                helper = this.placeColumn(worldIn, this.getDirectionRoot(current, dir, 1), rand.nextInt(3) + 2, rand, 0.0);
                int glob = 3;
                helper = helper.down(2);
                for (int kkk = 0; kkk <= glob; ++kkk) {
                    if (kkk == 0) {
                        this.placeColumn(worldIn, this.getDirectionRoot(helper, dir, 1), 1, rand, 0.0);
                        helper = helper.up();
                    }
                    helper = this.getDirectionRoot(helper, dir, kkk == 0 ? 2 : 1);
                    helper = this.placeColumn(worldIn, helper, rand.nextInt(4) + 1, rand, 0.0);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private void placeTrunk(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER)));
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
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in) {
            this.placeTrunk(worldIn, newPos);
            newPos = newPos.up();
            ++current;
            ++times;
        }
        return newPos;
    }

    private BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
        atm = atm.up();
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
                    atm = atm.north();
                    break;
                }
                case 11: {
                    atm = atm.east();
                    atm = atm.south();
                    break;
                }
                case 20: {
                    atm = atm.south();
                    atm = atm.east();
                    break;
                }
                case 21: {
                    atm = atm.south();
                    atm = atm.west();
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
}

