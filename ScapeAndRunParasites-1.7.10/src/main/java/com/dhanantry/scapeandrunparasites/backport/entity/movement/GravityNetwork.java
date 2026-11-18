package com.dhanantry.scapeandrunparasites.backport.entity.movement;
import java.lang.ref.WeakReference;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;import java.util.concurrent.atomic.AtomicInteger;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.*;import cpw.mods.fml.common.network.*;import cpw.mods.fml.common.network.simpleimpl.*;import cpw.mods.fml.relauncher.Side;import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.world.WorldServer;import net.minecraftforge.common.MinecraftForge;
public final class GravityNetwork {
    public static final GravityInbox INBOX=new GravityInbox();private static SimpleNetworkWrapper channel;
    private static final ConcurrentLinkedQueue<Request> requests=new ConcurrentLinkedQueue<Request>();private static final AtomicInteger queued=new AtomicInteger();private static final Map<String,Long> recent=new HashMap<String,Long>();private static long ticks;
    public static synchronized void initialize(){
        if(channel!=null)return;channel=NetworkRegistry.INSTANCE.newSimpleChannel("SRPGravity17");channel.registerMessage(ClientHandler.class,GravityPacket.class,0,Side.CLIENT);channel.registerMessage(RequestHandler.class,Resync.class,1,Side.SERVER);
        GravityNetwork events=new GravityNetwork();MinecraftForge.EVENT_BUS.register(events);FMLCommonHandler.instance().bus().register(events);
        if(FMLCommonHandler.instance().getSide().isClient())try{Class.forName("com.dhanantry.scapeandrunparasites.backport.entity.movement.client.GravityClient").getMethod("initialize").invoke(null);}catch(ReflectiveOperationException e){throw new IllegalStateException("Gravity client initialization failed",e);}
    }
    public static void changed(Entity e){
        if(channel==null||!(e.worldObj instanceof WorldServer))return;GravityPacket packet=new GravityPacket(e,GravityPacket.DELTA);
        for(net.minecraft.entity.player.EntityPlayer p:((WorldServer)e.worldObj).getEntityTracker().getTrackingPlayers(e))if(p instanceof EntityPlayerMP)channel.sendTo(packet,(EntityPlayerMP)p);
        if(e instanceof EntityPlayerMP)channel.sendTo(packet,(EntityPlayerMP)e);
    }
    private static void full(Entity e,EntityPlayerMP player){if(channel!=null)channel.sendTo(new GravityPacket(e,GravityPacket.FULL),player);}
    public static void request(int dimension,int id){if(channel!=null)channel.sendToServer(new Resync(dimension,id));}
    @SubscribeEvent public void start(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking e){if(e.entityPlayer instanceof EntityPlayerMP)full(e.target,(EntityPlayerMP)e.entityPlayer);}
    @SubscribeEvent public void stop(net.minecraftforge.event.entity.player.PlayerEvent.StopTracking e){if(channel!=null&&e.entityPlayer instanceof EntityPlayerMP)channel.sendTo(new GravityPacket(e.target,GravityPacket.REMOVE),(EntityPlayerMP)e.entityPlayer);}
    @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent e){if(e.player instanceof EntityPlayerMP)full(e.player,(EntityPlayerMP)e.player);}
    @SubscribeEvent public void dimension(PlayerEvent.PlayerChangedDimensionEvent e){if(e.player instanceof EntityPlayerMP)full(e.player,(EntityPlayerMP)e.player);}
    @SubscribeEvent public void respawn(PlayerEvent.PlayerRespawnEvent e){if(e.player instanceof EntityPlayerMP)full(e.player,(EntityPlayerMP)e.player);}
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e){
        if(e.phase!=TickEvent.Phase.END)return;ticks++;Request r;
        while((r=requests.poll())!=null){queued.decrementAndGet();EntityPlayerMP player=r.player.get();if(player==null||player.isDead||!(player.worldObj instanceof WorldServer)||player.worldObj.provider.dimensionId!=r.dimension)continue;
            Entity target=player.worldObj.getEntityByID(r.id);if(target==null)continue;WorldServer world=(WorldServer)player.worldObj;
            if(target!=player&&!world.getEntityTracker().getTrackingPlayers(target).contains(player))continue;
            String key=player.getUniqueID()+":"+r.id;Long previous=recent.get(key);if(previous!=null&&ticks-previous<20)continue;recent.put(key,ticks);full(target,player);
        }
        if(ticks%200==0)for(Iterator<Long> it=recent.values().iterator();it.hasNext();)if(ticks-it.next()>1200)it.remove();
    }
    public static final class ClientHandler implements IMessageHandler<GravityPacket,IMessage>{public IMessage onMessage(GravityPacket p,MessageContext c){INBOX.enqueue(p);return null;}}
    public static final class Resync implements IMessage{
        int dimension,id;public Resync(){}Resync(int d,int i){dimension=d;id=i;}
        public void toBytes(ByteBuf b){b.writeInt(dimension);b.writeInt(id);}public void fromBytes(ByteBuf b){dimension=b.readInt();id=b.readInt();if(b.isReadable())throw new IllegalArgumentException("Trailing gravity request");}
    }
    public static final class RequestHandler implements IMessageHandler<Resync,IMessage>{
        public IMessage onMessage(Resync p,MessageContext c){if(queued.incrementAndGet()>2048){queued.decrementAndGet();return null;}requests.add(new Request(c.getServerHandler().playerEntity,p.dimension,p.id));return null;}
    }
    private static final class Request{final WeakReference<EntityPlayerMP> player;final int dimension,id;Request(EntityPlayerMP p,int d,int i){player=new WeakReference<EntityPlayerMP>(p);dimension=d;id=i;}}
}
