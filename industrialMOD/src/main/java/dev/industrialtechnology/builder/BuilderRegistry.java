package dev.industrialtechnology.builder;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.*;
public final class BuilderRegistry {
    public static Item stick;
    public static void register(){stick=new ItemBuildingStick();GameRegistry.registerItem(stick,"building_stick");}
    public static void recipes(){GameRegistry.addRecipe(new ItemStack(stick)," I "," R "," S ",'I',Items.iron_ingot,'R',Items.redstone,'S',Items.stick);}
    private BuilderRegistry(){}
}
