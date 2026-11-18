/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.items.CapabilityItemHandler
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.IItemHandlerModifiable
 *  net.minecraftforge.items.SlotItemHandler
 */
package com.dhanantry.scapeandrunparasites.container;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.item.ItemModule;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ScannerContainer
extends Container {
    private final TileEntityRelayController te;
    private final boolean clientFallback;

    public ScannerContainer(InventoryPlayer playerInv, TileEntityRelayController te) {
        boolean fallback;
        this.te = te;
        Object handler = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        this.clientFallback = fallback = handler == null;
        if (fallback) {
            handler = new ReadonlyClientHandler();
        }
        IItemHandler finalHandler = handler;
        this.addSlotToContainer((Slot)new SlotItemHandler(finalHandler, 0, 80, 35){

            public boolean isItemValid(ItemStack stack) {
                if (ScannerContainer.this.clientFallback) {
                    return false;
                }
                return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemModule;
            }

            public int getSlotStackLimit() {
                return 1;
            }

            public boolean canTakeStack(EntityPlayer playerIn) {
                return !ScannerContainer.this.clientFallback && super.canTakeStack(playerIn);
            }
        });
        int xStart = 8;
        int yStart = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot((IInventory)playerInv, col + row * 9 + 9, xStart + col * 18, yStart + row * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot((IInventory)playerInv, i, xStart + i * 18, yStart + 58));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        double cz;
        double cy;
        if (this.te.getWorldObj() == null || WorldAccess.getTileEntity(this.te.getWorldObj(), this.te.getPos()) != this.te) {
            return false;
        }
        double cx = (double)this.te.getPos().getX() + 0.5;
        return playerIn.getDistanceSq(cx, cy = (double)this.te.getPos().getY() + 0.5, cz = (double)this.te.getPos().getZ() + 0.5) <= 64.0;
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (this.clientFallback) {
            return ItemStack.EMPTY;
        }
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack in = slot.getStack();
            ret = in.copy();
            if (index == 0) {
                if (!this.mergeItemStack(in, 1, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (in.getItem() instanceof ItemModule) {
                if (!this.mergeItemStack(in, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
            if (in.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return ret;
    }

    private static final class ReadonlyClientHandler
    implements IItemHandlerModifiable {
        private final ItemStack[] slots = new ItemStack[]{ItemStack.EMPTY};

        private ReadonlyClientHandler() {
        }

        public int getSlots() {
            return 1;
        }

        public ItemStack getStackInSlot(int slot) {
            return this.slots[0];
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        public int getSlotLimit(int slot) {
            return 1;
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemModule;
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            this.slots[0] = stack;
        }
    }
}

