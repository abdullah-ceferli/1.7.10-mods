package dev.industrialtechnology.builder;

import cpw.mods.fml.relauncher.*;
import dev.industrialtechnology.core.*;
import java.util.List;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.world.World;

public final class ItemBuildingStick extends Item {
    public ItemBuildingStick(){setMaxStackSize(1);setFull3D();setUnlocalizedName("industrialtech.building_stick");setTextureName("minecraft:stick");setCreativeTab(ModRegistry.TAB);}
    @Override public boolean onItemUseFirst(ItemStack s,EntityPlayer p,World w,int x,int y,int z,int side,float hx,float hy,float hz){if(w.isRemote)return false;if(p.isSneaking()){net.minecraft.util.MovingObjectPosition liquid=getMovingObjectPositionFromPlayer(w,p,true);if(liquid!=null&&BuilderLiquids.pick(w.getBlock(liquid.blockX,liquid.blockY,liquid.blockZ))!=null){x=liquid.blockX;y=liquid.blockY;z=liquid.blockZ;side=liquid.sideHit;}}if(p instanceof EntityPlayerMP)BuilderService.click((EntityPlayerMP)p,s,x,y,z,side);return true;}
    @Override public boolean onItemUse(ItemStack s,EntityPlayer p,World w,int x,int y,int z,int side,float hx,float hy,float hz){return w.isRemote||onItemUseFirst(s,p,w,x,y,z,side,hx,hy,hz);}
    @Override public ItemStack onItemRightClick(ItemStack s,World w,EntityPlayer p){if(p.isSneaking()){net.minecraft.util.MovingObjectPosition liquid=getMovingObjectPositionFromPlayer(w,p,true);if(liquid!=null&&BuilderLiquids.pick(w.getBlock(liquid.blockX,liquid.blockY,liquid.blockZ))!=null){if(!w.isRemote&&p instanceof EntityPlayerMP)BuilderService.click((EntityPlayerMP)p,s,liquid.blockX,liquid.blockY,liquid.blockZ,liquid.sideHit);return s;}if(!w.isRemote&&p instanceof EntityPlayerMP){BuilderService.cancel((EntityPlayerMP)p,true);BuilderData.clear(s);BuilderService.sync((EntityPlayerMP)p);}}else if(w.isRemote)IndustrialTechnology.proxy.builderAir();return s;}
    @SideOnly(Side.CLIENT) @Override public int getColorFromItemStack(ItemStack s,int pass){return 0x6ed9ff;}
    @SideOnly(Side.CLIENT) @SuppressWarnings({"rawtypes","unchecked"}) @Override public void addInformation(ItemStack s,EntityPlayer p,List text,boolean advanced){text.add("Hold Left Alt: build modes");text.add("Sneak + right-click a block or liquid: pick material");text.add("Right-click twice: start / end; sneak + air: cancel");text.add("Mode: "+BuilderData.mode(s).label+(BuilderData.hollow(s)?" (hollow)":""));ItemStack material=BuilderData.material(s);text.add("Material: "+(material==null?"not selected":material.getDisplayName()));}
}
