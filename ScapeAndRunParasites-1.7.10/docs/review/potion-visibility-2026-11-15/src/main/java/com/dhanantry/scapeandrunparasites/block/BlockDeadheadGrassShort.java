/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IShearable
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockDeadheadLeaves;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.client.IModelSRP;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDeadheadGrassShort
extends BlockBush
implements IModelSRP,
IShearable {
    public static final PropertyInteger TEXTURE = PropertyInteger.create((String)"texture", (int)0, (int)4);

    public BlockDeadheadGrassShort(String name) {
        super(Material.vine);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setHardness(0.0f);
        this.setStepSound(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)TEXTURE, (Comparable)Integer.valueOf(0)));
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.isValidDeadheadSupport((IBlockAccess)worldIn, pos.up());
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        return this.isValidDeadheadSupport((IBlockAccess)worldIn, pos.up());
    }

    private boolean isValidDeadheadSupport(IBlockAccess world, BlockPos supportPos) {
        IBlockState supportState = BlockStates.get(world, supportPos);
        Block supportBlock = supportState.getBlock();
        if (supportBlock == SRPBlocks.ParasiteTrunk) {
            return supportState.getValue(BlockParasiteTrunk.VARIANT) == BlockParasiteTrunk.EnumType.DEADHEAD;
        }
        if (supportBlock instanceof BlockDeadheadLeaves) {
            return true;
        }
        ResourceLocation id = RegistryNames.get(supportBlock);
        if (id == null || !supportState.isFullCube()) {
            return false;
        }
        String path = id.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("deadhead") || path.contains("dead_head");
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        long hash = (long)pos.getX() * 3129871L ^ (long)pos.getZ() * 116129781L ^ (long)pos.getY();
        hash = hash * hash * 42317861L + hash * 11L;
        int texture = (int)((hash >>> 16) % 5L);
        return state.withProperty((IProperty)TEXTURE, (Comparable)Integer.valueOf(texture));
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this);
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{TEXTURE});
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public boolean canSilkHarvest() {
        return true;
    }

    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock((Block)this), 1, 0);
    }

    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(Item.getItemFromBlock((Block)this), 1, 0));
    }

    @Override
    public void registerModels() {
        SRPMain.proxy.modelReg(Item.getItemFromBlock((Block)this), 0, "inventory");
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}

