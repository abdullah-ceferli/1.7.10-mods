package dev.industrialtechnology.hatch;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.common.util.ForgeDirection;

public final class ItemSlidingHatch extends ItemBlock {
    public ItemSlidingHatch(Block block){super(block);setHasSubtypes(true);setMaxDamage(0);}
    @Override public int getMetadata(int damage){return Math.max(0,Math.min(2,damage));}
    @Override public String getUnlocalizedName(ItemStack stack){return "tile.industrialtech.sliding_hatch."+getMetadata(stack.getItemDamage());}
    @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) @SuppressWarnings({"unchecked","rawtypes"}) public void addInformation(ItemStack s,EntityPlayer p,List text,boolean advanced){text.add("Right-click: slide open / closed");text.add("Craft alone: normal -> edge -> corner -> normal");text.add("Top of floor: inset; click near the desired join");text.add("Add a solid block to fill an empty offset part");}
    @Override public boolean onItemUse(ItemStack stack,EntityPlayer p,World w,int x,int y,int z,int side,float hx,float hy,float hz){
        if(stack.stackSize<=0||side<0||side>5)return false;
        int variant=getMetadata(stack.getItemDamage()),facing=MathHelper.floor_double(p.rotationYaw*4F/360F+.5)&3;
        Block hit=w.getBlock(x,y,z);boolean inset=side==1&&hit!=Blocks.air&&HatchFloor.accepts(w,x,y,z,hit,w.getBlockMetadata(x,y,z));
        boolean ox=variant==2||variant==1&&(facing&1)==0,oz=variant==2||variant==1&&(facing&1)!=0,upper=inset||side==0||side!=1&&hy>.5F;
        if(inset){
            if(variant==1){ox=Math.min(hx,1-hx)<=Math.min(hz,1-hz);oz=!ox;}
            if(ox&&hx<.5F)x--;if(oz&&hz<.5F)z--;
        }else if(!hit.isReplaceable(w,x,y,z)){ForgeDirection f=ForgeDirection.getOrientation(side);x+=f.offsetX;y+=f.offsetY;z+=f.offsetZ;}
        List<int[]> cells=new ArrayList<int[]>();List<Block> originals=new ArrayList<Block>();
        for(int dx=0;dx<=(ox?1:0);dx++)for(int dz=0;dz<=(oz?1:0);dz++){
            int X=x+dx,Z=z+dz;if(!BlockSlidingHatch.loaded(w,X,y,Z)||!p.canPlayerEdit(X,y,Z,side,stack))return false;
            for(int cx=(X-2)>>4;cx<=(X+2)>>4;cx++)for(int cz=(Z-2)>>4;cz<=(Z+2)>>4;cz++)if(!w.getChunkProvider().chunkExists(cx,cz))return false;
            Block original=w.getBlock(X,y,Z);int meta=w.getBlockMetadata(X,y,Z);
            if(w.getTileEntity(X,y,Z)!=null||(!inset&&!w.isAirBlock(X,y,Z))||!HatchFloor.accepts(w,X,y,Z,original,meta))return false;
            if(!w.isRemote&&(!w.canMineBlock(p,X,y,Z)||MinecraftServer.getServer().isBlockProtected(w,X,y,Z,p)))return false;
            for(HatchGeometry.Box b:HatchGeometry.boxes(ox,oz,upper,false)){HatchGeometry.Box c=b.clip(dx,dz);if(c!=null&&c.solid()&&!w.checkNoEntityCollision(AxisAlignedBB.getBoundingBox(X+c.x,y+c.y,Z+c.z,X+c.a,y+c.b,Z+c.c)))return false;}
            cells.add(new int[]{X,y,Z,meta});originals.add(original);
        }
        if(w.isRemote)return true;
        for(int i=0;i<cells.size();i++){int[] c=cells.get(i);Block b=originals.get(i);if(b!=Blocks.air&&MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(c[0],c[1],c[2],w,b,c[3],p)))return false;}
        // Recheck after protection callbacks, before native Forge captures the multi-place transaction.
        for(int i=0;i<cells.size();i++){int[] c=cells.get(i);if(w.getBlock(c[0],c[1],c[2])!=originals.get(i)||w.getBlockMetadata(c[0],c[1],c[2])!=c[3])return false;}
        int placed=0;
        for(int i=0;i<cells.size();i++){
            int[] c=cells.get(i);if(!w.setBlock(c[0],c[1],c[2],HatchRegistry.block,0,2)){rollback(w,cells,originals,placed);return false;}placed++;
            TileSlidingHatch t=BlockSlidingHatch.tile(w,c[0],c[1],c[2]);if(t==null){rollback(w,cells,originals,placed);return false;}
            t.baseX=x;t.baseY=y;t.baseZ=z;t.variant=variant;t.offsetX=ox;t.offsetZ=oz;t.upper=upper;t.backing=originals.get(i);t.backingMeta=c[3];t.changed();
        }
        stack.stackSize--;w.playSoundEffect(x+.5,y+.5,z+.5,"step.metal",1,.8F);return true;
    }
    private void rollback(World w,List<int[]> cells,List<Block> originals,int count){for(int i=0;i<count;i++){int[] c=cells.get(i);TileSlidingHatch t=BlockSlidingHatch.tile(w,c[0],c[1],c[2]);if(t!=null)t.removing=true;}for(int i=0;i<count;i++){int[] c=cells.get(i);w.setBlock(c[0],c[1],c[2],originals.get(i),c[3],2);}}
}
