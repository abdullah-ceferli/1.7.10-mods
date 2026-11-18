package com.pockyl.handson_delight.compat.nei;

import codechicken.nei.api.*;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.Content;

public final class NEIHandsOnDelightConfig implements IConfigureNEI {
    public static void preInit(){
        NBTTagCompound t=new NBTTagCompound();t.setString("handler",AssemblyHandler.class.getName());t.setString("modName","Hands-On Delight");t.setString("modId","handson_delight");t.setBoolean("modRequired",true);t.setString("itemName","farmersdelight:cutting_board");t.setInteger("handlerWidth",154);t.setInteger("handlerHeight",54);t.setInteger("maxRecipesPerPage",Integer.MAX_VALUE);t.setBoolean("multipleWidgetsAllowed",true);
        FMLInterModComms.sendMessage("NotEnoughItems","registerHandlerInfo",t);
        NBTTagCompound catalyst=new NBTTagCompound();catalyst.setString("handlerID",AssemblyHandler.UID);catalyst.setString("itemName","farmersdelight:cutting_board");FMLInterModComms.sendMessage("NotEnoughItems","registerCatalystInfo",catalyst);
    }
    public void loadConfig(){API.registerRecipeHandler(new AssemblyHandler());API.registerUsageHandler(new AssemblyHandler());}
    public String getName(){return "Hands-On Delight";}public String getVersion(){return com.pockyl.handson_delight.HandsOnDelight.VERSION;}
}
