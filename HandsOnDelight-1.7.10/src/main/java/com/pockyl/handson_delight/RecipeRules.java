package com.pockyl.handson_delight;

import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.relauncher.ReflectionHelper;
import vectorwing.farmersdelight.legacy.*;
import vectorwing.farmersdelight.legacy.api.FoundationRecipe;

/** Upstream assembly selection and personal-grid restriction. Automation uses the original recipe objects. */
public final class RecipeRules {
    private static final Field OWNER=ReflectionHelper.findField(InventoryCrafting.class,"eventHandler","field_70465_c");
    private static final Field FOUNDATION=ReflectionHelper.findField(NativeShapeless.class,"recipe");
    public static FoundationRecipe foundation(NativeShapeless r){try{return (FoundationRecipe)FOUNDATION.get(r);}catch(Exception e){throw new IllegalStateException(e);}}
    public static boolean eligible(List<List<ItemStack>> ingredients,ItemStack result){
        if(Stacks.empty(result)||ingredients.size()<2)return false;
        Set<String> kinds=new HashSet<String>();for(List<ItemStack> options:ingredients){
            if(options==null||options.isEmpty())continue;boolean workshop=true;SortedSet<String> keys=new TreeSet<String>();for(ItemStack s:options){keys.add(Item.itemRegistry.getNameForObject(s.getItem())+"@"+s.getItemDamage());if(!workshop(s))workshop=false;}if(workshop)return false;kinds.add(keys.toString());
        }
        if(kinds.size()<2)return false;
        String id=Item.itemRegistry.getNameForObject(result.getItem());
        if(id.equals("farmersdelight:milk_bottle"))return false;
        if(result.getItem() instanceof ItemFood||net.minecraft.block.Block.getBlockFromItem(result.getItem()) instanceof ServingBlock)return true;
        for(CuttingRecipes.Recipe r:CuttingRecipes.ALL)if(r.matchesInput(result))for(ItemStack s:r.outputs)if(s.getItem() instanceof ItemFood)return true;
        return id.equals("farmersdelight:wheat_dough");
    }
    private static boolean workshop(ItemStack s){
        if(s.getItem()==Item.getItemFromBlock(Blocks.obsidian))return true;
        for(int id:OreDictionary.getOreIDs(s)){String n=OreDictionary.getOreName(id);if(n.startsWith("ingot")||n.startsWith("nugget")||n.startsWith("gem")||n.equals("obsidian"))return true;}return false;
    }
    public static boolean moved(Object recipe){
        if(recipe instanceof SourceCrafting){SourceCrafting r=(SourceCrafting)recipe;List<List<ItemStack>> inputs=new ArrayList<List<ItemStack>>();for(List<ItemStack> i:r.inputs)if(i!=null)inputs.add(i);return r.enabled()&&eligible(inputs,r.output);}
        if(recipe instanceof NativeShapeless){FoundationRecipe r=foundation((NativeShapeless)recipe);List<List<ItemStack>> inputs=new ArrayList<List<ItemStack>>();for(String s:r.inputs)inputs.add(Ingredients.candidates(s));return eligible(inputs,((NativeShapeless)recipe).getRecipeOutput());}return false;
    }
    public static boolean allowed(Object recipe,InventoryCrafting grid){
        try{Object owner=OWNER.get(grid);return !(owner instanceof ContainerPlayer||owner instanceof ContainerWorkbench)||!moved(recipe);}catch(Exception e){throw new IllegalStateException(e);}
    }
    /** Named base, or -1 when the original rule accepts any first layer. */
    public static int base(List<List<ItemStack>> inputs){
        String[][] names={{"bowl"},{"glass_bottle"},{"bread","burger"},{"tortilla","flatbread"},{"toast"},{"pie_crust"},{"stick","blaze_rod"},{"kelp"},{"rice"},{"wheat","dough","flour"}};
        for(String[] group:names)for(int i=0;i<inputs.size();i++)for(ItemStack s:inputs.get(i)){String id=Item.itemRegistry.getNameForObject(s.getItem());for(String word:group)if(id.contains(word))return i;}return -1;
    }
    private RecipeRules(){}
}
