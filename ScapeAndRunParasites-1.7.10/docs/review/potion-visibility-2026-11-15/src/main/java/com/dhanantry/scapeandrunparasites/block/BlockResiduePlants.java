/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyDirection;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockResiduePlants
extends BlockBase {
    public static final PropertyDirection FACING = PropertyDirection.create((String)"facing");
    private static final AxisAlignedBB AABB_CENTER = Boxes.create(0.125, 0.0, 0.125, 0.875, 0.875, 0.875);

    private static boolean isResidueSupport(Block b) {
        return b == SRPBlocks.ResidueBlock || b == SRPBlocks.ResidueBricks;
    }

    public BlockResiduePlants(String name) {
        super(Material.plants, name, 0.0f, true, true, 0.0f);
        this.setStepSound(SRPSoundTypes.VOMIT);
        this.setLightOpacity(0);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.UP));
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Item item;
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        int count = 1 + rand.nextInt(2);
        if (fortune > 0) {
            count += rand.nextInt(fortune + 1);
        }
        if ((item = Item.setTextureName((String)"srparasites:infestedremain")) == null) {
            item = Item.setTextureName((String)"srparasites:infestremain");
        }
        if (item != null) {
            drops.add((Object)new ItemStack(item, count));
        }
    }

    protected boolean canSilkHarvest() {
        return true;
    }

    public boolean renderAsNormalBlock(IBlockState s) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState s) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return AABB_CENTER;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    private boolean canAttach(World world, BlockPos pos, EnumFacing facing) {
        BlockPos supportPos = pos.offset(facing.getOpposite());
        return BlockResiduePlants.isResidueSupport(BlockStates.get(world, supportPos).getBlock());
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing f : EnumFacing.values()) {
            if (!this.canAttach(world, pos, f)) continue;
            return true;
        }
        return false;
    }

    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return this.canAttach(world, pos, side);
    }

    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)side);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canAttach(world, pos, (EnumFacing)state.getValue((IProperty)FACING))) {
            WorldAccess.destroyBlock(world, pos, false);
        }
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        if (!this.canAttach(world, pos, (EnumFacing)state.getValue((IProperty)FACING))) {
            WorldAccess.destroyBlock(world, pos, false);
            return;
        }
        if (rand.nextInt(8) != 0) {
            return;
        }
        for (int tries = 0; tries < 3; ++tries) {
            EnumFacing f = EnumFacing.values()[rand.nextInt(6)];
            BlockPos newPos = pos.offset(f);
            if (!WorldAccess.isAirBlock(world, newPos) || !this.canAttach(world, newPos, f)) continue;
            BlockStates.set(world, newPos, BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)f), 2);
            break;
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing f = EnumFacing.getFront((int)MathHelper.clamp_int((int)meta, (int)0, (int)5));
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)f);
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)FACING)).getIndex();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{FACING});
    }
}

