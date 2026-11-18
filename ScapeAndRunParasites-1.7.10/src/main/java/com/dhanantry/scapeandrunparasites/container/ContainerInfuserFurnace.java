/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IContainerListener
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.inventory.SlotFurnaceOutput
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityFurnace
 */
package com.dhanantry.scapeandrunparasites.container;

import com.dhanantry.scapeandrunparasites.tileentity.TileEntityInfuserFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerInfuserFurnace
extends Container {
    private final TileEntityInfuserFurnace te;
    private int burnTime;
    private int currentBurnTime;
    private int cookSmelt;
    private int cookInfuse;

    public ContainerInfuserFurnace(InventoryPlayer playerInv, TileEntityInfuserFurnace te, EntityPlayer player) {
        this.te = te;
        this.addSlotToContainer(new Slot((IInventory)te, 0, 56, 17));
        this.addSlotToContainer(new Slot((IInventory)te, 1, 56, 53){

            public boolean isItemValid(ItemStack stack) {
                return TileEntityFurnace.isItemFuel((ItemStack)stack);
            }
        });
        this.addSlotToContainer(new Slot((IInventory)te, 3, 30, 35));
        this.addSlotToContainer((Slot)new SlotFurnaceOutput(player, (IInventory)te, 4, 140, 53));
        this.addSlotToContainer((Slot)new SlotFurnaceOutput(player, (IInventory)te, 5, 116, 35));
        this.addSlotToContainer((Slot)new SlotFurnaceOutput(player, (IInventory)te, 2, 140, 17));
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot((IInventory)playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot((IInventory)playerInv, col, 8 + col * 18, 142));
        }
    }

    public void addCraftingToCrafters(IContainerListener listener) {
        super.addCraftingToCrafters(listener);
        listener.sendAllWindowProperties((Container)this, (IInventory)this.te);
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener l : this.crafters) {
            if (this.burnTime != this.te.getField(0)) {
                l.sendProgressBarUpdate((Container)this, 0, this.te.getField(0));
            }
            if (this.currentBurnTime != this.te.getField(1)) {
                l.sendProgressBarUpdate((Container)this, 1, this.te.getField(1));
            }
            if (this.cookSmelt != this.te.getField(2)) {
                l.sendProgressBarUpdate((Container)this, 2, this.te.getField(2));
            }
            if (this.cookInfuse == this.te.getField(3)) continue;
            l.sendProgressBarUpdate((Container)this, 3, this.te.getField(3));
        }
        this.burnTime = this.te.getField(0);
        this.currentBurnTime = this.te.getField(1);
        this.cookSmelt = this.te.getField(2);
        this.cookInfuse = this.te.getField(3);
    }

    public void updateProgressBar(int id, int data) {
        this.te.setField(id, data);
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.te.isUseableByPlayer(playerIn);
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return empty;
        }
        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();
        int TE_SLOTS = 6;
        int INV_START = 6;
        int INV_END = this.inventorySlots.size();
        if (index < 6 ? !this.mergeItemStack(stack, 6, INV_END, true) : (TileEntityFurnace.isItemFuel((ItemStack)stack) ? !this.mergeItemStack(stack, 1, 2, false) : !this.mergeItemStack(stack, 0, 1, false) && !this.mergeItemStack(stack, 3, 4, false))) {
            return empty;
        }
        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return copy;
    }
}

