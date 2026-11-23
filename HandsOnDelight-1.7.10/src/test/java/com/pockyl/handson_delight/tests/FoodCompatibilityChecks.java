package com.pockyl.handson_delight.tests;

import com.pockyl.handson_delight.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.init.*;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import java.util.*;
import vectorwing.farmersdelight.legacy.*;

public final class FoodCompatibilityChecks {
    private static void check(boolean value,String text){if(!value)throw new AssertionError(text);}
    public static void run(World w,EntityPlayer p){
        for(String kind:new String[]{"flint","iron","golden","diamond"}) {
            Item blade=kind.equals("flint")?Items.flint:kind.equals("iron")?Items.iron_ingot:kind.equals("golden")?Items.gold_ingot:Items.diamond;
            ContainerWorkbench table=new ContainerWorkbench(p.inventory,w,0,80,0);table.craftMatrix.setInventorySlotContents(0,new ItemStack(blade));table.craftMatrix.setInventorySlotContents(1,new ItemStack(blade));table.craftMatrix.setInventorySlotContents(3,new ItemStack(Items.stick));
            Slot slot=(Slot)table.inventorySlots.get(0);check(slot.getStack()!=null&&slot.getStack().getItem()==Content.ITEMS.get(kind+"_knife"),"Conflicting knife result: "+kind);
            ItemStack made=slot.decrStackSize(1);slot.onPickupFromSlot(p,made);for(int i=0;i<9;i++)check(Stacks.empty(table.craftMatrix.getStackInSlot(i)),"Knife consumed wrong ingredients");
        }
        EntitySheep sheep=new EntitySheep(w);LivingDropsEvent drops=event(sheep);MinecraftForge.EVENT_BUS.post(drops);check(drops.drops.size()==1,"Adult sheep has no mutton or duplicate mutton");check(drops.drops.get(0).getEntityItem().getItem()==FoodCompatibility.rawMutton,"Wrong raw sheep meat");
        sheep.setFire(10);drops=event(sheep);MinecraftForge.EVENT_BUS.post(drops);check(drops.drops.size()==1&&drops.drops.get(0).getEntityItem().getItem()==FoodCompatibility.cookedMutton,"Burning sheep has no cooked mutton");
        sheep.extinguish();drops=event(sheep);drops.drops.add(new EntityItem(w,0,80,0,new ItemStack(FoodCompatibility.rawMutton)));MinecraftForge.EVENT_BUS.post(drops);check(drops.drops.size()==1,"Existing mutton provider duplicated");
        sheep.setGrowingAge(-100);drops=event(sheep);MinecraftForge.EVENT_BUS.post(drops);check(drops.drops.isEmpty(),"Baby sheep produced meat");
        sheep.setGrowingAge(0);drops=event(sheep);drops.setCanceled(true);MinecraftForge.EVENT_BUS.post(drops);check(drops.drops.isEmpty(),"Canceled drops produced mutton");
        check(FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(FoodCompatibility.rawMutton)).getItem()==FoodCompatibility.cookedMutton,"Mutton cannot cook");
        if(GameRegistry.findItem("croptopia","knife")==null)return;
        ContainerWorkbench table=new ContainerWorkbench(p.inventory,w,0,80,0);table.craftMatrix.setInventorySlotContents(1,new ItemStack(Items.iron_ingot));table.craftMatrix.setInventorySlotContents(3,new ItemStack(Items.stick));table.craftMatrix.setInventorySlotContents(4,new ItemStack(Items.iron_ingot));check(table.craftResult.getStackInSlot(0)!=null&&table.craftResult.getStackInSlot(0).getItem()==GameRegistry.findItem("croptopia","knife"),"Croptopia knife conflicts");
        check(FurnaceRecipes.smelting().getSmeltingResult(Stacks.resolve("croptopia:sea_lettuce")).getItem()==FoodCompatibility.driedSeaweed,"Seaweed not obtainable by drying sea lettuce");
        String[] restored={"croptopia:sushi","croptopia:tuna_roll","croptopia:beetroot_salad","croptopia:borscht","croptopia:candied_kumquats","croptopia:mead","croptopia:shaped_kiwi_sorbet","croptopia:shaped_nether_wart_stew","croptopia:shaped_treacle_tart","culturaldelights:calamari_roll","culturaldelights:chicken_roll","culturaldelights:egg_roll","culturaldelights:midori_roll","culturaldelights:mutton_sandwich","culturaldelights:rice_ball","culturaldelights:tropical_roll"};
        w.setBlock(40,79,0,Blocks.stone);w.setBlock(40,80,0,Content.cuttingBoard);CuttingBoardTile board=(CuttingBoardTile)w.getTileEntity(40,80,0);
        for(String id:restored){if(id.startsWith("culturaldelights:")&&GameRegistry.findItem("culturaldelights","calamari_roll")==null)continue;SourceCrafting found=null;for(SourceCrafting r:SourceCrafting.ALL)if(r.id.equals(id))found=r;check(found!=null,"Recipe still unavailable: "+id);if(!RecipeRules.moved(found))continue;RecipeBook.Entry entry=RecipeBook.find(board,"assemble/"+id);check(entry!=null,"Missing board book entry: "+id);List<ItemStack> layers=new ArrayList<ItemStack>();int base=RecipeRules.base(entry.inputs);if(base>=0)layers.add(entry.inputs.get(base).get(0).copy());for(int i=0;i<entry.inputs.size();i++)if(i!=base)layers.add(entry.inputs.get(i).get(0).copy());board.setInventorySlotContents(0,layers.remove(0));StationHooks.state(board).layers.clear();StationHooks.state(board).layers.addAll(layers);check(RecipeBook.assemble(board,p),"Actual board assembly failed: "+id);check(Stacks.same(board.getStackInSlot(0),entry.output)&&board.getStackInSlot(0).stackSize==entry.output.stackSize&&StationHooks.state(board).layers.isEmpty(),"Board did not replace ingredients with the exact recipe output: "+id);}
    }
    private static LivingDropsEvent event(EntitySheep sheep){return new LivingDropsEvent(sheep,DamageSource.generic,new ArrayList<EntityItem>(),0,false,0);}
}
