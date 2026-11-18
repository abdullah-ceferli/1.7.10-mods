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

public class WorldGenParasiteTree
extends WorldGenParasiteTreeAbstract {
    public WorldGenParasiteTree(boolean notify) {
        super(notify);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        BlockPos current;
        int i = 20;
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
            int first = rand.nextInt(4);
            current = this.directionToGrow(current, first, false);
            this.placeTrunk(worldIn, current);
            while (!BlockStates.get(worldIn, current.down()).isFullBlock() && current.down().getY() >= 1) {
                current = current.down();
                this.placeTrunk(worldIn, current);
            }
            current = position;
            current = this.directionToGrow(current, ++first, false);
            this.placeTrunk(worldIn, current);
            while (!BlockStates.get(worldIn, current.down()).isFullBlock() && current.down().getY() >= 1) {
                current = current.down();
                this.placeTrunk(worldIn, current);
            }
            current = position;
            while (!BlockStates.get(worldIn, current.down()).isFullBlock() && current.down().getY() >= 1) {
                current = current.down();
                this.placeTrunk(worldIn, current);
            }
            current = position;
            current = this.placeColumn(worldIn, current, rand.nextInt(3) + 5, rand, -1.0);
            first = rand.nextInt(4);
            current = this.directionToGrow(current, first, false);
            current = this.placeColumn(worldIn, current, rand.nextInt(3) + 5, rand, 0.3);
            if (rand.nextDouble() <= 0.3) {
                return true;
            }
            current = this.directionToGrow(current, rand.nextInt(4), false);
            current = this.placeColumn(worldIn, current, rand.nextInt(3) + 5, rand, 0.3);
            if (rand.nextDouble() <= 0.3) {
                return true;
            }
        } else {
            return false;
        }
        current = this.directionToGrow(current, rand.nextInt(4), false);
        current = this.placeColumn(worldIn, current, rand.nextInt(3) + 5, rand, 0.3);
        return true;
    }

    private void placeTrunk(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteTrunk).withProperty(BlockParasiteTrunk.VARIANT, (Comparable)((Object)BlockParasiteTrunk.EnumType.TREE)));
    }

    private void placeTrunk(World worldIn, BlockPos pos, int direction) {
        switch (direction) {
            case 1: {
                this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteTrunk).withProperty(BlockParasiteTrunk.VARIANT, (Comparable)((Object)BlockParasiteTrunk.EnumType.TREE)));
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
        }
    }

    private void placeCanister(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteCanister).withProperty(BlockParasiteCanister.VARIANT, (Comparable)((Object)BlockParasiteCanister.EnumType.SAC)));
    }

    private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, double extraChance) {
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        if (BlockStates.get(worldIn, pos.down()).getBlock() == Blocks.air && rand.nextDouble() <= 0.3 + extraChance) {
            this.placeCanister(worldIn, pos.down());
        }
        while (current < atm + in) {
            if (times == 2 && extraChance != -1.0) {
                int btwo;
                int bone = rand.nextInt(4);
                if (bone == (btwo = rand.nextInt(4))) {
                    ++bone;
                }
                this.addBranchs(worldIn, newPos, rand, rand.nextInt(3) + 1, bone);
                this.addBranchs(worldIn, newPos, rand, rand.nextInt(3) + 1, btwo);
            }
            this.placeTrunk(worldIn, newPos);
            newPos = newPos.up();
            ++current;
            ++times;
        }
        this.placeTrunk(worldIn, newPos);
        return newPos;
    }

    private void addBranchs(World worldIn, BlockPos pos, Random rand, int size, int direction) {
        pos = this.directionToGrow(pos, direction, false);
        this.placeTrunk(worldIn, pos);
        pos = pos.up();
        pos = this.directionToGrow(pos, direction, false);
        int curve = direction * 10 + rand.nextInt(2);
        for (int current = 0; current < size; ++current) {
            this.placeTrunk(worldIn, pos);
            if (rand.nextDouble() <= 0.25) {
                this.placeCanister(worldIn, pos.down());
            }
            pos = rand.nextDouble() <= 0.5 ? this.directionToGrow(pos, direction, false) : this.directionToGrow(pos, curve, true);
        }
        pos = pos.down();
        this.placeTrunk(worldIn, pos);
        if (rand.nextDouble() <= 0.25) {
            this.placeCanister(worldIn, pos.down());
        }
        if (rand.nextDouble() <= 0.5) {
            pos = rand.nextDouble() <= 0.5 ? this.directionToGrow(pos.down(), direction, false) : this.directionToGrow(pos.down(), curve, true);
            this.placeTrunk(worldIn, pos);
            if (rand.nextDouble() <= 0.25) {
                this.placeCanister(worldIn, pos.down());
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
                    atm = atm.north();
                    break;
                }
                case 11: {
                    atm = atm.east();
                    atm = atm.south();
                    break;
                }
                case 30: {
                    atm = atm.south();
                    atm = atm.east();
                    break;
                }
                case 31: {
                    atm = atm.south();
                    atm = atm.west();
                    break;
                }
                case 20: {
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
            case 2: {
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

