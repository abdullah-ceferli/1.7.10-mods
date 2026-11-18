package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.*;import net.minecraft.entity.*;import net.minecraft.entity.passive.EntityPig;import net.minecraft.potion.*;import net.minecraft.nbt.*;import net.minecraft.server.MinecraftServer;
import net.minecraft.init.Items;import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.potion.*;

@Mod(modid="srp_potion_fx_checks",name="Disposable native potion visibility checks",version="1")
public final class NativePotionFxChecks {
    static Potion red,blue;static final Path SAVE=Paths.get("potion-fx-fixture.dat");
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done,restart;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent e) {
        PotionFxEvents.register();PotionFxEvents.register();
        NativePotionRegistry.initialize(new File(e.getModConfigurationDirectory(),"srparasites-potion-ids.cfg"));
        red=new NativePotionBase("coth",false,0xFF0000);blue=new NativePotionBase("dod_smoke_trail",false,0x0000FF);
        restart=Files.exists(SAVE);
    }
    @Mod.EventHandler public void init(FMLInitializationEvent e)throws Exception {
        if(e.getSide().isClient())Class.forName("dev.srpbackport.tests.NativePotionFxClientChecks").getMethod("initialize").invoke(null);
        else FMLCommonHandler.instance().bus().register(this);
    }
    public static class Actor extends EntityPig {
        public Actor(World world){super(world);}
        public void nativePotionStep(){super.updatePotionEffects();}
    }
    public static class Rejecting extends Actor {
        public Rejecting(World w){super(w);}
        @Override public boolean isPotionApplicable(PotionEffect effect){return effect.getDuration()!=13&&super.isPotionApplicable(effect);}
    }
    public static class Nested extends Actor {
        boolean inside;
        public Nested(World w){super(w);}
        @Override protected void onNewPotionEffect(PotionEffect effect){
            super.onNewPotionEffect(effect);
            if(!inside&&effect.getPotionID()==red.id){inside=true;addPotionEffect(new PotionEffect(red.id,50,0));}
        }
    }
    interface Check {void run()throws Exception;}
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    static int color(Actor actor){return actor.getDataWatcher().getWatchableObjectInt(7);}
    static boolean hidden(Actor actor,int id){NBTTagCompound tag=new NBTTagCompound();PotionVisibility.get(actor).saveNBTData(tag);for(int value:tag.getCompoundTag(PotionVisibility.KEY).getIntArray("Hidden"))if(value==id)return true;return false;}
    static void refresh(Actor actor){actor.nativePotionStep();FMLCommonHandler.instance().onPostWorldTick(actor.worldObj);}
    void test(String name,Check body){try{body.run();report.add("PASS: "+name);}catch(Throwable t){failed++;report.add("FAIL: "+name+" :: "+t);t.printStackTrace();}}
    void tests(WorldServer w) {
        test("Forge construction registers initialized properties before the first real native application",()->{
            Actor actor=new Actor(w);check(actor.getExtendedProperties(PotionVisibility.KEY) instanceof PotionVisibility,"EntityConstructing registration missing");
            actor.addPotionEffect(new SRPPotionEffect(red,200,0,false,false));
            check(hidden(actor,red.id),"Accepted insertion hook did not record visibility");
            refresh(actor);check(color(actor)==0,"Property owner/init or native world tick registration failed");
            check(actor.getActivePotionEffect(red).getDuration()==199,"Recoloring advanced native duration twice");
        });
        test("Native hidden-only, visible-only and mixed colors retain the real amplifier-weighted color calculation",()->{
            Actor hiddenOnly=new Actor(w);hiddenOnly.addPotionEffect(new SRPPotionEffect(red,200,0,false,false));refresh(hiddenOnly);check(color(hiddenOnly)==0,"Empty-visible fallback color leaked");
            Actor visibleOnly=new Actor(w);visibleOnly.addPotionEffect(new PotionEffect(red.id,200,0));refresh(visibleOnly);check(color(visibleOnly)==0xFF0000,"Plain native effects should remain shown");
            Actor mixed=new Actor(w);mixed.addPotionEffect(new SRPPotionEffect(red,200,3,false,false));mixed.addPotionEffect(new PotionEffect(blue.id,200,1));mixed.addPotionEffect(new PotionEffect(Potion.moveSpeed.id,200,2));
            refresh(mixed);List<PotionEffect> visible=Arrays.asList(mixed.getActivePotionEffect(blue),mixed.getActivePotionEffect(Potion.moveSpeed));
            check(color(mixed)==PotionHelper.calcPotionLiquidColor(visible),"Hidden effect contaminated mixed color");check(mixed.getActivePotionEffects().size()==3,"Color filtering removed gameplay effects");
        });
        test("A weaker or shorter accepted application changes visibility even when native combine retains the existing object and strength",()->{
            Actor actor=new Actor(w);actor.addPotionEffect(new SRPPotionEffect(red,1000,4,false,false));PotionEffect retained=actor.getActivePotionEffect(red);
            actor.addPotionEffect(new PotionEffect(red.id,20,0));check(actor.getActivePotionEffect(red)==retained&&retained.getAmplifier()==4&&retained.getDuration()==1000,"Fixture did not exercise retained native combine state");
            check(!hidden(actor,red.id),"Plain vanilla/other-mod application did not reset hidden state");check(((SRPPotionEffect)retained).doesShowParticles(),"Effect-object visibility was stale after combine");refresh(actor);check(color(actor)==0xFF0000,"Latest shown application did not win");
            Actor nativeFirst=new Actor(w);nativeFirst.addPotionEffect(new PotionEffect(red.id,1000,4));PotionEffect base=nativeFirst.getActivePotionEffect(red);nativeFirst.addPotionEffect(new SRPPotionEffect(red,20,0,false,false));
            check(nativeFirst.getActivePotionEffect(red)==base&&!(base instanceof SRPPotionEffect),"Native-first fixture replaced its base effect");refresh(nativeFirst);check(color(nativeFirst)==0&&hidden(nativeFirst,red.id),"Hidden application onto a native base effect failed");
        });
        test("Rejected insertions and rejected combinations never change visibility",()->{
            Rejecting empty=new Rejecting(w);empty.addPotionEffect(new SRPPotionEffect(red,13,0,false,false));check(empty.getActivePotionEffect(red)==null&&!hidden(empty,red.id),"Rejected insertion reached application hook");
            Rejecting shown=new Rejecting(w);shown.addPotionEffect(new PotionEffect(red.id,1000,1));shown.addPotionEffect(new SRPPotionEffect(red,13,2,false,false));check(!hidden(shown,red.id),"Rejected combination hid an existing effect");refresh(shown);check(color(shown)==0xFF0000,"Rejected effect changed native color");
            Rejecting concealed=new Rejecting(w);concealed.addPotionEffect(new SRPPotionEffect(red,1000,1,false,false));concealed.addPotionEffect(new PotionEffect(red.id,13,2));check(hidden(concealed,red.id),"Rejected native combination cleared visibility");
        });
        test("Nested native applications inside accepted callbacks retain the most recent visibility",()->{
            Nested actor=new Nested(w);actor.addPotionEffect(new SRPPotionEffect(red,1000,2,false,false));check(actor.inside&&!hidden(actor,red.id),"Outer callback overwrote the nested shown application");refresh(actor);check(color(actor)==0xFF0000,"Nested application color mismatch");
        });
        test("Removal, clearing, expiry and milk cure run the native finish hook, including same-tick reapplication",()->{
            Actor actor=new Actor(w);actor.addPotionEffect(new SRPPotionEffect(red,200,0,false,false));actor.removePotionEffect(red.id);check(!hidden(actor,red.id),"Explicit removal did not clear visibility");
            actor.addPotionEffect(new PotionEffect(red.id,200,0));refresh(actor);check(color(actor)==0xFF0000,"Same-tick native reapplication inherited hidden state");
            actor.addPotionEffect(new SRPPotionEffect(red,200,0,false,false));actor.clearActivePotions();check(!hidden(actor,red.id),"Clearing missed finish hook");
            actor.addPotionEffect(new SRPPotionEffect(red,1,0,false,false));actor.nativePotionStep();check(actor.getActivePotionEffect(red)==null&&!hidden(actor,red.id),"Expiry missed finish hook");
            actor.addPotionEffect(new SRPPotionEffect(red,200,0,false,false));actor.curePotionEffects(new ItemStack(Items.milk_bucket));check(actor.getActivePotionEffect(red)==null&&!hidden(actor,red.id),"Milk cure missed finish hook");
            FMLCommonHandler.instance().onPostWorldTick(w);check(color(actor)==0,"Finished effects left stale color");
        });
        test("Hidden effects retain gameplay attributes and the ambient aggregate stays over all active effects",()->{
            Actor actor=new Actor(w);double before=actor.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();actor.addPotionEffect(new SRPPotionEffect(Potion.moveSlowdown,200,1,false,false));refresh(actor);
            check(color(actor)==0&&actor.isPotionActive(Potion.moveSlowdown),"Hiding removed the effect");check(actor.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()<before,"Hidden effect lost native gameplay");actor.removePotionEffect(Potion.moveSlowdown.id);check(Math.abs(actor.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()-before)<1e-9,"Finished hidden effect left attributes");
            Actor mixed=new Actor(w);mixed.addPotionEffect(new SRPPotionEffect(red,200,0,false,false));mixed.addPotionEffect(new PotionEffect(blue.id,200,0,true));refresh(mixed);check(color(mixed)==0x0000FF&&mixed.getDataWatcher().getWatchableObjectByte(8)==0,"Ambient was filtered with color");
            Actor ambient=new Actor(w);ambient.addPotionEffect(new SRPPotionEffect(red,200,0,true,false));ambient.addPotionEffect(new PotionEffect(blue.id,200,0,true));refresh(ambient);check(ambient.getDataWatcher().getWatchableObjectByte(8)==1,"Native ambient marker changed");
            SRPPotionEffect copy=new SRPPotionEffect(new SRPPotionEffect(red,200,1,true,false));check(copy.getIsAmbient()&&!copy.doesShowParticles(),"Copy lost ambient or visibility");
        });
        test("Native NBT inserts base effects directly and preserves hidden state loaded before ActiveEffects",()->{
            Actor actor=new Actor(w);actor.addPotionEffect(new PotionEffect(red.id,200,1));actor.addPotionEffect(new SRPPotionEffect(red,20,0,false,false));NBTTagCompound nbt=new NBTTagCompound();actor.writeToNBT(nbt);
            Actor loaded=new Actor(w);loaded.readFromNBT(nbt);check(loaded.getActivePotionEffect(red)!=null&&!(loaded.getActivePotionEffect(red) instanceof SRPPotionEffect),"Native NBT reconstruction changed its base factory");
            check(hidden(loaded,red.id),"Loading pruned or reset visibility before native effects were restored");refresh(loaded);check(color(loaded)==0,"Reloaded native base effect showed particles");
        });
        test(restart?"Hidden state survives a genuine second-JVM entity NBT reload and native reapplication":"Hidden state and native effects are saved for a genuine second-JVM reload",()->{
            if(restart){NBTTagCompound nbt;try(InputStream in=Files.newInputStream(SAVE)){nbt=CompressedStreamTools.readCompressed(in);}Actor loaded=new Actor(w);loaded.readFromNBT(nbt);check(hidden(loaded,red.id)&&loaded.getActivePotionEffects().size()==2,"Cross-JVM effect/state restoration failed");refresh(loaded);check(color(loaded)==0x0000FF,"Cross-JVM mixed color failed");loaded.addPotionEffect(new PotionEffect(red.id,10,0));refresh(loaded);check(!hidden(loaded,red.id)&&color(loaded)!=0x0000FF,"Native application after reload did not reset visibility");}
            else{Actor actor=new Actor(w);actor.addPotionEffect(new SRPPotionEffect(red,2000,2,false,false));actor.addPotionEffect(new PotionEffect(blue.id,2000,0));NBTTagCompound nbt=new NBTTagCompound();actor.writeToNBT(nbt);try(OutputStream out=Files.newOutputStream(SAVE)){CompressedStreamTools.writeCompressed(nbt,out);}}
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e){
        if(done||e.phase!=TickEvent.Phase.END||++ticks!=20)return;
        try{tests(MinecraftServer.getServer().worldServerForDimension(0));}catch(Throwable t){failed++;report.add("FAIL: setup :: "+t);t.printStackTrace();}
        finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get(restart?"potion-fx-restart.txt":"potion-fx-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException t){throw new RuntimeException(t);}MinecraftServer.getServer().initiateShutdown();}
    }
}
