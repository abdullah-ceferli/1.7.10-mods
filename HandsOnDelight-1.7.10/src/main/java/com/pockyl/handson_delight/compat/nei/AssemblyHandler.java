package com.pockyl.handson_delight.compat.nei;

import com.pockyl.handson_delight.*;
import java.awt.Rectangle;
import java.util.*;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.legacy.*;

/** The original JEI AssemblyLayout: base on its board, layers beside it, arrow and finished dish. */
public final class AssemblyHandler extends TemplateRecipeHandler {
    public static final String UID="handson_delight.assembly";
    public String getOverlayIdentifier(){return UID;}
    public String getRecipeName(){return StatCollector.translateToLocal("handson_delight.assembly");}
    public String getGuiTexture(){return "handson_delight:textures/gui/assembly.png";}
    @Override public void loadTransferRects(){transferRects.add(new RecipeTransferRect(new Rectangle(109,19,22,16),UID));}
    @Override public void drawBackground(int index){
        GL11.glColor4f(1,1,1,1);Gui.drawRect(0,0,154,54,0xffc6c6c6);Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(getGuiTexture()));
        Gui g=new Gui();g.drawTexturedModalRect(4,29,4,29,40,22);g.drawTexturedModalRect(109,19,48,21,22,16);g.drawTexturedModalRect(136,18,0,58,18,18);
        Cached e=(Cached)arecipes.get(index);int n=e.inputs.size()-1,rows=(n+2)/3;
        for(int i=0;i<n;i++)g.drawTexturedModalRect(50+i%3*18,(54-rows*18)/2+i/3*18,0,58,18,18);
    }
    private List<RecipeBook.Entry> entries(){List<RecipeBook.Entry> out=new ArrayList<RecipeBook.Entry>();for(RecipeBook.Entry e:RecipeBook.list(new CuttingBoardTile()))if(e.assembly!=null)out.add(e);return out;}
    @Override public void loadCraftingRecipes(String id,Object... results){if(id.equals(UID)||id.equals("all"))for(RecipeBook.Entry e:entries())arecipes.add(new Cached(e));else super.loadCraftingRecipes(id,results);}
    @Override public void loadCraftingRecipes(ItemStack result){for(RecipeBook.Entry e:entries())if(Stacks.same(e.output,result))arecipes.add(new Cached(e));}
    @Override public void loadUsageRecipes(ItemStack used){
        if(!Stacks.empty(used)&&used.getItem()==net.minecraft.item.Item.getItemFromBlock(Content.cuttingBoard)){loadCraftingRecipes(UID);return;}
        for(RecipeBook.Entry e:entries())for(List<ItemStack> options:e.inputs)if(Stacks.matches(options,used)){arecipes.add(new Cached(e));break;}
    }
    @Override public List<String> handleItemTooltip(GuiRecipe<?> gui,ItemStack stack,List<String> tip,int index){
        super.handleItemTooltip(gui,stack,tip,index);Cached c=(Cached)arecipes.get(index);if(gui.isMouseOver(c.inputs.get(0),index))tip.add("\u00a77"+StatCollector.translateToLocal("handson_delight.assembly.first"));return tip;
    }
    public final class Cached extends CachedRecipe {
        public final RecipeBook.Entry recipe;public final List<PositionedStack> inputs=new ArrayList<PositionedStack>();private final PositionedStack output;
        Cached(RecipeBook.Entry e){recipe=e;int base=base(e);inputs.add(new PositionedStack(e.inputs.get(base),16,27));int n=e.inputs.size()-1,j=0;
            for(int i=0;i<e.inputs.size();i++)if(i!=base){inputs.add(new PositionedStack(e.inputs.get(i),51+j%3*18,(54-((n+2)/3)*18)/2+j/3*18+1));j++;}
            output=new PositionedStack(e.output,137,19);
        }
        public List<PositionedStack> getIngredients(){return getCycledIngredients(cycleticks/20,inputs);}public PositionedStack getResult(){return output;}
    }
    /** The upstream base priority, retained for NEI's "goes down first" ingredient. */
    public static int base(RecipeBook.Entry e){
        String[][] names={{"bowl"},{"glass_bottle"},{"bread","burger"},{"tortilla","flatbread"},{"toast"},{"pie_crust"},{"stick","blaze_rod"},{"kelp"},{"rice"},{"wheat","dough","flour"}};
        for(String[] group:names)for(int i=0;i<e.inputs.size();i++)for(ItemStack s:e.inputs.get(i)){String id=net.minecraft.item.Item.itemRegistry.getNameForObject(s.getItem());for(String word:group)if(id.contains(word))return i;}return 0;
    }
}
