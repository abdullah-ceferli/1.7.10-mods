package com.jkvin114.displaydelight;
import cpw.mods.fml.common.registry.VillagerRegistry;import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.*;import net.minecraft.init.Items;import net.minecraft.item.ItemStack;import java.util.*;
/** Native farmer replacement for the modern wandering-trader offers. */
public final class DisplayTrades implements VillagerRegistry.IVillageTradeHandler {
    public void manipulateTradesForVillager(EntityVillager villager,MerchantRecipeList recipes,Random random){
        List<DisplayBlock> absent=new ArrayList<DisplayBlock>(),all=new ArrayList<DisplayBlock>();
        for(DisplayBlock b:Catalog.blocks.values())if(!b.definition.empty()&&!b.definition.food.startsWith("nethersdelight:")){all.add(b);if(b.food==null)absent.add(b);}
        List<DisplayBlock> choices=absent.isEmpty()?all:absent;if(choices.isEmpty())return;
        for(int i=0;i<3;i++){DisplayBlock b=choices.get(random.nextInt(choices.size()));recipes.add(new MerchantRecipe(new ItemStack(Items.emerald,b.definition.kind.equals("drink")||b.definition.small()?1:2),b.displayStack(b.definition.max,false)));}
    }
}
