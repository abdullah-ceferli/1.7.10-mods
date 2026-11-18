package com.dhanantry.scapeandrunparasites.backport.datasync;
import java.io.IOException;import java.lang.ref.WeakReference;import java.lang.ref.ReferenceQueue;import java.lang.reflect.*;import java.util.*;
import net.minecraft.entity.Entity;import net.minecraft.network.PacketBuffer;
/** Modern tracked values live beside the native DataWatcher, so its 5-bit ID space is never reused. */
public final class EntityDataManager {
    private static final Map<Class<? extends Entity>,Integer> LAST_ID=new HashMap<Class<? extends Entity>,Integer>();
    private static final Map<EntityRef,EntityDataManager> MANAGERS=new HashMap<EntityRef,EntityDataManager>();
    private static final ReferenceQueue<Entity> COLLECTED=new ReferenceQueue<Entity>();
    private static final class EntityRef extends WeakReference<Entity>{
        private final int hash;EntityRef(Entity entity,ReferenceQueue<Entity> queue){super(entity,queue);hash=System.identityHashCode(entity);}
        @Override public int hashCode(){return hash;}
        @Override public boolean equals(Object value){return this==value||value instanceof EntityRef&&get()!=null&&get()==((EntityRef)value).get();}
    }
    private static void discardCollected(){EntityRef reference;while((reference=(EntityRef)COLLECTED.poll())!=null)MANAGERS.remove(reference);}
    private static final Map<Class<?>,java.util.Optional<Method>> CALLBACKS=new HashMap<Class<?>,java.util.Optional<Method>>();
    private final WeakReference<Entity> owner;private final Map<Integer,DataEntry<?>> entries=new TreeMap<Integer,DataEntry<?>>();private boolean dirty;private long revision;
    public EntityDataManager(Entity owner){this.owner=new WeakReference<Entity>(owner);}
    public static EntityDataManager of(Entity entity){if(entity==null)throw new NullPointerException("entity");synchronized(MANAGERS){discardCollected();EntityRef reference=new EntityRef(entity,COLLECTED);EntityDataManager manager=MANAGERS.get(reference);if(manager==null){manager=new EntityDataManager(entity);MANAGERS.put(reference,manager);}return manager;}}
    public static EntityDataManager existing(Entity entity){synchronized(MANAGERS){discardCollected();return MANAGERS.get(new EntityRef(entity,null));}}
    public static List<EntityDataManager> activeManagers(){synchronized(MANAGERS){discardCollected();return new ArrayList<EntityDataManager>(MANAGERS.values());}}
    public Entity owner(){return owner.get();}public synchronized long revision(){return revision;}
    public static <T> DataParameter<T> createKey(Class<? extends Entity> type,DataSerializer<T> serializer){
        synchronized(LAST_ID){Integer previous=LAST_ID.get(type);int id=0;if(previous!=null)id=previous+1;else for(Class<?> p=type.getSuperclass();p!=null&&Entity.class.isAssignableFrom(p);p=p.getSuperclass()){Integer inherited=LAST_ID.get(p);if(inherited!=null){id=inherited+1;break;}}
            if(id>254)throw new IllegalArgumentException("Tracked data exceeds 254 inherited IDs for "+type.getName());LAST_ID.put(type,id);return new DataParameter<T>(id,serializer,type);}
    }
    @SuppressWarnings({"unchecked","rawtypes"}) public synchronized void register(DataParameter<?> key,Object value){
        if(entries.containsKey(key.getId()))throw new IllegalArgumentException("Duplicate tracked data ID "+key.getId());
        if(DataSerializers.getSerializerId(key.getSerializer())<0)throw new IllegalArgumentException("Unregistered tracked data serializer");
        Entity entity=owner();if(entity!=null&&key.declaringClass()!=null&&!key.declaringClass().isInstance(entity))throw new IllegalArgumentException("Tracked parameter belongs to another entity hierarchy");
        entries.put(key.getId(),new DataEntry(key,key.getSerializer().validate(value,true)));revision++;
    }
    private DataEntry<?> entry(DataParameter<?> key){DataEntry<?> found=entries.get(key.getId());if(found==null)throw new IllegalArgumentException("Unregistered tracked data ID "+key.getId());if(found.key.getSerializer()!=key.getSerializer())throw new IllegalArgumentException("Tracked data serializer mismatch");return found;}
    @SuppressWarnings("unchecked") public synchronized <T> T get(DataParameter<T> key){return (T)entry(key).value;}
    @SuppressWarnings({"unchecked","rawtypes"}) public synchronized void set(DataParameter<?> key,Object value){DataEntry found=entry(key);Object valid=key.getSerializer().validate(value,false);if(Objects.equals(found.value,valid))return;found.value=valid;revision++;notifyChanged(found.key);found.dirty=true;dirty=true;}
    public synchronized void setDirty(DataParameter<?> key){entry(key).dirty=true;dirty=true;revision++;}
    public synchronized boolean isDirty(){return dirty;}public synchronized boolean isEmpty(){return entries.isEmpty();}
    public synchronized void setClean(){dirty=false;for(DataEntry<?> entry:entries.values())entry.dirty=false;}
    public synchronized List<DataEntry<?>> getDirty(){if(!dirty)return null;List<DataEntry<?>> values=new ArrayList<DataEntry<?>>();for(DataEntry<?> entry:entries.values())if(entry.dirty){values.add(entry.copy());entry.dirty=false;}dirty=false;return values.isEmpty()?null:values;}
    public synchronized List<DataEntry<?>> getAll(){if(entries.isEmpty())return null;List<DataEntry<?>> result=new ArrayList<DataEntry<?>>();for(DataEntry<?> e:entries.values())result.add(e.copy());return result;}
    @SuppressWarnings({"unchecked","rawtypes"}) public synchronized void setEntryValues(List<DataEntry<?>> updates){
        if(updates==null)return;
        // Validate the whole message before changing entity state.
        for(DataEntry<?> incoming:updates){DataEntry<?> target=entries.get(incoming.key.getId());if(target==null)throw new IllegalArgumentException("Unknown remote tracked ID "+incoming.key.getId());if(target.key.getSerializer()!=incoming.key.getSerializer())throw new IllegalArgumentException("Remote tracked serializer mismatch");target.key.getSerializer().validate(incoming.value,false);}
        for(DataEntry<?> incoming:updates){DataEntry target=entries.get(incoming.key.getId());target.value=incoming.value;notifyChanged(target.key);}dirty=true;
    }
    private void notifyChanged(DataParameter<?> key){Entity entity=owner();if(entity==null)return;if(entity instanceof EntityDataListener){((EntityDataListener)entity).notifyDataManagerChange(key);return;}
        java.util.Optional<Method> callback;synchronized(CALLBACKS){callback=CALLBACKS.get(entity.getClass());if(callback==null){try{callback=java.util.Optional.of(entity.getClass().getMethod("notifyDataManagerChange",DataParameter.class));}catch(NoSuchMethodException absent){callback=java.util.Optional.empty();}CALLBACKS.put(entity.getClass(),callback);}}
        // Entity.notifyDataManagerChange in the original vanilla base has an empty body.
        if(callback.isPresent())try{callback.get().invoke(entity,key);}catch(ReflectiveOperationException e){throw new IllegalStateException("Tracked entity callback failed",e instanceof InvocationTargetException?((InvocationTargetException)e).getCause():e);}
    }
    public synchronized void writeEntries(PacketBuffer buffer)throws IOException{writeEntries(getAll(),buffer);}
    public static void writeEntries(List<DataEntry<?>> values,PacketBuffer buffer)throws IOException{if(values!=null)for(DataEntry<?> entry:values)writeEntry(entry,buffer);buffer.writeByte(255);}
    @SuppressWarnings({"unchecked","rawtypes"}) private static void writeEntry(DataEntry entry,PacketBuffer buffer)throws IOException{int id=DataSerializers.getSerializerId(entry.key.getSerializer());if(id<0)throw new IOException("Unknown tracked serializer");buffer.writeByte(entry.key.getId());buffer.writeVarIntToBuffer(id);entry.key.getSerializer().write(buffer,entry.value);}
    @SuppressWarnings({"unchecked","rawtypes"}) public static List<DataEntry<?>> readEntries(PacketBuffer buffer)throws IOException{List<DataEntry<?>> result=new ArrayList<DataEntry<?>>();Set<Integer> seen=new HashSet<Integer>();while(true){int id=buffer.readUnsignedByte();if(id==255)break;if(!seen.add(id))throw new IOException("Duplicate tracked ID in message");int type=buffer.readVarIntFromBuffer();DataSerializer serializer=DataSerializers.getSerializer(type);if(serializer==null)throw new IOException("Unknown tracked serializer "+type);result.add(new DataEntry(serializer.createKey(id),serializer.read(buffer)));}return result.isEmpty()?null:result;}
    public static final class DataEntry<T>{
        private final DataParameter<T> key;private T value;private boolean dirty=true;
        public DataEntry(DataParameter<T> key,T value){this.key=key;this.value=value;}
        public DataParameter<T> getKey(){return key;}public T getValue(){return value;}public void setValue(T value){this.value=value;}public boolean isDirty(){return dirty;}public void setDirty(boolean dirty){this.dirty=dirty;}
        public DataEntry<T> copy(){return new DataEntry<T>(key,key.getSerializer().copyValue(value));}
    }
}
