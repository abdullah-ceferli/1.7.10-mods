package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.entity.monster.EntityZombie;import net.minecraft.entity.passive.EntityBat;import net.minecraftforge.common.util.FakePlayer;import com.mojang.authlib.GameProfile;import io.netty.buffer.*;
import com.dhanantry.scapeandrunparasites.backport.sound.*;
@Mod(modid="srp_sound_checks",name="Disposable native sound checks",version="1")
public final class NativeSoundChecks {
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event)throws Exception{if(event.getSide().isClient())Class.forName("dev.srpbackport.tests.NativeSoundClientChecks").getMethod("initialize").invoke(null);SoundNetwork.initialize(new File(event.getModConfigurationDirectory(),"srp-sounds.cfg"));SoundNetwork.initialize(new File(event.getModConfigurationDirectory(),"srp-sounds.cfg"));}
    @Mod.EventHandler public void init(FMLInitializationEvent event)throws Exception{
        if(event.getSide().isClient())Class.forName("dev.srpbackport.tests.NativeSoundClientChecks").getMethod("initialize").invoke(null);else FMLCommonHandler.instance().bus().register(this);
    }
    interface Check{void run()throws Exception;}static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    void test(String name,Check task){try{task.run();report.add("PASS: "+name);}catch(Throwable e){failed++;report.add("FAIL: "+name+" :: "+e);e.printStackTrace();}}
    void tests(WorldServer world){
        test("Dedicated Forge initializes sound packets and entity hooks without loading client audio classes",()->{
            check(SoundAccess.CLIENT==null,"Client audio linked on server");check(SoundAccess.packet(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,ModernSoundCategory.PLAYERS,-0.12,81,0,1,0.73F)!=null,"No native FML packet");
            EntityZombie z=new EntityZombie(world);z.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,1,1);z.playSound("random.pop",1,1);
        });
        test("Sound packets preserve all ten categories, signed truncated coordinates and full float volume/pitch",()->{
            Random random=new Random(120710);for(int i=0;i<10000;i++){
                double x=(random.nextDouble()-0.5)*60000000;float pitch=random.nextFloat()*2,volume=random.nextFloat()*3;ModernSoundCategory c=ModernSoundCategory.values()[i%10];
                SoundMessage a=new SoundMessage(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,c,x,-0.124,127.999,volume,pitch);ByteBuf bytes=Unpooled.buffer();
                try{a.toBytes(bytes);SoundMessage b=new SoundMessage();b.fromBytes(bytes);check(b.category==c&&b.x==(int)(x*8.0)&&b.y==0&&b.z==1023,"Coordinates/category");check(Double.doubleToLongBits(SoundMath.coordinate(b.x))==Double.doubleToLongBits((double)((float)((int)(x*8.0))/8.0F)),"Coordinate float widening");check(Float.floatToIntBits(b.pitch)==Float.floatToIntBits(pitch)&&Float.floatToIntBits(b.volume)==Float.floatToIntBits(volume),"Float payload");}finally{bytes.release();}
            }
        });
        test("Invalid sound category and trailing payload bytes are rejected",()->{
            ByteBuf bytes=Unpooled.buffer();try{cpw.mods.fml.common.network.ByteBufUtils.writeUTF8String(bytes,"random.pop");bytes.writeByte(255);boolean bad=false;try{new SoundMessage().fromBytes(bytes);}catch(IllegalArgumentException expected){bad=true;}check(bad,"Bad category");bytes.clear();new SoundMessage("random.pop",ModernSoundCategory.MASTER,0,0,0,1,1).toBytes(bytes);bytes.writeByte(1);bad=false;try{new SoundMessage().fromBytes(bytes);}catch(IllegalArgumentException expected){bad=true;}check(bad,"Trailing bytes");}finally{bytes.release();}
        });
        test("Native players, monsters, bats and modern providers resolve their original category",()->{
            check(SoundAccess.category(new EntityZombie(world))==ModernSoundCategory.HOSTILE,"Zombie");check(SoundAccess.category(new EntityBat(world))==ModernSoundCategory.AMBIENT,"Bat");check(SoundAccess.category(new FakePlayer(world,new GameProfile(UUID.randomUUID(),"SoundQA")))==ModernSoundCategory.PLAYERS,"Player");
        });
        test("Sound delivery radius and distance-delay math retain strict boundaries and original float multiplication",()->{
            check(SoundMath.inRange(15.99,0,0,0,0,0,1)&&!SoundMath.inRange(16,0,0,0,0,0,1),"Base range");check(SoundMath.inRange(31.99,0,0,0,0,0,2)&&!SoundMath.inRange(32,0,0,0,0,0,2),"Loud range");check(SoundMath.delay(100,true)==0&&SoundMath.delay(1600,true)==20&&SoundMath.delay(1600,false)==0,"Delay");
        });
        test("All 12 original material families preserve five sounds, metal pitch and anvil volume",()->{
            for(java.lang.reflect.Field f:SoundType.class.getDeclaredFields())if(java.lang.reflect.Modifier.isStatic(f.getModifiers())&&f.getType()==SoundType.class){SoundType t=(SoundType)f.get(null);check(SoundIds.aliasCategory(t.getBreakSound())==ModernSoundCategory.BLOCKS,"Break category");check(t.getStepSound().startsWith("srpvanilla112:block."),"Step resource");check(!t.getHitSound().equals(t.getPlaceSound()),"Hit/place collapsed");}
            check(SoundType.METAL.getPitch()==1.5F&&SoundType.ANVIL.getVolume()==0.3F,"Material parameters");
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;try{tests(MinecraftServer.getServer().worldServerForDimension(0));}finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("sound-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}}
}
