/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import java.util.Random;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteColonyB1
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteColonyB1(boolean notify, int stage) {
        super(notify, stage);
        this.wall = BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER));
        this.floor = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(World worldIn, Random rand, BlockPos posss) {
        BlockPos enter = posss;
        this.replaceCircleGround(worldIn, posss.down(), 12, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.down(2), 12, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.down(3), 12, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.RED)));
        int missing = 40;
        int height = 22 + rand.nextInt(3);
        int xx = 2;
        int zz = 2;
        int tic = 2;
        int cool = 3;
        int changeX = 0;
        int changeZ = 0;
        for (int i = 0; i < height; ++i) {
            --changeZ;
            if (worldIn.rand.nextInt(2) == 0 && --changeX <= 0) {
                if (worldIn.rand.nextInt(3) == 0) {
                    xx = Math.min(9, xx + 2);
                    zz = Math.min(9, zz + 2);
                } else {
                    xx = Math.max(3, xx - 1);
                    zz = Math.max(3, zz - 1);
                }
                changeX = cool;
            }
            this.generateCircle(BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.BONE)), BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.rand, posss, xx, zz, 1, 20000, 6);
            int hx = xx - tic;
            int hz = zz - tic;
            this.generateCircle(BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FLESH)), BlockStates.defaultState(Blocks.BONE_BLOCK), worldIn, worldIn.rand, posss, hx, hz, 1, 20000, 6);
            posss = posss.up(1);
        }
        BlockPos he = posss;
        int aa = zz;
        int bonusH = rand.nextInt(5);
        posss = posss.up(18 + bonusH - zz / 2 * 2);
        if (rand.nextBoolean()) {
            this.generateSphere(worldIn, posss, zz + 1, 2, rand, false, 1, false, 1, 1, 5, BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.FLESH)), BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FLESH)), BlockStates.defaultState(Blocks.air), missing);
        }
        this.generateDNAHelix(BlockStates.defaultState(Blocks.BONE_BLOCK), worldIn, worldIn.rand, he.down(4), aa - 2, 2, 11 + bonusH);
        this.generateDNAHelix(BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.rand, he.down(3), aa - 2, 2, 11 + bonusH);
        this.generateDNAHelix(BlockStates.defaultState(Blocks.BONE_BLOCK), worldIn, worldIn.rand, he.down(2), aa - 2, 2, 11 + bonusH);
        return true;
    }
}

