/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockSlab$EnumBlockHalf
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemSlab
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.IStagedBlock;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyEnum;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockHarleskinnSlab
extends BlockSlab {
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create((String)"variant", Variant.class);
    private final boolean doubleSlab;
    private final boolean dropsItems;
    private BlockSlab halfPartner;
    private BlockSlab doublePartner;

    public BlockHarleskinnSlab(boolean isDouble, String name, String toolClass, int harvestLevel, SoundType soundType) {
        this(isDouble, name, toolClass, harvestLevel, soundType, 2.0f, 3.0f, true);
    }

    public BlockHarleskinnSlab(boolean isDouble, String name, String toolClass, int harvestLevel, SoundType soundType, float hardness, float resistance, boolean dropsItems) {
        super(Material.wood);
        this.doubleSlab = isDouble;
        this.dropsItems = dropsItems;
        this.setStepSound(SRPSoundTypes.FLESH);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setHardness(hardness);
        this.setResistance(resistance);
        if (toolClass != null) {
            this.setHarvestLevel(toolClass, harvestLevel);
        }
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        IBlockState base = this.blockState.getBaseState().withProperty(VARIANT, (Comparable)((Object)Variant.DEFAULT));
        this.setDefaultState(isDouble ? base : base.withProperty((IProperty)HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM));
        this.setTickRandomly(true);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
    }

    public BlockHarleskinnSlab(boolean isDouble, String name, String toolClass, int harvestLevel, SoundType soundType, float hardness, float resistance, boolean dropsItems, BlockSlab partnerDouble) {
        this(isDouble, name, toolClass, harvestLevel, soundType, hardness, resistance, dropsItems);
        if (!this.doubleSlab) {
            this.halfPartner = this;
            this.doublePartner = Objects.requireNonNull(partnerDouble, "partnerDouble");
            Item item = (Item)RegistryNames.set(new ItemSlab((Block)this, (BlockSlab)this, this.doublePartner), Objects.requireNonNull(RegistryNames.get(this)));
            item.setCreativeTab((CreativeTabs)SRPMain.SRP_CREATIVETAB);
            SRPItems.SRP_ITEMS.add(item);
        }
    }

    public BlockHarleskinnSlab setHalfPartner(BlockSlab half) {
        this.halfPartner = half;
        return this;
    }

    public boolean isDouble() {
        return this.doubleSlab;
    }

    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    public Comparable<?> getTypeForItem(ItemStack stack) {
        return Variant.DEFAULT;
    }

    protected BlockStateContainer createBlockState() {
        return this.isDouble() ? new BlockStateContainer((Block)this, new IProperty[]{VARIANT}) : new BlockStateContainer((Block)this, new IProperty[]{VARIANT, HALF});
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState s = BlockStates.defaultState(this).withProperty(VARIANT, (Comparable)((Object)Variant.DEFAULT));
        if (!this.isDouble()) {
            s = s.withProperty((IProperty)HALF, (Comparable)((meta & 8) != 0 ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM));
        }
        return s;
    }

    public int getMetaFromState(IBlockState state) {
        int m = 0;
        if (!this.isDouble() && state.getValue((IProperty)HALF) == BlockSlab.EnumBlockHalf.TOP) {
            m |= 8;
        }
        return m;
    }

    public String getTranslationKey(int meta) {
        return this.getUnlocalizedName();
    }

    private BlockSlab halfForDrops() {
        if (this.halfPartner != null) {
            return this.halfPartner;
        }
        return this.doubleSlab ? null : this;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (!this.dropsItems) {
            return Items.AIR;
        }
        BlockSlab half = this.halfForDrops();
        return half != null ? Item.getItemFromBlock((Block)half) : Items.AIR;
    }

    public int quantityDropped(Random rand) {
        if (!this.dropsItems) {
            return 0;
        }
        return this.isDouble() ? 2 : 1;
    }

    protected ItemStack getSilkTouchDrop(IBlockState state) {
        if (!this.dropsItems) {
            return ItemStack.EMPTY;
        }
        BlockSlab half = this.halfForDrops();
        return half != null ? new ItemStack(Item.getItemFromBlock((Block)half)) : ItemStack.EMPTY;
    }

    public ItemStack getPickBlock(IBlockState s, RayTraceResult t, World w, BlockPos p, EntityPlayer pl) {
        BlockSlab half = this.halfForDrops();
        return half != null ? new ItemStack(Item.getItemFromBlock((Block)half)) : ItemStack.EMPTY;
    }

    public ItemStack getItem(World w, BlockPos p, IBlockState s) {
        BlockSlab half = this.halfForDrops();
        return half != null ? new ItemStack(Item.getItemFromBlock((Block)half)) : ItemStack.EMPTY;
    }

    public int damageDropped(IBlockState state) {
        return 0;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        if (!worldIn.isRemote) {
            WorldAccess.scheduleUpdate(worldIn, pos, (Block)this, 10);
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!worldIn.isRemote) {
            WorldAccess.scheduleUpdate(worldIn, pos, (Block)this, 10);
        }
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.updateTick(worldIn, pos, state, rand);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        if (BlockHarleskinnSlab.touchingAnyInfestation(worldIn, pos)) {
            BeckonBlockInfestation.beckonInfestation(worldIn, pos, rand, 1, false);
            WorldAccess.scheduleUpdate(worldIn, pos, (Block)this, 20);
        }
    }

    private static boolean touchingAnyInfestation(World worldIn, BlockPos pos) {
        for (int dir = 0; dir <= 5; ++dir) {
            String path;
            BlockPos helper = BlockParasiteSpreading.directionToSpread(pos, dir);
            IBlockState st = BlockStates.get(worldIn, helper);
            Block b = st.getBlock();
            if (b instanceof IStagedBlock) {
                return true;
            }
            ResourceLocation rl = RegistryNames.get(b);
            if (rl == null || !"srparasites".equals(rl.getResourceDomain()) || !(path = rl.getResourcePath().toLowerCase(Locale.ROOT)).contains("infest")) continue;
            return true;
        }
        return false;
    }

    public static enum Variant implements IStringSerializable
    {
        DEFAULT;


        public String getName() {
            return "default";
        }
    }
}

