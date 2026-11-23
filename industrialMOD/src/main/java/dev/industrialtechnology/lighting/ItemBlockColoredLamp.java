package dev.industrialtechnology.lighting;

import java.util.List;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

public final class ItemBlockColoredLamp extends ItemBlock {
    private final BlockColoredLamp lamp;
    public ItemBlockColoredLamp(Block block){super(block);lamp=(BlockColoredLamp)block;setHasSubtypes(true);setMaxDamage(0);}
    @Override public int getMetadata(int damage){return damage&15;}
    @Override public String getUnlocalizedName(ItemStack stack){return "tile.industrialtech."+(lamp.mode==BlockColoredLamp.ALWAYS?"colored_lamp.":"colored_redstone_lamp.")+LampColors.NAMES[stack.getItemDamage()&15];}
    @SideOnly(Side.CLIENT) @Override public int getColorFromItemStack(ItemStack stack,int pass){return LampColors.color(stack.getItemDamage(),lamp.lit());}
    @SideOnly(Side.CLIENT) @Override @SuppressWarnings({"rawtypes","unchecked"}) public void addInformation(ItemStack s,EntityPlayer p,List text,boolean advanced){text.add(lamp.mode==BlockColoredLamp.ALWAYS?"Always on | Light level 15":"Powered by redstone | Light level 15 when on");}
}
