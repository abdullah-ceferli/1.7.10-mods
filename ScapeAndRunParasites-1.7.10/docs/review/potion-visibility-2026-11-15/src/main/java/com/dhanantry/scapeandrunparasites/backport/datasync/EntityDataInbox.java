package com.dhanantry.scapeandrunparasites.backport.datasync;
import java.util.*;import net.minecraft.entity.Entity;import net.minecraft.world.World;
/** Network threads enqueue only. World/entity changes are applied by the client tick on its main thread. */
public final class EntityDataInbox {
    public interface RequestSender{void request(int dimension,int entityId);}
    private static final int MAX_PENDING=4096,EXPIRES=1200;
    private final ArrayDeque<EntityDataPacket> incoming=new ArrayDeque<EntityDataPacket>();
    private final Map<Key,Pending> pending=new LinkedHashMap<Key,Pending>();
    private final IdentityHashMap<Entity,Binding> bindings=new IdentityHashMap<Entity,Binding>();
    private final IdentityHashMap<Entity,Integer> requested=new IdentityHashMap<Entity,Integer>();
    private World previousWorld;private int ticks;
    public synchronized void enqueue(EntityDataPacket packet){if(incoming.size()>=MAX_PENDING)throw new IllegalStateException("Tracked-data receive queue overflow");incoming.add(packet);}
    public synchronized void clear(){incoming.clear();pending.clear();bindings.clear();requested.clear();previousWorld=null;ticks=0;}
    public synchronized int boundEntities(){return bindings.size();}public synchronized int pendingEntities(){return pending.size();}
    public synchronized void tick(World world,RequestSender requests){
        ticks++;if(world!=previousWorld){bindings.clear();requested.clear();previousWorld=world;}
        List<EntityDataPacket> queue; synchronized(this){queue=new ArrayList<EntityDataPacket>(incoming);incoming.clear();}
        for(EntityDataPacket packet:queue){Key key=new Key(packet);if(packet.kind==EntityDataPacket.REMOVE){pending.remove(key);if(world!=null&&world.provider.dimensionId==packet.dimension){Entity entity=world.getEntityByID(packet.entityId);Binding binding=bindings.get(entity);if(binding!=null&&binding.identity.equals(packet.identity))bindings.remove(entity);}continue;}
            Pending group=pending.get(key);if(group==null){if(pending.size()>=MAX_PENDING)throw new IllegalStateException("Tracked-data pending entity overflow");group=new Pending();pending.put(key,group);}group.lastTick=ticks;
            if(packet.kind==EntityDataPacket.FULL){if(group.full==null||packet.revision>=group.full.revision)group.full=packet;}
            else{group.deltas.put(packet.revision,packet);if(group.deltas.size()>256)throw new IllegalStateException("Too many tracked updates awaiting an entity snapshot");}
        }
        for(Iterator<Map.Entry<Key,Pending>> it=pending.entrySet().iterator();it.hasNext();){Map.Entry<Key,Pending> row=it.next();Key key=row.getKey();Pending group=row.getValue();
            if(ticks-group.lastTick>EXPIRES){it.remove();continue;}if(world==null||world.provider.dimensionId!=key.dimension)continue;Entity entity=world.getEntityByID(key.entityId);if(entity==null||!entity.getClass().getName().equals(key.entityClass))continue;
            EntityDataManager manager=EntityDataManager.existing(entity);if(manager==null||manager.isEmpty())continue;Binding binding=bindings.get(entity);
            if(group.full!=null){if(binding!=null&&!binding.identity.equals(key.identity))continue;if(binding==null)binding=new Binding(key.identity);if(group.full.revision>=binding.revision){manager.setEntryValues(group.full.values);binding.revision=group.full.revision;}bindings.put(entity,binding);group.full=null;}
            if(binding==null||!binding.identity.equals(key.identity))continue;
            for(EntityDataPacket delta:group.deltas.values())if(delta.revision>binding.revision){manager.setEntryValues(delta.values);binding.revision=delta.revision;}
            it.remove();
        }
        if(world==null)return;
        for(Iterator<Entity> it=bindings.keySet().iterator();it.hasNext();){Entity entity=it.next();if(entity.isDead||entity.worldObj!=world||world.getEntityByID(entity.getEntityId())!=entity)it.remove();}
        for(Iterator<Entity> it=requested.keySet().iterator();it.hasNext();){Entity entity=it.next();if(entity.isDead||entity.worldObj!=world||world.getEntityByID(entity.getEntityId())!=entity)it.remove();}
        for(Object value:world.loadedEntityList){Entity entity=(Entity)value;EntityDataManager manager=EntityDataManager.existing(entity);if(manager==null||manager.isEmpty()||bindings.containsKey(entity))continue;Integer last=requested.get(entity);if(last==null||ticks-last>=40){requests.request(world.provider.dimensionId,entity.getEntityId());requested.put(entity,ticks);}}
    }
    private static final class Binding{final UUID identity;long revision=-1;Binding(UUID identity){this.identity=identity;}}
    private static final class Pending{int lastTick;EntityDataPacket full;final TreeMap<Long,EntityDataPacket> deltas=new TreeMap<Long,EntityDataPacket>();}
    private static final class Key{final int dimension,entityId;final UUID identity;final String entityClass;Key(EntityDataPacket packet){dimension=packet.dimension;entityId=packet.entityId;identity=packet.identity;entityClass=packet.entityClass;}public boolean equals(Object other){if(!(other instanceof Key))return false;Key key=(Key)other;return dimension==key.dimension&&entityId==key.entityId&&identity.equals(key.identity)&&entityClass.equals(key.entityClass);}public int hashCode(){return Objects.hash(dimension,entityId,identity,entityClass);}}
}
