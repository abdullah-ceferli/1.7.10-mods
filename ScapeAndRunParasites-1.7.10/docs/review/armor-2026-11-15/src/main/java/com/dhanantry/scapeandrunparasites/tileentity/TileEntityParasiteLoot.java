/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntityLockable
 *  net.minecraft.util.NonNullList
 */
package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.container.ContainerParasiteLoot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;

public class TileEntityParasiteLoot
extends TileEntityLockable
implements IInventory {
    private static final int INV_SIZE = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);

    public float getFullness() {
        int total = this.getTotalSlots();
        if (total <= 0) {
            return 0.0f;
        }
        return (float)this.getUsedSlotCount() / (float)total;
    }

    public int getFullnessField() {
        return (int)Math.round((double)this.getFullness() * 1000.0);
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerParasiteLoot(playerInventory, this, playerIn);
    }

    public String getGuiID() {
        return "srparasites:parasite_loot";
    }

    public int getSizeInventory() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for (ItemStack s : this.items) {
            if (s.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public ItemStack getStackInSlot(int index) {
        return (ItemStack)this.items.get(index);
    }

    public ItemStack decrStackSize(int index, int count) {
        ItemStack s = ItemStackHelper.getAndSplit(this.items, (int)index, (int)count);
        this.markDirty();
        return s;
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack s = ItemStackHelper.getAndRemove(this.items, (int)index);
        this.markDirty();
        return s;
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        if (!stack.isEmpty() && !this.isItemValidForSlot(index, stack)) {
            stack = ItemStack.EMPTY;
        }
        this.items.set(index, (Object)stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        if (WorldAccess.getTileEntity(this.worldObj, this.pos) != this) {
            return false;
        }
        return player.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    public void openInventory(EntityPlayer player) {
    }

    public void closeInventory(EntityPlayer player) {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return TileEntityParasiteLoot.isValidParasiteLootItem(stack);
    }

    public static boolean isValidParasiteLootItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof ItemBlock) {
            return false;
        }
        if (RegistryNames.get(stack.getItem()) == null) {
            return false;
        }
        return "srparasites".equals(RegistryNames.get(stack.getItem()).getResourceDomain());
    }

    public int getField(int id) {
        return id == 0 ? this.getFullnessField() : 0;
    }

    public void setField(int id, int value) {
    }

    public int getFieldCount() {
        return 1;
    }

    public void clear() {
        this.items.clear();
    }

    public String getCommandSenderName() {
        return "container.srparasites.parasite_loot";
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems((NBTTagCompound)tag, this.items);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
        ItemStackHelper.loadAllItems((NBTTagCompound)tag, this.items);
    }

    public int getTotalSlots() {
        return this.getSizeInventory();
    }

    public int getFreeSlotCount() {
        int free = 0;
        for (int i = 0; i < this.items.size(); ++i) {
            if (!((ItemStack)this.items.get(i)).isEmpty()) continue;
            ++free;
        }
        return free;
    }

    public int getUsedSlotCount() {
        return this.getTotalSlots() - this.getFreeSlotCount();
    }
}

