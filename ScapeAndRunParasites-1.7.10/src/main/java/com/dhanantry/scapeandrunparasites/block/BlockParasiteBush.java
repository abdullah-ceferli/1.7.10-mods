/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IShearable
 *  net.minecraftforge.oredict.OreDictionary
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyEnum;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.util.IStringSerializable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.oredict.OreDictionary;

public class BlockParasiteBush
extends BlockBush
implements IMetaName,
IShearable {
    public static final PropertyBool NODE = PropertyBool.create((String)"node");
    public static final PropertyBool END = PropertyBool.create((String)"end");
    protected static final AxisAlignedBB TALL_GRASS_AABB = Boxes.create(0.09999999403953552, 0.0, 0.09999999403953552, (double)0.9f, (double)0.8f, (double)0.9f);
    protected static final AxisAlignedBB REED_AABB = Boxes.create(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create((String)"variant", EnumType.class);
    private static final String[] RAW_MEAT_KEYS = new String[]{"listAllmeatraw", "listAllfishraw", "foodMeatRaw", "foodFishRaw"};

    public BlockParasiteBush(String name, float hardness) {
        super(Material.vine);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, (Comparable)((Object)EnumType.TENDRIL)));
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setHardness(hardness);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        ItemBlock itemBlock = this.getItemBlock();
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumType variant = (EnumType)((Object)state.getValue(VARIANT));
        if (variant == EnumType.TENDRIL || variant == EnumType.BINE) {
            return REED_AABB;
        }
        return TALL_GRASS_AABB;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        EnumType selfVariant = (EnumType)((Object)state.getValue(VARIANT));
        if ((selfVariant == EnumType.BINE || selfVariant == EnumType.TENDRIL) && this.hasSRPCeilingSupport(worldIn, pos)) {
            return true;
        }
        BlockPos down = pos.down();
        IBlockState below = BlockStates.get(worldIn, down);
        String variantName = null;
        for (IProperty p : below.getPropertyKeys()) {
            if (!"variant".equals(p.getName())) continue;
            IProperty raw = p;
            Comparable val = below.getValue(raw);
            if (val instanceof IStringSerializable) {
                variantName = ((IStringSerializable)val).getName();
                break;
            }
            if (val == null) break;
            variantName = String.valueOf(val);
            break;
        }
        if (below.getBlock() instanceof BlockParasiteBush && ("tendril".equals(variantName) || "bine".equals(variantName) || "vine".equals(variantName) || "spine".equals(variantName))) {
            IBlockState cur;
            BlockPos base = down;
            for (int guard = 0; guard < 64 && (cur = BlockStates.get(worldIn, base)).getBlock() instanceof BlockParasiteBush; ++guard) {
                base = base.down();
            }
            IBlockState sup = BlockStates.get(worldIn, base);
            Block b = sup.getBlock();
            ResourceLocation id = RegistryNames.get(b);
            if (id == null) {
                return false;
            }
            String domain = id.getResourceDomain();
            String path = id.getResourcePath();
            if (!"srparasites".equals(domain)) {
                return false;
            }
            if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
                return false;
            }
            return sup.isSideSolid((IBlockAccess)worldIn, base, EnumFacing.UP);
        }
        IBlockState sup = BlockStates.get(worldIn, down);
        Block b = sup.getBlock();
        ResourceLocation id = RegistryNames.get(b);
        if (id == null) {
            return false;
        }
        String domain = id.getResourceDomain();
        String path = id.getResourcePath();
        if (!"srparasites".equals(domain)) {
            return false;
        }
        if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
            return false;
        }
        return sup.isSideSolid((IBlockAccess)worldIn, down, EnumFacing.UP);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (BlockParasiteBush.isRawMeat(held)) {
            if (!world.isRemote) {
                int meta = BlockStates.meta(this, state);
                Item itemBlock = Item.getItemFromBlock((Block)this);
                if (itemBlock != null) {
                    BlockParasiteBush.spawnAsEntity((World)world, (BlockPos)pos.up(), (ItemStack)new ItemStack(itemBlock, 1, meta));
                }
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
                world.playEvent(2005, pos, 0);
                player.getCooldownTracker().setCooldown(held.getItem(), 10);
            }
            return true;
        }
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        if (this.hasSRPCeilingSupport(worldIn, pos)) {
            return true;
        }
        IBlockState below = BlockStates.get(worldIn, pos.down());
        if (below.getBlock() instanceof BlockParasiteBush) {
            EnumType v = (EnumType)((Object)below.getValue(VARIANT));
            if (v == EnumType.TENDRIL || v == EnumType.BINE) {
                BlockPos base = pos.down();
                for (int guard = 0; BlockStates.get(worldIn, base).getBlock() instanceof BlockParasiteBush && guard < 64; ++guard) {
                    base = base.down();
                }
                IBlockState sup = BlockStates.get(worldIn, base);
                Block b = sup.getBlock();
                ResourceLocation id = RegistryNames.get(b);
                if (id == null) {
                    return false;
                }
                String domain = id.getResourceDomain();
                String path = id.getResourcePath();
                if (!"srparasites".equals(domain)) {
                    return false;
                }
                if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
                    return false;
                }
                return sup.isSideSolid((IBlockAccess)worldIn, base, EnumFacing.UP);
            }
            return false;
        }
        IBlockState sup = BlockStates.get(worldIn, pos.down());
        Block b = sup.getBlock();
        ResourceLocation id = RegistryNames.get(b);
        if (id == null) {
            return false;
        }
        String domain = id.getResourceDomain();
        String path = id.getResourcePath();
        if (!"srparasites".equals(domain)) {
            return false;
        }
        if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
            return false;
        }
        return sup.isSideSolid((IBlockAccess)worldIn, pos.down(), EnumFacing.UP);
    }

    private boolean isValidSRPCeilingSupport(World world, BlockPos supportPosAbove) {
        IBlockState sup = BlockStates.get(world, supportPosAbove);
        Block b = sup.getBlock();
        ResourceLocation id = RegistryNames.get(b);
        if (id == null) {
            return false;
        }
        String domain = id.getResourceDomain();
        String path = id.getResourcePath();
        if (!"srparasites".equals(domain)) {
            return false;
        }
        if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
            return false;
        }
        return sup.isSideSolid((IBlockAccess)world, supportPosAbove, EnumFacing.DOWN);
    }

    private boolean hasSRPCeilingSupport(World world, BlockPos pos) {
        BlockPos p = pos.up();
        for (int guard = 0; BlockStates.get(world, p).getBlock() instanceof BlockParasiteBush && guard < 64; ++guard) {
            p = p.up();
        }
        return this.isValidSRPCeilingSupport(world, p);
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!this.canBlockStay(worldIn, pos, state)) {
            WorldAccess.destroyBlock(worldIn, pos, true);
            return;
        }
        if (fromPos != null && (fromPos.equals((Object)pos.down()) || fromPos.equals((Object)pos.up()))) {
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            WorldAccess.markBlockRangeForRenderUpdate(worldIn, pos, pos);
        }
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.air), 3);
        }
    }

    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return false;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return false;
    }

    public int damageDropped(IBlockState state) {
        return BlockStates.meta(this, state);
    }

    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType variant : EnumType.values()) {
            items.add((Object)new ItemStack((Block)this, 1, variant.ordinal()));
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty(VARIANT, (Comparable)((Object)EnumType.values()[meta]));
    }

    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        if (!SRPConfigWorld.bushClimbingEnabled) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            EnumType type = (EnumType)((Object)state.getValue(VARIANT));
            return type == EnumType.TENDRIL || type == EnumType.BINE;
        }
        return false;
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumType)((Object)state.getValue(VARIANT))).ordinal();
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock((Block)this), 1, BlockStates.meta(this, BlockStates.get(world, pos)));
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        boolean node = false;
        boolean end = false;
        if (this.isVariant(state, "tooh")) {
            node = this.isSpecialGround(world, pos.down());
        }
        if (this.isVariant(state, "tendril")) {
            boolean top;
            boolean bottom = WorldAccess.isAirBlock(world, pos.down());
            end = top = !bottom && WorldAccess.isAirBlock(world, pos.up());
            node = bottom;
        }
        if (this.isVariant(state, "bine")) {
            boolean top = WorldAccess.isAirBlock(world, pos.up());
            boolean bottom = !top && WorldAccess.isAirBlock(world, pos.down());
            node = top;
            end = bottom;
        }
        return state.withProperty((IProperty)NODE, (Comparable)Boolean.valueOf(node)).withProperty((IProperty)END, (Comparable)Boolean.valueOf(end));
    }

    private boolean isVariant(IBlockState state, String name) {
        Comparable v = state.getValue(VARIANT);
        return v instanceof IStringSerializable && ((IStringSerializable)v).getName().equals(name);
    }

    private boolean isSpecialGround(IBlockAccess world, BlockPos groundPos) {
        IBlockState below = BlockStates.get(world, groundPos);
        Block b = below.getBlock();
        ResourceLocation id = RegistryNames.get(b);
        if (id == null || !"srparasites".equals(id.getResourceDomain())) {
            return false;
        }
        String path = id.getResourcePath();
        if ("parasitestain".equals(path)) {
            return true;
        }
        if ("parasiterubble".equals(path)) {
            return BlockStates.meta(b, below) == 2;
        }
        return false;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{END, NODE, VARIANT});
    }

    @Override
    public Enum[] getVariants() {
        return EnumType.values();
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockVariant((Block)this);
    }

    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        IBlockState state = BlockStates.get(world, pos);
        int meta = BlockStates.meta(this, state);
        Item itemBlock = Item.getItemFromBlock((Block)this);
        if (itemBlock == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new ItemStack(itemBlock, 1, meta));
    }

    public boolean isToolEffective(String type, IBlockState state) {
        return "axe".equals(type) || "shears".equals(type);
    }

    public String getHarvestTool(IBlockState state) {
        return "axe";
    }

    public int getHarvestLevel(IBlockState state) {
        return 0;
    }

    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return 0.2f;
    }

    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        ItemStack held = player.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() instanceof ItemSword) {
            return super.getPlayerRelativeBlockHardness(state, player, world, pos) * 4.0f;
        }
        return super.getPlayerRelativeBlockHardness(state, player, world, pos);
    }

    private boolean isSRPSolidTop(World world, BlockPos supportPos) {
        IBlockState sup = BlockStates.get(world, supportPos);
        Block b = sup.getBlock();
        ResourceLocation id = RegistryNames.get(b);
        if (id == null) {
            return false;
        }
        String domain = id.getResourceDomain();
        String path = id.getResourcePath();
        if (!"srparasites".equals(domain)) {
            return false;
        }
        if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
            return false;
        }
        return sup.isSideSolid((IBlockAccess)world, supportPos, EnumFacing.UP);
    }

    private static boolean isRawMeat(ItemStack stack) {
        int[] ids;
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item it = stack.getItem();
        if (it == Items.beef || it == Items.porkchop || it == Items.chicken || it == Items.MUTTON || it == Items.RABBIT || it == Items.fish) {
            return true;
        }
        for (int id : ids = OreDictionary.getOreIDs((ItemStack)stack)) {
            String name = OreDictionary.getOreName((int)id);
            for (String key : RAW_MEAT_KEYS) {
                if (!key.equalsIgnoreCase(name)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        if (side == EnumFacing.UP) {
            IBlockState below = BlockStates.get(worldIn, pos.down());
            if (below.getBlock() instanceof BlockParasiteBush) {
                EnumType v = (EnumType)((Object)below.getValue(VARIANT));
                if (v == EnumType.TENDRIL || v == EnumType.BINE) {
                    BlockPos base = pos.down();
                    for (int guard = 0; BlockStates.get(worldIn, base).getBlock() instanceof BlockParasiteBush && guard < 64; ++guard) {
                        base = base.down();
                    }
                    IBlockState sup = BlockStates.get(worldIn, base);
                    ResourceLocation id = RegistryNames.get(sup.getBlock());
                    if (id == null) {
                        return false;
                    }
                    String domain = id.getResourceDomain();
                    String path = id.getResourcePath();
                    if (!"srparasites".equals(domain)) {
                        return false;
                    }
                    if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
                        return false;
                    }
                    return sup.isSideSolid((IBlockAccess)worldIn, base, EnumFacing.UP);
                }
                return false;
            }
            IBlockState sup = BlockStates.get(worldIn, pos.down());
            ResourceLocation id = RegistryNames.get(sup.getBlock());
            if (id == null) {
                return false;
            }
            String domain = id.getResourceDomain();
            String path = id.getResourcePath();
            if (!"srparasites".equals(domain)) {
                return false;
            }
            if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
                return false;
            }
            return sup.isSideSolid((IBlockAccess)worldIn, pos.down(), EnumFacing.UP);
        }
        if (side == EnumFacing.DOWN) {
            return this.hasSRPCeilingSupport(worldIn, pos);
        }
        return false;
    }

    protected boolean canSustainBush(IBlockState state) {
        ResourceLocation id = RegistryNames.get(state.getBlock());
        if (id == null) {
            return false;
        }
        String domain = id.getResourceDomain();
        String path = id.getResourcePath();
        if (!"srparasites".equals(domain)) {
            return false;
        }
        if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
            return false;
        }
        return state.isOpaqueCube();
    }

    private boolean isValidSRPSupport(World world, BlockPos supportPos) {
        IBlockState sup = BlockStates.get(world, supportPos);
        Block b = sup.getBlock();
        ResourceLocation id = RegistryNames.get(b);
        if (id == null) {
            return false;
        }
        String domain = id.getResourceDomain();
        String path = id.getResourcePath();
        if (!"srparasites".equals(domain)) {
            return false;
        }
        if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
            return false;
        }
        return sup.isSideSolid((IBlockAccess)world, supportPos, EnumFacing.UP);
    }

    private boolean hasSRPBaseSupport(World world, BlockPos posAboveSupport) {
        BlockPos p = posAboveSupport.down();
        for (int guard = 0; BlockStates.get(world, p).getBlock() instanceof BlockParasiteBush && guard < 64; ++guard) {
            p = p.down();
        }
        return this.isValidSRPSupport(world, p);
    }

    public static enum EnumType implements IStringSerializable
    {
        TENDRIL,
        BINE,
        POP,
        EYE,
        TOOH;


        public String getName() {
            return this.name().toLowerCase();
        }

        public String toString() {
            return this.getName();
        }
    }
}

