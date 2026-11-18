package com.pockyl.handson_delight;

import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import vectorwing.farmersdelight.legacy.*;

public final class Actions {
    public static final int INSERT=1,TAKE=2,CUT=3,TURN=4,STIR=5,FILL=6,CLOSE=7,SERVE=8,ASSEMBLE=9,NEIGHBOR=10,CARVE=11;
    public static boolean insert(InventoryTile t,EntityPlayer p,int inventorySlot,int target,boolean bulk){
        if(inventorySlot< -1||inventorySlot>=36)return false;ItemStack held=inventorySlot<0?p.inventory.getItemStack():p.inventory.getStackInSlot(inventorySlot);if(Stacks.empty(held))return false;
        StationState s=StationHooks.state(t);int amount=bulk?Math.min(t instanceof CookingPotTile?64:16,held.stackSize):1;
        if(t instanceof CuttingBoardTile){
            if(Stacks.empty(t.getStackInSlot(0))){
                if(!s.layers.isEmpty())return false;boolean cuttable=false;for(CuttingRecipes.Recipe r:CuttingRecipes.ALL)if(r.matchesInput(held)){cuttable=true;break;}
                if(!cuttable){boolean ingredient=false;for(RecipeBook.Entry e:RecipeBook.list(t))if(e.assembly!=null)for(List<ItemStack> choice:e.inputs)if(Stacks.matches(choice,held))ingredient=true;if(!ingredient)return false;amount=Math.min(amount,16);}t.setInventorySlotContents(0,Stacks.copy(held,amount));s.cuts=0;
            }else{
                if(s.layers.size()>=8)return false;
                List<ItemStack> layers=RecipeBook.layers((CuttingBoardTile)t);ItemStack added=Stacks.copy(held,amount);layers.add(added);boolean matches=false;
                for(RecipeBook.Entry e:RecipeBook.list(t))if(RecipeBook.prefix(e,layers)){matches=true;break;}
                if(!matches){layers.remove(layers.size()-1);layers.add(0,added);for(RecipeBook.Entry e:RecipeBook.list(t))if(RecipeBook.prefix(e,layers)){matches=true;break;}if(matches){ItemStack old=t.getStackInSlot(0);t.setInventorySlotContents(0,added);s.layers.add(0,old);}}
                else s.layers.add(added);
                if(!matches)return false;

            }
        }else{
            int slots=t instanceof CookingPotTile?6:t.getSizeInventory();if(target<0||target>=slots)return false;
            ItemStack old=t.getStackInSlot(target);if(!Stacks.empty(old)&&!Stacks.same(old,held))return false;
            if(t instanceof GrillingTile){if(((GrillingTile)t).blocked()||GrillingRecipes.find(held)==null||s.frying[target].ready())return false;}
            int limit=t instanceof CookingPotTile?64:t instanceof StoveTile?1:16;amount=Math.min(amount,Math.min(limit,held.getMaxStackSize())-Stacks.size(old));if(amount<=0)return false;
            t.setInventorySlotContents(target,Stacks.copy(held,Stacks.size(old)+amount));
            if(t instanceof CookingPotTile){s.manual=true;s.attended=t.getWorldObj().getTotalWorldTime();s.cook=p.getUniqueID();}
            else {s.frying[target].byHand(p.getUniqueID());s.frying[target].batch(Stacks.size(t.getStackInSlot(target)));s.observed[target]=Stacks.copy(held,1);}
        }
        ItemStack placed=Stacks.copy(held,amount);
        if(!p.capabilities.isCreativeMode){held.stackSize-=amount;if(held.stackSize<=0){if(inventorySlot<0)p.inventory.setItemStack(null);else p.inventory.setInventorySlotContents(inventorySlot,null);}}
        StationHooks.visual(t,INSERT,target,placed,null,null);
        if(t instanceof StoveTile&&bulk)for(int i=0;i<6;i++)if(Stacks.empty(t.getStackInSlot(i)))insert(t,p,inventorySlot,i,false);
        if(t instanceof CuttingBoardTile)RecipeBook.finishIfTerminal((CuttingBoardTile)t,p);t.changed();return true;
    }
    public static void give(EntityPlayer p,InventoryTile t,ItemStack a){
        if(Stacks.empty(a))return;if(p!=null)p.inventory.addItemStackToInventory(a);
        if(!Stacks.empty(a))t.getWorldObj().spawnEntityInWorld(new EntityItem(t.getWorldObj(),t.xCoord+0.5,t.yCoord+1,t.zCoord+0.5,a));
    }
    public static boolean take(InventoryTile t,EntityPlayer p,int slot){
        if(slot<0||slot>=t.getSizeInventory()||t instanceof CookingPotTile&&slot==6)return false;
        StationState s=StationHooks.state(t);
        if(t instanceof CuttingBoardTile&&!s.layers.isEmpty()){ItemStack top=s.layers.remove(s.layers.size()-1);StationHooks.visual(t,TAKE,s.layers.size()+1,top,null,null);give(p,t,top);t.changed();return true;}
        ItemStack a=t.decrStackSize(slot,Stacks.size(t.getStackInSlot(slot)));if(Stacks.empty(a))return false;StationHooks.visual(t,TAKE,slot,a,null,null);s.cuts=0;give(p,t,a);
        if(t instanceof GrillingTile){s.frying[slot].farmersDelightsWay();s.observed[slot]=null;((GrillingTile)t).progress[slot]=0;}t.changed();return true;
    }
    public static boolean cut(CuttingBoardTile t,EntityPlayer p,int toolSlot){
        if(toolSlot< -1||toolSlot>=36||!StationHooks.state(t).layers.isEmpty())return false;ItemStack tool=toolSlot<0?p.inventory.getItemStack():p.inventory.getStackInSlot(toolSlot),input=t.getStackInSlot(0);
        CuttingRecipes.Recipe r=CuttingRecipes.find(input,tool);if(r==null)return false;
        StationState s=StationHooks.state(t);int strokes=CutPlan.marks(input,r);StationHooks.visual(t,CUT,s.cuts,input,tool,r.outputs.isEmpty()?null:r.outputs.get(0));
        if(++s.cuts<strokes){t.getWorldObj().playSoundEffect(t.xCoord+0.5,t.yCoord+0.1,t.zCoord+0.5,"farmersdelight:block.cutting_board.knife",0.7F,1F);t.changed();return true;}
        s.cuts=0;int n=Math.min(16,Stacks.size(input));for(int i=0;i<n;i++){if(Stacks.empty(tool)||!StationHooks.processFor(t,tool,p))break;}
        if(Stacks.empty(tool)){if(toolSlot<0)p.inventory.setItemStack(null);else p.inventory.setInventorySlotContents(toolSlot,null);}t.changed();return true;
    }
    /** All inventory choices are planned on copies before the first real slot changes. */
    public static boolean fill(InventoryTile t,EntityPlayer p,String id){
        return fill(t,p,id,false);
    }
    public static boolean fill(InventoryTile t,EntityPlayer p,String id,boolean bulk){
        RecipeBook.Entry e=RecipeBook.find(t,id);if(e==null)return false;
        if(t instanceof StoveTile){
            StoveTile stove=(StoveTile)t;if(stove.blocked())return false;IngredientPlan plan=new IngredientPlan(p);List<Integer> slots=new ArrayList<Integer>();List<ItemStack> food=new ArrayList<ItemStack>();
            for(int slot=0;slot<6;slot++)if(Stacks.empty(stove.getStackInSlot(slot))){
                int found=-1;for(int i=0;i<36;i++)if(!Stacks.empty(plan.inventory[i])&&Stacks.matches(e.inputs.get(0),plan.inventory[i])){found=i;break;}if(found<0)break;
                slots.add(slot);food.add(Stacks.copy(plan.inventory[found],1));plan.inventory[found].stackSize--;if(plan.inventory[found].stackSize<=0)plan.inventory[found]=null;if(!bulk)break;
            }
            if(slots.isEmpty())return false;plan.commit(p);StationState state=StationHooks.state(t);for(int i=0;i<slots.size();i++){int slot=slots.get(i);stove.setInventorySlotContents(slot,food.get(i));stove.progress[slot]=0;state.frying[slot].farmersDelightsWay();state.frying[slot].byHand(p.getUniqueID());state.frying[slot].batch(1);state.observed[slot]=food.get(i).copy();}stove.changed();return true;
        }
        if(t instanceof CookingPotTile||t instanceof SkilletTile){
            if(t instanceof GrillingTile&&(((GrillingTile)t).blocked()||StationHooks.state(t).frying[0].ready()))return false;
            int slots=t instanceof CookingPotTile?6:1,limit=slots==6?64:16;List<ItemStack> existing=new ArrayList<ItemStack>();ItemStack[] old=new ItemStack[slots];
            for(int i=0;i<slots;i++){old[i]=t.getStackInSlot(i);IngredientPlan.add(existing,old[i]);}
            IngredientPlan plan=new IngredientPlan(p);if(!plan.fill(e.inputs,existing,slots,bulk?16:1,limit))return false;
            ItemStack[] laid=plan.lay(old,limit);plan.commit(p);for(int i=0;i<slots;i++)t.setInventorySlotContents(i,laid[i]);StationState state=StationHooks.state(t);
            if(t instanceof CookingPotTile){state.manual=true;state.attended=t.getWorldObj().getTotalWorldTime();state.cook=p.getUniqueID();}
            else {state.frying[0].byHand(p.getUniqueID());state.frying[0].batch(Stacks.size(laid[0]));state.observed[0]=Stacks.copy(laid[0],1);}t.changed();return true;
        }
        if(t instanceof CuttingBoardTile&&e.assembly!=null)return fillBoard((CuttingBoardTile)t,p,e,bulk);
        if(t instanceof CookingPotTile){for(int i=0;i<6;i++)if(!Stacks.empty(t.getStackInSlot(i)))return false;}
        else {for(int i=0;i<t.getSizeInventory();i++)if(!Stacks.empty(t.getStackInSlot(i)))return false;}
        if(!StationHooks.state(t).layers.isEmpty())return false;
        ItemStack[] copy=new ItemStack[36];for(int i=0;i<36;i++)copy[i]=Stacks.empty(p.inventory.getStackInSlot(i))?null:p.inventory.getStackInSlot(i).copy();
        List<ItemStack> chosen=new ArrayList<ItemStack>();
        for(List<ItemStack> alternatives:e.inputs){
            int found=-1;for(int i=0;i<36;i++)if(!Stacks.empty(copy[i])&&Stacks.matches(alternatives,copy[i])){found=i;break;}
            if(found<0)return false;chosen.add(Stacks.copy(copy[found],1));if(!p.capabilities.isCreativeMode){copy[found].stackSize--;if(copy[found].stackSize<=0)copy[found]=null;}
        }
        if(t instanceof GrillingTile&&((GrillingTile)t).blocked())return false;
        if(t instanceof CookingPotTile){for(int i=0;i<chosen.size();i++)t.setInventorySlotContents(i,chosen.get(i));StationHooks.state(t).manual=true;}
        else if(t instanceof CuttingBoardTile){t.setInventorySlotContents(0,chosen.get(0));for(int i=1;i<chosen.size();i++)StationHooks.state(t).layers.add(chosen.get(i));}
        else {t.setInventorySlotContents(0,chosen.get(0));StationState s=StationHooks.state(t);s.frying[0].byHand(p.getUniqueID());s.observed[0]=Stacks.copy(chosen.get(0),1);}
        for(int i=0;i<36;i++)p.inventory.setInventorySlotContents(i,copy[i]);
        if(t instanceof CuttingBoardTile)RecipeBook.assemble((CuttingBoardTile)t,p);
        t.changed();return true;
    }
    private static boolean fillBoard(CuttingBoardTile t,EntityPlayer p,RecipeBook.Entry e,boolean bulk){
        StationState state=StationHooks.state(t);List<ItemStack> old=RecipeBook.layers(t);int[] used=RecipeBook.assigned(e,old);boolean reuse=used!=null&&!old.isEmpty();
        if(!reuse){
            if(!state.layers.isEmpty())return false;
            ItemStack there=t.getStackInSlot(0);boolean dish=false,cuttable=false;
            for(RecipeBook.Entry possible:RecipeBook.list(t))if(possible.assembly!=null&&Stacks.same(possible.output,there))dish=true;
            for(CuttingRecipes.Recipe r:CuttingRecipes.ALL)if(r.matchesInput(there))cuttable=true;
            if(cuttable&&!dish)return false;old=new ArrayList<ItemStack>();used=new int[0];
        }
        ItemStack[] kinds=new ItemStack[e.inputs.size()];int want=bulk?16:1;
        if(reuse){want=16;for(int i=0;i<used.length;i++){ItemStack item=i==0?t.getStackInSlot(0):state.layers.get(i-1);kinds[used[i]]=Stacks.copy(item,1);if(item.getMaxStackSize()>1)want=Math.min(want,item.stackSize);}}
        want=Math.min(want,Math.min(64,e.output.getMaxStackSize())/e.output.stackSize);
        IngredientPlan plan=new IngredientPlan(p);int sets=0;
        for(int n=0;n<want;n++){
            ItemStack[] next=new ItemStack[36];for(int i=0;i<36;i++)next[i]=Stacks.empty(plan.inventory[i])?null:plan.inventory[i].copy();ItemStack[] choices=kinds.clone();boolean whole=true;
            for(int ingredient=0;ingredient<e.inputs.size();ingredient++){
                boolean onBoard=false;for(int chosen:used)if(chosen==ingredient)onBoard=true;if(onBoard)continue;
                if(n>0&&choices[ingredient]!=null&&choices[ingredient].getMaxStackSize()==1)continue;
                int found=-1;for(int slot=0;slot<36;slot++)if(!Stacks.empty(next[slot])&&Stacks.matches(e.inputs.get(ingredient),next[slot])&&(choices[ingredient]==null||Stacks.same(choices[ingredient],next[slot]))){found=slot;break;}
                if(found<0){whole=false;break;}choices[ingredient]=Stacks.copy(next[found],1);next[found].stackSize--;if(next[found].stackSize<=0)next[found]=null;
            }
            if(!whole)break;System.arraycopy(next,0,plan.inventory,0,36);kinds=choices;sets++;
        }
        if(sets==0)return false;
        List<ItemStack> ordered=new ArrayList<ItemStack>();for(ItemStack kind:kinds)ordered.add(kind);net.minecraft.inventory.InventoryCrafting grid=RecipeBook.arrange(e,ordered);
        if(grid==null||!e.assembly.matches(grid,t.getWorldObj()))return false;ItemStack result=e.assembly.getCraftingResult(grid);if(Stacks.empty(result)||!Stacks.same(result,e.output)||result.stackSize*sets>Math.min(64,result.getMaxStackSize()))return false;
        result.stackSize*=sets;List<ItemStack> returns=new ArrayList<ItemStack>();
        for(int slot=0;slot<36;slot++){
            ItemStack was=p.inventory.getStackInSlot(slot);int spent=Stacks.size(was)-Stacks.size(plan.inventory[slot]);for(int i=0;i<spent;i++)returns.add(CookingRecipes.remainder(Stacks.copy(was,1)));
        }
        if(reuse)for(int i=0;i<used.length;i++){
            ItemStack was=i==0?t.getStackInSlot(0):state.layers.get(i-1);int consumed=was.getMaxStackSize()==1?1:sets;
            if(was.stackSize>consumed)returns.add(Stacks.copy(was,was.stackSize-consumed));for(int n=0;n<consumed;n++)returns.add(CookingRecipes.remainder(Stacks.copy(was,1)));
        }else if(!Stacks.empty(t.getStackInSlot(0)))returns.add(t.getStackInSlot(0).copy());
        plan.commit(p);StationHooks.visual(t,ASSEMBLE,0,old.isEmpty()?kinds[0]:old.get(0),null,result);state.visualLayers.clear();int visualBase=RecipeRules.base(e.inputs);if(visualBase>=0)state.visualLayers.add(kinds[visualBase].copy());for(int k=0;k<kinds.length;k++)if(k!=visualBase)state.visualLayers.add(kinds[k].copy());state.layers.clear();state.cuts=0;t.setInventorySlotContents(0,result);
        for(ItemStack remainder:returns)give(p,t,remainder);
        cpw.mods.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(p,result,grid);result.onCrafting(t.getWorldObj(),p,result.stackSize);t.changed();return true;
    }
    public static boolean carve(CuttingBoardTile t,EntityPlayer p,int slot){if(slot< -1||slot>=36||!Stacks.empty(t.getStackInSlot(0))||!StationHooks.state(t).layers.isEmpty())return false;ItemStack held=slot<0?p.inventory.getItemStack():p.inventory.getStackInSlot(slot);if(Stacks.empty(held)||!(held.getItem() instanceof net.minecraft.item.ItemTool||held.getItem() instanceof net.minecraft.item.ItemSword||held.getItem() instanceof net.minecraft.item.ItemShears))return false;
        t.setInventorySlotContents(0,Stacks.copy(held,1));t.carved=true;StationHooks.visual(t,CARVE,0,held,null,null);if(!p.capabilities.isCreativeMode){held.stackSize--;if(held.stackSize<=0){if(slot<0)p.inventory.setItemStack(null);else p.inventory.setInventorySlotContents(slot,null);}}t.changed();return true;
    }
    public static boolean serve(CookingPotTile t,EntityPlayer p,int slot,boolean all){boolean any=false;for(int n=0;n<(all?64:1);n++){ItemStack meal=t.getStackInSlot(6);if(Stacks.empty(meal))break;if(all){boolean room=false;for(int i=0;i<36;i++){ItemStack old=p.inventory.getStackInSlot(i);if(Stacks.empty(old)||Stacks.same(old,meal)&&old.stackSize<Math.min(64,old.getMaxStackSize()))room=true;}if(!room)break;}if(!serve(t,p,slot))break;any=true;}return any;}
    public static boolean serve(CookingPotTile t,EntityPlayer p,int slot){
        if(slot< -1||slot>=36)return false;ItemStack held=slot<0?p.inventory.getItemStack():p.inventory.getStackInSlot(slot);ItemStack meal=t.getStackInSlot(6);if(Stacks.empty(meal))return false;
        ItemStack result=t.serve(held,p.capabilities.isCreativeMode);if(Stacks.empty(result))return false;StationHooks.visual(t,SERVE,0,result,held,null);
        if(Stacks.empty(held)){if(slot<0)p.inventory.setItemStack(null);else p.inventory.setInventorySlotContents(slot,null);}give(p,t,result);t.payExperience();return true;
    }
    private Actions(){}
}
