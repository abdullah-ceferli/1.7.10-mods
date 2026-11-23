package com.pockyl.handson_delight;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.oredict.OreDictionary;
import vectorwing.farmersdelight.legacy.*;

/** Native acquisition and explicit ingredient equivalents for the installed food ports. */
public final class FoodCompatibility {
    public static Item rawMutton,cookedMutton,driedSeaweed;
    public static void register() {
        rawMutton=food("raw_mutton",2,.3F,"farmersdelight:mutton_chops");
        cookedMutton=food("cooked_mutton",6,.8F,"farmersdelight:cooked_mutton_chops");
        driedSeaweed=food("dried_seaweed",1,.3F,"minecraft:reeds");
        OreDictionary.registerOre("foodMuttonraw",rawMutton);
        OreDictionary.registerOre("foodMuttoncooked",cookedMutton);
        GameRegistry.addSmelting(rawMutton,new ItemStack(cookedMutton),.35F);
    }
    private static Item food(String id,int hunger,float saturation,String texture) {
        Item item=new ItemFood(hunger,saturation,false).setUnlocalizedName("handson_delight."+id).setTextureName(texture).setCreativeTab(Content.TAB);
        GameRegistry.registerItem(item,id);return item;
    }
    private static ItemStack item(String mod,String id){Item i=GameRegistry.findItem(mod,id);return i==null?null:new ItemStack(i);}
    /** Called only after the original resolver. Never overrides an existing modern provider. */
    public static ItemStack resolved(ItemStack original,String id) {
        if(original!=null||Loader.isModLoaded("etfuturum"))return original;
        if(id.equals("minecraft:mutton"))return rawMutton==null?null:new ItemStack(rawMutton);
        if(id.equals("minecraft:cooked_mutton"))return cookedMutton==null?null:new ItemStack(cookedMutton);
        if(id.equals("minecraft:seagrass"))return item("croptopia","sea_lettuce");
        if(id.equals("minecraft:dried_kelp"))return item("croptopia","sea_lettuce")==null?null:new ItemStack(driedSeaweed);
        if(id.equals("minecraft:beetroot"))return item("croptopia","radish");
        if(id.equals("minecraft:sweet_berries"))return item("croptopia","raspberry");
        if(id.equals("minecraft:honey_bottle"))return item("croptopia","molasses");
        if(id.equals("minecraft:crimson_fungus"))return new ItemStack(net.minecraft.init.Blocks.red_mushroom);
        if(id.equals("minecraft:warped_fungus"))return new ItemStack(net.minecraft.init.Blocks.brown_mushroom);
        // The original resolver hides these registered foods when their modern ingredients are absent.
        if(id.startsWith("farmersdelight:")&&id.matches(".*(sweet_berry|honey_|nether_salad).*"))return item("farmersdelight",id.substring(id.indexOf(':')+1));
        return null;
    }
    public static void recipes() {
        ItemStack sea=item("croptopia","sea_lettuce");if(sea!=null)GameRegistry.addSmelting(sea,new ItemStack(driedSeaweed),.1F);
        for(String name:new String[]{"flint","iron","golden","diamond"}) {
            Item knife=Content.ITEMS.get(name+"_knife");if(knife==null)continue;
            removeCrafting(knife);
            Item blade=name.equals("flint")?Items.flint:name.equals("iron")?Items.iron_ingot:name.equals("golden")?Items.gold_ingot:Items.diamond;
            GameRegistry.addRecipe(new ItemStack(knife),"BB","S ",'B',blade,'S',Items.stick);
        }
        ItemStack cropKnife=item("croptopia","knife");if(cropKnife!=null){removeCrafting(cropKnife.getItem());GameRegistry.addRecipe(cropKnife," I","SI",'I',Items.iron_ingot,'S',Items.stick);}
    }
    @SuppressWarnings("unchecked") private static void removeCrafting(Item item) {
        List<IRecipe> all=CraftingManager.getInstance().getRecipeList();
        all.removeIf(r->r.getRecipeOutput()!=null&&r.getRecipeOutput().getItem()==item);
        SourceCrafting.ALL.removeIf(r->r.output.getItem()==item);
    }
    @SubscribeEvent(priority=EventPriority.LOWEST) public void drops(LivingDropsEvent event) {
        if(event.isCanceled()||event.entityLiving.worldObj.isRemote||!(event.entityLiving instanceof EntitySheep)||((EntitySheep)event.entityLiving).isChild())return;
        // Let another installed mutton provider own its drops; do not add a second serving.
        for(EntityItem drop:event.drops){String name=Item.itemRegistry.getNameForObject(drop.getEntityItem().getItem());if(name!=null&&name.toLowerCase(Locale.ROOT).contains("mutton"))return;}
        ItemStack meat=Stacks.resolve(event.entityLiving.isBurning()?"minecraft:cooked_mutton":"minecraft:mutton");if(meat==null)return;
        int loot=Math.max(0,Math.min(100,event.lootingLevel));meat.stackSize=1+event.entityLiving.getRNG().nextInt(2)+(loot==0?0:event.entityLiving.getRNG().nextInt(loot+1));
        event.drops.add(new EntityItem(event.entityLiving.worldObj,event.entityLiving.posX,event.entityLiving.posY,event.entityLiving.posZ,meat));
    }
    private FoodCompatibility(){}
    public static FoodCompatibility events(){return new FoodCompatibility();}
}
