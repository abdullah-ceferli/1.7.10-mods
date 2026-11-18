/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockParasiteFog
extends Block {
    public static final PropertyInteger STAGE = PropertyInteger.create((String)"air", (int)0, (int)2);

    public BlockParasiteFog(String name) {
        super(Material.air);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setTickRandomly(true);
        SRPBlocks.SRP_BLOCKS.add(this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(0)));
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextFloat() < 0.1f) {
            double x = (double)pos.getX() + 0.5 + rand.nextDouble() * 0.9;
            double y = (double)pos.getY() + 0.05 + rand.nextDouble() * 0.9;
            double z = (double)pos.getZ() + 0.5 + rand.nextDouble() * 0.9;
            ParticleSpawner.spawnParticle(SRPEnumParticle.COOLER_FOG, x, y, z, 0.0, 0.0, 0.0, 0, 0, 0);
        }
        if (rand.nextFloat() < 0.005f) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SRPSounds.FOG, SoundCategory.AMBIENT, 1.3f, 1.0f, false);
        }
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{STAGE});
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue((IProperty)STAGE);
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(meta & 2));
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        int meta = BlockStates.meta(BlockStates.get(worldIn, pos).getBlock(), BlockStates.get(worldIn, pos));
        if (meta == 0) {
            BlockStates.set(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteFog).withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(1)));
        }
        if (meta == 2) {
            int i1 = pos.getY();
            double l1 = pos.getX();
            double i2 = pos.getZ();
            int BGrange = 2;
            for (int k2 = -1 * BGrange; k2 <= 1 * BGrange; ++k2) {
                for (int l2 = -1 * BGrange; l2 <= 1 * BGrange; ++l2) {
                    for (int j = -1 * BGrange; j <= 1 * BGrange; ++j) {
                        int meta2;
                        double i3 = l1 + (double)k2;
                        double k = i1 + j;
                        double l = i2 + (double)l2;
                        BlockPos blockpos = new BlockPos(i3, k, l);
                        IBlockState iblockstate = BlockStates.get(worldIn, blockpos);
                        Block block = iblockstate.getBlock();
                        if (block != SRPBlocks.ParasiteFog || (meta2 = BlockStates.meta(block, iblockstate)) == 2) continue;
                        BlockStates.set(worldIn, blockpos, BlockStates.defaultState(SRPBlocks.ParasiteFog).withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(2)));
                    }
                }
            }
            BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.air));
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.isEmpty() || stack.getItem() != Items.glass_bottle) {
            return false;
        }
        if (!worldIn.isRemote) {
            ItemStack fogBottle;
            if (!playerIn.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            if (!playerIn.inventory.addItemStackToInventory(fogBottle = new ItemStack(SRPItems.FOG_BOTTLE))) {
                playerIn.dropPlayerItemWithRandomChoice(fogBottle, false);
            }
            BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.air), 3);
        }
        return true;
    }

    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        BlockPos offsetPos = pos.offset(side);
        IBlockState other = BlockStates.get(world, offsetPos);
        if (other.getBlock() == this) {
            return false;
        }
        return super.shouldSideBeRendered(state, world, pos, side);
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.SOLID;
    }
}

