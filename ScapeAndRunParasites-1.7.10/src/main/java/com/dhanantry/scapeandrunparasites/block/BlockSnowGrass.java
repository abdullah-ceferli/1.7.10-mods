/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.common.IShearable
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;

public class BlockSnowGrass
extends Block
implements IShearable {
    private static final AxisAlignedBB SHORT_GRASS_AABB = Boxes.create(0.1, 0.0, 0.1, 0.9, 1.0, 0.9);
    private static final AxisAlignedBB TALL_GRASS_AABB = Boxes.create(0.1, 0.0, 0.1, 0.9, 2.0, 0.9);
    private final boolean tallGrass;

    public BlockSnowGrass(String name, boolean tallGrass) {
        super(Material.snow);
        this.tallGrass = tallGrass;
        RegistryNames.set(this, "srparasites", name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setHardness(0.1f);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.PLANT);
        SRPBlocks.SRP_BLOCKS.add(this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlockSnowGrass(this), RegistryNames.get(this)));
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (world.isRemote) {
            return;
        }
        BlockPos below = pos.down();
        IBlockState belowState = BlockStates.get(world, below);
        if (belowState.getBlock() == Blocks.grass) {
            BlockStates.set(world, below, BlockStates.defaultState(SRPBlocks.SnowCoveredGrass), 3);
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(world, null, pos, SoundEvents.BLOCK_SNOW_BREAK, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 0.65f, 1.0f);
            BlockPos below = pos.down();
            if (BlockStates.get(world, below).getBlock() == SRPBlocks.SnowCoveredGrass) {
                BlockStates.set(world, below, BlockStates.defaultState(Blocks.grass), 3);
            }
        }
        super.breakBlock(world, pos, state);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.tallGrass ? TALL_GRASS_AABB : SHORT_GRASS_AABB;
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return true;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        BlockPos below = pos.down();
        IBlockState belowState = BlockStates.get(world, below);
        return belowState.isSideSolid((IBlockAccess)world, below, EnumFacing.UP);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!this.canPlaceBlockAt(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            WorldAccess.setBlockToAir(world, pos);
        }
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (RANDOM.nextInt(8) != 0) {
            return;
        }
        ItemStack seed = ForgeHooks.getGrassSeed((Random)RANDOM, (int)fortune);
        if (!seed.isEmpty()) {
            drops.add((Object)seed);
        }
    }

    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Nonnull
    public NonNullList<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return NonNullList.withSize((int)1, (Object)new ItemStack(Item.getItemFromBlock((Block)this)));
    }

    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock((Block)this));
    }

    private static class ItemBlockSnowGrass
    extends ItemBlock {
        public ItemBlockSnowGrass(Block block) {
            super(block);
        }

        public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
            boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
            if (placed && !world.isRemote) {
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(world, null, pos, SoundEvents.BLOCK_SNOW_PLACE, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 0.65f, 1.0f);
            }
            return placed;
        }
    }
}

