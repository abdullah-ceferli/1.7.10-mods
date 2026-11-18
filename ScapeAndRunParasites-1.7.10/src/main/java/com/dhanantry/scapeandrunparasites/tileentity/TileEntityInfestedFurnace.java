/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ContainerFurnace
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.IInteractionObject
 */
package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ITickable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IInteractionObject;

public class TileEntityInfestedFurnace
extends TileEntity
implements ISidedInventory,
ITickable,
IInteractionObject {
    public static boolean DEBUG = false;
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 1};
    private static final int[] SLOTS_SIDES = new int[]{1};
    private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize((int)3, (Object)ItemStack.EMPTY);
    private int furnaceBurnTime;
    private int currentItemBurnTime;
    private int cookTime;
    private int totalCookTime = 200;
    private String customName;

    public void update() {
        boolean wasBurning = this.isBurning();
        boolean dirty = false;
        if (this.isBurning()) {
            --this.furnaceBurnTime;
        }
        if (!this.worldObj.isRemote) {
            ItemStack fuel = (ItemStack)this.furnaceItemStacks.get(1);
            ItemStack input = (ItemStack)this.furnaceItemStacks.get(0);
            if (this.isBurning() || !fuel.isEmpty() && !input.isEmpty()) {
                if (!this.isBurning() && this.canSmelt()) {
                    this.currentItemBurnTime = this.furnaceBurnTime = TileEntityInfestedFurnace.getItemBurnTime(fuel);
                    if (this.isBurning()) {
                        dirty = true;
                        if (!fuel.isEmpty()) {
                            ItemStack container = fuel.getItem().getContainerItem(fuel);
                            fuel.shrink(1);
                            if (fuel.isEmpty()) {
                                this.furnaceItemStacks.set(1, (Object)container);
                            }
                        }
                    }
                }
                if (this.isBurning() && this.canSmelt()) {
                    ++this.cookTime;
                    if (this.cookTime >= this.totalCookTime) {
                        this.cookTime = 0;
                        this.totalCookTime = 200;
                        this.smeltItem();
                        dirty = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = 0;
            }
            if (wasBurning != this.isBurning()) {
                if (DEBUG) {
                    System.out.println("Infested Furnace: burnStateChange pos=" + this.pos + " " + wasBurning + " -> " + this.isBurning());
                }
                BlockInfestedFurnace.setLitState(this.isBurning(), this.worldObj, this.pos);
                dirty = true;
            }
        }
        if (dirty) {
            this.markDirty();
        }
    }

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    private boolean canSmelt() {
        ItemStack input = (ItemStack)this.furnaceItemStacks.get(0);
        if (input.isEmpty()) {
            return false;
        }
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
        if (result.isEmpty()) {
            return false;
        }
        ItemStack output = (ItemStack)this.furnaceItemStacks.get(2);
        if (output.isEmpty()) {
            return true;
        }
        if (!output.isItemEqual(result)) {
            return false;
        }
        int res = output.getCount() + result.getCount();
        return res <= this.getInventoryStackLimit() && res <= output.getMaxStackSize();
    }

    private void smeltItem() {
        if (!this.canSmelt()) {
            return;
        }
        ItemStack input = (ItemStack)this.furnaceItemStacks.get(0);
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
        ItemStack output = (ItemStack)this.furnaceItemStacks.get(2);
        if (output.isEmpty()) {
            this.furnaceItemStacks.set(2, (Object)result.copy());
        } else if (output.getItem() == result.getItem()) {
            output.grow(result.getCount());
        }
        input.shrink(1);
    }

    public static int getItemBurnTime(ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime((ItemStack)stack);
    }

    public int getSizeInventory() {
        return this.furnaceItemStacks.size();
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.furnaceItemStacks) {
            if (itemstack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public ItemStack getStackInSlot(int index) {
        return (ItemStack)this.furnaceItemStacks.get(index);
    }

    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.furnaceItemStacks, (int)index, (int)count);
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        return ItemStackHelper.getAndRemove(this.furnaceItemStacks, (int)index);
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack prev = (ItemStack)this.furnaceItemStacks.get(index);
        boolean same = !stack.isEmpty() && stack.isItemEqual(prev) && ItemStack.areItemStackTagsEqual((ItemStack)stack, (ItemStack)prev);
        this.furnaceItemStacks.set(index, (Object)stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        if (index == 0 && !same) {
            this.totalCookTime = 200;
            this.cookTime = 0;
            this.markDirty();
        }
    }

    public String getCommandSenderName() {
        return this.hasCustomInventoryName() ? this.customName : "tile.srparasites.infested_furnace.name";
    }

    public boolean hasCustomInventoryName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomInventoryName(String name) {
        this.customName = name;
    }

    public IChatComponent getDisplayName() {
        return this.hasCustomInventoryName() ? new ChatComponentText(this.customName) : new ChatComponentTranslation("tile.srparasites.infested_furnace.name", new Object[0]);
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        TileEntity te = WorldAccess.getTileEntity(this.worldObj, this.pos);
        if (te != this) {
            if (DEBUG) {
                System.out.println("Infested Furnace: isUsableByPlayer FAILED pos=" + this.pos + " worldTE=" + (te == null ? "null" : te.getClass().getName()) + " this=" + ((Object)((Object)this)).getClass().getName());
            }
            return false;
        }
        return player.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    public void openInventory(EntityPlayer player) {
        if (DEBUG) {
            System.out.println("Infested Furnace: openInventory pos=" + this.pos + " player=" + player.getCommandSenderName());
        }
    }

    public void closeInventory(EntityPlayer player) {
        if (DEBUG) {
            System.out.println("Infested Furnace: closeInventory pos=" + this.pos + " player=" + player.getCommandSenderName());
        }
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        }
        if (index != 1) {
            return true;
        }
        return TileEntityFurnace.isItemFuel((ItemStack)stack);
    }

    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_BOTTOM : (side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES);
    }

    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (direction == EnumFacing.DOWN && index == 1) {
            return stack.getItem() == Items.bucket;
        }
        return true;
    }

    public int getField(int id) {
        switch (id) {
            case 0: {
                return this.furnaceBurnTime;
            }
            case 1: {
                return this.currentItemBurnTime;
            }
            case 2: {
                return this.cookTime;
            }
            case 3: {
                return this.totalCookTime;
            }
        }
        return 0;
    }

    public void setField(int id, int value) {
        switch (id) {
            case 0: {
                this.furnaceBurnTime = value;
                break;
            }
            case 1: {
                this.currentItemBurnTime = value;
                break;
            }
            case 2: {
                this.cookTime = value;
                break;
            }
            case 3: {
                this.totalCookTime = value;
            }
        }
    }

    public int getFieldCount() {
        return 4;
    }

    public void clear() {
        for (int i = 0; i < this.furnaceItemStacks.size(); ++i) {
            this.furnaceItemStacks.set(i, (Object)ItemStack.EMPTY);
        }
    }

    public String getGuiID() {
        return "minecraft:furnace";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        if (DEBUG) {
            System.out.println("Infested Furnace: createContainer pos=" + this.pos + " player=" + playerIn.getCommandSenderName());
        }
        return new ContainerFurnace(playerInventory, (IInventory)this);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems((NBTTagCompound)compound, this.furnaceItemStacks);
        compound.setInteger("BurnTime", this.furnaceBurnTime);
        compound.setInteger("CookTime", this.cookTime);
        compound.setInteger("CookTimeTotal", this.totalCookTime);
        compound.setInteger("CurrentBurnTime", this.currentItemBurnTime);
        if (this.hasCustomInventoryName()) {
            compound.setString("CustomName", this.customName);
        }
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.furnaceItemStacks = NonNullList.withSize((int)3, (Object)ItemStack.EMPTY);
        ItemStackHelper.loadAllItems((NBTTagCompound)compound, this.furnaceItemStacks);
        this.furnaceBurnTime = compound.getInteger("BurnTime");
        this.cookTime = compound.getInteger("CookTime");
        this.totalCookTime = compound.getInteger("CookTimeTotal");
        this.currentItemBurnTime = compound.getInteger("CurrentBurnTime");
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }
}

