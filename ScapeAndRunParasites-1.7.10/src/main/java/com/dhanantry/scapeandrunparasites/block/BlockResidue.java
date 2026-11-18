/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockResiduePlants;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import java.util.Random;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockResidue
extends BlockBase {
    public BlockResidue(String name) {
        super(Material.rock, name, 1.5f, true, true, 10.0f);
        this.setStepSound(SRPSoundTypes.VOMIT);
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return true;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID;
    }

    private boolean tooManyNeighborPlants(World world, BlockPos pos) {
        int count = 0;
        for (EnumFacing f : EnumFacing.values()) {
            if (BlockStates.get(world, pos.offset(f)).getBlock() != SRPBlocks.ResiduePlants || ++count < 2) continue;
            return true;
        }
        return false;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        if (rand.nextInt(16) != 0) {
            return;
        }
        for (int tries = 0; tries < 3; ++tries) {
            EnumFacing face = EnumFacing.values()[rand.nextInt(6)];
            BlockPos plantPos = pos.offset(face);
            if (!WorldAccess.isAirBlock(world, plantPos) || this.tooManyNeighborPlants(world, plantPos) || !SRPBlocks.ResiduePlants.canPlaceBlockOnSide(world, plantPos, face)) continue;
            IBlockState plantState = BlockStates.defaultState(SRPBlocks.ResiduePlants).withProperty((IProperty)BlockResiduePlants.FACING, (Comparable)face);
            BlockStates.set(world, plantPos, plantState, 2);
            break;
        }
    }

    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (entity instanceof EntityPlayer && entity.onGround) {
            entity.motionX *= 0.5;
            entity.motionZ *= 0.5;
        }
        super.onEntityWalk(world, pos, entity);
    }
}

