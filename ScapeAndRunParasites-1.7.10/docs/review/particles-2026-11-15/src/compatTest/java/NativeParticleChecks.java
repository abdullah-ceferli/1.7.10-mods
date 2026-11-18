package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.init.Blocks;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraftforge.common.util.FakePlayer;
import com.mojang.authlib.GameProfile;import io.netty.buffer.*;
import com.dhanantry.scapeandrunparasites.backport.particle.*;import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;
@Mod(modid="srp_particle_checks",name="Disposable native particle checks",version="1")
public final class NativeParticleChecks {
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event){ParticleNetwork.initialize();ParticleNetwork.initialize();}
    @Mod.EventHandler public void init(FMLInitializationEvent event)throws Exception {
        if(event.getSide().isClient())Class.forName("dev.srpbackport.tests.NativeParticleClientChecks").getMethod("initialize").invoke(null);
        else FMLCommonHandler.instance().bus().register(this);
    }
    interface Check{void run()throws Exception;}
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    void test(String name,Check task){try{task.run();report.add("PASS: "+name);}catch(Throwable problem){failed++;report.add("FAIL: "+name+" :: "+problem);problem.printStackTrace();}}
    void tests(WorldServer w){
        test("Dedicated Forge initializes the particle channel without linking client renderer classes",()->{
            check(ParticleAccess.CLIENT==null,"Client renderer linked on dedicated server");
            ParticleAccess.alwaysVisible((World)w,EnumParticleTypes.SPELL_MOB.getParticleID(),0,80,0,0,0,0);
            ParticleAccess.spawn((World)w,EnumParticleTypes.END_ROD,0,80,0,0,0,0); // Original local-world overload has no server receiver.
            ParticleAccess.spawn(w,EnumParticleTypes.END_ROD,0,80,0,1,0,0,0,0,new int[0]); // Empty real server player list.
        });
        test("Real Netty buffers round-trip all original particle IDs, float packet values and required arguments",()->{
            for(EnumParticleTypes type:EnumParticleTypes.values()){
                ParticleBatch a=new ParticleBatch(-1,type,true,-0.125123456,81.23456789,127.999999,17,0.123,0.234,0.345,0.456,0xE123,41,99);ByteBuf bytes=Unpooled.buffer();
                try{a.toBytes(bytes);ParticleBatch b=new ParticleBatch();b.fromBytes(bytes);check(b.dimension==-1&&b.type==type&&b.longDistance&&b.count==17,"Identity fields");
                    check(Float.floatToIntBits(a.x)==Float.floatToIntBits(b.x)&&Float.floatToIntBits(a.y)==Float.floatToIntBits(b.y)&&Float.floatToIntBits(a.speed)==Float.floatToIntBits(b.speed)&&Arrays.equals(a.arguments,b.arguments),"Payload loss");}
                finally{bytes.release();}
            }
        });
        test("Particle packet decoding rejects unknown IDs and trailing data instead of desynchronizing the stream",()->{
            ByteBuf bytes=Unpooled.buffer();try{bytes.writeInt(0);bytes.writeByte(255);boolean bad=false;try{new ParticleBatch().fromBytes(bytes);}catch(IllegalArgumentException expected){bad=true;}check(bad,"Unknown ID accepted");bytes.clear();new ParticleBatch(0,EnumParticleTypes.PORTAL,false,0,0,0,0,0,0,0,1).toBytes(bytes);bytes.writeByte(1);bad=false;try{new ParticleBatch().fromBytes(bytes);}catch(IllegalArgumentException expected){bad=true;}check(bad,"Trailing bytes accepted");}finally{bytes.release();}
        });
        test("Native block metadata and item damage are encoded in the correct legacy particle names",()->{
            int state=BlockStates.stateId(BlockStates.fromMeta(Blocks.wool,14));int block=net.minecraft.block.Block.getIdFromBlock(Blocks.wool);
            check(ParticleAccess.legacyName(EnumParticleTypes.BLOCK_CRACK,state).equals("blockcrack_"+block+"_14"),"Block crack metadata");
            check(ParticleAccess.legacyName(EnumParticleTypes.BLOCK_DUST,state).equals("blockdust_"+block+"_14"),"Block dust metadata");
            check(ParticleAccess.legacyName(EnumParticleTypes.ITEM_CRACK,276,37).equals("iconcrack_276_37"),"Item damage");check(ParticleAccess.legacyName(EnumParticleTypes.PORTAL,1,2,3).equals("portal"),"Unused arguments changed particle name");
        });
        test("Recipient selection preserves original inclusive 32/512-block boundaries and signed entity positions",()->{
            check(ParticleBatch.inRange(0,0,0,32,0,0,false)&&!ParticleBatch.inRange(0,0,0,Math.nextUp(32.0),0,0,false),"Normal radius");
            check(ParticleBatch.inRange(0,0,0,512,0,0,true)&&!ParticleBatch.inRange(0,0,0,Math.nextUp(512.0),0,0,true),"Long radius");
            EntityPlayerMP player=new FakePlayer(w,new GameProfile(UUID.fromString("1f744db6-20b8-48f8-a231-9b250ed95536"),"ParticleRangeQA"));player.setPosition(-0.25,70.75,-16.25);
            com.dhanantry.scapeandrunparasites.backport.math.BlockPos pos=EntityQueries.position(player);check(pos.getX()==-1&&pos.getY()==71&&pos.getZ()==-17,"Position mapping");
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;
        try{tests(MinecraftServer.getServer().worldServerForDimension(0));}catch(Throwable problem){failed++;report.add("FAIL: setup :: "+problem);problem.printStackTrace();}
        finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("particle-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException problem){throw new RuntimeException(problem);}MinecraftServer.getServer().initiateShutdown();}
    }
}
