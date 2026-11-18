/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ContainerChest
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntityLockableLoot
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityDermoidCyst
extends TileEntityLockableLoot
implements ITickable {
    private NonNullList<ItemStack> items = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
    private String customName;
    private int numPlayersUsing = 0;
    private int ticksSinceSync = 0;

    protected NonNullList<ItemStack> getItems() {
        return this.items;
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
        ItemStack stack = ItemStackHelper.getAndSplit(this.items, (int)index, (int)count);
        if (!stack.isEmpty()) {
            this.markDirty();
        }
        return stack;
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        return ItemStackHelper.getAndRemove(this.items, (int)index);
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        this.items.set(index, (Object)stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public String getCommandSenderName() {
        return this.hasCustomInventoryName() ? this.customName : "container.dermoid_cyst";
    }

    public boolean hasCustomInventoryName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String name) {
        this.customName = name;
        this.markForRenderUpdate();
    }

    public String getGuiID() {
        return "minecraft:chest";
    }

    public Container createContainer(InventoryPlayer inv, EntityPlayer player) {
        return new ContainerChest((IInventory)inv, (IInventory)this, player);
    }

    public boolean isNamedBoris() {
        if (this.customName == null) {
            return false;
        }
        String n = this.customName.trim();
        return "Boris".equalsIgnoreCase(n) || "Borris".equalsIgnoreCase(n);
    }

    public int getNumPlayersUsing() {
        return this.numPlayersUsing;
    }

    public void openInventory(EntityPlayer player) {
        if (player.isSpectator()) {
            return;
        }
        ++this.numPlayersUsing;
        if (!this.worldObj.isRemote) {
            this.worldObj.playSound(null, this.pos, SRPSounds.FLESH_GROW, SoundCategory.BLOCKS, 10.0f, 1.0f);
            this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
        }
        this.markForRenderUpdate();
    }

    public void closeInventory(EntityPlayer player) {
        if (player.isSpectator()) {
            return;
        }
        this.numPlayersUsing = Math.max(0, this.numPlayersUsing - 1);
        if (!this.worldObj.isRemote) {
            this.worldObj.playSound(null, this.pos, SRPSounds.FLESH_GROW, SoundCategory.BLOCKS, 10.0f, 1.0f);
            this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
        }
        this.markForRenderUpdate();
    }

    public boolean receiveClientEvent(int id, int param) {
        if (id == 1) {
            this.numPlayersUsing = param;
            if (this.worldObj != null) {
                WorldAccess.markBlockRangeForRenderUpdate(this.worldObj, this.pos, this.pos);
            }
            return true;
        }
        return super.receiveClientEvent(id, param);
    }

    public void update() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        if (++this.ticksSinceSync % 80 == 0) {
            int old = this.numPlayersUsing;
            this.numPlayersUsing = 0;
            double r = 5.0;
            AxisAlignedBB box = Boxes.grow(Boxes.create(this.pos), r);
            for (EntityPlayer p : EntityQueries.entities(this.worldObj, EntityPlayer.class, box)) {
                IInventory lower;
                if (!(p.openContainer instanceof ContainerChest) || (lower = ((ContainerChest)p.openContainer).getLowerChestInventory()) != this) continue;
                ++this.numPlayersUsing;
            }
            if (this.numPlayersUsing != old) {
                this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
                WorldAccess.markBlockRangeForRenderUpdate(this.worldObj, this.pos, this.pos);
            }
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.items = NonNullList.withSize((int)this.getSizeInventory(), (Object)ItemStack.EMPTY);
        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems((NBTTagCompound)nbt, this.items);
        }
        if (nbt.hasKey("CustomName", 8)) {
            this.customName = nbt.getString("CustomName");
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems((NBTTagCompound)nbt, this.items);
        }
        if (this.hasCustomInventoryName()) {
            nbt.setString("CustomName", this.customName);
        }
        return nbt;
    }

    private void markForRenderUpdate() {
        if (this.worldObj != null) {
            WorldAccess.markBlockRangeForRenderUpdate(this.worldObj, this.pos, this.pos);
            if (!this.worldObj.isRemote) {
                this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
            }
        }
        this.markDirty();
    }
}

