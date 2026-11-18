/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import java.util.Random;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WorldGenParasiteBush
extends WorldGenParasiteGenAbstract {
    private final IBlockState tallGrassState;
    private int type;
    private boolean parasite;

    public WorldGenParasiteBush(boolean notify, BlockParasiteBush.EnumType variant, int type) {
        super(notify);
        this.tallGrassState = BlockStates.defaultState(SRPBlocks.ParasiteBush).withProperty(BlockParasiteBush.VARIANT, (Comparable)((Object)variant));
        this.type = type;
        this.parasite = true;
    }

    public WorldGenParasiteBush(boolean notify, BlockInfestedBush.EnumType variant, int type) {
        super(notify);
        this.tallGrassState = BlockStates.defaultState(SRPBlocks.InfestedBush).withProperty(BlockInfestedBush.VARIANT, (Comparable)((Object)variant));
        this.type = type;
        this.parasite = false;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (this.type == 3) {
            this.flowerGen(worldIn, rand, position);
            return true;
        }
        if (this.type == 4) {
            for (int i = 0; i < 128; ++i) {
                BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                if (this.parasite) {
                    if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air || !SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || !this.checkFloor(worldIn, blockpos)) continue;
                    this.VinGen(worldIn, rand, blockpos);
                    continue;
                }
                if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air || !SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || !this.checkFloor(worldIn, blockpos)) continue;
                this.VinGen(worldIn, rand, blockpos);
            }
            this.VinGen(worldIn, rand, position);
            return true;
        }
        IBlockState iblockstate = BlockStates.get(worldIn, position);
        while ((iblockstate.getBlock() == Blocks.air || iblockstate.getBlock().isLeaves(iblockstate, (IBlockAccess)worldIn, position)) && position.getY() > 0) {
            position = position.down();
            iblockstate = BlockStates.get(worldIn, position);
        }
        switch (this.type) {
            case 1: {
                for (int i = 0; i < 128; ++i) {
                    BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                    if (this.parasite) {
                        if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air || !SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || !this.checkFloor(worldIn, blockpos)) continue;
                        BlockStates.set(worldIn, blockpos, this.tallGrassState, 2);
                        continue;
                    }
                    if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air || !SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || !this.checkFloor(worldIn, blockpos)) continue;
                    BlockStates.set(worldIn, blockpos, this.tallGrassState, 2);
                }
                break;
            }
            case 2: {
                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                    if (this.parasite) {
                        if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air || !SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || !this.checkFloor(worldIn, blockpos)) continue;
                        BlockStates.set(worldIn, blockpos, this.tallGrassState, 2);
                        continue;
                    }
                    if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air || !SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || !this.checkFloor(worldIn, blockpos)) continue;
                    BlockStates.set(worldIn, blockpos, this.tallGrassState, 2);
                }
                break;
            }
        }
        return true;
    }

    public boolean flowerGen(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (this.parasite) {
                if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air || blockpos.getY() >= 255 || !SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || !this.checkFloor(worldIn, blockpos)) continue;
                BlockStates.set(worldIn, blockpos, this.tallGrassState, 2);
                continue;
            }
            if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air || blockpos.getY() >= 255 || !SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || !this.checkFloor(worldIn, blockpos)) continue;
            BlockStates.set(worldIn, blockpos, this.tallGrassState, 2);
        }
        return true;
    }

    public boolean VinGen(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (BlockStates.get(worldIn, blockpos).getBlock() != Blocks.air) continue;
            int j = 1 + rand.nextInt(rand.nextInt(3) + 3);
            for (int k = 0; k < j; ++k) {
                if (this.parasite) {
                    if (!SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos.up(k), this.tallGrassState) || BlockStates.get(worldIn, blockpos.up(k)).getBlock() != Blocks.air) continue;
                    BlockStates.set(worldIn, blockpos.up(k), this.tallGrassState, 2);
                    continue;
                }
                if (!SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) || BlockStates.get(worldIn, blockpos.up(k)).getBlock() != Blocks.air) continue;
                BlockStates.set(worldIn, blockpos.up(k), this.tallGrassState, 2);
            }
        }
        return true;
    }

    private boolean checkFloor(World worldIn, BlockPos position) {
        return BlockStates.get(worldIn, position.down()).isFullCube();
    }
}

