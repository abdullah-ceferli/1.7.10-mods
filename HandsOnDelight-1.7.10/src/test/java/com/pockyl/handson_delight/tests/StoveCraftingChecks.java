package com.pockyl.handson_delight.tests;

import com.pockyl.handson_delight.StoveCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.Stacks;

/** Crafts through a real player's workbench and output slot, rather than testing only a recipe object. */
public final class StoveCraftingChecks {
    private static void require(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static int count(){int n=0;for(Object entry:CraftingManager.getInstance().getRecipeList())if(entry instanceof IRecipe&&Stacks.same(new ItemStack(Content.stove),((IRecipe)entry).getRecipeOutput()))n++;return n;}
    public static void run(World world,EntityPlayer player){
        int existing=count();require(existing==1,"Expected one native stove recipe, got "+existing);StoveCrafting.register();require(count()==existing,"Stove fallback duplicates an existing recipe");
        ContainerWorkbench table=new ContainerWorkbench(player.inventory,world,0,80,0);
        int[] brickSlots={3,5,6,8};
        for(int i=0;i<3;i++)table.craftMatrix.setInventorySlotContents(i,new ItemStack(Items.iron_ingot));
        for(int slot:brickSlots)table.craftMatrix.setInventorySlotContents(slot,new ItemStack(Blocks.brick_block));
        table.craftMatrix.setInventorySlotContents(7,new ItemStack(Blocks.furnace));
        ItemStack expected=new ItemStack(Content.stove);
        require(Stacks.same(expected,CraftingManager.getInstance().findMatchingRecipe(table.craftMatrix,world)),"Player workbench does not craft a stove");
        Slot output=(Slot)table.inventorySlots.get(0);require(Stacks.same(expected,output.getStack())&&output.getStack().stackSize==1,"Workbench output must be exactly one stove");
        ItemStack crafted=output.decrStackSize(1);output.onPickupFromSlot(player,crafted);require(Stacks.same(expected,crafted)&&crafted.stackSize==1,"Native output pickup lost the stove");
        for(int i=0;i<9;i++)require(Stacks.empty(table.craftMatrix.getStackInSlot(i)),"Crafting did not consume exactly the iron, bricks and furnace");
        for(int i=0;i<3;i++)table.craftMatrix.setInventorySlotContents(i,new ItemStack(Items.iron_ingot));
        for(int slot:brickSlots)table.craftMatrix.setInventorySlotContents(slot,new ItemStack(Blocks.brick_block));
        require(!Stacks.same(expected,CraftingManager.getInstance().findMatchingRecipe(table.craftMatrix,world)),"Stove crafts without a furnace");
        table.craftMatrix.setInventorySlotContents(7,new ItemStack(Blocks.furnace));
        for(int slot:brickSlots)table.craftMatrix.setInventorySlotContents(slot,new ItemStack(Items.brick));
        require(!Stacks.same(expected,CraftingManager.getInstance().findMatchingRecipe(table.craftMatrix,world)),"Loose bricks incorrectly replace brick blocks");
    }
    private StoveCraftingChecks(){}
}
