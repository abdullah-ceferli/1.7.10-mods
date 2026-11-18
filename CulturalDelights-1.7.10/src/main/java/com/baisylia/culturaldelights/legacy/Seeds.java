package com.baisylia.culturaldelights.legacy;
import com.google.gson.JsonObject;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class Seeds extends Ingredient implements net.minecraftforge.common.IPlantable {
    public final Plant crop;
    public Seeds(Plant plant,JsonObject d){super(d);crop=plant;}
    public boolean onItemUse(ItemStack stack,EntityPlayer p,World w,int x,int y,int z,int side,float u,float v,float t) {
        if(side!=1||!p.canPlayerEdit(x,y+1,z,side,stack)||!w.isAirBlock(x,y+1,z)||!crop.canBlockStay(w,x,y+1,z))return false;
        if(!w.isRemote){w.setBlock(x,y+1,z,crop,0,3);if(!p.capabilities.isCreativeMode)stack.stackSize--;}
        return true;
    }
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess w,int x,int y,int z){return net.minecraftforge.common.EnumPlantType.Crop;}
    public net.minecraft.block.Block getPlant(net.minecraft.world.IBlockAccess w,int x,int y,int z){return crop;}
    public int getPlantMetadata(net.minecraft.world.IBlockAccess w,int x,int y,int z){return 0;}
}
