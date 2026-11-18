/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.tileentity.TileEntityLockable
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 */
package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.container.ContainerInfuserFurnace;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipe;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityLockable;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ITickable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

public class TileEntityInfuserFurnace
extends TileEntityLockable
implements ITickable,
ISidedInventory {
    public static final int SLOT_SMELT_IN = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_SMELT_OUT = 2;
    public static final int SLOT_INFUSE_IN = 3;
    public static final int SLOT_INFUSE_OUT = 4;
    public static final int SLOT_BOTTLE_OUT = 5;
    private NonNullList<ItemStack> items = NonNullList.withSize((int)6, (Object)ItemStack.EMPTY);
    private int burnTime;
    private int currentBurnTime;
    private int cookTimeSmelt;
    private int cookTimeInfuse;
    private static final int COOK_TIME_TOTAL = 200;
    private static final int[] TOP = new int[]{0};
    private static final int[] BOTTOM = new int[]{2, 4, 5};
    private static final int[] SIDES = new int[]{1, 3};

    private InfuserFurnaceRecipe getCurrentRecipe() {
        ItemStack smelt = this.getStackInSlot(0);
        ItemStack infuse = this.getStackInSlot(3);
        return InfuserFurnaceRecipes.find(smelt, infuse);
    }

    public void update() {
        ItemStack fuel;
        int fuelBurn;
        boolean wasBurning = this.isBurning();
        boolean dirty = false;
        if (this.isBurning()) {
            --this.burnTime;
        }
        InfuserFurnaceRecipe r = this.getCurrentRecipe();
        if (!this.isBurning() && (this.canSmelt() || this.canInfuse(r)) && (fuelBurn = TileEntityFurnace.getItemBurnTime((ItemStack)(fuel = (ItemStack)this.items.get(1)))) > 0) {
            this.burnTime = fuelBurn;
            this.currentBurnTime = fuelBurn;
            dirty = true;
            Item fuelItem = fuel.getItem();
            fuel.shrink(1);
            if (fuel.isEmpty()) {
                Item container = fuelItem.getContainerItem();
                this.items.set(1, (Object)(container != null ? new ItemStack(container) : ItemStack.EMPTY));
            }
        }
        if (this.isBurning() && this.canSmelt()) {
            ++this.cookTimeSmelt;
            if (this.cookTimeSmelt >= 200) {
                this.cookTimeSmelt = 0;
                this.doSmelt();
                dirty = true;
            }
        } else {
            this.cookTimeSmelt = 0;
        }
        if (this.isBurning() && this.canInfuse(r)) {
            ++this.cookTimeInfuse;
            if (this.cookTimeInfuse >= (r != null ? r.cookTime : 200)) {
                this.cookTimeInfuse = 0;
                this.doInfuse(r);
                dirty = true;
            }
        } else {
            this.cookTimeInfuse = 0;
        }
        if (wasBurning != this.isBurning()) {
            dirty = true;
        }
        if (dirty) {
            this.markDirty();
        }
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    private boolean canSmelt() {
        ItemStack in = (ItemStack)this.items.get(0);
        if (in.isEmpty()) {
            return false;
        }
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(in);
        if (result.isEmpty()) {
            return false;
        }
        ItemStack out = (ItemStack)this.items.get(2);
        return this.canOutputStack(out, result);
    }

    private void doSmelt() {
        if (!this.canSmelt()) {
            return;
        }
        ItemStack in = (ItemStack)this.items.get(0);
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(in).copy();
        ItemStack out = (ItemStack)this.items.get(2);
        this.items.set(2, (Object)this.pushToOutput(out, result));
        in.shrink(1);
    }

    private boolean canInfuse(InfuserFurnaceRecipe r) {
        if (r == null) {
            return false;
        }
        if (!TileEntityInfuserFurnace.canMerge(this.getStackInSlot(4), r.infusedOut)) {
            return false;
        }
        return TileEntityInfuserFurnace.canMerge(this.getStackInSlot(5), r.bottleOut);
    }

    private void doInfuse(InfuserFurnaceRecipe r) {
        if (!this.canInfuse(r)) {
            return;
        }
        this.getStackInSlot(0).shrink(1);
        this.getStackInSlot(3).shrink(1);
        this.mergeInto(4, r.infusedOut);
        this.mergeInto(5, r.bottleOut);
        this.markDirty();
    }

    private boolean canOutputStack(ItemStack existing, ItemStack toAdd) {
        if (toAdd.isEmpty()) {
            return false;
        }
        if (existing.isEmpty()) {
            return true;
        }
        if (!existing.isItemEqual(toAdd)) {
            return false;
        }
        int total = existing.getCount() + toAdd.getCount();
        return total <= existing.getMaxStackSize() && total <= this.getInventoryStackLimit();
    }

    private ItemStack pushToOutput(ItemStack existing, ItemStack toAdd) {
        if (existing.isEmpty()) {
            return toAdd;
        }
        existing.grow(toAdd.getCount());
        return existing;
    }

    private static boolean canMerge(ItemStack slotStack, ItemStack add) {
        if (add.isEmpty()) {
            return true;
        }
        if (slotStack.isEmpty()) {
            return true;
        }
        if (!ItemStack.areItemsEqual((ItemStack)slotStack, (ItemStack)add)) {
            return false;
        }
        if (!ItemStack.areItemStackTagsEqual((ItemStack)slotStack, (ItemStack)add)) {
            return false;
        }
        return slotStack.getCount() + add.getCount() <= slotStack.getMaxStackSize();
    }

    private void mergeInto(int slot, ItemStack add) {
        if (add.isEmpty()) {
            return;
        }
        ItemStack cur = this.getStackInSlot(slot);
        if (cur.isEmpty()) {
            this.setInventorySlotContents(slot, add.copy());
        } else {
            cur.grow(add.getCount());
            this.setInventorySlotContents(slot, cur);
        }
    }

    public String getGuiID() {
        return "srparasites:infuser_furnace";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerInfuserFurnace(playerInventory, this, playerIn);
    }

    public String getCommandSenderName() {
        return "container.infuser_furnace";
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public IChatComponent getDisplayName() {
        return new ChatComponentTranslation(this.getCommandSenderName(), new Object[0]);
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
        return ItemStackHelper.getAndSplit(this.items, (int)index, (int)count);
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

    public boolean isUseableByPlayer(EntityPlayer player) {
        return WorldAccess.getTileEntity(this.worldObj, this.pos) == this && player.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    public void openInventory(EntityPlayer player) {
    }

    public void closeInventory(EntityPlayer player) {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2 || index == 4 || index == 5) {
            return false;
        }
        if (index == 1) {
            return TileEntityFurnace.isItemFuel((ItemStack)stack);
        }
        return true;
    }

    public int getField(int id) {
        switch (id) {
            case 0: {
                return this.burnTime;
            }
            case 1: {
                return this.currentBurnTime;
            }
            case 2: {
                return this.cookTimeSmelt;
            }
            case 3: {
                return this.cookTimeInfuse;
            }
        }
        return 0;
    }

    public void setField(int id, int value) {
        switch (id) {
            case 0: {
                this.burnTime = value;
                break;
            }
            case 1: {
                this.currentBurnTime = value;
                break;
            }
            case 2: {
                this.cookTimeSmelt = value;
                break;
            }
            case 3: {
                this.cookTimeInfuse = value;
            }
        }
    }

    public int getFieldCount() {
        return 4;
    }

    public void clear() {
        this.items.clear();
    }

    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            return BOTTOM;
        }
        if (side == EnumFacing.UP) {
            return TOP;
        }
        return SIDES;
    }

    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == 2 || index == 4 || index == 5;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack s = (ItemStack)this.items.get(i);
            if (s.isEmpty()) continue;
            NBTTagCompound c = new NBTTagCompound();
            c.setByte("Slot", (byte)i);
            s.writeToNBT(c);
            list.appendTag((NBTBase)c);
        }
        compound.setTag("Items", (NBTBase)list);
        compound.setInteger("BurnTime", this.burnTime);
        compound.setInteger("CurrentBurnTime", this.currentBurnTime);
        compound.setInteger("CookSmelt", this.cookTimeSmelt);
        compound.setInteger("CookInfuse", this.cookTimeInfuse);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.items = NonNullList.withSize((int)6, (Object)ItemStack.EMPTY);
        NBTTagList list = compound.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            int slot = c.getByte("Slot") & 0xFF;
            if (slot < 0 || slot >= this.items.size()) continue;
            this.items.set(slot, (Object)new ItemStack(c));
        }
        this.burnTime = compound.getInteger("BurnTime");
        this.currentBurnTime = compound.getInteger("CurrentBurnTime");
        this.cookTimeSmelt = compound.getInteger("CookSmelt");
        this.cookTimeInfuse = compound.getInteger("CookInfuse");
    }
}

