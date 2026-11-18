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

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import java.util.Random;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteNexusProtection2
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteNexusProtection2(boolean notify, int stage) {
        super(notify, stage);
        this.wall = BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER));
        this.floor = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(World worldIn, Random rand, BlockPos posss) {
        int radius = 6;
        int height = 5;
        IBlockState pillarBlock = BlockStates.defaultState(Blocks.stone);
        this.generateRandomPillar(worldIn, posss.down(1), radius, height, pillarBlock);
        radius = 9;
        height = 9;
        pillarBlock = BlockStates.defaultState(Blocks.stone);
        this.generateRandomPillar(worldIn, posss.down(1), radius, height, pillarBlock);
        return true;
    }

    public void generateRandomPillar(World worldIn, BlockPos center, int radius, int height, IBlockState blockState) {
        Random rand = new Random();
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        BlockPos basePos = this.getCirclePoint(center, radius, theta);
        if ((basePos = ParasiteEventEntity.getFloor(worldIn, basePos, 7)) == null) {
            return;
        }
        basePos = basePos.down();
        String out = "beckon_";
        int outt = worldIn.rand.nextInt(4) + 2;
        out = out + outt + "x" + outt;
        switch (outt) {
            case 2: {
                out = out + "_1";
                break;
            }
            case 3: {
                switch (worldIn.rand.nextInt(5) + 1) {
                    case 1: {
                        out = out + "_1";
                        break;
                    }
                    case 2: {
                        out = out + "_2";
                        break;
                    }
                    case 3: {
                        out = out + "_3";
                        break;
                    }
                    case 4: {
                        out = out + "_4";
                        break;
                    }
                    case 5: {
                        out = out + "_5";
                    }
                }
                break;
            }
            case 4: {
                switch (worldIn.rand.nextInt(2) + 1) {
                    case 1: {
                        out = out + "_1";
                        break;
                    }
                    case 2: {
                        out = out + "_2";
                    }
                }
                break;
            }
            case 5: {
                out = out + "_1";
            }
        }
        System.out.println("ds aa222 " + out);
        WorldGenCustomStructures.generateInPosition(new WorldGenStructure(out), rand, worldIn, basePos, 0, 0, 0);
    }
}

