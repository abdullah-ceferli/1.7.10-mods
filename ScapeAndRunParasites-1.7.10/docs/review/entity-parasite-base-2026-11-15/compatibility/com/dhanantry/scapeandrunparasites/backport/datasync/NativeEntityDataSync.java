package com.dhanantry.scapeandrunparasites.backport.datasync;
import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;import java.util.concurrent.atomic.AtomicInteger;import java.lang.ref.WeakReference;
import io.netty.buffer.ByteBuf;import cpw.mods.fml.common.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;import cpw.mods.fml.common.network.simpleimpl.*;import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.network.Packet;import net.minecraft.world.*;
import net.minecraftforge.common.MinecraftForge;import net.minecraftforge.event.entity.player.PlayerEvent;
public final class NativeEntityDataSync {
    public static final EntityDataInbox CLIENT_INBOX=new EntityDataInbox();
    private static SimpleNetworkWrapper channel;private static final ConcurrentLinkedQueue<PendingRequest> requests=new ConcurrentLinkedQueue<PendingRequest>();private static final AtomicInteger queued=new AtomicInteger();
    private static final Map<String,Long> lastRequest=new HashMap<String,Long>();private static long ticks;
    public static synchronized void initialize(){if(channel!=null)return;channel=NetworkRegistry.INSTANCE.newSimpleChannel("SRPEntityData");channel.registerMessage(ClientHandler.class,EntityDataPacket.class,0,Side.CLIENT);channel.registerMessage(RequestHandler.class,ResyncRequest.class,1,Side.SERVER);NativeEntityDataSync hooks=new NativeEntityDataSync();MinecraftForge.EVENT_BUS.register(hooks);FMLCommonHandler.instance().bus().register(hooks);}
    public static Packet packetFor(EntityDataPacket message){if(channel==null)throw new IllegalStateException("Tracked-data channel is not initialized");return channel.getPacketFrom(message);}
    public static void request(int dimension,int entityId){if(channel!=null)channel.sendToServer(new ResyncRequest(dimension,entityId));}
    private static void full(Entity entity,EntityPlayerMP player){EntityDataManager manager=EntityDataManager.existing(entity);if(manager!=null&&!manager.isEmpty())channel.sendTo(new EntityDataPacket(entity,manager,EntityDataPacket.FULL,manager.getAll()),player);}
    @SubscribeEvent public void start(PlayerEvent.StartTracking event){if(event.entityPlayer instanceof EntityPlayerMP)full(event.target,(EntityPlayerMP)event.entityPlayer);}
    @SubscribeEvent public void stop(PlayerEvent.StopTracking event){EntityDataManager manager=EntityDataManager.existing(event.target);if(manager!=null&&!manager.isEmpty()&&event.entityPlayer instanceof EntityPlayerMP)channel.sendTo(new EntityDataPacket(event.target,manager,EntityDataPacket.REMOVE,null),(EntityPlayerMP)event.entityPlayer);}
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(event.phase!=TickEvent.Phase.END)return;ticks++;
        PendingRequest request;while((request=requests.poll())!=null){queued.decrementAndGet();EntityPlayerMP player=request.player.get();if(player==null||player.isDead||player.worldObj==null||player.worldObj.provider.dimensionId!=request.dimension)continue;
            Entity entity=player.worldObj.getEntityByID(request.entityId);if(entity==null)continue;WorldServer world=(WorldServer)player.worldObj;if(entity!=player&&!world.getEntityTracker().getTrackingPlayers(entity).contains(player))continue;
            String token=player.getUniqueID()+":"+request.entityId;Long previous=lastRequest.get(token);if(previous!=null&&ticks-previous<20)continue;lastRequest.put(token,ticks);full(entity,player);
        }
        if(ticks%200==0)for(Iterator<Long> it=lastRequest.values().iterator();it.hasNext();)if(ticks-it.next()>1200)it.remove();
        for(EntityDataManager manager:EntityDataManager.activeManagers()){Entity entity=manager.owner();if(entity==null||entity.isDead||!(entity.worldObj instanceof WorldServer)||!manager.isDirty())continue;List<EntityDataManager.DataEntry<?>> dirty=manager.getDirty();if(dirty==null)continue;EntityDataPacket packet=new EntityDataPacket(entity,manager,EntityDataPacket.DELTA,dirty);for(net.minecraft.entity.player.EntityPlayer player:((WorldServer)entity.worldObj).getEntityTracker().getTrackingPlayers(entity))if(player instanceof EntityPlayerMP)channel.sendTo(packet,(EntityPlayerMP)player);}
    }
    public static final class ClientHandler implements IMessageHandler<EntityDataPacket,IMessage>{public IMessage onMessage(EntityDataPacket message,MessageContext context){CLIENT_INBOX.enqueue(message);return null;}}
    public static final class ResyncRequest implements IMessage{public int dimension,entityId;public ResyncRequest(){}ResyncRequest(int dimension,int id){this.dimension=dimension;entityId=id;}public void toBytes(ByteBuf b){b.writeInt(dimension);b.writeInt(entityId);}public void fromBytes(ByteBuf b){dimension=b.readInt();entityId=b.readInt();if(b.isReadable())throw new IllegalArgumentException("Trailing tracked-data request bytes");}}
    public static final class RequestHandler implements IMessageHandler<ResyncRequest,IMessage>{public IMessage onMessage(ResyncRequest message,MessageContext context){if(queued.incrementAndGet()>2048){queued.decrementAndGet();return null;}requests.add(new PendingRequest(context.getServerHandler().playerEntity,message.dimension,message.entityId));return null;}}
    private static final class PendingRequest{final WeakReference<EntityPlayerMP> player;final int dimension,entityId;PendingRequest(EntityPlayerMP player,int dimension,int id){this.player=new WeakReference<EntityPlayerMP>(player);this.dimension=dimension;entityId=id;}}
}
