/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.BlockWall
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.IGrowable;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyEnum;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.util.IStringSerializable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockThornshade
extends BlockBush
implements IGrowable {
    public static final PropertyEnum<EnumThornshadeStage> STAGE = PropertyEnum.create((String)"stage", EnumThornshadeStage.class);
    private static final AxisAlignedBB AABB_SMALL = Boxes.create(0.2, 0.0, 0.2, 0.8, 0.5, 0.8);
    private static final AxisAlignedBB AABB_MEDIUM = Boxes.create(0.1, 0.0, 0.1, 0.9, 0.8, 0.9);
    private static final AxisAlignedBB AABB_FULL = Boxes.create(0.0, 0.0, 0.0, 1.0, 0.9, 1.0);

    public BlockThornshade() {
        super(Material.plants);
        RegistryNames.set(this, "thornshade");
        this.setBlockName("srparasites.thornshade");
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.PLANT);
        this.setHardness(0.2f);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, (Comparable)((Object)EnumThornshadeStage.STAGE0_TS)));
        SRPBlocks.SRP_BLOCKS.add((Block)this);
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState base = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        IBlockState soil = BlockStates.get(worldIn, pos.down());
        boolean onSrpSoil = this.isSrpSoil(soil);
        boolean snowy = this.isSnowy(worldIn, pos);
        EnumThornshadeStage stage = onSrpSoil ? (snowy ? EnumThornshadeStage.STAGE0_TS_SNOW : EnumThornshadeStage.STAGE0_TS) : this.getDeadStateStage(snowy);
        return base.withProperty(STAGE, (Comparable)((Object)stage));
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumThornshadeStage stage = (EnumThornshadeStage)((Object)state.getValue(STAGE));
        switch (stage) {
            case STAGE0_TS: 
            case STAGE0_TS_SNOW: {
                return AABB_SMALL;
            }
            case STAGE1_TS: 
            case STAGE1_TS_SNOW: 
            case STAGE2_TS_NOBERRY: 
            case STAGE2_TS_NOBERRY_SNOW: 
            case DEAD_TS: 
            case DEAD_TS_SNOW: {
                return AABB_MEDIUM;
            }
        }
        return AABB_FULL;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    protected boolean canSustainBush(IBlockState state) {
        Block block = state.getBlock();
        if (block == null || state.getMaterial() == Material.air) {
            return false;
        }
        if (block == this) {
            return false;
        }
        return !(block instanceof BlockSlab) && !(block instanceof BlockStairs) && !(block instanceof BlockFence) && !(block instanceof BlockFenceGate) && !(block instanceof BlockWall);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState soil = BlockStates.get(worldIn, pos.down());
        if (soil.getBlock() == this) {
            return false;
        }
        return this.canSustainBush(soil) && super.canPlaceBlockAt(worldIn, pos);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        IBlockState soil = BlockStates.get(worldIn, pos.down());
        if (!this.canSustainBush(soil)) {
            if (!worldIn.isRemote) {
                Block.spawnAsEntity((World)worldIn, (BlockPos)pos, (ItemStack)new ItemStack(SRPItems.itemThornshadeBerry));
            }
            WorldAccess.setBlockToAir(worldIn, pos);
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canRemainOnCurrentSoil(worldIn, pos, state)) {
            IBlockState deadState = this.getDeadState(worldIn, pos);
            BlockStates.set(worldIn, pos, deadState, 2);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        IBlockState soil = BlockStates.get(worldIn, pos.down());
        if (soil.getBlock() == this) {
            return false;
        }
        return this.canSustainBush(soil);
    }

    private boolean isSrpSoil(IBlockState state) {
        Block block = state.getBlock();
        if (block == null || RegistryNames.get(block) == null) {
            return false;
        }
        return "srparasites".equals(RegistryNames.get(block).getResourceDomain());
    }

    private boolean canRemainOnCurrentSoil(World world, BlockPos pos, IBlockState state) {
        IBlockState soil = BlockStates.get(world, pos.down());
        return this.canSustainBush(soil);
    }

    private boolean isSnowy(World world, BlockPos pos) {
        boolean snowfall;
        boolean bl = snowfall = WorldAccess.canSeeSky(world, pos.up()) && WorldAccess.isRainingAt(world, pos.up()) && world.getBiome(pos).getTemperature(pos) < 0.15f;
        if (snowfall) {
            return true;
        }
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos checkPos = pos.offset(face);
            IBlockState state = BlockStates.get(world, checkPos);
            if (state.getBlock() != Blocks.snow_layer && state.getBlock() != Blocks.snow) continue;
            return true;
        }
        return false;
    }

    private EnumThornshadeStage applySnow(EnumThornshadeStage stage, boolean snowy) {
        switch (stage) {
            case STAGE0_TS: 
            case STAGE0_TS_SNOW: {
                return snowy ? EnumThornshadeStage.STAGE0_TS_SNOW : EnumThornshadeStage.STAGE0_TS;
            }
            case STAGE1_TS: 
            case STAGE1_TS_SNOW: {
                return snowy ? EnumThornshadeStage.STAGE1_TS_SNOW : EnumThornshadeStage.STAGE1_TS;
            }
            case STAGE2_TS: 
            case STAGE2_TS_SNOW: {
                return snowy ? EnumThornshadeStage.STAGE2_TS_SNOW : EnumThornshadeStage.STAGE2_TS;
            }
            case STAGE2_TS_NOBERRY: 
            case STAGE2_TS_NOBERRY_SNOW: {
                return snowy ? EnumThornshadeStage.STAGE2_TS_NOBERRY_SNOW : EnumThornshadeStage.STAGE2_TS_NOBERRY;
            }
            case DEAD_TS: 
            case DEAD_TS_SNOW: {
                return snowy ? EnumThornshadeStage.DEAD_TS_SNOW : EnumThornshadeStage.DEAD_TS;
            }
        }
        return stage;
    }

    private EnumThornshadeStage getDeadStateStage(boolean snowy) {
        return snowy ? EnumThornshadeStage.DEAD_TS_SNOW : EnumThornshadeStage.DEAD_TS;
    }

    private IBlockState getDeadState(World world, BlockPos pos) {
        boolean snowy = this.isSnowy(world, pos);
        return BlockStates.defaultState(this).withProperty(STAGE, (Comparable)((Object)this.getDeadStateStage(snowy)));
    }

    private boolean isDeadStage(EnumThornshadeStage stage) {
        return stage == EnumThornshadeStage.DEAD_TS || stage == EnumThornshadeStage.DEAD_TS_SNOW;
    }

    private boolean isBerryStage(EnumThornshadeStage stage) {
        return stage == EnumThornshadeStage.STAGE2_TS || stage == EnumThornshadeStage.STAGE2_TS_SNOW;
    }

    private boolean isNoBerryStage(EnumThornshadeStage stage) {
        return stage == EnumThornshadeStage.STAGE2_TS_NOBERRY || stage == EnumThornshadeStage.STAGE2_TS_NOBERRY_SNOW;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        EnumThornshadeStage stage = (EnumThornshadeStage)((Object)state.getValue(STAGE));
        if (!this.canRemainOnCurrentSoil(worldIn, pos, state)) {
            IBlockState deadState = this.getDeadState(worldIn, pos);
            BlockStates.set(worldIn, pos, deadState, 2);
            return;
        }
        IBlockState soil = BlockStates.get(worldIn, pos.down());
        if (!this.isSrpSoil(soil) && !this.isDeadStage(stage)) {
            IBlockState deadState = this.getDeadState(worldIn, pos);
            BlockStates.set(worldIn, pos, deadState, 2);
            return;
        }
        boolean snowy = this.isSnowy(worldIn, pos);
        EnumThornshadeStage adjusted = this.applySnow(stage, snowy);
        if (adjusted != stage) {
            BlockStates.set(worldIn, pos, state.withProperty(STAGE, (Comparable)((Object)adjusted)), 2);
            stage = adjusted;
        }
        if (this.isDeadStage(stage)) {
            return;
        }
        if (rand.nextInt(5) != 0) {
            return;
        }
        switch (stage) {
            case STAGE0_TS: 
            case STAGE0_TS_SNOW: {
                BlockStates.set(worldIn, pos, state.withProperty(STAGE, (Comparable)((Object)(snowy ? EnumThornshadeStage.STAGE1_TS_SNOW : EnumThornshadeStage.STAGE1_TS))), 2);
                break;
            }
            case STAGE1_TS: 
            case STAGE1_TS_SNOW: {
                BlockStates.set(worldIn, pos, state.withProperty(STAGE, (Comparable)((Object)(snowy ? EnumThornshadeStage.STAGE2_TS_SNOW : EnumThornshadeStage.STAGE2_TS))), 2);
                break;
            }
            case STAGE2_TS_NOBERRY: 
            case STAGE2_TS_NOBERRY_SNOW: {
                BlockStates.set(worldIn, pos, state.withProperty(STAGE, (Comparable)((Object)(snowy ? EnumThornshadeStage.STAGE2_TS_SNOW : EnumThornshadeStage.STAGE2_TS))), 2);
                break;
            }
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumThornshadeStage stage = (EnumThornshadeStage)((Object)state.getValue(STAGE));
        if (!this.isBerryStage(stage)) {
            return false;
        }
        if (worldIn.isRemote) {
            return true;
        }
        int count = 1 + worldIn.rand.nextInt(2);
        BlockThornshade.spawnAsEntity((World)worldIn, (BlockPos)pos, (ItemStack)new ItemStack(SRPItems.itemThornshadeBerry, count));
        boolean snowy = this.isSnowy(worldIn, pos);
        EnumThornshadeStage next = snowy ? EnumThornshadeStage.STAGE2_TS_NOBERRY_SNOW : EnumThornshadeStage.STAGE2_TS_NOBERRY;
        BlockStates.set(worldIn, pos, state.withProperty(STAGE, (Comparable)((Object)next)), 2);
        return true;
    }

    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add((Object)new ItemStack((Block)this, 1, EnumThornshadeStage.STAGE0_TS.ordinal()));
        items.add((Object)new ItemStack((Block)this, 1, EnumThornshadeStage.STAGE1_TS.ordinal()));
        items.add((Object)new ItemStack((Block)this, 1, EnumThornshadeStage.STAGE2_TS.ordinal()));
        items.add((Object)new ItemStack((Block)this, 1, EnumThornshadeStage.STAGE2_TS_NOBERRY.ordinal()));
        items.add((Object)new ItemStack((Block)this, 1, EnumThornshadeStage.DEAD_TS.ordinal()));
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add((Object)new ItemStack((Block)this));
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack((Block)this);
    }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        EnumThornshadeStage stage = (EnumThornshadeStage)((Object)state.getValue(STAGE));
        return !this.isDeadStage(stage) && !this.isBerryStage(stage);
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return false;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        EnumThornshadeStage stage = (EnumThornshadeStage)((Object)state.getValue(STAGE));
        boolean snowy = this.isSnowy(worldIn, pos);
        switch (stage) {
            case STAGE0_TS: 
            case STAGE0_TS_SNOW: {
                BlockStates.set(worldIn, pos, state.withProperty(STAGE, (Comparable)((Object)(snowy ? EnumThornshadeStage.STAGE1_TS_SNOW : EnumThornshadeStage.STAGE1_TS))), 2);
                break;
            }
            case STAGE1_TS: 
            case STAGE1_TS_SNOW: {
                BlockStates.set(worldIn, pos, state.withProperty(STAGE, (Comparable)((Object)(snowy ? EnumThornshadeStage.STAGE2_TS_SNOW : EnumThornshadeStage.STAGE2_TS))), 2);
                break;
            }
            case STAGE2_TS_NOBERRY: 
            case STAGE2_TS_NOBERRY_SNOW: {
                BlockStates.set(worldIn, pos, state.withProperty(STAGE, (Comparable)((Object)(snowy ? EnumThornshadeStage.STAGE2_TS_SNOW : EnumThornshadeStage.STAGE2_TS))), 2);
                break;
            }
        }
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumThornshadeStage)((Object)state.getValue(STAGE))).ordinal();
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumThornshadeStage[] values = EnumThornshadeStage.values();
        if (meta < 0 || meta >= values.length) {
            meta = 0;
        }
        return BlockStates.defaultState(this).withProperty(STAGE, (Comparable)((Object)values[meta]));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{STAGE});
    }

    public static enum EnumThornshadeStage implements IStringSerializable
    {
        STAGE0_TS("stage0_ts"),
        STAGE0_TS_SNOW("stage0_ts_snow"),
        STAGE1_TS("stage1_ts"),
        STAGE1_TS_SNOW("stage1_ts_snow"),
        STAGE2_TS("stage2_ts"),
        STAGE2_TS_SNOW("stage2_ts_snow"),
        STAGE2_TS_NOBERRY("stage2_ts_noberry"),
        STAGE2_TS_NOBERRY_SNOW("stage2_ts_noberry_snow"),
        DEAD_TS("dead_ts"),
        DEAD_TS_SNOW("dead_ts_snow");

        private final String name;

        private EnumThornshadeStage(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}

