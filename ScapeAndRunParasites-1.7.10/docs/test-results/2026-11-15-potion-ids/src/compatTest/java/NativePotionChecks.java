package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.lang.reflect.Modifier;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.*;import net.minecraft.entity.passive.EntityPig;import net.minecraft.potion.*;import net.minecraft.nbt.*;import net.minecraft.server.MinecraftServer;
import net.minecraft.network.PacketBuffer;import net.minecraft.network.play.server.S1DPacketEntityEffect;import io.netty.buffer.Unpooled;
import net.minecraftforge.common.config.Configuration;
import com.dhanantry.scapeandrunparasites.backport.potion.*;

@Mod(modid="srp_potion_checks",name="Disposable native potion ID checks",version="1")
public final class NativePotionChecks {
    static final Path FIXTURE=Paths.get("potion-native-fixture.dat");
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done,restart,conflict;
    Potion[] before;final Map<String,Potion> effects=new LinkedHashMap<String,Potion>();
    static class ForeignPotion extends Potion {ForeignPotion(){super(24,true,123);}}
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent e)throws Exception {
        restart=Files.exists(FIXTURE);conflict=Boolean.getBoolean("srp.testPotionConflict");before=Potion.potionTypes.clone();
        File file=new File(e.getModConfigurationDirectory(),"srparasites-potion-ids.cfg");
        Configuration fixture=new Configuration(file);fixture.load();
        if(conflict){fixture.get("effect_ids","coth",24).set(24);fixture.save();new ForeignPotion();
            try {NativePotionRegistry.initialize(file);throw new AssertionError("Occupied ID accepted");}
            catch(IllegalStateException expected){if(!expected.getMessage().contains("already occupied"))throw expected;report.add("PASS: Occupied configured potion ID is rejected before table expansion or SRP construction");}
            return;
        }
        // Exercise the upper safe ID using a deliberate test configuration, not an allocator.
        fixture.get("effect_ids","the_sign",127).set(127);fixture.save();
        NativePotionRegistry.initialize(file);
        List<String> names=new ArrayList<String>(NativePotionRegistry.bindings().keySet());if(restart)Collections.reverse(names);
        for(String name:names)effects.put(name,new NativePotionBase(name,false,0x336699));
        ((NativePotionBase)effects.get("srparasites:rage")).registerPotionAttributeModifier(net.minecraft.entity.SharedMonsterAttributes.movementSpeed,NativePotionRegistry.attributeId("rage","movementSpeed"),0.2,2);

    }
    @Mod.EventHandler public void init(FMLInitializationEvent e){FMLCommonHandler.instance().bus().register(this);}
    interface Check {void run()throws Exception;}
    void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    void test(String name,Check c){try{c.run();report.add("PASS: "+name);}catch(Throwable t){failed++;report.add("FAIL: "+name+" :: "+t);t.printStackTrace();}}
    void tests(WorldServer world) {
        if(conflict)return;
        test("Exactly 37 named effect definitions use fixed unique byte-safe IDs while preserving vanilla instances",()->{
            Set<String> expected=new HashSet<String>();
            try(BufferedReader reader=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/expected-srp-effects.txt"),"UTF-8"))){String name;while((name=reader.readLine())!=null)if(!name.isEmpty())expected.add(name);}
            check(expected.size()==37&&expected.equals(NativePotionRegistry.bindings().keySet()),"Effect catalog differs from SRPPotions source");
            check(Potion.potionTypes.length>=128,"Native table too small");
            for(int i=0;i<before.length;i++)check(before[i]==Potion.potionTypes[i],"Vanilla slot changed: "+i);
            Set<Integer> used=new HashSet<Integer>();for(Map.Entry<String,Potion> entry:effects.entrySet()){
                Potion effect=entry.getValue();check(effect.id>=24&&effect.id<=127&&used.add(effect.id),"Unsafe or duplicate ID");
                check(NativePotionRegistry.bindings().get(entry.getKey())==effect.id,"Config binding");
                check(NativePotionRegistry.find(entry.getKey())==effect&&NativePotionRegistry.name(effect).toString().equals(entry.getKey()),"Name/identity lookup");
                check(Potion.potionTypes[effect.id]==effect,"Native slot does not contain effect");
            }
            check(effects.get("srparasites:the_sign").id==127,"Fixture did not cover ID 127");
            check(NativePotionRegistry.name(Potion.moveSlowdown).toString().equals("minecraft:slowness"),"Vanilla name mapping");
        });
        test("Invalid ranges, duplicate IDs and changed saved-world assignments are rejected",()->{
            for(int id:new int[]{-1,0,23,128,255}){Map<String,Integer> invalid=new HashMap<String,Integer>();invalid.put("a",id);boolean rejected=false;try{NativePotionRegistry.validate(invalid);}catch(IllegalArgumentException expected){rejected=true;}check(rejected,"Accepted invalid ID "+id);}
            Map<String,Integer> duplicate=new HashMap<String,Integer>();duplicate.put("a",50);duplicate.put("b",50);boolean rejected=false;try{NativePotionRegistry.validate(duplicate);}catch(IllegalArgumentException expected){rejected=true;}check(rejected,"Accepted duplicate IDs");
            NativePotionRegistry.PotionIds saved=(NativePotionRegistry.PotionIds)world.mapStorage.loadData(NativePotionRegistry.PotionIds.class,"srp_native_potion_ids");check(saved!=null,"World binding event not invoked");
            saved.requireMatches(NativePotionRegistry.bindings());Map<String,Integer> changed=new LinkedHashMap<String,Integer>(NativePotionRegistry.bindings());changed.put("srparasites:coth",120);
            rejected=false;try{saved.requireMatches(changed);}catch(IllegalStateException expected){rejected=true;}check(rejected,"Changed saved ID accepted");
        });
        test("Native attribute bridge applies and removes the stable named modifier",()->{
            Potion rage=effects.get("srparasites:rage");EntityPig actor=new EntityPig(world);
            net.minecraft.entity.ai.attributes.IAttributeInstance speed=actor.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed);
            double before=speed.getAttributeValue();String first=NativePotionRegistry.attributeId("rage","movementSpeed");
            check(first.equals(NativePotionRegistry.attributeId("srparasites:rage","movementSpeed")),"Canonical stable UUID");
            actor.addPotionEffect(new PotionEffect(rage.id,1200,1));
            check(Math.abs(speed.getAttributeValue()-before*1.4)<1e-9,"Native amplified modifier was not applied");
            actor.removePotionEffect(rage.id);check(Math.abs(speed.getAttributeValue()-before)<1e-9,"Modifier remained after removal");
        });
        test("All configured effects survive native effect NBT and real effect-packet serialization, including ID 127",()->{
            for(Potion effect:effects.values()) {
                PotionEffect applied=new PotionEffect(effect.id,2400,2,true);NBTTagCompound tag=applied.writeCustomPotionEffectToNBT(new NBTTagCompound());
                PotionEffect loaded=PotionEffect.readCustomPotionEffectFromNBT(tag);check(loaded!=null&&loaded.getPotionID()==effect.id&&loaded.getDuration()==2400&&loaded.getAmplifier()==2&&loaded.getIsAmbient(),"Native effect NBT");
                PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());try {
                    new S1DPacketEntityEffect(41,applied).writePacketData(buffer);
                    check(buffer.readableBytes()==8&&buffer.getInt(0)==41&&buffer.getByte(4)==effect.id&&buffer.getByte(5)==2&&buffer.getShort(6)==2400,"Native effect packet signed fields");
                    S1DPacketEntityEffect packet=new S1DPacketEntityEffect();packet.readPacketData(buffer);
                    check(!buffer.isReadable(),"Trailing packet bytes");
                    PacketBuffer rebuilt=new PacketBuffer(Unpooled.buffer());try {
                        packet.writePacketData(rebuilt);
                        check(rebuilt.readableBytes()==8&&rebuilt.readInt()==41&&rebuilt.readByte()==effect.id&&rebuilt.readByte()==2&&rebuilt.readShort()==2400,"Native decoded packet re-encoding");
                    }finally{rebuilt.release();}
                }finally{buffer.release();}
            }
        });
        test(restart?"Native entity effects and saved name/ID assignments reload in a second JVM with reversed construction order":"Native entity effects and name/ID assignments are saved for a genuine second-JVM reload",()->{
            if(restart){NBTTagCompound nbt;try(InputStream in=Files.newInputStream(FIXTURE)){nbt=CompressedStreamTools.readCompressed(in);}
                NBTTagCompound ids=nbt.getCompoundTag("Ids");for(Map.Entry<String,Integer> entry:NativePotionRegistry.bindings().entrySet())check(ids.getInteger(entry.getKey())==entry.getValue(),"ID changed after restart");
                EntityPig actor=new EntityPig(world);actor.readFromNBT(nbt.getCompoundTag("Entity"));check(actor.getActivePotionEffects().size()==37,"Effect count after native entity reload");
                for(Potion potion:effects.values())check(actor.getActivePotionEffect(potion)!=null&&actor.getActivePotionEffect(potion).getAmplifier()==2,"Lost native effect after restart");
                net.minecraft.entity.ai.attributes.IAttributeInstance speed=actor.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed);
                double base=speed.getBaseValue();check(Math.abs(speed.getAttributeValue()-base*1.6)<1e-9,"Saved modifier amount changed");
                actor.removePotionEffect(effects.get("srparasites:rage").id);
                check(Math.abs(speed.getAttributeValue()-base)<1e-9,"Restart changed UUID and left a stale modifier");

            }else{EntityPig actor=new EntityPig(world);for(Potion potion:effects.values())actor.addPotionEffect(new PotionEffect(potion.id,2400,2,true));
                NBTTagCompound nbt=new NBTTagCompound(),entity=new NBTTagCompound(),ids=new NBTTagCompound();actor.writeToNBT(entity);nbt.setTag("Entity",entity);
                for(Map.Entry<String,Integer> entry:NativePotionRegistry.bindings().entrySet())ids.setInteger(entry.getKey(),entry.getValue());nbt.setTag("Ids",ids);
                try(OutputStream out=Files.newOutputStream(FIXTURE)){CompressedStreamTools.writeCompressed(nbt,out);}
            }
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e) {
        if(done||e.phase!=TickEvent.Phase.END||++ticks!=20)return;
        try{tests(MinecraftServer.getServer().worldServerForDimension(0));}catch(Throwable t){failed++;report.add("FAIL: setup :: "+t);t.printStackTrace();}
        finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get(conflict?"potion-conflict.txt":restart?"potion-restart.txt":"potion-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException t){throw new RuntimeException(t);}MinecraftServer.getServer().initiateShutdown();}
    }
}
