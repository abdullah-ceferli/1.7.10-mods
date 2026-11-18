package com.jkvin114.displaydelight;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

/** Native port of Display Delight by jkvin114. Port maintenance: Zahin. */
@Mod(modid=DisplayDelight.ID,name="Display Delight",version=DisplayDelight.VERSION,acceptedMinecraftVersions="[1.7.10]")
public final class DisplayDelight {
    public static final String ID="displaydelight",VERSION="1.7.0-backport.1";
    @SidedProxy(clientSide="com.jkvin114.displaydelight.client.ClientProxy",serverSide="com.jkvin114.displaydelight.CommonProxy")
    public static CommonProxy proxy;
    public static final CreativeTabs TAB=new CreativeTabs(ID){public Item getTabIconItem(){return Item.getItemFromBlock(Catalog.blocks.get("food_plate"));}};
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent e){
        Settings.load(e.getSuggestedConfigurationFile());Catalog.register();
        GameRegistry.registerTileEntity(DisplayTile.class,ID+":display");MinecraftForge.EVENT_BUS.register(new Interactions());
        proxy.initialize();
    }
    @Mod.EventHandler public void init(FMLInitializationEvent e){
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Catalog.blocks.get("food_plate"),2),"S S"," S ",'S',new ItemStack(Blocks.wooden_slab,1,32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Catalog.blocks.get("small_food_plate"),2),"SS",'S',new ItemStack(Blocks.wooden_slab,1,32767)));
        if(Settings.farmerTrades)VillagerRegistry.instance().registerVillageTradeHandler(0,new DisplayTrades());
    }
    @Mod.EventHandler public void postInit(FMLPostInitializationEvent e){Catalog.bindFoods();}
}
