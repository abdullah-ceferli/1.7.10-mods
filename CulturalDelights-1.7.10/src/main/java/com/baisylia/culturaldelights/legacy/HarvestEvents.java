package com.baisylia.culturaldelights.legacy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.VillagerRegistry;
import java.util.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.*;
import net.minecraft.item.*;
import net.minecraft.village.*;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import com.google.gson.*;
public final class HarvestEvents {
    @SubscribeEvent public void drops(LivingDropsEvent e){
        if(e.entityLiving.worldObj.isRemote)return;
        if(e.entityLiving instanceof EntitySquid){ItemStack s=Port.CONTENT.stack(Catalog.MOD.equals("croptopia")?"calamari":"squid");if(Catalog.MOD.equals("culturaldelights")){for(Iterator<EntityItem> it=e.drops.iterator();it.hasNext();){ItemStack drop=it.next().getEntityItem();if(drop.getItem()==net.minecraft.init.Items.dye&&drop.getItemDamage()==0)it.remove();}s.stackSize=1+Math.max(0,e.lootingLevel);}if(s!=null)e.drops.add(new EntityItem(e.entityLiving.worldObj,e.entityLiving.posX,e.entityLiving.posY,e.entityLiving.posZ,s));}
    }
    public static void fishing(){
        // Preserve the release table's 35% replacement route at the native fishing-hook boundary.
    }
    public static ItemStack fish(ItemStack original,Random random){if(random.nextFloat()>.35F)return original;List<ItemStack> drops=new ArrayList<ItemStack>();JsonObject all=Port.CONTENT.loot;if(all.has("gameplay/fishing/fish"))Loot.table(all.getAsJsonObject("gameplay/fishing/fish"),0,0,null,random,drops);return drops.isEmpty()?original:drops.get(0);}
    public static ItemStack getRandomFishable(Random r,float luck,int lure,int extra){return fish(net.minecraftforge.common.FishingHooks.getRandomFishable(r,luck,lure,extra),r);}
    public static ItemStack getRandomFishable(Random r,float luck){return fish(net.minecraftforge.common.FishingHooks.getRandomFishable(r,luck),r);}
    public static final class Trades implements VillagerRegistry.IVillageTradeHandler {
        public void manipulateTradesForVillager(EntityVillager v,MerchantRecipeList trades,Random r){if(!Catalog.MOD.equals("culturaldelights"))return;String[] ids={"cucumber","eggplant","white_eggplant","corn_cob","avocado"};int[] amounts={22,15,20,15,20};int index=r.nextInt(ids.length);ItemStack buy=Port.CONTENT.stack(ids[index]);if(buy==null)return;buy.stackSize=amounts[index];MerchantRecipe recipe=new MerchantRecipe(buy,new ItemStack(net.minecraft.init.Items.emerald));trades.add(recipe);}
    }
}
