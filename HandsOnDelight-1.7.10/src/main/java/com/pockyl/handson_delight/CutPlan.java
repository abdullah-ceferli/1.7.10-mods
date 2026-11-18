package com.pockyl.handson_delight;

import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.CuttingRecipes;

/** Cutting.plan's three-mark limit and original across/straight/wedge/strip rules. */
public final class CutPlan {
    public static int style(ItemStack stack){String id=net.minecraft.item.Item.itemRegistry.getNameForObject(stack.getItem());if(id.endsWith("wheat_dough"))return 3;if(id.endsWith("kelp_roll"))return 1;if(id.endsWith("_pie")||id.endsWith("cake")||id.endsWith("cheesecake")||id.equals("minecraft:melon")||id.equals("minecraft:pumpkin"))return 2;return 0;}
    public static int marks(ItemStack stack,CuttingRecipes.Recipe recipe){if(style(stack)==3)return 3;int pieces=recipe.outputs.isEmpty()?2:recipe.outputs.get(0).stackSize+recipe.outputs.size()-1;return Math.max(1,Math.min(3,style(stack)==2?(pieces+1)/2:pieces-1));}
    public static float spot(ItemStack stack,int mark,int marks){return .2F+.6F*(mark+1F)/(marks+1F);}
    private CutPlan(){}
}
