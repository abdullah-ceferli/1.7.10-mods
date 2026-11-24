package dev.industrialtechnology.builder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.*;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;

/** All world/inventory mutations execute on the server with shared candidate/placement budgets. */
public final class BuilderService {
    public static final int GLOBAL_SCAN=2048,PER_JOB_SCAN=1024,GLOBAL_ATTEMPTS=64,PER_JOB_ATTEMPTS=32,MAX_JOBS=16;
    private static final Set<EntityPlayerMP> loggedOut=Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<EntityPlayerMP,Boolean>()));
    private static final ThreadLocal<Guard> placing=new ThreadLocal<Guard>();
    private static final class Guard{final Job job;final BuildPos pos;Guard(Job job,BuildPos pos){this.job=job;this.pos=pos;}}
    private static final LinkedHashMap<UUID,Job> jobs=new LinkedHashMap<UUID,Job>();
    private static final java.util.concurrent.ArrayBlockingQueue<Pending> commands=new java.util.concurrent.ArrayBlockingQueue<Pending>(128);private static int turn;
    public static int lastScans,lastAttempts;
    private static final class Pending{final EntityPlayerMP player;final BuildCommand command;Pending(EntityPlayerMP p,BuildCommand c){player=p;command=c;}}
    private static final class Job{
        final EntityPlayerMP player;final World world;final String token;final BuildShape shape;final ItemStack material;final int side;final BuilderCarpenters.Template carp;
        final ArrayList<BuildPos> points=new ArrayList<BuildPos>();int index,placed,skipped;boolean prepared;
        Job(EntityPlayerMP p,ItemStack tool,BuildShape shape,ItemStack material,int side){player=p;world=p.worldObj;token=BuilderData.token(tool);this.shape=shape;this.material=material;this.side=side;carp=BuilderCarpenters.read(tool,world);if(BuilderCarpenters.supported(Block.getBlockFromItem(material.getItem()))&&carp==null)throw new IllegalArgumentException("Select the Carpenter's block again to save its shape and covers.");}
    }
    public static void enqueue(EntityPlayerMP p,BuildCommand command){if(p!=null&&!loggedOut.contains(p))commands.offer(new Pending(p,command));}
    public static boolean active(EntityPlayer p){return jobs.containsKey(p.getUniqueID());}
    public static void sync(EntityPlayerMP p){p.inventory.markDirty();p.inventoryContainer.detectAndSendChanges();}
    public static void message(EntityPlayer p,String text){p.addChatMessage(new ChatComponentText("Building Stick: "+text));}
    public static void cancel(EntityPlayerMP p,boolean announce){Job job=jobs.remove(p.getUniqueID());if(announce&&job!=null)message(p,"Stopped. Placed "+job.placed+" blocks.");}
    public static void clear(){jobs.clear();commands.clear();loggedOut.clear();turn=0;}
    public static boolean loaded(World w,int x,int y,int z){return y>=0&&y<w.getHeight()&&w.getChunkProvider().chunkExists(x>>4,z>>4);}
    private static boolean margin(World w,BuildPos pos){if(!loaded(w,pos.x,pos.y,pos.z))return false;for(int x=(pos.x-2)>>4;x<=((pos.x+2)>>4);x++)for(int z=(pos.z-2)>>4;z<=((pos.z+2)>>4);z++)if(!w.getChunkProvider().chunkExists(x,z))return false;return true;}
    private static boolean nearby(EntityPlayer p,BuildPos point){return p.getDistanceSq(point.x+.5,point.y+.5,point.z+.5)<=64;}
    public static void click(EntityPlayerMP p,ItemStack tool,int x,int y,int z,int side){
        if(!BuilderData.held(p)||tool!=p.getHeldItem()||side<0||side>5||!loaded(p.worldObj,x,y,z)||!nearby(p,new BuildPos(x,y,z)))return;
        if(p.isSneaking()){
            Block block=p.worldObj.getBlock(x,y,z);Item item=Item.getItemFromBlock(block);ItemStack selected=item==null?null:new ItemStack(item,1,block.damageDropped(p.worldObj.getBlockMetadata(x,y,z)));
            ItemStack liquid=BuilderLiquids.pick(block);if(liquid!=null)selected=liquid;
            net.minecraft.nbt.NBTTagCompound carpenter=BuilderCarpenters.capture(p.worldObj,x,y,z);
            if(!BuilderData.ordinary(selected)&&!BuilderLiquids.is(selected)&&carpenter==null){message(p,"Choose a building block, water or lava; decorative Carpenter's Blocks are supported; containers are excluded.");return;}
            cancel(p,false);BuilderData.token(tool);BuilderData.material(tool,selected);if(carpenter!=null)BuilderData.tag(tool).setTag(BuilderCarpenters.TAG,carpenter);sync(p);message(p,"Material: "+BuilderCarpenters.label(tool,selected));return;
        }
        Block hit=p.worldObj.getBlock(x,y,z);ForgeDirection face=ForgeDirection.getOrientation(side);BuildPos point=hit.isReplaceable(p.worldObj,x,y,z)?new BuildPos(x,y,z):new BuildPos(x+face.offsetX,y+face.offsetY,z+face.offsetZ);
        point(p,tool,point,side);
    }
    private static void point(EntityPlayerMP p,ItemStack tool,BuildPos point,int side){
        if(active(p)){message(p,"A build is running. Sneak-right-click air to stop it.");return;}
        ItemStack material=BuilderData.material(tool);if(material==null){message(p,"Sneak-right-click a building block to pick its material.");return;}
        if(!loaded(p.worldObj,point.x,point.y,point.z))return;
        BuildPos a=BuilderData.anchor(p,tool);BuildMode mode=BuilderData.mode(tool);
        if(a==null&&mode!=BuildMode.SINGLE){BuilderData.anchor(p,tool,point,side);sync(p);message(p,"Start selected. Right-click the end point; sneak-right-click air cancels.");return;}
        if(a==null)a=point;
        try{
            BuildShape shape=new BuildShape(mode,BuilderData.hollow(tool),a,point);if(jobs.size()>=MAX_JOBS){message(p,"Other builds are busy. Try again shortly.");return;}
            int face=BuilderData.anchor(p,tool)==null?side:BuilderData.tag(tool).getByte("BuildSide");jobs.put(p.getUniqueID(),new Job(p,tool,shape,material,face));BuilderData.clear(tool);sync(p);message(p,"Preparing "+mode.label.toLowerCase(Locale.ROOT)+"...");
        }catch(IllegalArgumentException e){message(p,e.getMessage());}
    }
    private static void command(Pending request){EntityPlayerMP p=request.player;BuildCommand c=request.command;if(p==null||p.isDead||loggedOut.contains(p)||!BuilderData.held(p)||c.slot!=p.inventory.currentItem||c.slot>8||c.mode<0||c.mode>=BuildMode.values().length)return;ItemStack tool=p.getHeldItem();
        if(c.action==BuildCommand.CONFIGURE){if(BuilderData.mode(tool)==BuildMode.at(c.mode)&&BuilderData.hollow(tool)==c.hollow)return;cancel(p,false);BuilderData.tag(tool).setByte("BuildMode",(byte)c.mode);BuilderData.tag(tool).setBoolean("Hollow",c.hollow);BuilderData.clear(tool);sync(p);}
        else if(c.action==BuildCommand.CANCEL){cancel(p,true);BuilderData.clear(tool);sync(p);}
        else if(c.action==BuildCommand.AIR_END&&BuilderData.anchor(p,tool)!=null){BuildPos end=new BuildPos(c.x,c.y,c.z);if(!nearby(p,end)||!loaded(p.worldObj,end.x,end.y,end.z))return;Vec3 eye=Vec3.createVectorHelper(p.posX,p.posY+p.getEyeHeight(),p.posZ),target=Vec3.createVectorHelper(c.x+.5,c.y+.5,c.z+.5);MovingObjectPosition ray=p.worldObj.rayTraceBlocks(eye,target);if(ray!=null&&(ray.blockX!=c.x||ray.blockY!=c.y||ray.blockZ!=c.z))return;point(p,tool,end,BuilderData.tag(tool).getByte("BuildSide"));}
    }
    private static boolean current(Job j){return !j.player.isDead&&!loggedOut.contains(j.player)&&j.player.worldObj==j.world&&BuilderData.held(j.player)&&j.token.equals(BuilderData.tag(j.player.getHeldItem()).getString("BuildToken"));}
    private static int slot(EntityPlayer p,ItemStack material){for(int i=0;i<p.inventory.mainInventory.length;i++){ItemStack s=p.inventory.mainInventory[i];if(s!=null&&s.stackSize>0&&s.getItem()==material.getItem()&&s.getItemDamage()==material.getItemDamage()&&!s.hasTagCompound())return i;}return -1;}
    private static void refund(EntityPlayer p,ItemStack material){ItemStack back=material.copy();back.stackSize=1;if(!p.inventory.addItemStackToInventory(back))p.entityDropItem(back,0);}
    /** -1 = out of stock, 0 = skipped, 1 = paid placement. */
    private static int place(Job j,BuildPos pos){
        EntityPlayerMP p=j.player;World w=j.world;if(!margin(w,pos))return 0;if(j.carp!=null){placing.set(new Guard(j,pos));try{return j.carp.place(p,pos,j.side);}finally{placing.remove();}}if(BuilderLiquids.is(j.material)){placing.set(new Guard(j,pos));try{return BuilderLiquids.place(p,j.material,pos,j.side);}finally{placing.remove();}}Block before=w.getBlock(pos.x,pos.y,pos.z);int meta=w.getBlockMetadata(pos.x,pos.y,pos.z);
        if(w.getTileEntity(pos.x,pos.y,pos.z)!=null||!before.isReplaceable(w,pos.x,pos.y,pos.z)||!w.canMineBlock(p,pos.x,pos.y,pos.z)||MinecraftServer.getServer().isBlockProtected(w,pos.x,pos.y,pos.z,p)||!p.canPlayerEdit(pos.x,pos.y,pos.z,j.side,j.material))return 0;
        boolean creative=p.capabilities.isCreativeMode;int materialSlot=creative?-1:slot(p,j.material);if(!creative&&materialSlot<0)return -1;
        ItemStack use=j.material.copy();use.stackSize=1;if(!creative)p.inventory.decrStackSize(materialSlot,1);
        boolean used=false;placing.set(new Guard(j,pos));
        try{boolean accepted=use.tryPlaceItemIntoWorld(p,w,pos.x,pos.y,pos.z,j.side,.5F,.5F,.5F);boolean changed=w.getBlock(pos.x,pos.y,pos.z)!=before||w.getBlockMetadata(pos.x,pos.y,pos.z)!=meta;used=changed||!creative&&use.stackSize<1;return accepted&&used?1:used?1:0;}
        finally{placing.remove();if(!creative&&!used){boolean changed=!loaded(w,pos.x,pos.y,pos.z)||use.stackSize<1||w.getBlock(pos.x,pos.y,pos.z)!=before||w.getBlockMetadata(pos.x,pos.y,pos.z)!=meta;if(!changed)refund(p,j.material);}}
    }
    public static void step(){
        lastScans=lastAttempts=0;for(int i=0;i<16;i++){Pending c=commands.poll();if(c==null)break;command(c);}
        // Rotation keeps builds fair when the global budget is shared by several players.
        if(jobs.isEmpty())return;ArrayList<UUID> order=new ArrayList<UUID>(jobs.keySet());if(!order.isEmpty())Collections.rotate(order,-Math.floorMod(turn++,order.size()));
        for(UUID id:order){Job j=jobs.remove(id);if(j==null)continue;if(!current(j))continue;boolean finish=false;
            try{
                if(!j.prepared){int budget=Math.min(PER_JOB_SCAN,GLOBAL_SCAN-lastScans);while(budget-->0&&j.shape.hasNext()){BuildPos pos=j.shape.next();lastScans++;if(pos!=null)j.points.add(pos);if(j.points.size()>BuildShape.MAX_BLOCKS){message(j.player,"Shape exceeds the 4,096-block limit. No blocks were placed.");finish=true;break;}}if(!finish&&!j.shape.hasNext()){j.prepared=true;message(j.player,"Placing "+j.points.size()+" blocks...");}}
                if(j.prepared&&!finish){int budget=Math.min(PER_JOB_ATTEMPTS,GLOBAL_ATTEMPTS-lastAttempts);while(budget-->0&&j.index<j.points.size()){if(!current(j)){finish=true;break;}lastAttempts++;int result=place(j,j.points.get(j.index++));if(result<0){message(j.player,"Out of "+(j.carp==null?j.material.getDisplayName():"matching Carpenter block/cover materials")+". Placed "+j.placed+" blocks.");finish=true;break;}if(result>0)j.placed++;else j.skipped++;}sync(j.player);if(j.index==j.points.size()){message(j.player,"Placed "+j.placed+" blocks; skipped "+j.skipped+" occupied, protected or unloaded positions.");finish=true;}}
            }catch(RuntimeException e){dev.industrialtechnology.core.IndustrialTechnology.log.error("Building Stick operation stopped",e);message(j.player,"Build stopped because a block rejected placement.");finish=true;}
            if(!finish)jobs.put(id,j);
        }
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(event.phase==TickEvent.Phase.END)step();}
    @SubscribeEvent(priority=cpw.mods.fml.common.eventhandler.EventPriority.HIGHEST) public void guard(net.minecraftforge.event.world.BlockEvent.PlaceEvent event){
        Guard g=placing.get();if(g==null||event.world!=g.job.world||event.player!=g.job.player)return;
        java.util.List<net.minecraftforge.common.util.BlockSnapshot> snapshots=event instanceof net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent?((net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent)event).getReplacedBlockSnapshots():Collections.singletonList(event.blockSnapshot);
        for(net.minecraftforge.common.util.BlockSnapshot s:snapshots)if(s.x!=g.pos.x||s.y!=g.pos.y||s.z!=g.pos.z||s.world.getTileEntity(s.x,s.y,s.z)!=null&&(g.job.carp==null||!g.job.carp.matches(s.world,g.pos))){event.setCanceled(true);return;}
    }
    /** Carpenter's otherwise captures sneak-use and sends its own activation packet. Selection is read-only. */
    @SubscribeEvent(priority=cpw.mods.fml.common.eventhandler.EventPriority.HIGHEST) public void carpenterPick(net.minecraftforge.event.entity.player.PlayerInteractEvent event){
        if(event.world.isRemote||event.action!=net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK||event.useItem==cpw.mods.fml.common.eventhandler.Event.Result.DENY||!BuilderData.held(event.entityPlayer)||!event.entityPlayer.isSneaking()||!(event.entityPlayer instanceof EntityPlayerMP)||!loaded(event.world,event.x,event.y,event.z)||!BuilderCarpenters.supported(event.world.getBlock(event.x,event.y,event.z)))return;
        click((EntityPlayerMP)event.entityPlayer,event.entityPlayer.getHeldItem(),event.x,event.y,event.z,event.face);event.setCanceled(true);
    }
    @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent event){if(event.player.worldObj.isRemote)return;jobs.remove(event.player.getUniqueID());if(event.player instanceof EntityPlayerMP){loggedOut.add((EntityPlayerMP)event.player);commands.removeIf(c->c.player==event.player);}}
    @SubscribeEvent public void unload(WorldEvent.Unload event){if(!event.world.isRemote){Iterator<Job> it=jobs.values().iterator();while(it.hasNext())if(it.next().world==event.world)it.remove();}}
}
