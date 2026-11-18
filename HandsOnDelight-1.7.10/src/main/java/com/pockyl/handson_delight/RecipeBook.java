package com.pockyl.handson_delight;

import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import vectorwing.farmersdelight.legacy.*;

/** Reads the installed native recipes; book choices cannot fabricate a result. */
public final class RecipeBook {
    public static final class Entry {
        public final String id;public final ItemStack output;public final List<List<ItemStack>> inputs;
        public final IRecipe assembly;public final List<List<ItemStack>> grid;
        public List<ItemStack> foot=Collections.emptyList();public String note="";public CuttingRecipes.Recipe cut;
        private List<List<ItemStack>> display;
        public List<List<ItemStack>> displayInputs(){if(display==null){display=new ArrayList<List<ItemStack>>(inputs);if(cut!=null)display.add(cut.tools());}return display;}
        Entry(String id,ItemStack output,List<List<ItemStack>> inputs,IRecipe assembly){this(id,output,inputs,assembly,assembly instanceof SourceCrafting?((SourceCrafting)assembly).grid():null);}
        Entry(String id,ItemStack output,List<List<ItemStack>> inputs,IRecipe assembly,List<List<ItemStack>> grid){this.id=id;this.output=output;this.inputs=inputs;this.assembly=assembly;this.grid=grid;}
    }
    public static List<Entry> list(InventoryTile tile){
        List<Entry> result=new ArrayList<Entry>();
        if(tile instanceof CookingPotTile)for(CookingRecipes.Recipe r:CookingRecipes.ALL){Entry e=new Entry("pot/"+r.id,r.output,r.inputs,null);e.foot=new ArrayList<ItemStack>();if(!Stacks.empty(r.container))e.foot.add(r.container);e.foot.add(new ItemStack(net.minecraft.init.Items.clock));e.note=Math.round(r.time/20F/StationHooks.pace(1))+"s";result.add(e);}
        else if(tile instanceof GrillingTile)for(GrillingRecipes.Recipe r:GrillingRecipes.ALL){Entry e=new Entry("grill/"+r.id,r.output,Collections.singletonList(r.inputs),null);e.foot=Collections.singletonList(new ItemStack(net.minecraft.init.Items.clock));e.note=Math.round((tile instanceof StoveTile?r.time:GrillingRecipes.skilletTime(r.time,0))/20F/1.5F)+"s";result.add(e);}
        else if(tile instanceof CuttingBoardTile){
            for(CuttingRecipes.Recipe r:CuttingRecipes.ALL)if(!r.outputs.isEmpty()){Entry e=new Entry("cut/"+r.id,r.outputs.get(0),Collections.singletonList(r.inputs),null);e.cut=r;if(r.outputs.size()>1)e.foot=r.outputs;result.add(e);}
            for(SourceCrafting r:SourceCrafting.ALL)if(r.enabled()){
                List<List<ItemStack>> inputs=new ArrayList<List<ItemStack>>();for(List<ItemStack> i:r.grid())if(i!=null)inputs.add(i);
                if(RecipeRules.eligible(inputs,r.output))result.add(new Entry("assemble/"+r.id,r.output,inputs,r));
            }
            for(Object raw:CraftingManager.getInstance().getRecipeList())if(raw instanceof NativeShapeless){
                vectorwing.farmersdelight.legacy.api.FoundationRecipe r=RecipeRules.foundation((NativeShapeless)raw);
                List<List<ItemStack>> inputs=new ArrayList<List<ItemStack>>();for(String i:r.inputs)inputs.add(Ingredients.candidates(i));
                ItemStack output=((IRecipe)raw).getRecipeOutput();if(!RecipeRules.eligible(inputs,output))continue;
                List<List<ItemStack>> grid=new ArrayList<List<ItemStack>>(inputs);while(grid.size()<9)grid.add(null);result.add(new Entry("assemble/"+r.id,output,inputs,(IRecipe)raw,grid));
            }
        }
        Collections.sort(result,new Comparator<Entry>(){public int compare(Entry a,Entry b){return a.id.compareTo(b.id);}});return result;
    }
    public static Entry find(InventoryTile t,String id){for(Entry e:list(t))if(e.id.equals(id))return e;return null;}
    private static final Container GRID=new Container(){public boolean canInteractWith(EntityPlayer p){return true;}};
    /** Assigns the physical layers to ingredient positions, then asks the actual native recipe to match. */
    public static InventoryCrafting arrange(Entry e,List<ItemStack> layers){
        if(e.assembly==null||layers.size()!=e.inputs.size())return null;
        int[] slots=new int[layers.size()];Arrays.fill(slots,-1);
        if(!assign(e.inputs,layers,slots,0,0))return null;
        InventoryCrafting grid=new InventoryCrafting(GRID,3,3);int ingredient=0,index=0;
        for(List<ItemStack> options:e.grid){
            if(options!=null)grid.setInventorySlotContents(index,Stacks.copy(layers.get(slots[ingredient++]),1));index++;
        }
        return grid;
    }
    public static boolean prefix(Entry e,List<ItemStack> layers){
        if(e.assembly==null||layers.size()>e.inputs.size())return false;
        return assigned(e,layers)!=null;
    }
    public static int[] assigned(Entry e,List<ItemStack> layers){
        int[] assignment=new int[layers.size()];int base=RecipeRules.base(e.inputs);
        if(layers.isEmpty())return assignment;
        if(base>=0){if(!Stacks.matches(e.inputs.get(base),layers.get(0)))return null;assignment[0]=base;return assignPrefix(e.inputs,layers,1,1<<base,assignment)?assignment:null;}
        return assignPrefix(e.inputs,layers,0,0,assignment)?assignment:null;
    }
    private static boolean assignPrefix(List<List<ItemStack>> inputs,List<ItemStack> layers,int at,int used,int[] assignment){
        if(at==layers.size())return true;for(int i=0;i<inputs.size();i++)if((used&(1<<i))==0&&Stacks.matches(inputs.get(i),layers.get(at))){assignment[at]=i;if(assignPrefix(inputs,layers,at+1,used|(1<<i),assignment))return true;}return false;
    }
    private static boolean prefixAt(List<List<ItemStack>> inputs,List<ItemStack> layers,int at,int used){
        if(at==layers.size())return true;
        for(int i=0;i<inputs.size();i++)if((used&(1<<i))==0&&Stacks.matches(inputs.get(i),layers.get(at))&&prefixAt(inputs,layers,at+1,used|(1<<i)))return true;
        return false;
    }
    private static boolean assign(List<List<ItemStack>> inputs,List<ItemStack> layers,int[] slots,int at,int used){
        if(at==inputs.size())return true;
        for(int i=0;i<layers.size();i++)if((used&(1<<i))==0&&Stacks.matches(inputs.get(at),layers.get(i))){slots[at]=i;if(assign(inputs,layers,slots,at+1,used|(1<<i)))return true;}return false;
    }
    public static List<ItemStack> layers(CuttingBoardTile t){List<ItemStack> a=new ArrayList<ItemStack>();if(!Stacks.empty(t.getStackInSlot(0)))a.add(t.getStackInSlot(0).copy());a.addAll(StationHooks.state(t).layers);return a;}
    public static boolean assemble(CuttingBoardTile t,EntityPlayer p){
        List<ItemStack> layers=layers(t);if(layers.size()<2)return false;
        for(Entry e:list(t))if(e.assembly!=null){
            InventoryCrafting grid=arrange(e,layers);if(grid==null||!e.assembly.matches(grid,t.getWorldObj()))continue;
            ItemStack output=e.assembly.getCraftingResult(grid);if(Stacks.empty(output))continue;
            int rounds=Math.min(16,Math.min(64,output.getMaxStackSize())/output.stackSize);for(ItemStack layer:layers)if(layer.getMaxStackSize()>1)rounds=Math.min(rounds,layer.stackSize);if(rounds<1)return false;
            StationHooks.visual(t,Actions.ASSEMBLE,0,layers.get(0),null,output);StationHooks.state(t).layers.clear();output.stackSize*=rounds;t.setInventorySlotContents(0,output);
            for(ItemStack input:layers){int consumed=input.getMaxStackSize()==1?1:rounds;if(input.stackSize>consumed)Actions.give(p,t,Stacks.copy(input,input.stackSize-consumed));for(int n=0;n<consumed;n++)Actions.give(p,t,CookingRecipes.remainder(Stacks.copy(input,1)));}
            cpw.mods.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(p,output,grid);
            output.onCrafting(t.getWorldObj(),p,output.stackSize);t.changed();return true;
        }
        return false;
    }
    public static boolean finishIfTerminal(CuttingBoardTile t,EntityPlayer p){List<ItemStack> layers=layers(t);for(Entry e:list(t))if(e.assembly!=null&&e.inputs.size()>layers.size()&&prefix(e,layers))return false;return assemble(t,p);}
    private RecipeBook(){}
}
