package com.dhanantry.scapeandrunparasites.backport.potion;

import java.io.File;
import java.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Fixed, configurable native IDs. Never silently renumber an occupied slot or an existing world. */
public final class NativePotionRegistry {
    private static final Map<String,Integer> DEFAULTS = new LinkedHashMap<String,Integer>();
    private static final Map<String,Integer> ids = new LinkedHashMap<String,Integer>();
    private static final Map<String,Potion> byName = new LinkedHashMap<String,Potion>();
    private static final Map<Potion,ResourceLocation> names = new IdentityHashMap<Potion,ResourceLocation>();
    private static boolean initialized;
    static {
        DEFAULTS.put("coth",64);
        DEFAULTS.put("dod_smoke_trail",65);
        DEFAULTS.put("thornshade_thorns",66);
        DEFAULTS.put("fear",67);
        DEFAULTS.put("antimall",68);
        DEFAULTS.put("distorted_enlightenment",69);
        DEFAULTS.put("bleed",70);
        DEFAULTS.put("corrosive",71);
        DEFAULTS.put("viral",72);
        DEFAULTS.put("vomit",73);
        DEFAULTS.put("rage",74);
        DEFAULTS.put("repel",75);
        DEFAULTS.put("senses",76);
        DEFAULTS.put("prey",77);
        DEFAULTS.put("debar",78);
        DEFAULTS.put("needler",79);
        DEFAULTS.put("foster",80);
        DEFAULTS.put("link",81);
        DEFAULTS.put("pivot",82);
        DEFAULTS.put("jugg",83);
        DEFAULTS.put("parate",84);
        DEFAULTS.put("primitive",85);
        DEFAULTS.put("adapted",86);
        DEFAULTS.put("pure",87);
        DEFAULTS.put("crude",88);
        DEFAULTS.put("feral",89);
        DEFAULTS.put("nexus",90);
        DEFAULTS.put("spotted",91);
        DEFAULTS.put("braining",92);
        DEFAULTS.put("novision",93);
        DEFAULTS.put("indeaf",94);
        DEFAULTS.put("overheating",95);
        DEFAULTS.put("conta",96);
        DEFAULTS.put("muscleout",97);
        DEFAULTS.put("effectpos",98);
        DEFAULTS.put("effectneg",99);
        DEFAULTS.put("the_sign",100);
    }
    private NativePotionRegistry() {}

    public static synchronized void initialize(File file) {
        if(initialized)return;
        Configuration config=new Configuration(file);config.load();
        Map<String,Integer> proposed=new LinkedHashMap<String,Integer>();Set<Integer> used=new HashSet<Integer>();
        for(Map.Entry<String,Integer> entry:DEFAULTS.entrySet()) {
            Property property=config.get("effect_ids",entry.getKey(),entry.getValue(),"Stable numeric ID. Keep client/server configs identical. Changing this after creating a world is rejected.");
            if(!property.isIntValue())throw new IllegalArgumentException("Invalid potion ID for "+entry.getKey()+": "+property.getString());
            proposed.put("srparasites:"+entry.getKey(),property.getInt());
        }
        validate(proposed);
        for(Map.Entry<String,Integer> entry:proposed.entrySet()) {
            int id=entry.getValue();
            if(!used.add(id))throw new IllegalArgumentException("Duplicate potion ID "+id);
            if(id<Potion.potionTypes.length&&Potion.potionTypes[id]!=null)
                throw new IllegalStateException("Potion ID "+id+" for "+entry.getKey()+" is already occupied by "+Potion.potionTypes[id].getName()+"; configure a free stable ID before loading a world");
        }
        // This one field is made mutable by the narrowly scoped production access transformer.
        if(Potion.potionTypes.length<128)Potion.potionTypes=Arrays.copyOf(Potion.potionTypes,128);
        ids.putAll(proposed);initialized=true;config.save();
        MinecraftForge.EVENT_BUS.register(new WorldBindings());
    }

