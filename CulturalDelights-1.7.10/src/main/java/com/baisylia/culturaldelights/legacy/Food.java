package com.baisylia.culturaldelights.legacy;
import com.google.gson.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.world.World;
public final class Food extends ItemFood {
    public final JsonObject definition;
    public Food(JsonObject d) {super(Catalog.num(d,"hunger",0),d.get("saturation").getAsFloat(),Catalog.yes(d,"meat"));definition=d;setMaxStackSize(Catalog.num(d,"stack",64));if(Catalog.yes(d,"always"))setAlwaysEdible();}
    public int getMaxItemUseDuration(ItemStack s) {return Catalog.yes(definition,"fast")?16:32;}
    public EnumAction getItemUseAction(ItemStack s) {return Catalog.yes(definition,"drink")?EnumAction.drink:EnumAction.eat;}
    public boolean hasEffect(ItemStack s,int pass) {return Catalog.yes(definition,"glint");}
    public boolean hasContainerItem(ItemStack s){return !Catalog.str(definition,"craftReturnItem","").isEmpty();}
    public ItemStack getContainerItem(ItemStack s){return Port.CONTENT.stack(Catalog.str(definition,"craftReturnItem",""));}
    public EnumRarity getRarity(ItemStack s){String name=Catalog.str(definition,"rarity","common");return name.equals("epic")?EnumRarity.epic:name.equals("rare")?EnumRarity.rare:name.equals("uncommon")?EnumRarity.uncommon:EnumRarity.common;}
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) public void addInformation(ItemStack s,EntityPlayer p,java.util.List lines,boolean advanced){if(definition.has("tooltip"))for(JsonElement line:definition.getAsJsonArray("tooltip"))lines.add(net.minecraft.util.EnumChatFormatting.GRAY+line.getAsString());}
    public ItemStack onEaten(ItemStack s,World w,EntityPlayer p) {
        int previous=s.stackSize;ItemStack result=super.onEaten(s,w,p);
        if(Catalog.yes(definition,"infinite")&&!p.capabilities.isCreativeMode){result.stackSize=previous;return result;}
        if(p.capabilities.isCreativeMode){result.stackSize=previous;return result;}
        ItemStack rest=Port.CONTENT.stack(Catalog.str(definition,"returnItem",""));
        if(rest!=null) {if(result.stackSize<=0)return rest;if(!p.inventory.addItemStackToInventory(rest)&&!w.isRemote)p.dropPlayerItemWithRandomChoice(rest,false);}
        return result;
    }
    protected void onFoodEaten(ItemStack s,World w,EntityPlayer p) {
        if(w.isRemote)return;
        for(JsonElement e:definition.getAsJsonArray("effects")) {
            JsonObject effect=e.getAsJsonObject();if(w.rand.nextFloat()>effect.get("chance").getAsFloat())continue;
            Potion potion=potion(Catalog.str(effect,"name",""));
            if(potion!=null)p.addPotionEffect(new PotionEffect(potion.id,Catalog.num(effect,"duration",0),Catalog.num(effect,"level",0)));
        }
    }
    static Potion potion(String name) {
        if(name.equals("CONFUSION"))return Potion.confusion;if(name.equals("HUNGER"))return Potion.hunger;if(name.equals("POISON"))return Potion.poison;
        if(name.equals("DAMAGE_BOOST"))return Potion.damageBoost;if(name.equals("DAMAGE_RESISTANCE"))return Potion.resistance;
        if(name.equals("NOURISHMENT"))return vectorwing.farmersdelight.legacy.Content.nourishment;
        return null;
    }
}
