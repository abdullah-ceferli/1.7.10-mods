package com.pockyl.handson_delight;

import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import vectorwing.farmersdelight.legacy.Stacks;

/** Native adaptation of StationRecipes.fill, parts and lay; plans complete sets before changing real inventories. */
public final class IngredientPlan {
    public final ItemStack[] inventory;public List<ItemStack> kinds=new ArrayList<ItemStack>();public int[] parts;public int sets;
    public IngredientPlan(EntityPlayer p){inventory=new ItemStack[36];for(int i=0;i<36;i++)inventory[i]=Stacks.empty(p.inventory.getStackInSlot(i))?null:p.inventory.getStackInSlot(i).copy();}
    public static void add(List<ItemStack> into,ItemStack stack){if(Stacks.empty(stack))return;for(ItemStack held:into)if(Stacks.same(held,stack)){held.stackSize+=stack.stackSize;return;}into.add(stack.copy());}
    public boolean fill(List<List<ItemStack>> ingredients,List<ItemStack> existing,int slots,int want,int perSlot){
        if(ingredients.isEmpty()||ingredients.size()>slots)return false;
        for(ItemStack old:existing){boolean found=false;for(List<ItemStack> accepts:ingredients)if(Stacks.matches(accepts,old))found=true;if(!found)return false;add(kinds,old);}
        for(int set=0;set<want;set++){
            int[] from=new int[ingredients.size()];Arrays.fill(from,-1);List<ItemStack> more=new ArrayList<ItemStack>();for(ItemStack k:kinds)more.add(k.copy());boolean whole=true;
            for(int i=0;i<from.length;i++){
                from[i]=source(ingredients.get(i),from,i);if(from[i]<0){whole=false;break;}add(more,Stacks.copy(inventory[from[i]],1));
            }
            int[] placement=whole?parts(ingredients,more):null;
            if(placement==null||!fits(more,placement,perSlot))break;
            for(int at:from){inventory[at].stackSize--;if(inventory[at].stackSize<=0)inventory[at]=null;}kinds=more;parts=placement;sets++;
        }
        return sets>0;
    }
    private int source(List<ItemStack> accepts,int[] from,int at){
        int any=-1;for(int i=0;i<36;i++)if(!Stacks.empty(inventory[i])&&Stacks.matches(accepts,inventory[i])){
            int need=1;for(int j=0;j<at;j++)if(from[j]==i)need++;if(inventory[i].stackSize<need)continue;
            for(ItemStack old:kinds)if(Stacks.same(old,inventory[i]))return i;if(any<0)any=i;
        }return any;
    }
    private static boolean fits(List<ItemStack> kinds,int[] parts,int limit){for(int i=0;i<parts.length;i++)if(kinds.get(i).stackSize>parts[i]*Math.min(limit,kinds.get(i).getMaxStackSize()))return false;return true;}
    public static int[] parts(List<List<ItemStack>> ingredients,List<ItemStack> kinds){
        if(kinds.isEmpty()||kinds.size()>ingredients.size())return null;boolean[][] accepts=new boolean[ingredients.size()][kinds.size()];
        for(int i=0;i<accepts.length;i++)for(int j=0;j<kinds.size();j++)accepts[i][j]=Stacks.matches(ingredients.get(i),kinds.get(j));int[][] best={null};assign(0,accepts,kinds,new int[kinds.size()],best);return best[0];
    }
    private static void assign(int at,boolean[][] accepts,List<ItemStack> kinds,int[] parts,int[][] best){
        int unused=0;for(int n:parts)if(n==0)unused++;if(unused>accepts.length-at)return;
        if(at==accepts.length){if(best[0]==null||servings(kinds,parts)>servings(kinds,best[0]))best[0]=parts.clone();return;}
        for(int i=0;i<parts.length;i++)if(accepts[at][i]&&parts[i]<kinds.get(i).stackSize){parts[i]++;assign(at+1,accepts,kinds,parts,best);parts[i]--;}
    }
    private static int servings(List<ItemStack> kinds,int[] parts){int n=Integer.MAX_VALUE;for(int i=0;i<parts.length;i++)if(parts[i]>0)n=Math.min(n,kinds.get(i).stackSize/parts[i]);return n;}
    public ItemStack[] lay(ItemStack[] old,int limit){
        int[] at=new int[old.length],placed=new int[kinds.size()];Arrays.fill(at,-1);
        for(int slot=0;slot<old.length;slot++)for(int kind=0;kind<kinds.size();kind++)if(placed[kind]<parts[kind]&&Stacks.same(old[slot],kinds.get(kind))){at[slot]=kind;placed[kind]++;break;}
        int free=0;for(int kind=0;kind<kinds.size();kind++)while(placed[kind]<parts[kind]){while(at[free]>=0)free++;at[free]=kind;placed[kind]++;}
        int[] left=new int[kinds.size()],remain=parts.clone();for(int i=0;i<left.length;i++)left[i]=kinds.get(i).stackSize;
        ItemStack[] result=new ItemStack[old.length];for(int i=0;i<result.length;i++)if(at[i]>=0){int k=at[i],share=(left[k]+remain[k]-1)/remain[k];result[i]=Stacks.copy(kinds.get(k),share);left[k]-=share;remain[k]--;}return result;
    }
    public void commit(EntityPlayer p){for(int i=0;i<36;i++)p.inventory.setInventorySlotContents(i,inventory[i]);}
}