    public static void validate(Map<String,Integer> values) {
        Set<Integer> seen=new HashSet<Integer>();
        for(Map.Entry<String,Integer> e:values.entrySet()) {
            int id=e.getValue();
            if(id<24||id>127)throw new IllegalArgumentException("Potion ID for "+e.getKey()+" must be between 24 and 127, got "+id);
            if(!seen.add(id))throw new IllegalArgumentException("Duplicate potion ID "+id);
        }
    }
    private static String canonical(String name){return name.indexOf(':')<0?"srparasites:"+name:name;}
    public static synchronized int id(String name) {
        if(!initialized)throw new IllegalStateException("Initialize SRP potion IDs during preInit before constructing any SRP effect");
        Integer id=ids.get(canonical(name));if(id==null)throw new IllegalArgumentException("Unconfigured effect: "+name);
        Potion existing=Potion.potionTypes[id];
        if(existing!=null)throw new IllegalStateException("Potion ID already constructed or occupied: "+name+" / "+id);
        return id;
    }
    static synchronized void bind(String name,Potion effect) {
        String key=canonical(name);Integer expected=ids.get(key);
        if(expected==null||expected!=effect.id||Potion.potionTypes[effect.id]!=effect||byName.containsKey(key))throw new IllegalStateException("Invalid potion binding: "+key);
        byName.put(key,effect);names.put(effect,new ResourceLocation(key));
    }
    public static String attributeId(String effect,String attribute){
        return UUID.nameUUIDFromBytes((canonical(effect)+"/"+attribute).getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
    }
    public static synchronized Potion find(String name){return byName.get(canonical(name));}
    public static synchronized ResourceLocation name(Potion effect){
        ResourceLocation registered=names.get(effect);if(registered!=null)return registered;
        String[] vanilla={null,"speed","slowness","haste","mining_fatigue","strength","instant_health","instant_damage","jump_boost","nausea","regeneration","resistance","fire_resistance","water_breathing","invisibility","blindness","night_vision","hunger","weakness","poison","wither","health_boost","absorption","saturation"};
        int id=effect.id;return id>0&&id<vanilla.length&&Potion.potionTypes[id]==effect?new ResourceLocation("minecraft",vanilla[id]):null;
    }
    public static synchronized Map<String,Integer> bindings(){return Collections.unmodifiableMap(new LinkedHashMap<String,Integer>(ids));}

    public static void verifyWorld(World world) {
        if(world.isRemote)return;
        PotionIds data=(PotionIds)world.mapStorage.loadData(PotionIds.class,"srp_native_potion_ids");
        if(data==null){data=new PotionIds("srp_native_potion_ids");data.mapping.putAll(bindings());world.mapStorage.setData(data.mapName,data);data.markDirty();}
        else {data.requireMatches(bindings());if(!data.mapping.equals(bindings())){data.mapping.putAll(bindings());data.markDirty();}}
    }
    public static final class WorldBindings {
        @SubscribeEvent public void load(WorldEvent.Load event){verifyWorld(event.world);}
    }
    public static final class PotionIds extends WorldSavedData {
        final Map<String,Integer> mapping=new LinkedHashMap<String,Integer>();
        public PotionIds(String name){super(name);}
        @Override public void readFromNBT(NBTTagCompound nbt) {
            mapping.clear();NBTTagCompound saved=nbt.getCompoundTag("Ids");
            for(Object key:saved.func_150296_c()){String name=(String)key;mapping.put(name,saved.getInteger(name));}
        }
        @Override public void writeToNBT(NBTTagCompound nbt) {
            NBTTagCompound saved=new NBTTagCompound();for(Map.Entry<String,Integer> entry:mapping.entrySet())saved.setInteger(entry.getKey(),entry.getValue());nbt.setTag("Ids",saved);
        }
        public void requireMatches(Map<String,Integer> current) {
            for(Map.Entry<String,Integer> old:mapping.entrySet())if(!old.getValue().equals(current.get(old.getKey())))
                throw new IllegalStateException("World potion ID changed for "+old.getKey()+": saved="+old.getValue()+", configured="+current.get(old.getKey())+". Restore the saved ID mapping before loading this world.");
        }
    }
}
