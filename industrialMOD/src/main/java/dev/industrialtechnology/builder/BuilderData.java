package dev.industrialtechnology.builder;

import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;

public final class BuilderData {
    public static boolean held(EntityPlayer p){return p!=null&&p.getHeldItem()!=null&&p.getHeldItem().getItem()==BuilderRegistry.stick;}
    public static NBTTagCompound tag(ItemStack s){if(!s.hasTagCompound())s.setTagCompound(new NBTTagCompound());return s.getTagCompound();}
    public static BuildMode mode(ItemStack s){return s!=null&&s.hasTagCompound()&&s.getTagCompound().hasKey("BuildMode")?BuildMode.at(s.getTagCompound().getByte("BuildMode")):BuildMode.LINE;}
    public static boolean hollow(ItemStack s){return s!=null&&s.hasTagCompound()&&s.getTagCompound().getBoolean("Hollow");}
    public static String token(ItemStack s){NBTTagCompound n=tag(s);if(!n.hasKey("BuildToken"))n.setString("BuildToken",UUID.randomUUID().toString());return n.getString("BuildToken");}
    public static ItemStack material(ItemStack tool){if(tool==null||!tool.hasTagCompound())return null;NBTTagCompound n=tool.getTagCompound();Item item=(Item)Item.itemRegistry.getObject(n.getString("BuildMaterial"));if(item==net.minecraft.init.Items.water_bucket||item==net.minecraft.init.Items.lava_bucket)return new ItemStack(item);if(!(item instanceof ItemBlock))return null;ItemStack s=new ItemStack(item,1,n.getInteger("BuildDamage"));return ordinary(s)?s:null;}
    public static boolean ordinary(ItemStack s){if(s==null||!(s.getItem() instanceof ItemBlock)||s.hasTagCompound())return false;Block b=Block.getBlockFromItem(s.getItem());return b!=null&&b!=net.minecraft.init.Blocks.air&&!b.getMaterial().isLiquid()&&!b.hasTileEntity(((ItemBlock)s.getItem()).getMetadata(s.getItemDamage()));}
    public static void material(ItemStack tool,ItemStack s){NBTTagCompound n=tag(tool);n.setString("BuildMaterial",Item.itemRegistry.getNameForObject(s.getItem()));n.setInteger("BuildDamage",s.getItemDamage());clear(tool);}
    public static BuildPos anchor(EntityPlayer p,ItemStack s){if(s==null||!s.hasTagCompound())return null;NBTTagCompound n=s.getTagCompound();if(!n.getBoolean("HasAnchor")||n.getInteger("BuildDimension")!=p.dimension||!n.getString("BuildOwner").equals(p.getUniqueID().toString()))return null;return new BuildPos(n.getInteger("BuildX"),n.getInteger("BuildY"),n.getInteger("BuildZ"));}
    public static void anchor(EntityPlayer p,ItemStack s,BuildPos pos,int side){NBTTagCompound n=tag(s);token(s);n.setBoolean("HasAnchor",true);n.setString("BuildOwner",p.getUniqueID().toString());n.setInteger("BuildDimension",p.dimension);n.setInteger("BuildX",pos.x);n.setInteger("BuildY",pos.y);n.setInteger("BuildZ",pos.z);n.setByte("BuildSide",(byte)side);}
    public static void clear(ItemStack s){tag(s).setBoolean("HasAnchor",false);}
    private BuilderData(){}
}
