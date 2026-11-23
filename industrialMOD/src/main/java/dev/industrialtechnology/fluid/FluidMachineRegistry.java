package dev.industrialtechnology.fluid;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import dev.industrialtechnology.core.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** HBM linking stays in the branch where that optional mod is present. */
public final class FluidMachineRegistry {
    public static Block sucker,puller;
    public static boolean hasHbm;
    public static void register(){
        hasHbm=Loader.isModLoaded("hbm");
        sucker=new BlockFluidMachine(true);puller=new BlockFluidMachine(false);
        GameRegistry.registerBlock(sucker,"fluid_sucker");GameRegistry.registerBlock(puller,"fluid_puller");
        if(hasHbm)HbmFluidBridge.register();
        else{GameRegistry.registerTileEntity(TileFluidMachine.Sucker.class,"industrialtech:fluid_sucker");GameRegistry.registerTileEntity(TileFluidMachine.Puller.class,"industrialtech:fluid_puller");}
    }
    public static TileFluidMachine create(boolean sucker){return hasHbm?HbmFluidBridge.create(sucker):sucker?new TileFluidMachine.Sucker():new TileFluidMachine.Puller();}
    public static boolean identifier(ItemStack stack){return hasHbm&&HbmFluidBridge.identifier(stack);}
    public static int identified(World world,int x,int y,int z,ItemStack stack){return hasHbm?HbmFluidBridge.identified(world,x,y,z,stack):-1;}
    public static void recipes(){
        GameRegistry.addRecipe(new ItemStack(sucker),"IGI","CBC","IHI",'I',Items.iron_ingot,'G',ModRegistry.gear,'C',ModRegistry.circuit,'B',Items.bucket,'H',Blocks.hopper);
        GameRegistry.addRecipe(new ItemStack(puller),"IGI","CBC","IDI",'I',Items.iron_ingot,'G',ModRegistry.gear,'C',ModRegistry.circuit,'B',Items.bucket,'D',Blocks.dispenser);
    }
    private FluidMachineRegistry(){}
}
