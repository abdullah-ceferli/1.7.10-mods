/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ContainerChest
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.InventoryLargeChest
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntityLockableLoot
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.datafix.DataFixer
 *  net.minecraft.util.datafix.FixTypes
 *  net.minecraft.util.datafix.IDataWalker
 *  net.minecraft.util.datafix.walkers.ItemStackDataLists
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.items.CapabilityItemHandler
 *  net.minecraftforge.items.IItemHandler
 */
package com.dhanantry.scapeandrunparasites.entity.tile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanisterC;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ITickable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileEntityCanister
extends TileEntityLockableLoot
implements ITickable {
    private NonNullList<ItemStack> chestContents = NonNullList.withSize((int)40, (Object)ItemStack.EMPTY);
    public int numPlayersUsing;
    private int ticksSinceSync;

    public int getSizeInventory() {
        return 40;
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.chestContents) {
            if (itemstack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public String getCommandSenderName() {
        return this.hasCustomInventoryName() ? this.customName : "container.chest";
    }

    public static void registerFixesChest(DataFixer fixer) {
        fixer.registerWalker(FixTypes.BLOCK_ENTITY, (IDataWalker)new ItemStackDataLists(TileEntityCanister.class, new String[]{"Items"}));
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.chestContents = NonNullList.withSize((int)this.getSizeInventory(), (Object)ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems((NBTTagCompound)compound, this.chestContents);
        }
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems((NBTTagCompound)compound, this.chestContents);
        }
        if (this.hasCustomInventoryName()) {
            compound.setString("CustomName", this.customName);
        }
        return compound;
    }

    public int getInventoryStackLimit() {
        return 100;
    }

    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
    }

    public void update() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        ++this.ticksSinceSync;
        if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
            this.numPlayersUsing = 0;
            for (EntityPlayer entityplayer : EntityQueries.entities(this.worldObj, EntityPlayer.class, Boxes.create((double)((float)i - 5.0f), (double)((float)j - 5.0f), (double)((float)k - 5.0f), (double)((float)(i + 1) + 5.0f), (double)((float)(j + 1) + 5.0f), (double)((float)(k + 1) + 5.0f)))) {
                IInventory iinventory;
                if (!(entityplayer.openContainer instanceof ContainerChest) || (iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory()) != this && (!(iinventory instanceof InventoryLargeChest) || !((InventoryLargeChest)iinventory).isPartOfLargeChest((IInventory)this))) continue;
                ++this.numPlayersUsing;
            }
        }
        if (!this.worldObj.isRemote && this.ticksSinceSync % 20 == 0 && this.ticksSinceSync > 20 * SRPConfig.cystDelay) {
            int ccc = 0;
            for (int in = 0; in < this.getSizeInventory(); ++in) {
                if (((IItemHandler)Objects.requireNonNull(this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))).getStackInSlot(in).getItem() != Items.AIR) {
                    ItemStack stack = ((IItemHandler)Objects.requireNonNull(this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))).getStackInSlot(in);
                    if (ParasiteEventEntity.checkName(Objects.requireNonNull(RegistryNames.get(stack.getItem())).toString(), SRPConfig.cystItemBlackList, false)) continue;
                    if (stack.isItemStackDamageable()) {
                        if (stack.attemptDamageItem(5, this.worldObj.rand, null)) {
                            stack.shrink(1);
                            if (SRPConfigSystems.useEvolution) {
                                SRPSaveData data = SRPSaveData.get(this.worldObj, 45);
                                data.setTotalKills(this.worldObj.provider.dimensionId, SRPConfig.cystConsumePoint, true, this.worldObj, true, 43);
                            }
                        }
                    } else {
                        stack.shrink(1);
                        if (SRPConfigSystems.useEvolution) {
                            SRPSaveData data = SRPSaveData.get(this.worldObj, 46);
                            data.setTotalKills(this.worldObj.provider.dimensionId, SRPConfig.cystConsumePoint, true, this.worldObj, true, 44);
                        }
                    }
                    if (this.worldObj.rand.nextBoolean()) {
                        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, null, (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), SRPSounds.CYST_EATING, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 0.25f, 1.0f);
                    }
                    SRPMain.network.sendToAll((IMessage)new SRPPacketParticle((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.7, (double)this.pos.getZ() + 0.5, 0.5f, 0.5f, 2));
                    return;
                }
                ++ccc;
            }
            if (ccc == this.getSizeInventory()) {
                if (BlockStates.get(this.worldObj, this.pos.down()).getBlock() != Blocks.air) {
                    BlockStates.set(this.worldObj, this.pos, BlockStates.defaultState(SRPBlocks.ParasiteCanister).withProperty(BlockParasiteCanister.VARIANT, (Comparable)((Object)BlockParasiteCanister.EnumType.CYST)));
                } else {
                    BlockStates.set(this.worldObj, this.pos, BlockStates.defaultState(Blocks.air));
                }
            }
        }
    }

    public boolean addStack(List<ItemStack> moblist2) {
        for (ItemStack mob : moblist2) {
            if (mob.isEmpty()) continue;
            for (int in = 0; in <= 26; ++in) {
                ItemStack slot = ((IItemHandler)Objects.requireNonNull(this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))).getStackInSlot(in);
                if (ItemStack.areItemsEqual((ItemStack)slot, (ItemStack)mob)) {
                    mob = ((IItemHandler)Objects.requireNonNull(this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))).insertItem(in, mob, false);
                    continue;
                }
                if (slot.getItem() != Items.AIR) continue;
                mob = ((IItemHandler)Objects.requireNonNull(this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))).insertItem(in, mob, false);
            }
        }
        return false;
    }

    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    public void openInventory(EntityPlayer player) {
        if (player.isCreative()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }
            ++this.numPlayersUsing;
            this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
        }
    }

    public void closeInventory(EntityPlayer player) {
        if (player.isCreative() && this.getBlockType() instanceof BlockParasiteCanisterC) {
            --this.numPlayersUsing;
            this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
        }
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return (T)super.getCapability(capability, facing);
    }

    public IItemHandler getSingleChestHandler() {
        return (IItemHandler)super.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public void invalidate() {
        super.invalidate();
        this.updateContainingBlockInfo();
    }

    public String getGuiID() {
        return "srparasites:canister";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        this.fillWithLoot(playerIn);
        return new ContainerChest((IInventory)playerInventory, (IInventory)this, playerIn);
    }

    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }
}

