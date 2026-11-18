package com.pockyl.handson_delight.compat.nei;

import java.util.*;
import codechicken.nei.*;
import codechicken.nei.recipe.*;
import com.pockyl.handson_delight.*;
import vectorwing.farmersdelight.legacy.*;

/** Hides only the personal crafting representations that the original plugin moves to its board category. */
public final class NEIHooks {
    public static void filter(Object raw){
        TemplateRecipeHandler h=(TemplateRecipeHandler)raw;List<RecipeBook.Entry> dishes=RecipeBook.list(new CuttingBoardTile());
        Iterator<TemplateRecipeHandler.CachedRecipe> it=h.arecipes.iterator();while(it.hasNext()){
            TemplateRecipeHandler.CachedRecipe c=it.next();PositionedStack out=c.getResult();if(out==null)continue;
            for(RecipeBook.Entry e:dishes)if(e.assembly!=null&&Stacks.same(e.output,out.item)&&matches(e.inputs,c.getIngredients(),0,0)){it.remove();break;}
        }
    }
    private static boolean matches(List<List<net.minecraft.item.ItemStack>> recipe,List<PositionedStack> cached,int at,int used){
        if(recipe.size()!=cached.size())return false;if(at==recipe.size())return true;
        for(int i=0;i<cached.size();i++)if((used&(1<<i))==0){boolean same=false;for(net.minecraft.item.ItemStack option:cached.get(i).items)if(Stacks.matches(recipe.get(at),option)){same=true;break;}if(same&&matches(recipe,cached,at+1,used|(1<<i)))return true;}return false;
    }
    public static boolean view(net.minecraft.item.ItemStack stack,boolean usages){return usages?GuiUsageRecipe.openRecipeGui("item",stack):GuiCraftingRecipe.openRecipeGui("item",stack);}
    public static boolean related(net.minecraft.client.gui.GuiScreen screen,net.minecraft.client.gui.GuiScreen kitchen){return screen instanceof GuiRecipe&&((GuiRecipe<?>)screen).getFirstScreenGeneral()==kitchen;}
    private NEIHooks(){}
}
