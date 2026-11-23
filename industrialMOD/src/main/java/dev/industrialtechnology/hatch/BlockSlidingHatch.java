package dev.industrialtechnology.hatch;

import java.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.Event;

public final class BlockSlidingHatch extends BlockContainer {
    public static int renderId;
    public BlockSlidingHatch(){super(Material.iron);setHardness(3);setResistance(10);setStepSound(soundTypeMetal);setBlockName("industrialtech.sliding_hatch");setBlockTextureName("iron_block");setCreativeTab(dev.industrialtechnology.core.ModRegistry.TAB);}
    @Override public TileEntity createNewTileEntity(World w,int metadata){return new TileSlidingHatch();}
    @Override public boolean isOpaqueCube(){return false;}
    @Override public boolean renderAsNormalBlock(){return false;}
    @Override public int getRenderType(){return renderId;}
    @Override public boolean isSideSolid(IBlockAccess w,int x,int y,int z,ForgeDirection face){return false;}
    public static TileSlidingHatch tile(IBlockAccess w,int x,int y,int z){TileEntity t=w.getTileEntity(x,y,z);return t instanceof TileSlidingHatch?(TileSlidingHatch)t:null;}
    public static boolean loaded(World w,int x,int y,int z){return y>=0&&y<w.getHeight()&&w.getChunkProvider().chunkExists(x>>4,z>>4);}
    public static List<HatchGeometry.Box> boxes(IBlockAccess w,int x,int y,int z){TileSlidingHatch t=tile(w,x,y,z);List<HatchGeometry.Box> out=new ArrayList<HatchGeometry.Box>();if(t==null)return out;for(HatchGeometry.Box b:HatchGeometry.boxes(t.offsetX,t.offsetZ,t.upper,t.open)){HatchGeometry.Box c=b.clip(x-t.baseX,z-t.baseZ);if(c!=null)out.add(c);}return out;}
    @Override @SuppressWarnings({"rawtypes","unchecked"}) public void addCollisionBoxesToList(World w,int x,int y,int z,AxisAlignedBB mask,List list,Entity entity){for(HatchGeometry.Box b:boxes(w,x,y,z))if(b.solid()){AxisAlignedBB box=AxisAlignedBB.getBoundingBox(x+b.x,y+b.y,z+b.z,x+b.a,y+b.b,z+b.c);if(box.intersectsWith(mask))list.add(box);}}
    @Override public MovingObjectPosition collisionRayTrace(World w,int x,int y,int z,Vec3 from,Vec3 to){MovingObjectPosition best=null;double distance=Double.MAX_VALUE;for(HatchGeometry.Box b:boxes(w,x,y,z))if(b.solid()){setBlockBounds((float)b.x,(float)b.y,(float)b.z,(float)b.a,(float)b.b,(float)b.c);MovingObjectPosition hit=super.collisionRayTrace(w,x,y,z,from,to);if(hit!=null&&from.squareDistanceTo(hit.hitVec)<distance){best=hit;distance=from.squareDistanceTo(hit.hitVec);}}setBlockBounds(0,0,0,1,.1875F,1);return best;}
    @Override public void setBlockBoundsBasedOnState(IBlockAccess w,int x,int y,int z){List<HatchGeometry.Box> boxes=boxes(w,x,y,z);double X=1,Y=1,Z=1,A=0,B=0,C=0;for(HatchGeometry.Box b:boxes){X=Math.min(X,b.x);Y=Math.min(Y,b.y);Z=Math.min(Z,b.z);A=Math.max(A,b.a);B=Math.max(B,b.b);C=Math.max(C,b.c);}if(boxes.isEmpty())setBlockBounds(0,0,0,1,.1875F,1);else setBlockBounds((float)X,(float)Y,(float)Z,(float)A,(float)Math.min(1,B),(float)C);}
    public static List<TileSlidingHatch> parts(World w,TileSlidingHatch t){List<TileSlidingHatch> all=new ArrayList<TileSlidingHatch>();for(int dx=0;dx<=(t.offsetX?1:0);dx++)for(int dz=0;dz<=(t.offsetZ?1:0);dz++){int x=t.baseX+dx,z=t.baseZ+dz;if(!loaded(w,x,t.baseY,z))return null;TileSlidingHatch part=tile(w,x,t.baseY,z);if(!t.same(part))return null;all.add(part);}return all;}
    @Override public boolean onBlockActivated(World w,int x,int y,int z,EntityPlayer p,int side,float hx,float hy,float hz){if(w.isRemote)return true;TileSlidingHatch t=tile(w,x,y,z);if(t==null)return true;List<TileSlidingHatch> all=parts(w,t);if(all==null)return true;for(TileSlidingHatch part:all){if(!w.canMineBlock(p,part.xCoord,part.yCoord,part.zCoord)||!p.canPlayerEdit(part.xCoord,part.yCoord,part.zCoord,side,new ItemStack(this))||MinecraftServer.getServer().isBlockProtected(w,part.xCoord,part.yCoord,part.zCoord,p))return true;if(part.xCoord!=x||part.zCoord!=z){PlayerInteractEvent e=ForgeEventFactory.onPlayerInteract(p,PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,part.xCoord,part.yCoord,part.zCoord,side,w);if(e.isCanceled()||e.useBlock==Event.Result.DENY)return true;}}
        if(parts(w,t)==null)return true;boolean open=!t.open;for(TileSlidingHatch part:all){part.open=open;part.changed();}w.playSoundEffect(x+.5,y+.5,z+.5,"tile.piston."+(open?"out":"in"),.45F,1.2F);return true;}
    @Override public boolean removedByPlayer(World w,EntityPlayer p,int x,int y,int z,boolean harvest){TileSlidingHatch t=tile(w,x,y,z);if(w.isRemote)return true;if(t==null)return w.setBlockToAir(x,y,z);List<TileSlidingHatch> all=parts(w,t);if(all==null)return false;for(TileSlidingHatch a:all){if(!w.canMineBlock(p,a.xCoord,a.yCoord,a.zCoord)||!p.canPlayerEdit(a.xCoord,a.yCoord,a.zCoord,1,new ItemStack(this))||MinecraftServer.getServer().isBlockProtected(w,a.xCoord,a.yCoord,a.zCoord,p))return false;if(a.xCoord!=x||a.zCoord!=z){net.minecraftforge.event.world.BlockEvent.BreakEvent e=new net.minecraftforge.event.world.BlockEvent.BreakEvent(a.xCoord,a.yCoord,a.zCoord,w,this,0,p);if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e))return false;}}
        if(parts(w,t)==null)return false;for(TileSlidingHatch a:all)a.removing=true;for(TileSlidingHatch a:all)w.setBlock(a.xCoord,a.yCoord,a.zCoord,net.minecraft.init.Blocks.air,0,2);if(!p.capabilities.isCreativeMode)dropBlockAsItem(w,x,y,z,new ItemStack(this,1,t.variant));return true;}
    @Override public void harvestBlock(World w,EntityPlayer p,int x,int y,int z,int meta){}
    @Override public ArrayList<ItemStack> getDrops(World w,int x,int y,int z,int meta,int fortune){ArrayList<ItemStack> out=new ArrayList<ItemStack>();TileSlidingHatch t=tile(w,x,y,z);if(t!=null&&t.xCoord==t.baseX&&t.zCoord==t.baseZ&&!t.removing)out.add(new ItemStack(this,1,t.variant));return out;}
    @Override public void breakBlock(World w,int x,int y,int z,Block old,int metadata){TileSlidingHatch t=tile(w,x,y,z);if(t!=null&&!t.removing){List<TileSlidingHatch> all=parts(w,t);if(all!=null){for(TileSlidingHatch a:all)a.removing=true;for(TileSlidingHatch a:all)if(a.xCoord!=x||a.zCoord!=z)w.setBlock(a.xCoord,a.yCoord,a.zCoord,net.minecraft.init.Blocks.air,0,2);}}super.breakBlock(w,x,y,z,old,metadata);}
    @Override public ItemStack getPickBlock(MovingObjectPosition target,World w,int x,int y,int z){TileSlidingHatch t=tile(w,x,y,z);return new ItemStack(this,1,t==null?0:t.variant);}
    @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) @SuppressWarnings({"rawtypes","unchecked"}) public void getSubBlocks(Item item,net.minecraft.creativetab.CreativeTabs tab,List out){for(int i=0;i<3;i++)out.add(new ItemStack(item,1,i));}
}
