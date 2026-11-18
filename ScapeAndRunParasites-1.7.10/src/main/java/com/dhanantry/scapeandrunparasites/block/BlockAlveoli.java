/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IShearable
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAlveoli
extends BlockBase
implements IShearable {
    public static final PropertyBool ACTIVE = PropertyBool.create((String)"active");
    public static final PropertyBool DEPLETED = PropertyBool.create((String)"depleted");

    public BlockAlveoli() {
        super(Material.clay, "alveoli", 1.0f, true, true);
        this.setStepSound(SRPSoundTypes.FLESH);
        this.setLightOpacity(0);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)ACTIVE, (Comparable)Boolean.TRUE).withProperty((IProperty)DEPLETED, (Comparable)Boolean.FALSE));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{ACTIVE, DEPLETED});
    }

    public IBlockState getStateFromMeta(int meta) {
        boolean active = (meta & 1) != 0;
        boolean depleted = (meta & 2) != 0;
        return BlockStates.defaultState(this).withProperty((IProperty)ACTIVE, (Comparable)Boolean.valueOf(active)).withProperty((IProperty)DEPLETED, (Comparable)Boolean.valueOf(depleted));
    }

    public int getMetaFromState(IBlockState state) {
        int m = 0;
        if (((Boolean)state.getValue((IProperty)ACTIVE)).booleanValue()) {
            m |= 1;
        }
        if (((Boolean)state.getValue((IProperty)DEPLETED)).booleanValue()) {
            m |= 2;
        }
        return m;
    }

    public boolean isOpaqueCube(IBlockState s) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState s) {
        return false;
    }

    public boolean doesSideBlockRendering(IBlockState s, IBlockAccess w, BlockPos p, EnumFacing f) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() == Items.glass_bottle) {
            if (!w.isRemote) {
                ItemStack fluid;
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
                if (!player.inventory.addItemStackToInventory(fluid = new ItemStack(SRPItems.ALVEOLAR_FLUID))) {
                    player.dropPlayerItemWithRandomChoice(fluid, false);
                }
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(w, null, pos, SoundEvents.ITEM_BOTTLE_FILL, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setDepleted(w, pos, state);
            }
            return true;
        }
        return false;
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        boolean near = this.isNearFollicle(worldIn, pos, 6);
        return BlockStates.defaultState(this).withProperty((IProperty)ACTIVE, (Comparable)Boolean.valueOf(near)).withProperty((IProperty)DEPLETED, (Comparable)Boolean.valueOf(false));
    }

    public void onBlockAdded(World w, BlockPos pos, IBlockState state) {
        super.onBlockAdded(w, pos, state);
        if (!((Boolean)state.getValue((IProperty)DEPLETED)).booleanValue()) {
            boolean near = this.isNearFollicle(w, pos, 6);
            if ((Boolean)state.getValue((IProperty)ACTIVE) != near) {
                BlockStates.set(w, pos, state.withProperty((IProperty)ACTIVE, (Comparable)Boolean.valueOf(near)), 2);
            }
        }
    }

    private void setDepleted(World w, BlockPos pos, IBlockState state) {
        IBlockState s = state.withProperty((IProperty)DEPLETED, (Comparable)Boolean.valueOf(true)).withProperty((IProperty)ACTIVE, (Comparable)Boolean.valueOf(false));
        BlockStates.set(w, pos, s, 3);
        WorldAccess.scheduleUpdate(w, pos, (Block)this, 1200);
    }

    public void updateTick(World w, BlockPos pos, IBlockState state, Random rand) {
        if (((Boolean)state.getValue((IProperty)DEPLETED)).booleanValue()) {
            boolean near = this.isNearFollicle(w, pos, 6);
            BlockStates.set(w, pos, state.withProperty((IProperty)DEPLETED, (Comparable)Boolean.valueOf(false)).withProperty((IProperty)ACTIVE, (Comparable)Boolean.valueOf(near)), 3);
        } else {
            boolean near = this.isNearFollicle(w, pos, 6);
            if ((Boolean)state.getValue((IProperty)ACTIVE) != near) {
                BlockStates.set(w, pos, state.withProperty((IProperty)ACTIVE, (Comparable)Boolean.valueOf(near)), 3);
            }
        }
    }

    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (((Boolean)state.getValue((IProperty)DEPLETED)).booleanValue()) {
            return;
        }
        boolean near = this.isNearFollicle(w, pos, 6);
        if ((Boolean)state.getValue((IProperty)ACTIVE) != near) {
            BlockStates.set(w, pos, state.withProperty((IProperty)ACTIVE, (Comparable)Boolean.valueOf(near)), 3);
        }
    }

    private boolean isNearFollicle(World w, BlockPos pos, int r) {
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; ++dx) {
            for (int dy = -r; dy <= r; ++dy) {
                for (int dz = -r; dz <= r; ++dz) {
                    String path;
                    p.setPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    IBlockState s = BlockStates.get(w, (BlockPos)p);
                    ResourceLocation rl = RegistryNames.get(s.getBlock());
                    if (rl == null || !(path = rl.getResourcePath()).contains("hair_follicle")) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack((Block)this));
    }
}

