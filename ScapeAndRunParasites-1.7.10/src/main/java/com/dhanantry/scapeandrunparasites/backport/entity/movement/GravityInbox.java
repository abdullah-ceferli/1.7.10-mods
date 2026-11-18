package com.dhanantry.scapeandrunparasites.backport.entity.movement;
import java.util.*;import net.minecraft.entity.Entity;import net.minecraft.world.World;
/** Main-thread application and resynchronization handle snapshots arriving before native spawn packets. */
public final class GravityInbox {
    private final ArrayDeque<GravityPacket> incoming=new ArrayDeque<GravityPacket>();
    private final Map<Key,Pending> pending=new LinkedHashMap<Key,Pending>();
    private final IdentityHashMap<Entity,Binding> bindings=new IdentityHashMap<Entity,Binding>();
    private final IdentityHashMap<Entity,Integer> requested=new IdentityHashMap<Entity,Integer>();
    private World previous;private int ticks;
    public interface RequestSender{void request(int dimension,int entityId);}
    public synchronized void enqueue(GravityPacket packet){if(incoming.size()>=4096)throw new IllegalStateException("Gravity inbox overflow");incoming.add(packet);}
    public synchronized void clear(){incoming.clear();pending.clear();bindings.clear();requested.clear();previous=null;ticks=0;}
    public synchronized int boundEntities(){return bindings.size();}
    public synchronized void tick(World world,RequestSender requests){
        ticks++;if(world!=previous){bindings.clear();requested.clear();previous=world;}
        while(!incoming.isEmpty()){
            GravityPacket packet=incoming.remove();Key key=new Key(packet);
            if(packet.kind==GravityPacket.REMOVE){pending.remove(key);if(world!=null&&world.provider.dimensionId==packet.dimension){Entity e=world.getEntityByID(packet.entityId);Binding b=bindings.get(e);if(b!=null&&b.identity.equals(packet.identity)){bindings.remove(e);requested.remove(e);}}continue;}
            Pending p=pending.get(key);if(p==null){if(pending.size()>=4096)throw new IllegalStateException("Gravity pending overflow");p=new Pending();pending.put(key,p);}p.tick=ticks;
            if(packet.kind==GravityPacket.FULL){if(p.full==null||packet.revision>=p.full.revision)p.full=packet;}
            else if(p.delta==null||packet.revision>=p.delta.revision)p.delta=packet;
        }
        for(Iterator<Map.Entry<Key,Pending>> it=pending.entrySet().iterator();it.hasNext();){
            Map.Entry<Key,Pending> row=it.next();Key key=row.getKey();Pending p=row.getValue();
            if(ticks-p.tick>1200){it.remove();continue;}if(world==null||world.provider.dimensionId!=key.dimension)continue;
            Entity e=world.getEntityByID(key.id);if(e==null||!(key.type.equals(net.minecraft.entity.player.EntityPlayer.class.getName())?e instanceof net.minecraft.entity.player.EntityPlayer:e.getClass().getName().equals(key.type)))continue;
            Binding b=bindings.get(e);if(b!=null&&!b.identity.equals(key.identity))continue;
            if(p.full!=null){if(b==null)b=new Binding(key.identity);if(p.full.revision>=b.revision){GravityAccess.receive(e,p.full.noGravity,p.full.revision);b.revision=p.full.revision;}bindings.put(e,b);}
            if(b==null)continue;if(p.delta!=null&&p.delta.revision>b.revision){GravityAccess.receive(e,p.delta.noGravity,p.delta.revision);b.revision=p.delta.revision;}
            it.remove();
        }
        for(Iterator<Entity> it=bindings.keySet().iterator();it.hasNext();){Entity e=it.next();if(e.isDead||e.worldObj!=world||world==null||world.getEntityByID(e.getEntityId())!=e)it.remove();}
        for(Iterator<Entity> it=requested.keySet().iterator();it.hasNext();){Entity e=it.next();if(e.isDead||e.worldObj!=world||world==null||world.getEntityByID(e.getEntityId())!=e)it.remove();}
        if(world==null)return;
        for(Object o:world.loadedEntityList){Entity e=(Entity)o;if(bindings.containsKey(e))continue;Integer last=requested.get(e);if(last==null||ticks-last>=40){requests.request(world.provider.dimensionId,e.getEntityId());requested.put(e,ticks);}}
    }
    private static final class Pending{GravityPacket full,delta;int tick;}
    private static final class Binding{final UUID identity;long revision=-1;Binding(UUID id){identity=id;}}
    private static final class Key{
        final int dimension,id;final UUID identity;final String type;
        Key(GravityPacket p){dimension=p.dimension;id=p.entityId;identity=p.identity;type=p.entityClass;}
        @Override public int hashCode(){return Objects.hash(dimension,id,identity,type);}
        @Override public boolean equals(Object o){if(!(o instanceof Key))return false;Key k=(Key)o;return dimension==k.dimension&&id==k.id&&identity.equals(k.identity)&&type.equals(k.type);}
    }
}
