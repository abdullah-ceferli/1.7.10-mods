package com.jkvin114.displaydelight;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.eventhandler.Event;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.World;import net.minecraft.entity.player.EntityPlayer;import net.minecraft.entity.item.EntityItem;
import net.minecraft.block.Block;import net.minecraft.item.*;import net.minecraftforge.common.util.ForgeDirection;

public final class Interactions {
    private static void consume(EntityPlayer p,ItemStack s,int amount){if(!p.capabilities.isCreativeMode){s.stackSize-=amount;if(s.stackSize<=0)p.inventory.setInventorySlotContents(p.inventory.currentItem,null);}}
    private static void give(EntityPlayer p,ItemStack s){if(!p.inventory.addItemStackToInventory(s)&&s.stackSize>0)p.dropPlayerItemWithRandomChoice(s,false);p.inventory.markDirty();}
    private static boolean axe(ItemStack s){return s!=null&&(s.getItem() instanceof ItemAxe||s.getItem().getToolClasses(s).contains("axe"));}
    public static boolean use(World w,int x,int y,int z,EntityPlayer p){
        Block raw=w.getBlock(x,y,z);if(!(raw instanceof DisplayBlock))return false;DisplayBlock b=(DisplayBlock)raw;
        ItemStack held=p.getHeldItem();boolean empty=held==null;boolean small=b.definition.small();int count=b.count(w,x,y,z);boolean hidden=b.hidden(w,x,y,z);
        if(!p.capabilities.allowEdit||!p.canPlayerEdit(x,y,z,1,held))return false;
        if(!b.definition.empty()&&b.definition.plated()&&empty&&b.food!=null){
            if(w.isRemote)return true;int amount=p.isSneaking()&&Settings.takeAll?count:1;
            if(amount==count){if(hidden)w.setBlockToAir(x,y,z);else DisplayBlock.place(w,x,y,z,Catalog.plate(small),0,1,false);}
            else DisplayTile.at(w,x,y,z).set(count-amount,hidden);
            ItemStack food=b.food.copy();food.stackSize=amount;give(p,food);p.swingItem();return true;
        }
        if(b.definition.plated()&&!hidden&&axe(held)){
            if(w.isRemote)return true;DisplayTile.at(w,x,y,z).set(count,true);
            if(b.food!=null&&!p.capabilities.isCreativeMode)give(p,new ItemStack(Catalog.plate(small)));p.swingItem();return true;
        }
        if(b.definition.plated()&&hidden&&held!=null&&held.getItem()==Item.getItemFromBlock(Catalog.plate(small))){
            if(w.isRemote)return true;DisplayTile.at(w,x,y,z).set(count,false);consume(p,held,1);p.swingItem();return true;
        }
        String type=small?"small":"plate";DisplayBlock food=Catalog.find(held,type);
        if(food!=null&&(b.definition.empty()||food==b&&count<b.definition.max)){
            if(w.isRemote)return true;int existing=b.definition.empty()?0:count;
            int amount=p.isSneaking()&&Settings.placeAll?Math.min(food.definition.max-existing,held.stackSize):1;
            if(DisplayBlock.place(w,x,y,z,food,b.definition.empty()?(net.minecraft.util.MathHelper.floor_double(p.rotationYaw*4/360+.5)&3):w.getBlockMetadata(x,y,z),existing+amount,b.definition.empty()?false:hidden))consume(p,held,amount);
            p.swingItem();return true;
        }
        return false;
    }
    @SubscribeEvent(priority=cpw.mods.fml.common.eventhandler.EventPriority.LOWEST) public void interact(PlayerInteractEvent e){
        if(e.action!=PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)return;
        EntityPlayer p=e.entityPlayer;World w=e.world;if(!p.capabilities.allowEdit)return;
        boolean handled=use(w,e.x,e.y,e.z,p);
        if(!handled&&p.isSneaking()){
            ItemStack stack=p.getHeldItem();DisplayBlock target=Catalog.find(stack,"direct");
            if(target!=null){
                ForgeDirection side=ForgeDirection.getOrientation(e.face);int x=e.x+side.offsetX,y=e.y+side.offsetY,z=e.z+side.offsetZ;
                if(y>=0&&y<w.getHeight()&&w.canPlaceEntityOnSide(target,x,y,z,false,e.face,p,stack)&&p.canPlayerEdit(x,y,z,e.face,stack)){
                    if(w.isRemote)handled=true;
                    else {handled=DisplayBlock.place(w,x,y,z,target,net.minecraft.util.MathHelper.floor_double(p.rotationYaw*4/360+.5)&3,1,false);if(handled){consume(p,stack,1);p.swingItem();}}
                }
            }
        }
        if(handled){e.useBlock=Event.Result.DENY;e.useItem=Event.Result.DENY;e.setCanceled(true);}
    }
}
