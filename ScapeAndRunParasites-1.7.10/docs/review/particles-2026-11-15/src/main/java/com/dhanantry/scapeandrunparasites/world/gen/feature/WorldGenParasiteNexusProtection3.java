/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
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
import java.util.List;
import java.util.Random;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteNexusProtection3
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteNexusProtection3(boolean notify, int stage) {
        super(notify, stage);
        this.wall = BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER));
        this.floor = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(World worldIn, Random rand, BlockPos posss) {
        int max;
        int min;
        int radius = 8;
        int steps = 64;
        IBlockState pillarBlock = BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.FUNGUS));
        IBlockState pillarBlock2 = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER));
        List<BlockPos> circle = this.getCirclePoints(posss, radius, steps);
        for (BlockPos pos : circle) {
            min = 1;
            max = 5;
            this.generatePillar(worldIn, pos, worldIn.rand.nextInt(max - min + 1) + min, pillarBlock, pillarBlock2);
        }
        pillarBlock = BlockStates.defaultState(SRPBlocks.ParasiteFog);
        while (radius > 0) {
            circle = this.getCirclePoints(posss, --radius, steps);
            for (BlockPos pos : circle) {
                min = 7;
                max = 14;
                this.generatePillar(worldIn, pos, worldIn.rand.nextInt(max - min + 1) + min, pillarBlock, pillarBlock);
            }
        }
        this.generateCircle(BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER)), BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.rand, posss.down(5), 8, 8, 5, 2, 0);
        this.generateCircle(BlockStates.defaultState(SRPBlocks.ParasiteFog), BlockStates.defaultState(SRPBlocks.ParasiteFog), worldIn, worldIn.rand, posss.down(5), 4, 4, 5, 20000000, 0);
        this.replaceCircleGround(worldIn, posss.down(), 8, pillarBlock2);
        return true;
    }
}

