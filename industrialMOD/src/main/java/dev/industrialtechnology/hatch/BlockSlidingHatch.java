package dev.industrialtechnology.hatch;

import java.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.*;
import net.minecraftforge.event.world.BlockEvent;
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
    @Override public boolean isSideSolid(IBlockAccess w,int x,int y,int z,ForgeDirection face){
        TileSlidingHatch t=tile(w,x,y,z);if(t==null||t.backing==Blocks.air)return false;
        double hx=t.baseX+(t.offsetX?.5:0),hz=t.baseZ+(t.offsetZ?.5:0);
        return face==ForgeDirection.WEST&&x<hx||face==ForgeDirection.EAST&&x+1>hx+1||face==ForgeDirection.NORTH&&z<hz||face==ForgeDirection.SOUTH&&z+1>hz+1;
    }
    public static TileSlidingHatch tile(IBlockAccess w,int x,int y,int z){TileEntity t=w.getTileEntity(x,y,z);return t instanceof TileSlidingHatch?(TileSlidingHatch)t:null;}
    public static boolean loaded(World w,int x,int y,int z){return w!=null&&y>=0&&y<w.getHeight()&&w.getChunkProvider().chunkExists(x>>4,z>>4);}
    public static List<HatchGeometry.Box> staticBoxes(IBlockAccess w,int x,int y,int z){TileSlidingHatch t=tile(w,x,y,z);List<HatchGeometry.Box> out=new ArrayList<HatchGeometry.Box>();if(t==null)return out;clip(out,HatchGeometry.frame(t.offsetX,t.offsetZ,t.upper),x-t.baseX,z-t.baseZ);if(t.backing!=Blocks.air)clip(out,HatchGeometry.floor(t.offsetX,t.offsetZ,x-t.baseX,z-t.baseZ),x-t.baseX,z-t.baseZ);return out;}
    private static void clip(List<HatchGeometry.Box> out,List<HatchGeometry.Box> boxes,int dx,int dz){for(HatchGeometry.Box b:boxes){HatchGeometry.Box c=b.clip(dx,dz);if(c!=null)out.add(c);}}
    public static List<HatchGeometry.Box> boxes(IBlockAccess w,int x,int y,int z){List<HatchGeometry.Box> out=staticBoxes(w,x,y,z);TileSlidingHatch t=tile(w,x,y,z);if(t!=null)clip(out,HatchGeometry.lid(t.offsetX,t.offsetZ,t.upper,t.progress(0)),x-t.baseX,z-t.baseZ);return out;}
    @Override @SuppressWarnings({"rawtypes","unchecked"}) public void addCollisionBoxesToList(World w,int x,int y,int z,AxisAlignedBB mask,List list,Entity entity){for(HatchGeometry.Box b:boxes(w,x,y,z))if(b.solid()){AxisAlignedBB box=AxisAlignedBB.getBoundingBox(x+b.x,y+b.y,z+b.z,x+b.a,y+b.b,z+b.c);if(box.intersectsWith(mask))list.add(box);}}
    @Override public MovingObjectPosition collisionRayTrace(World w,int x,int y,int z,Vec3 from,Vec3 to){MovingObjectPosition best=null;double distance=Double.MAX_VALUE;for(HatchGeometry.Box b:boxes(w,x,y,z))if(b.solid()){setBlockBounds((float)b.x,(float)b.y,(float)b.z,(float)b.a,(float)b.b,(float)b.c);MovingObjectPosition hit=super.collisionRayTrace(w,x,y,z,from,to);if(hit!=null&&from.squareDistanceTo(hit.hitVec)<distance){best=hit;distance=from.squareDistanceTo(hit.hitVec);}}setBlockBounds(0,0,0,1,.1875F,1);return best;}
    @Override public void setBlockBoundsBasedOnState(IBlockAccess w,int x,int y,int z){List<HatchGeometry.Box> boxes=boxes(w,x,y,z);double X=1,Y=1,Z=1,A=0,B=0,C=0;for(HatchGeometry.Box b:boxes){X=Math.min(X,b.x);Y=Math.min(Y,b.y);Z=Math.min(Z,b.z);A=Math.max(A,b.a);B=Math.max(B,b.b);C=Math.max(C,b.c);}if(boxes.isEmpty())setBlockBounds(0,0,0,1,.1875F,1);else setBlockBounds((float)X,(float)Y,(float)Z,(float)A,(float)Math.min(1,B),(float)C);}
    public static List<TileSlidingHatch> parts(World w,TileSlidingHatch t){List<TileSlidingHatch> all=new ArrayList<TileSlidingHatch>();for(int dx=0;dx<=(t.offsetX?1:0);dx++)for(int dz=0;dz<=(t.offsetZ?1:0);dz++){int x=t.baseX+dx,z=t.baseZ+dz;if(!loaded(w,x,t.baseY,z))return null;TileSlidingHatch part=tile(w,x,t.baseY,z);if(!t.same(part))return null;all.add(part);}return all;}
    private boolean allowed(World w,EntityPlayer p,TileSlidingHatch t,int side,ItemStack stack){return w.canMineBlock(p,t.xCoord,t.yCoord,t.zCoord)&&p.canPlayerEdit(t.xCoord,t.yCoord,t.zCoord,side,stack)&&!MinecraftServer.getServer().isBlockProtected(w,t.xCoord,t.yCoord,t.zCoord,p);}
    @Override public boolean onBlockActivated(World w,int x,int y,int z,EntityPlayer p,int side,float hx,float hy,float hz){
        TileSlidingHatch t=tile(w,x,y,z);if(t==null)return false;ItemStack held=p.getCurrentEquippedItem();
        // Building beside a hatch must place blocks instead of accidentally toggling it.
        if(held!=null&&held.getItem() instanceof ItemBlock&&!(held.getItem() instanceof ItemSlidingHatch)){
            Block b=Block.getBlockFromItem(held.getItem());int meta=held.getItem().getMetadata(held.getItemDamage());
            if(t.backing!=Blocks.air||HatchGeometry.floor(t.offsetX,t.offsetZ,x-t.baseX,z-t.baseZ).isEmpty()||!HatchFloor.accepts(w,x,y,z,b,meta))return false;
            if(w.isRemote)return true;if(!allowed(w,p,t,side,held)||held.stackSize<=0)return true;
            for(HatchGeometry.Box box:HatchGeometry.floor(t.offsetX,t.offsetZ,x-t.baseX,z-t.baseZ)){HatchGeometry.Box c=box.clip(x-t.baseX,z-t.baseZ);if(c!=null&&!w.checkNoEntityCollision(AxisAlignedBB.getBoundingBox(x+c.x,y+c.y,z+c.z,x+c.a,y+c.b,z+c.c)))return true;}
            BlockEvent.PlaceEvent event=new BlockEvent.PlaceEvent(BlockSnapshot.getBlockSnapshot(w,x,y,z),w.getBlock(x,y-1,z),p);
            if(MinecraftForge.EVENT_BUS.post(event)||tile(w,x,y,z)!=t||t.backing!=Blocks.air||p.getCurrentEquippedItem()!=held||held.stackSize<=0)return true;
            if(!p.capabilities.isCreativeMode){held.stackSize--;if(held.stackSize==0)p.inventory.mainInventory[p.inventory.currentItem]=null;}t.backing=b;t.backingMeta=meta&15;t.changed();w.notifyBlocksOfNeighborChange(x,y,z,this);return true;
        }
        if(w.isRemote)return true;List<TileSlidingHatch> all=parts(w,t);if(all==null)return true;
        for(TileSlidingHatch part:all){if(!allowed(w,p,part,side,new ItemStack(this)))return true;if(part.xCoord!=x||part.zCoord!=z){PlayerInteractEvent e=ForgeEventFactory.onPlayerInteract(p,PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,part.xCoord,part.yCoord,part.zCoord,side,w);if(e.isCanceled()||e.useBlock==Event.Result.DENY)return true;}}
        if(parts(w,t)==null)return true;if(t.open&&occupied(w,t))return true;move(w,t,all,!t.open);return true;
    }
    private static boolean occupied(World w,TileSlidingHatch t){double x=t.baseX+(t.offsetX?.5:0),z=t.baseZ+(t.offsetZ?.5:0),y=t.baseY+(t.upper?.8125:0);AxisAlignedBB slab=AxisAlignedBB.getBoundingBox(x+.0625,y+.03125,z+.0625,x+.9375,y+.15625,z+.9375);for(Object o:w.getEntitiesWithinAABB(Entity.class,slab)){Entity e=(Entity)o;if(e instanceof EntityLivingBase||e.canBeCollidedWith())return true;}return false;}
    private static void move(World w,TileSlidingHatch t,List<TileSlidingHatch> all,boolean target){float from=t.progress(0);long now=w.getTotalWorldTime();for(TileSlidingHatch a:all)a.move(target,from,now);w.scheduleBlockUpdate(t.baseX,t.baseY,t.baseZ,HatchRegistry.block,1);w.playSoundEffect(t.baseX+.5,t.baseY+.5,t.baseZ+.5,"tile.piston."+(target?"out":"in"),.45F,1.2F);}
    @Override public void updateTick(World w,int x,int y,int z,Random random){TileSlidingHatch t=tile(w,x,y,z);if(t==null||x!=t.baseX||z!=t.baseZ)return;List<TileSlidingHatch> all=parts(w,t);if(all==null)return;if(!t.open&&t.moving()&&occupied(w,t)){move(w,t,all,true);return;}if(t.moving())w.scheduleBlockUpdate(x,y,z,this,1);}
    private static void restore(World w,List<TileSlidingHatch> all){for(TileSlidingHatch a:all)a.removing=true;for(TileSlidingHatch a:all)w.setBlock(a.xCoord,a.yCoord,a.zCoord,a.backing,a.backingMeta,3);}
    @Override public boolean removedByPlayer(World w,EntityPlayer p,int x,int y,int z,boolean harvest){TileSlidingHatch t=tile(w,x,y,z);if(w.isRemote)return true;if(t==null)return w.setBlockToAir(x,y,z);List<TileSlidingHatch> all=parts(w,t);if(all==null)return false;for(TileSlidingHatch a:all){if(!allowed(w,p,a,1,new ItemStack(this)))return false;if(a.xCoord!=x||a.zCoord!=z){BlockEvent.BreakEvent e=new BlockEvent.BreakEvent(a.xCoord,a.yCoord,a.zCoord,w,this,0,p);if(MinecraftForge.EVENT_BUS.post(e))return false;}}
        if(parts(w,t)==null)return false;restore(w,all);if(!p.capabilities.isCreativeMode)dropBlockAsItem(w,x,y,z,new ItemStack(this,1,t.variant));return true;}
    @Override public void harvestBlock(World w,EntityPlayer p,int x,int y,int z,int meta){}
    @Override public ArrayList<ItemStack> getDrops(World w,int x,int y,int z,int meta,int fortune){ArrayList<ItemStack> out=new ArrayList<ItemStack>();TileSlidingHatch t=tile(w,x,y,z);if(t!=null&&t.xCoord==t.baseX&&t.zCoord==t.baseZ&&!t.removing)out.add(new ItemStack(this,1,t.variant));return out;}
    @Override public void onBlockExploded(World w,int x,int y,int z,Explosion explosion){TileSlidingHatch t=tile(w,x,y,z);List<TileSlidingHatch> all=t==null?null:parts(w,t);if(all!=null)restore(w,all);else super.onBlockExploded(w,x,y,z,explosion);}
    @Override public void breakBlock(World w,int x,int y,int z,Block old,int metadata){
        TileSlidingHatch t=tile(w,x,y,z);
        if(!w.isRemote&&!w.restoringBlockSnapshots&&t!=null&&!t.removing){
            List<TileSlidingHatch> all=parts(w,t);if(all!=null){for(TileSlidingHatch a:all)a.removing=true;for(TileSlidingHatch a:all)if(a!=t)w.setBlock(a.xCoord,a.yCoord,a.zCoord,a.backing,a.backingMeta,3);}
            // An external replacement owns this cell. Return its saved floor as an item, never overwrite it.
            if(t.backing!=Blocks.air&&Item.getItemFromBlock(t.backing)!=null)dropBlockAsItem(w,x,y,z,new ItemStack(t.backing,1,t.backingMeta));
        }
        super.breakBlock(w,x,y,z,old,metadata);
    }
    @Override public ItemStack getPickBlock(MovingObjectPosition target,World w,int x,int y,int z){TileSlidingHatch t=tile(w,x,y,z);return new ItemStack(this,1,t==null?0:t.variant);}
    @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) @SuppressWarnings({"rawtypes","unchecked"}) public void getSubBlocks(Item item,net.minecraft.creativetab.CreativeTabs tab,List out){for(int i=0;i<3;i++)out.add(new ItemStack(item,1,i));}
}
