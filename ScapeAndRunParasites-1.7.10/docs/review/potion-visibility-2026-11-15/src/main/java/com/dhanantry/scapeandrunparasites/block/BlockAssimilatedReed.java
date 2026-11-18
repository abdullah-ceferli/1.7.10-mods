/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBushBase;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAssimilatedReed
extends BlockBushBase {
    private static final String MODID = "srparasites";
    private static final String DEADBLOOD_NAME = "deadblood";
    private static final Fluid DEADBLOOD = FluidRegistry.getFluid((String)"deadblood");
    public static final PropertyInteger AGE = PropertyInteger.create((String)"age", (int)0, (int)15);
    private static final AxisAlignedBB AABB = Boxes.create(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);

    public BlockAssimilatedReed() {
        this("assimilated_reed", 0.0f, true, true);
    }

    public BlockAssimilatedReed(String name, float hardness, boolean creative, boolean tickRandom) {
        super(name, hardness, creative, tickRandom);
        this.setStepSound(SoundType.PLANT);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)AGE, (Comparable)Integer.valueOf(0)));
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return AABB;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        IBlockState below = BlockStates.get(world, pos.down());
        if (below.getBlock() == this) {
            return true;
        }
        return BlockAssimilatedReed.isValidBaseBlock(below) && BlockAssimilatedReed.hasAdjacentGrowthFluid(world, pos.down());
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        this.checkAndDrop(world, pos, state);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkAndDrop(world, pos, state);
    }

    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        IBlockState below = BlockStates.get(world, pos.down());
        if (below.getBlock() == this) {
            return true;
        }
        return BlockAssimilatedReed.isValidBaseBlock(below) && BlockAssimilatedReed.hasAdjacentGrowthFluid(world, pos.down());
    }

    private void checkAndDrop(World world, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(world, pos, state)) {
            WorldAccess.destroyBlock(world, pos, true);
        }
    }

    private static boolean isValidBaseBlock(IBlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.grass || block == Blocks.dirt || block == Blocks.sand) {
            return true;
        }
        ResourceLocation id = RegistryNames.get(block);
        return id != null && MODID.equals(id.getResourceDomain());
    }

    private static boolean hasAdjacentGrowthFluid(World world, BlockPos base) {
        for (EnumFacing f : EnumFacing.HORIZONTALS) {
            IBlockState st = BlockStates.get(world, base.offset(f));
            if (st.getMaterial() == Material.water) {
                return true;
            }
            Fluid neighbor = FluidRegistry.lookupFluidForBlock((Block)st.getBlock());
            if (neighbor == null || (DEADBLOOD == null || neighbor != DEADBLOOD) && !DEADBLOOD_NAME.equals(neighbor.getName()) && !"srparasites:deadblood".equals(neighbor.getName())) continue;
            return true;
        }
        return false;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!WorldAccess.isAreaLoaded(world, pos, 1)) {
            return;
        }
        if (!this.canBlockStay(world, pos, state)) {
            this.checkAndDrop(world, pos, state);
            return;
        }
        if (WorldAccess.isAirBlock(world, pos.up())) {
            int height = 1;
            while (BlockStates.get(world, pos.down(height)).getBlock() == this) {
                ++height;
            }
            if (height < 7) {
                int age = (Integer)state.getValue((IProperty)AGE);
                if (age >= 15) {
                    BlockStates.set(world, pos.up(), BlockStates.defaultState(this));
                    BlockStates.set(world, pos, state.withProperty((IProperty)AGE, (Comparable)Integer.valueOf(0)), 4);
                } else {
                    BlockStates.set(world, pos, state.withProperty((IProperty)AGE, (Comparable)Integer.valueOf(age + 1)), 4);
                }
            }
        }
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{AGE});
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue((IProperty)AGE);
    }

    public IBlockState getStateFromMeta(int meta) {
        if (meta < 0) {
            meta = 0;
        }
        if (meta > 15) {
            meta = 15;
        }
        return BlockStates.defaultState(this).withProperty((IProperty)AGE, (Comparable)Integer.valueOf(meta));
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock((Block)this);
    }
}

