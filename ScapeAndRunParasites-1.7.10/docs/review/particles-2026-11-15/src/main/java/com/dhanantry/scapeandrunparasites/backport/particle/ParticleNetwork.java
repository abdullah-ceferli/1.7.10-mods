package com.dhanantry.scapeandrunparasites.backport.particle;
import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.network.NetworkRegistry;import cpw.mods.fml.common.network.simpleimpl.*;import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.world.WorldServer;
import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
/** No client classes are linked by server initialization; Netty handlers only enqueue decoded packets. */
public final class ParticleNetwork {
    private static SimpleNetworkWrapper channel;
    public static final ConcurrentLinkedQueue<ParticleBatch> INBOX=new ConcurrentLinkedQueue<ParticleBatch>();
    public static synchronized void initialize() {
        if(channel!=null)return;
        channel=NetworkRegistry.INSTANCE.newSimpleChannel("SRPParticles17");channel.registerMessage(Handler.class,ParticleBatch.class,0,Side.CLIENT);
        if(FMLCommonHandler.instance().getSide().isClient())try {
            Class.forName("com.dhanantry.scapeandrunparasites.backport.particle.client.ClientParticles").getMethod("initialize").invoke(null);
        }catch(ReflectiveOperationException problem){throw new IllegalStateException("Could not initialize native particle client",problem);}
    }
    static void send(WorldServer world,ParticleBatch batch,double x,double y,double z) {
        if(channel==null)throw new IllegalStateException("Particle network is not initialized");
        for(Object player:world.playerEntities)if(player instanceof EntityPlayerMP) {
            EntityPlayerMP target=(EntityPlayerMP)player;BlockPos pos=EntityQueries.position(target);
            if(ParticleBatch.inRange(pos.getX(),pos.getY(),pos.getZ(),x,y,z,batch.longDistance))channel.sendTo(batch,target);
        }
    }
    public static final class Handler implements IMessageHandler<ParticleBatch,IMessage> {
        @Override public IMessage onMessage(ParticleBatch batch,MessageContext context){INBOX.add(batch);return null;}
    }
}
