package dev.industrialtechnology.hatch;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;

public final class ItemSlidingHatch extends ItemBlock {
    public ItemSlidingHatch(Block block){super(block);setHasSubtypes(true);setMaxDamage(0);}
    @Override public int getMetadata(int damage){return Math.max(0,Math.min(2,damage));}
    @Override public String getUnlocalizedName(ItemStack stack){return "tile.industrialtech.sliding_hatch."+getMetadata(stack.getItemDamage());}
    @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) @SuppressWarnings({"unchecked","rawtypes"}) public void addInformation(ItemStack s,EntityPlayer p,List text,boolean advanced){text.add("Right-click: slide open / closed");text.add("Craft alone: normal -> edge -> corner -> normal");text.add("Offset parts need empty space across the join");}
    @Override public boolean onItemUse(ItemStack stack,EntityPlayer p,World w,int x,int y,int z,int side,float hx,float hy,float hz){
        if(stack.stackSize<=0||side<0||side>5)return false;
        Block hit=w.getBlock(x,y,z);if(!hit.isReplaceable(w,x,y,z)){ForgeDirection f=ForgeDirection.getOrientation(side);x+=f.offsetX;y+=f.offsetY;z+=f.offsetZ;}
        int variant=getMetadata(stack.getItemDamage()),facing=MathHelper.floor_double(p.rotationYaw*4F/360F+.5)&3;
        boolean offsetX=variant==2||variant==1&&(facing&1)==0,offsetZ=variant==2||variant==1&&(facing&1)!=0,upper=side==0||side!=1&&hy>.5F;
        List<int[]> cells=new ArrayList<int[]>();
        for(int dx=0;dx<=(offsetX?1:0);dx++)for(int dz=0;dz<=(offsetZ?1:0);dz++){
            int X=x+dx,Z=z+dz;if(!BlockSlidingHatch.loaded(w,X,y,Z)||!p.canPlayerEdit(X,y,Z,side,stack))return false;
            for(int cx=(X-2)>>4;cx<=(X+2)>>4;cx++)for(int cz=(Z-2)>>4;cz<=(Z+2)>>4;cz++)if(!w.getChunkProvider().chunkExists(cx,cz))return false;
            if(!w.isAirBlock(X,y,Z)||w.getTileEntity(X,y,Z)!=null)return false;
            if(!w.isRemote&&(!w.canMineBlock(p,X,y,Z)||MinecraftServer.getServer().isBlockProtected(w,X,y,Z,p)))return false;
            for(HatchGeometry.Box b:HatchGeometry.boxes(offsetX,offsetZ,upper,false)){HatchGeometry.Box clip=b.clip(dx,dz);if(clip!=null&&clip.solid()&&!w.checkNoEntityCollision(AxisAlignedBB.getBoundingBox(X+clip.x,y+clip.y,Z+clip.z,X+clip.a,y+clip.b,Z+clip.c)))return false;}
            cells.add(new int[]{X,y,Z});
        }
        if(w.isRemote)return true;
        // Native ItemStack.tryPlaceItemIntoWorld captures every part as one cancellable MultiPlaceEvent.
        List<int[]> placed=new ArrayList<int[]>();
        for(int[] cell:cells){if(!w.setBlock(cell[0],cell[1],cell[2],HatchRegistry.block,0,2)){rollback(w,placed);return false;}placed.add(cell);TileSlidingHatch t=BlockSlidingHatch.tile(w,cell[0],cell[1],cell[2]);if(t==null){rollback(w,placed);return false;}t.baseX=x;t.baseY=y;t.baseZ=z;t.variant=variant;t.offsetX=offsetX;t.offsetZ=offsetZ;t.upper=upper;t.changed();}
        stack.stackSize--;w.playSoundEffect(x+.5,y+.5,z+.5,"step.metal",1,.8F);return true;
    }
    private void rollback(World w,List<int[]> cells){for(int[] c:cells){TileSlidingHatch t=BlockSlidingHatch.tile(w,c[0],c[1],c[2]);if(t!=null)t.removing=true;}for(int[] c:cells)w.setBlock(c[0],c[1],c[2],net.minecraft.init.Blocks.air,0,2);}
}
