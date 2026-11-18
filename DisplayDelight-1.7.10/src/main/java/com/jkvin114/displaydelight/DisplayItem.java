package com.jkvin114.displaydelight;
import net.minecraft.block.Block;import net.minecraft.item.*;import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;import java.util.List;import cpw.mods.fml.relauncher.*;
public final class DisplayItem extends ItemBlock {
    public DisplayItem(Block block){super(block);}
    @Override public boolean onItemUse(ItemStack s,EntityPlayer p,World w,int x,int y,int z,int side,float hx,float hy,float hz){
        if(!p.capabilities.allowEdit)return false;if(Interactions.use(w,x,y,z,p))return true;return super.onItemUse(s,p,w,x,y,z,side,hx,hy,hz);
    }
    @Override @SideOnly(Side.CLIENT) public void addInformation(ItemStack s,EntityPlayer p,List list,boolean advanced){
        DisplayBlock b=(DisplayBlock)field_150939_a;
        if(!b.definition.empty())list.add(b.food==null?"Decorative display; source food mod is absent.":"Food: "+b.food.getDisplayName());
    }
}
