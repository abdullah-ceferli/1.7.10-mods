package dev.industrialtechnology.lighting;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraftforge.oredict.OreDictionary;

public final class ColoredLampRegistry {
    public static BlockColoredLamp always,redstone,powered;
    public static void register(){
        always=new BlockColoredLamp(BlockColoredLamp.ALWAYS);redstone=new BlockColoredLamp(BlockColoredLamp.OFF);powered=new BlockColoredLamp(BlockColoredLamp.ON);
        GameRegistry.registerBlock(always,ItemBlockColoredLamp.class,"colored_lamp");
        GameRegistry.registerBlock(redstone,ItemBlockColoredLamp.class,"colored_redstone_lamp");
        GameRegistry.registerBlock(powered,(Class<? extends ItemBlock>)null,"colored_redstone_lamp_on");
    }
    public static void recipes(){for(int color=0;color<16;color++){
        ItemStack dye=new ItemStack(Items.dye,1,15-color);
        GameRegistry.addRecipe(new ItemStack(always,4,color),"GSG","SDS","GSG",'G',Blocks.glass,'S',Items.glowstone_dust,'D',dye);
        GameRegistry.addShapelessRecipe(new ItemStack(redstone,1,color),new ItemStack(always,1,color),Items.redstone);
        GameRegistry.addShapelessRecipe(new ItemStack(always,1,color),new ItemStack(always,1,OreDictionary.WILDCARD_VALUE),dye);
        GameRegistry.addShapelessRecipe(new ItemStack(redstone,1,color),new ItemStack(redstone,1,OreDictionary.WILDCARD_VALUE),dye);
    }}
    private ColoredLampRegistry(){}
}
