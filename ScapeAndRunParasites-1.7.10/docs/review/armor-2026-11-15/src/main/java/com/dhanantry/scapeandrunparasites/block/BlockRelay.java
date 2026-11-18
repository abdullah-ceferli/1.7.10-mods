/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockRelayController;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class BlockRelay
extends Block {
    public BlockRelay(String name) {
        super(Material.iron);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        SRPBlocks.SRP_BLOCKS.add(this);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote) {
            this.tryFormRelay(world, pos, placer);
        }
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!world.isRemote) {
            this.tryFormRelay(world, pos, null);
        }
        super.neighborChanged(state, world, pos, blockIn, fromPos);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (this.isStructureComplete(world, pos)) {
                List hostileMobs = world.getEntities(EntityLiving.class, e -> e.isCreatureType(EnumCreatureType.MONSTER, false));
                int parasiteHostileCount = 0;
                for (EntityLiving mob : hostileMobs) {
                    ResourceLocation rl = EntityList.getKey((Entity)mob);
                    if (rl == null || !"srparasites".equals(rl.getResourceDomain())) continue;
                    ++parasiteHostileCount;
                }
                double percentage = hostileMobs.isEmpty() ? 0.0 : (double)parasiteHostileCount * 100.0 / (double)hostileMobs.size();
                player.addChatMessage((IChatComponent)new ChatComponentText(String.format("\u00a7a[DEBUG] Hostile mobs: %d | Parasite hostiles: %d (%.1f%%)", hostileMobs.size(), parasiteHostileCount, percentage)));
            } else {
                player.addChatMessage((IChatComponent)new ChatComponentText("\u00a7c[DEBUG] Structure incomplete."));
            }
        }
        return true;
    }

    private void tryFormRelay(World world, BlockPos pos, @Nullable EntityLivingBase placer) {
        BlockPos[] candidateMiddles;
        for (BlockPos mid : candidateMiddles = new BlockPos[]{pos, pos.up(), pos.down()}) {
            BlockPos bottom = mid.down();
            BlockPos top = mid.up();
            IBlockState sBottom = BlockStates.get(world, bottom);
            IBlockState sMiddle = BlockStates.get(world, mid);
            IBlockState sTop = BlockStates.get(world, top);
            if (sBottom.getBlock() != SRPBlocks.RelayBase || sMiddle.getBlock() != SRPBlocks.RelayMiddle || sTop.getBlock() != SRPBlocks.RelayRoof) continue;
            EnumFacing facing = EnumFacing.NORTH;
            IBlockState controllerState = BlockStates.defaultState(SRPBlocks.RELAY_CONTROLLER).withProperty((IProperty)BlockRelayController.FACING, (Comparable)facing);
            WorldAccess.destroyBlock(world, mid, false);
            WorldAccess.destroyBlock(world, top, false);
            BlockStates.set(world, bottom, controllerState, 2);
            ((BlockRelayController)SRPBlocks.RELAY_CONTROLLER).onBlockPlacedBy(world, bottom, controllerState, placer, ItemStack.EMPTY);
            return;
        }
    }

    private boolean isStructureComplete(World world, BlockPos pos) {
        BlockPos bottom = this.findBottom(world, pos);
        Block base = SRPBlocks.RelayBase;
        Block middle = SRPBlocks.RelayMiddle;
        Block roof = SRPBlocks.RelayRoof;
        boolean isBase = BlockStates.get(world, bottom).getBlock() == base;
        boolean isMiddle = BlockStates.get(world, bottom.up()).getBlock() == middle;
        boolean isRoof = BlockStates.get(world, bottom.up(2)).getBlock() == roof;
        return isBase && isMiddle && isRoof;
    }

    private BlockPos findBottom(World world, BlockPos pos) {
        while (BlockStates.get(world, pos.down()).getBlock() instanceof BlockRelay) {
            pos = pos.down();
        }
        return pos;
    }

    private static class FakePlacer
    extends EntityLivingBase {
        protected FakePlacer(World worldIn) {
            super(worldIn);
        }

        public Iterable<ItemStack> getArmorInventoryList() {
            return Collections.emptyList();
        }

        public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
            return ItemStack.EMPTY;
        }

        public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        }

        public EnumHandSide getPrimaryHand() {
            return EnumHandSide.RIGHT;
        }
    }
}

