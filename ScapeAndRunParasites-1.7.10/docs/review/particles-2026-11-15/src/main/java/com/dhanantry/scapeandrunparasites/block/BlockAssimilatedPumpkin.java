/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyDirection;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.util.Mirror;
import com.dhanantry.scapeandrunparasites.backport.util.Rotation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockAssimilatedPumpkin
extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockAssimilatedPumpkin(String regName, boolean glowing) {
        super(Material.gourd);
        RegistryNames.set(this, regName);
        this.setBlockName("srparasites." + regName);
        this.setHardness(1.0f);
        this.setStepSound(SoundType.WOOD);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        if (glowing) {
            this.setLightLevel(1.0f);
        }
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH));
        SRPBlocks.SRP_BLOCKS.add(this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemAssimilatedPumpkin(this), RegistryNames.get(this)));
    }

    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing dir = placer.getHorizontalFacing().getOpposite();
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)dir);
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)FACING)).getHorizontalIndex();
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)EnumFacing.byHorizontalIndex((int)(meta & 3)));
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty((IProperty)FACING, (Comparable)rot.rotate((EnumFacing)state.getValue((IProperty)FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation((EnumFacing)state.getValue((IProperty)FACING)));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{FACING});
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public static class ItemAssimilatedPumpkin
    extends ItemBlock {
        public ItemAssimilatedPumpkin(Block block) {
            super(block);
        }

        public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot slot, Entity entity) {
            return slot == EntityEquipmentSlot.HEAD;
        }

        public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
            return EntityEquipmentSlot.HEAD;
        }

        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
            ItemStack held = player.getHeldItem(hand);
            ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.isEmpty()) {
                ItemStack one = held.copy();
                one.setCount(1);
                player.setItemStackToSlot(EntityEquipmentSlot.HEAD, one);
                held.shrink(1);
                return new ActionResult(EnumActionResult.SUCCESS, (Object)held);
            }
            return super.onItemRightClick(world, player, hand);
        }
    }
}

