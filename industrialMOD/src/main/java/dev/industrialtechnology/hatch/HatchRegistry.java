package dev.industrialtechnology.hatch;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
public final class HatchRegistry {
    public static BlockSlidingHatch block;
    public static void register(){block=new BlockSlidingHatch();GameRegistry.registerBlock(block,ItemSlidingHatch.class,"sliding_hatch");GameRegistry.registerTileEntity(TileSlidingHatch.class,"industrialtech:sliding_hatch");}
    public static void recipes(){GameRegistry.addRecipe(new ItemStack(block,2),"III","PRP",'I',Items.iron_ingot,'P',Blocks.piston,'R',Items.redstone);for(int i=0;i<3;i++)GameRegistry.addShapelessRecipe(new ItemStack(block,1,(i+1)%3),new ItemStack(block,1,i));}
    private HatchRegistry(){}
}
