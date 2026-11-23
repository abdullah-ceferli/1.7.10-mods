package com.pockyl.handson_delight;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.Stacks;

/** Keeps the source stove pattern, using a native furnace instead of the missing campfire. */
public final class StoveCrafting {
    public static void register(){
        ItemStack stove=new ItemStack(Content.stove);
        for(Object entry:CraftingManager.getInstance().getRecipeList())
            if(entry instanceof IRecipe&&Stacks.same(stove,((IRecipe)entry).getRecipeOutput()))return;
        GameRegistry.addRecipe(new ShapedOreRecipe(stove,"iii","B B","BFB",'i',"ingotIron",'B',Blocks.brick_block,'F',Blocks.furnace));
    }
    private StoveCrafting(){}
}
