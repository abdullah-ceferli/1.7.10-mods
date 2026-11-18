package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.*;import net.minecraft.world.chunk.Chunk;import net.minecraft.entity.*;import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.entity.*;

@Mod(modid="srp_difficulty_checks",name="Disposable difficulty checks",version="1")
public final class NativeDifficultyChecks {
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done;
    @Mod.EventHandler public void init(FMLInitializationEvent e){FMLCommonHandler.instance().bus().register(this);}
    interface Check {void run()throws Exception;}
    void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    void same(DifficultyInstance expected,DifficultyInstance actual){check(expected.getDifficulty()==actual.getDifficulty(),"Difficulty enum");check(Float.floatToIntBits(expected.getAdditionalDifficulty())==Float.floatToIntBits(actual.getAdditionalDifficulty()),"Local difficulty");}
    void test(String name,Check c){try{c.run();report.add("PASS: "+name);}catch(Throwable t){failed++;report.add("FAIL: "+name+" :: "+t);t.printStackTrace();}}
    public static class Probe extends LegacyEntityMob {
        int calls;DifficultyInstance received;
        Probe(World w){super(w);}
        @Override public IEntityLivingData onInitialSpawn(DifficultyInstance d,IEntityLivingData data){calls++;received=d;return super.onInitialSpawn(d,data);}
    }
    public static class NativeProbe extends net.minecraft.entity.passive.EntityPig {
        int calls;
        NativeProbe(World world){super(world);}
        @Override public IEntityLivingData onSpawnWithEgg(IEntityLivingData data){calls++;return data;}
    }
    static class SpawnData implements IEntityLivingData {}
    void tests(WorldServer w) {
        test("Native global difficulty, day-time and all eight provider moon phases feed local difficulty",()->{
            Chunk chunk=w.getChunkFromChunkCoords(0,0);long old=w.getWorldTime(),inhabited=chunk.inhabitedTime;EnumDifficulty previous=w.difficultySetting;
            try {
                chunk.inhabitedTime=1800000;
                for(EnumDifficulty level:EnumDifficulty.values())for(int phase=0;phase<8;phase++){
                    w.difficultySetting=level;w.setWorldTime(1000000L+phase*24000L);
                    check(DifficultyAccess.difficulty(w)==level,"Global enum");
                    float moon=WorldProvider.moonPhaseFactors[w.provider.getMoonPhase(w.getWorldTime())];
                    same(new DifficultyInstance(level,w.getWorldTime(),chunk.inhabitedTime,moon),DifficultyAccess.at(w,new BlockPos(2,70,2)));
                }
                w.difficultySetting=EnumDifficulty.HARD;w.setWorldTime(1512000);
                check(w.getTotalWorldTime()!=w.getWorldTime(),"Fixture must distinguish clocks");
                DifficultyInstance expected=new DifficultyInstance(EnumDifficulty.HARD,w.getWorldTime(),chunk.inhabitedTime,WorldProvider.moonPhaseFactors[w.provider.getMoonPhase(w.getWorldTime())]);
                same(expected,DifficultyAccess.at(w,new BlockPos(2,-900,2)));
            } finally {w.setWorldTime(old);chunk.inhabitedTime=inhabited;w.difficultySetting=previous;}
        });
        test("Unloaded server chunk is not generated and contributes zero inhabited time and moon factor",()->{
            BlockPos pos=new BlockPos(1234567,70,-1234567);int x=pos.getX()>>4,z=pos.getZ()>>4;
            check(!w.getChunkProvider().chunkExists(x,z),"Fixture chunk already loaded");
            same(new DifficultyInstance(w.difficultySetting,w.getWorldTime(),0,0),DifficultyAccess.at(w,pos));
            check(!w.getChunkProvider().chunkExists(x,z),"Difficulty lookup generated an unloaded chunk");
        });
        test("Entity position helper selects the native negative-coordinate chunk",()->{
            Probe actor=new Probe(w);actor.setPosition(-0.25,70.75,-16.25);BlockPos pos=EntityQueries.position(actor);
            check(pos.getX()==-1&&pos.getZ()==-17,"Signed block position");
            Chunk chunk=w.getChunkFromBlockCoords(-1,-17);long old=chunk.inhabitedTime;
            try {chunk.inhabitedTime=1234567;float moon=WorldProvider.moonPhaseFactors[w.provider.getMoonPhase(w.getWorldTime())];same(new DifficultyInstance(w.difficultySetting,w.getWorldTime(),1234567,moon),DifficultyAccess.at(w,pos));}
            finally{chunk.inhabitedTime=old;}
        });
        test("Static spawn boundary dispatches native and converted EntityLiving receivers exactly once",()->{
            SpawnData group=new SpawnData();DifficultyInstance d=new DifficultyInstance(EnumDifficulty.HARD,1512000,3600000,1);
            Probe modern=new Probe(w);EntityLiving erasedModern=modern;
            check(DifficultyAccess.initialSpawn(erasedModern,d,group)==group,"Modern group data");
            check(modern.calls==1&&modern.received==d,"Modern branch lost context or called twice");
            NativeProbe nativeMob=new NativeProbe(w);EntityLiving erasedNative=nativeMob;
            check(DifficultyAccess.initialSpawn(erasedNative,d,group)==group,"Native group data");
            check(nativeMob.calls==1,"Native branch not called once");
        });
        test("Native and explicit modern spawn initialization dispatch once and retain group data",()->{
            Probe actor=new Probe(w);actor.setPosition(2,70,2);SpawnData data=new SpawnData();
            check(actor.onSpawnWithEgg(data)==data,"Native spawn group data");check(actor.calls==1,"Native callback dispatch count");same(DifficultyAccess.at(w,EntityQueries.position(actor)),actor.received);
            DifficultyInstance custom=new DifficultyInstance(EnumDifficulty.HARD,1512000,3600000,1);
            check(actor.onInitialSpawn(custom,data)==data,"Explicit spawn group data");check(actor.calls==2&&actor.received==custom,"Explicit modern call lost context or re-entered dispatcher");
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e) {
        if(done||e.phase!=TickEvent.Phase.END||++ticks!=20)return;
        try{tests(MinecraftServer.getServer().worldServerForDimension(0));}
        catch(Throwable t){failed++;report.add("FAIL: setup :: "+t);t.printStackTrace();}
        finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("difficulty-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException t){throw new RuntimeException(t);}MinecraftServer.getServer().initiateShutdown();}
    }
}
