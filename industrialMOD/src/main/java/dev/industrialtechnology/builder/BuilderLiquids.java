package dev.industrialtechnology.builder;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;

/** One accepted source consumes one filled bucket. Native liquid updates perform all flow. */
public final class BuilderLiquids {
    public static boolean is(ItemStack material){return material!=null&&(material.getItem()==Items.water_bucket||material.getItem()==Items.lava_bucket);}
    public static ItemStack pick(Block block){return block==Blocks.water||block==Blocks.flowing_water?new ItemStack(Items.water_bucket):block==Blocks.lava||block==Blocks.flowing_lava?new ItemStack(Items.lava_bucket):null;}
    public static int place(EntityPlayerMP p,ItemStack material,BuildPos pos,int side) {
        World w=p.worldObj;boolean water=material.getItem()==Items.water_bucket;if(water&&w.provider.isHellWorld)return 0;
        Block old=w.getBlock(pos.x,pos.y,pos.z);int oldMeta=w.getBlockMetadata(pos.x,pos.y,pos.z);Block fluid=water?Blocks.flowing_water:Blocks.flowing_lava;
        boolean same=water?(old==Blocks.water||old==Blocks.flowing_water):(old==Blocks.lava||old==Blocks.flowing_lava);
        if(w.getTileEntity(pos.x,pos.y,pos.z)!=null||(!w.isAirBlock(pos.x,pos.y,pos.z)&&!(same&&oldMeta!=0))||!w.canMineBlock(p,pos.x,pos.y,pos.z)||MinecraftServer.getServer().isBlockProtected(w,pos.x,pos.y,pos.z,p)||!p.canPlayerEdit(pos.x,pos.y,pos.z,side,material))return 0;
        int slot=-1;if(!p.capabilities.isCreativeMode){for(int i=0;i<p.inventory.mainInventory.length;i++){ItemStack s=p.inventory.mainInventory[i];if(s!=null&&s.stackSize>0&&s.getItem()==material.getItem()&&!s.hasTagCompound()){slot=i;break;}}if(slot<0)return -1;}
        BlockSnapshot snapshot=BlockSnapshot.getBlockSnapshot(w,pos.x,pos.y,pos.z);
        if(!w.setBlock(pos.x,pos.y,pos.z,fluid,0,2))return 0;
        boolean canceled=MinecraftForge.EVENT_BUS.post(new BlockEvent.PlaceEvent(snapshot,old,p));
        Block after=w.getBlock(pos.x,pos.y,pos.z);boolean accepted=(water?(after==Blocks.water||after==Blocks.flowing_water):(after==Blocks.lava||after==Blocks.flowing_lava))&&w.getBlockMetadata(pos.x,pos.y,pos.z)==0;
        if(canceled||!accepted||slot>=0&&(p.inventory.mainInventory[slot]==null||p.inventory.mainInventory[slot].getItem()!=material.getItem()||p.inventory.mainInventory[slot].stackSize<1)){
            boolean restoring=w.restoringBlockSnapshots;w.restoringBlockSnapshots=true;try{snapshot.restore(true,false);}finally{w.restoringBlockSnapshots=restoring;}return 0;
        }
        if(slot>=0){p.inventory.decrStackSize(slot,1);ItemStack empty=new ItemStack(Items.bucket);if(!p.inventory.addItemStackToInventory(empty))p.entityDropItem(empty,0);}
        w.markBlockForUpdate(pos.x,pos.y,pos.z);w.notifyBlocksOfNeighborChange(pos.x,pos.y,pos.z,fluid);w.scheduleBlockUpdate(pos.x,pos.y,pos.z,fluid,fluid.tickRate(w));return 1;
    }
    private BuilderLiquids(){}
}
