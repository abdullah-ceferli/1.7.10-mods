/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBuglin
extends BlockBase {
    protected static final AxisAlignedBB TALL_GRASS_AABB = Boxes.create(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);

    public BlockBuglin(String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
        super(Material.sponge, name, hardness, creative, tickRandom, resistance);
        this.setStepSound(SRPSoundTypes.TUNNEL);
        this.setDefaultState(this.blockState.getBaseState());
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3)) {
            return;
        }
        if (DifficultyAccess.difficulty(worldIn) == EnumDifficulty.PEACEFUL) {
            return;
        }
        if (!SRPConfigMobs.lodoEnabled) {
            return;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), 1.0, 1.0, 1.0);
        List moblist = EntityQueries.entities(worldIn, EntityLodo.class, axisalignedbb);
        if (!moblist.isEmpty()) {
            return;
        }
        axisalignedbb = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), 16.0, 16.0, 16.0);
        moblist = EntityQueries.entities(worldIn, EntityParasiteBase.class, axisalignedbb);
        if (moblist.size() > 10) {
            return;
        }
        EntityLodo l222 = new EntityLodo(worldIn);
        l222.setPosition((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
        l222.setFloorTimer();
        worldIn.spawnEntityInWorld((Entity)l222);
        worldIn.setEntityState((Entity)l222, (byte)50);
    }

    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return TALL_GRASS_AABB;
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    protected boolean canBlockStay(World worldIn, BlockPos pos) {
        BlockPos down = pos.down();
        IBlockState below = BlockStates.get(worldIn, down);
        return below.isSideSolid((IBlockAccess)worldIn, down, EnumFacing.UP);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        if (!worldIn.isRemote && !this.canBlockStay(worldIn, pos)) {
            WorldAccess.destroyBlock(worldIn, pos, false);
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!worldIn.isRemote && !this.canBlockStay(worldIn, pos)) {
            WorldAccess.destroyBlock(worldIn, pos, false);
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        Entity ent;
        if (!worldIn.isRemote && WorldAccess.isAreaLoaded(worldIn, pos, 1) && DifficultyAccess.difficulty(worldIn) != EnumDifficulty.PEACEFUL && (ent = EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation("srparasites", "buglin"), (World)worldIn)) != null) {
            ent.setPosition((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
            worldIn.spawnEntityInWorld(ent);
        }
        super.breakBlock(worldIn, pos, state);
    }
}

