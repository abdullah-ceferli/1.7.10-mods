/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.EnumPushReaction
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.BlockRenderLayer
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
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.Random;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBloodyIce
extends BlockParasiteSpreading {
    private final boolean ignoreSimilarity;

    public BlockBloodyIce(Material material, String name, float hardness, boolean creative, boolean infested) {
        super(material, name, hardness, creative, infested);
        this.slipperiness = 0.98f;
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.GLASS);
        this.ignoreSimilarity = false;
    }

    public BlockBloodyIce(Material material, String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
        this(material, name, hardness, creative, tickRandom);
        this.setResistance(resistance);
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.NORMAL;
    }

    protected boolean canSilkHarvest() {
        return true;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = BlockStates.get(blockAccess, pos.offset(side));
        Block block = iblockstate.getBlock();
        if (block == this) {
            return false;
        }
        return !this.ignoreSimilarity && block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        if (worldIn.isRemote) {
            return;
        }
        if (!(entityIn instanceof EntityPlayer)) {
            return;
        }
        if (!SRPConfigWorld.bloodyIceBreakOnHardLanding) {
            return;
        }
        if ((double)fallDistance < SRPConfigWorld.bloodyIceBreakFallDistance) {
            return;
        }
        int diameter = SRPConfigWorld.bloodyIceBreakDiameter;
        if (diameter < 1) {
            diameter = 1;
        }
        int radius = (diameter - 1) / 2;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                BlockPos p = pos.add(dx, 0, dz);
                IBlockState st = BlockStates.get(worldIn, p);
                if (st.getBlock() != this) continue;
                worldIn.playEvent(2001, p, BlockStates.stateId((IBlockState)st));
                WorldAccess.destroyBlock(worldIn, p, true);
            }
        }
    }
}

