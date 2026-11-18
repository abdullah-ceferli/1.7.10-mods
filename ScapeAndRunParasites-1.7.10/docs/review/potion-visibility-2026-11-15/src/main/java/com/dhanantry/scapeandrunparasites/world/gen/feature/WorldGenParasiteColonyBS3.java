/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
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
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteColonyBS3
extends WorldGenParasiteColonyBase {
    private IBlockState floor = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.DIRT));
    private IBlockState tacle = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER));
    private IBlockState wall = BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL));

    public WorldGenParasiteColonyBS3(boolean notify, int stage) {
        super(notify, stage);
    }

    public boolean generate(World worldIn, Random rand, BlockPos posss) {
        BlockPos enter = posss;
        this.replaceCircleGround(worldIn, posss.down(), 12, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.down(2), 8, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.down(3), 8, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.RED)));
        int radius = 3;
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        int missing = 40;
        this.generateSphere(worldIn, posss, 2, 10, rand, false, 3, false, 2, 1, 2, BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL)), BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.BRICKS)), BlockStates.defaultState(Blocks.air), missing);
        posss = enter;
        radius = 6;
        theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        this.generateSphere(worldIn, posss, 2, 10, rand, false, 3, false, 4, 2, 4, BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL)), BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.BRICKS)), BlockStates.defaultState(Blocks.air), missing);
        return true;
    }
}

