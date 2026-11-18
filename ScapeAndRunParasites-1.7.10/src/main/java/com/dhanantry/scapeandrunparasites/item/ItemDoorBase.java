/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockDoor$EnumDoorHalf
 *  net.minecraft.block.BlockDoor$EnumHingePosition
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemDoor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDoorBase
extends ItemDoor {
    private final Block block;

    public ItemDoorBase(Block block) {
        super(block);
        this.block = block;
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack;
        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        IBlockState iblockstate = BlockStates.get(worldIn, pos);
        Block block = iblockstate.getBlock();
        if (!block.isReplaceable((IBlockAccess)worldIn, pos)) {
            pos = pos.offset(facing);
        }
        if (player.canPlayerEdit(pos, facing, itemstack = player.getHeldItem(hand)) && this.block.canPlaceBlockAt(worldIn, pos)) {
            EnumFacing enumfacing = EnumFacing.fromAngle((double)player.rotationYaw);
            int i = enumfacing.getFrontOffsetX();
            int j = enumfacing.getFrontOffsetZ();
            boolean flag = i < 0 && hitZ < 0.5f || i > 0 && hitZ > 0.5f || j < 0 && hitX > 0.5f || j > 0 && hitX < 0.5f;
            ItemDoorBase.placeDoor(worldIn, pos, enumfacing, this.block, flag);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundType soundtype = com.dhanantry.scapeandrunparasites.backport.sound.SoundType.get(BlockStates.get(worldIn, pos).getBlock(), BlockStates.get(worldIn, pos), worldIn, pos, (Entity)player);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(worldIn, player, pos, soundtype.getPlaceSound(), com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door, boolean isRightHinge) {
        boolean flag1;
        BlockPos blockpos = pos.offset(facing.rotateY());
        BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
        int i = (BlockStates.get(worldIn, blockpos1).isNormalCube() ? 1 : 0) + (BlockStates.get(worldIn, blockpos1.up()).isNormalCube() ? 1 : 0);
        int j = (BlockStates.get(worldIn, blockpos).isNormalCube() ? 1 : 0) + (BlockStates.get(worldIn, blockpos.up()).isNormalCube() ? 1 : 0);
        boolean flag = BlockStates.get(worldIn, blockpos1).getBlock() == door || BlockStates.get(worldIn, blockpos1.up()).getBlock() == door;
        boolean bl = flag1 = BlockStates.get(worldIn, blockpos).getBlock() == door || BlockStates.get(worldIn, blockpos.up()).getBlock() == door;
        if ((!flag || flag1) && j <= i) {
            if (flag1 && !flag || j < i) {
                isRightHinge = false;
            }
        } else {
            isRightHinge = true;
        }
        BlockPos blockpos2 = pos.up();
        boolean flag2 = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos2);
        IBlockState iblockstate = BlockStates.defaultState(door).withProperty((IProperty)BlockDoor.FACING, (Comparable)facing).withProperty((IProperty)BlockDoor.HINGE, (Comparable)(isRightHinge ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT)).withProperty((IProperty)BlockDoor.POWERED, (Comparable)Boolean.valueOf(flag2)).withProperty((IProperty)BlockDoor.OPEN, (Comparable)Boolean.valueOf(flag2));
        BlockStates.set(worldIn, pos, iblockstate.withProperty((IProperty)BlockDoor.HALF, (Comparable)BlockDoor.EnumDoorHalf.LOWER), 2);
        BlockStates.set(worldIn, blockpos2, iblockstate.withProperty((IProperty)BlockDoor.HALF, (Comparable)BlockDoor.EnumDoorHalf.UPPER), 2);
        worldIn.notifyNeighborsOfStateChange(pos, door, false);
        worldIn.notifyNeighborsOfStateChange(blockpos2, door, false);
    }

    public static boolean setTileEntityNBT(World worldIn, @Nullable EntityPlayer player, BlockPos pos, ItemStack stackIn) {
        TileEntity tileentity;
        MinecraftServer minecraftserver = worldIn.getMinecraftServer();
        if (minecraftserver == null) {
            return false;
        }
        NBTTagCompound nbttagcompound = stackIn.getSubCompound("BlockEntityTag");
        if (nbttagcompound != null && (tileentity = WorldAccess.getTileEntity(worldIn, pos)) != null) {
            if (!(worldIn.isRemote || !tileentity.onlyOpsCanSetNbt() || player != null && player.canUseCommandBlock())) {
                return false;
            }
            NBTTagCompound nbttagcompound1 = tileentity.writeToNBT(new NBTTagCompound());
            NBTTagCompound nbttagcompound2 = nbttagcompound1.copy();
            nbttagcompound1.merge(nbttagcompound);
            nbttagcompound1.setInteger("x", pos.getX());
            nbttagcompound1.setInteger("y", pos.getY());
            nbttagcompound1.setInteger("z", pos.getZ());
            if (!nbttagcompound1.equals((Object)nbttagcompound2)) {
                tileentity.readFromNBT(nbttagcompound1);
                tileentity.markDirty();
                return true;
            }
        }
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        Block block = BlockStates.get(worldIn, pos).getBlock();
        if (block == Blocks.snow_layer && block.isReplaceable((IBlockAccess)worldIn, pos)) {
            side = EnumFacing.UP;
        } else if (!block.isReplaceable((IBlockAccess)worldIn, pos)) {
            pos = pos.offset(side);
        }
        return worldIn.mayPlace(this.block, pos, false, side, (Entity)null);
    }

    public String getUnlocalizedName(ItemStack stack) {
        return this.block.getUnlocalizedName();
    }

    public String getUnlocalizedName() {
        return this.block.getUnlocalizedName();
    }

    public CreativeTabs getCreativeTab() {
        return this.block.getCreativeTabToDisplayOn();
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            this.block.getSubBlocks(tab, items);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        this.block.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public Block getBlock() {
        return this.getBlockRaw() == null ? null : (Block)this.getBlockRaw().delegate.get();
    }

    private Block getBlockRaw() {
        return this.block;
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!BlockStates.set(world, pos, newState, 11)) {
            return false;
        }
        IBlockState state = BlockStates.get(world, pos);
        if (state.getBlock() == this.block) {
            ItemDoorBase.setTileEntityNBT(world, player, pos, stack);
            this.block.onBlockPlacedBy(world, pos, state, (EntityLivingBase)player, stack);
            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            }
        }
        return true;
    }
}

