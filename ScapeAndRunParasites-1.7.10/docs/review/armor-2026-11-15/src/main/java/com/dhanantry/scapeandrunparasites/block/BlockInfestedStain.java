/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.IStagedBlock;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockInfestedStain
extends BlockParasiteSpreading
implements IStagedBlock {
    public static final PropertyInteger STAGE = PropertyInteger.create((String)"stage", (int)0, (int)5);
    public static final PropertyBool SNOWY = PropertyBool.create((String)"snowy");

    @Override
    public PropertyInteger getStageProperty() {
        return STAGE;
    }

    public BlockInfestedStain(Material material, String name, float hardness, boolean creative, boolean infested) {
        super(material, name, hardness, creative, infested);
        this.setHarvestLevel("shovel", 0);
        this.setStepSound(SRPSoundTypes.BECKON);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(0)).withProperty((IProperty)SNOWY, (Comparable)Boolean.FALSE));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{STAGE, SNOWY});
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue((IProperty)STAGE);
    }

    public IBlockState getStateFromMeta(int meta) {
        if (meta >= 5) {
            meta = 5;
        }
        return BlockStates.defaultState(this).withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(meta & 7));
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState above = BlockStates.get(worldIn, pos.up());
        boolean snowy = above.getBlock() == Blocks.snow || above.getBlock() == Blocks.snow_layer;
        return state.withProperty((IProperty)SNOWY, (Comparable)Boolean.valueOf(snowy));
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        BlockPos above = pos.up();
        boolean openAbove = WorldAccess.isAirBlock(worldIn, above) || !BlockStates.get(worldIn, above).isSideSolid((IBlockAccess)worldIn, above, EnumFacing.DOWN);
        if (openAbove & rand.nextFloat() < 0.001f) {
            double x = (double)pos.getX() + 0.5 + rand.nextDouble() * 0.5;
            double y = (double)pos.getY() + 1.0;
            double z = (double)pos.getZ() + 0.5 + rand.nextDouble() * 0.5;
            ParticleSpawner.spawnParticle(SRPEnumParticle.DOT, x, y, z, 0.0, 0.0, 0.0, 17, 74, 20);
        }
        if (BlockStates.get(worldIn, pos.up()).getBlock() != Blocks.air && BlockStates.get(worldIn, pos.up()).getBlock() != SRPBlocks.ParasiteBush) {
            return;
        }
        int stage = BlockStates.meta(this, stateIn);
        if (stage == 2) {
            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleS) {
                double d0 = (double)pos.getX() + rand.nextDouble();
                double d1 = (double)pos.getY() + 2.5;
                double d2 = (double)pos.getZ() + rand.nextDouble();
                ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0, 0.0, 0.0, 0, 0, 0);
            }
        } else if (stage == 3) {
            double d2;
            double d1;
            double d0;
            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleF) {
                d0 = (double)pos.getX() + rand.nextDouble();
                d1 = (double)pos.getY() + 1.5;
                d2 = (double)pos.getZ() + rand.nextDouble();
                int g = 200;
                if (rand.nextInt(3) == 0) {
                    g += 50;
                }
                ParticleSpawner.spawnParticle(SRPEnumParticle.FOG, d0, d1, d2, 0.0, 1.0E-4, 0.0, 0, g, 0);
            }
            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleS) {
                d0 = (double)pos.getX() + rand.nextDouble();
                d1 = (double)pos.getY() + 2.5;
                d2 = (double)pos.getZ() + rand.nextDouble();
                ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0, 0.0, 0.0, 0, 0, 0);
            }
        }
    }
}

