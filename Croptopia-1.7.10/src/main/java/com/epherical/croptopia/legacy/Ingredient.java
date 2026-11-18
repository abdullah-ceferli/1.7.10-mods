package com.epherical.croptopia.legacy;
import com.google.gson.JsonObject;
import net.minecraft.item.*;
public class Ingredient extends Item {
    final JsonObject definition;
    public Ingredient(JsonObject d) {definition=d;setMaxStackSize(Catalog.str(d,"kind","").equals("utensil")?1:Catalog.num(d,"stack",64));}
    public boolean hasContainerItem(ItemStack stack) {return Catalog.str(definition,"kind","").equals("utensil");}
    public ItemStack getContainerItem(ItemStack stack) {ItemStack copy=stack.copy();copy.stackSize=1;return copy;}
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {return false;}
    public boolean hasEffect(ItemStack stack,int pass) {return Catalog.yes(definition,"glint");}
    public EnumRarity getRarity(ItemStack stack){String name=Catalog.str(definition,"rarity","common");return name.equals("epic")?EnumRarity.epic:name.equals("rare")?EnumRarity.rare:name.equals("uncommon")?EnumRarity.uncommon:EnumRarity.common;}
}
