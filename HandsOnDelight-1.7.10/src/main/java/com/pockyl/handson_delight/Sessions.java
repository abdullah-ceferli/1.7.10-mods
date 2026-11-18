package com.pockyl.handson_delight;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.security.SecureRandom;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vectorwing.farmersdelight.legacy.*;

public final class Sessions {
    private static final Map<UUID,Session> OPEN=new HashMap<UUID,Session>();
    private static final SecureRandom RANDOM=new SecureRandom();
    private static final ArrayBlockingQueue<Runnable> QUEUE=new ArrayBlockingQueue<Runnable>(256);
    private static boolean checkingPermission;
    public static void clearServer(){OPEN.clear();QUEUE.clear();checkingPermission=false;}
    public static final class Session {
        public final long token;public final EntityPlayerMP player;public final InventoryTile tile;public final int face;
        long lastAction=-1000,lastStir=-1000;
        Session(EntityPlayerMP p,InventoryTile t,int face){token=RANDOM.nextLong();player=p;tile=t;this.face=face;}
    }
    public static void enqueue(final EntityPlayerMP p,final Packets.Action a){
        QUEUE.offer(new Runnable(){public void run(){handle(p,a);}});
    }
    @SubscribeEvent(priority=EventPriority.LOWEST) public void interact(PlayerInteractEvent e){
        // Minecraft checks the client event before it sends the native use-block packet.
        // Only intercept on the server, where the authorized station screen is opened.
        if(e.world.isRemote||checkingPermission||e.isCanceled()||e.useBlock==Event.Result.DENY||e.action!=PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)return;
        TileEntity t=e.world.getTileEntity(e.x,e.y,e.z);if(!StationHooks.supported(t))return;
        if(e.entityPlayer.isSneaking()&&(!(t instanceof CuttingBoardTile)||StationHooks.state(t).layers.isEmpty()))return;
        e.setCanceled(true);
        if(!e.world.isRemote&&e.entityPlayer instanceof EntityPlayerMP)open((EntityPlayerMP)e.entityPlayer,(InventoryTile)t,e.face);
    }
    public static Session open(EntityPlayerMP p,InventoryTile t,int face){
        if(!valid(p,t))return null;
        for(Session other:OPEN.values())if(other.tile==t&&other.player!=p){p.addChatMessage(new net.minecraft.util.ChatComponentText("Someone is already using this station."));return null;}
        p.closeScreen();Session s=new Session(p,t,face);OPEN.put(p.getUniqueID(),s);
        StationState state=StationHooks.state(t);if(t instanceof CookingPotTile&&t.getWorldObj().getTotalWorldTime()-state.attended>40){state.work=((CookingPotTile)t).progress;state.pace=1;}
        state.attended=t.getWorldObj().getTotalWorldTime();t.changed();
        StationHooks.syncPractice(p);
        HandsOnDelight.NETWORK.sendTo(new Packets.Open(s.token,p.dimension,t.xCoord,t.yCoord,t.zCoord),p);return s;
    }
    public static boolean valid(EntityPlayer p,InventoryTile t){
        return p!=null&&!p.isDead&&p.getHealth()>0&&p.worldObj==t.getWorldObj()&&!t.isInvalid()&&p.worldObj.getTileEntity(t.xCoord,t.yCoord,t.zCoord)==t&&p.getDistanceSq(t.xCoord+0.5,t.yCoord+0.5,t.zCoord+0.5)<=64;
    }
    private static boolean allowed(Session s){
        EntityPlayerMP p=s.player;InventoryTile t=s.tile;
        if(!p.canPlayerEdit(t.xCoord,t.yCoord,t.zCoord,s.face,p.getHeldItem())||!p.worldObj.canMineBlock(p,t.xCoord,t.yCoord,t.zCoord))return false;
        PlayerInteractEvent check=new PlayerInteractEvent(p,PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,t.xCoord,t.yCoord,t.zCoord,s.face,p.worldObj);
        checkingPermission=true;try{MinecraftForge.EVENT_BUS.post(check);}finally{checkingPermission=false;}
        return !check.isCanceled()&&check.useBlock!=Event.Result.DENY;
    }
    public static void handle(EntityPlayerMP p,Packets.Action a){
        Session s=OPEN.get(p.getUniqueID());if(s==null||a.token!=s.token)return;
        if(a.action==Actions.CLOSE){returnCursor(p);OPEN.remove(p.getUniqueID());return;}
        if(!valid(p,s.tile)){returnCursor(p);OPEN.remove(p.getUniqueID());return;}
        long now=p.worldObj.getTotalWorldTime();
        if(a.action==Actions.STIR){if(s.lastStir==now||!Float.isFinite(a.x)||!Float.isFinite(a.z)||!Float.isFinite(a.clock))return;s.lastStir=now;}
        else {if(now-s.lastAction<(a.action==Actions.CUT?3:4))return;s.lastAction=now;}
        if(!allowed(s))return;
        InventoryTile t=s.tile;
        try{
            switch(a.action){
                case Actions.NEIGHBOR:
                    if(p.inventory.getItemStack()!=null||Math.abs(a.a)>3||Math.abs(a.b)>3||!Float.isFinite(a.x)||a.x!=Math.rint(a.x)||Math.abs(a.x)>1)return;
                    TileEntity next=p.worldObj.getTileEntity(t.xCoord+a.a,t.yCoord+(int)a.x,t.zCoord+a.b);if(!StationHooks.supported(next)||next==t||!valid(p,(InventoryTile)next))return;
                    Session target=new Session(p,(InventoryTile)next,s.face);if(allowed(target))open(p,(InventoryTile)next,s.face);return;
                case Actions.INSERT:Actions.insert(t,p,a.a,a.b,(a.x>0.5F));break;
                case Actions.TAKE:Actions.take(t,p,a.a);break;
                case Actions.CUT:if(t instanceof CuttingBoardTile&&a.b==StationHooks.state(t).cuts)Actions.cut((CuttingBoardTile)t,p,a.a);break;
                case Actions.TURN:if(t instanceof GrillingTile)StationHooks.turn((GrillingTile)t,a.a);break;
                case Actions.STIR:if(t instanceof CookingPotTile)StationHooks.stir((CookingPotTile)t,p,a.x,a.z,a.clock);break;
                case Actions.FILL:boolean success=Actions.fill(t,p,a.recipe,a.x>.5F);HandsOnDelight.NETWORK.sendTo(new Packets.Filled(s.token,a.recipe,success),p);break;
                case Actions.ASSEMBLE:if(t instanceof CuttingBoardTile)RecipeBook.assemble((CuttingBoardTile)t,p);break;
                case Actions.CARVE:if(t instanceof CuttingBoardTile)Actions.carve((CuttingBoardTile)t,p,a.a);break;
                case Actions.SERVE:if(t instanceof CookingPotTile)Actions.serve((CookingPotTile)t,p,a.a,a.x>.5F);break;
                default:return;
            }
            p.inventory.markDirty();p.inventoryContainer.detectAndSendChanges();p.playerNetServerHandler.sendPacket(new net.minecraft.network.play.server.S2FPacketSetSlot(-1,-1,p.inventory.getItemStack()));
        }catch(RuntimeException ex){returnCursor(p);OPEN.remove(p.getUniqueID());HandsOnDelight.log.error("Station action failed",ex);p.addChatMessage(new net.minecraft.util.ChatComponentText("Cooking interaction stopped. See the game log."));}
    }
    private static void returnCursor(EntityPlayerMP p){
        net.minecraft.item.ItemStack held=p.inventory.getItemStack();if(held==null)return;p.inventory.setItemStack(null);
        if(!p.inventory.addItemStackToInventory(held)&&held.stackSize>0)p.dropPlayerItemWithRandomChoice(held,false);
        p.inventoryContainer.detectAndSendChanges();p.playerNetServerHandler.sendPacket(new net.minecraft.network.play.server.S2FPacketSetSlot(-1,-1,null));
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e){
        if(e.phase!=TickEvent.Phase.START)return;Runnable r;int n=0;while(n++<256&&(r=QUEUE.poll())!=null)r.run();
        Iterator<Session> it=OPEN.values().iterator();while(it.hasNext()){
            Session s=it.next();if(!valid(s.player,s.tile)){returnCursor(s.player);it.remove();continue;}StationHooks.state(s.tile).attended=s.tile.getWorldObj().getTotalWorldTime();
        }
    }
    @SubscribeEvent public void stopped(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e){OPEN.remove(e.player.getUniqueID());}
}
