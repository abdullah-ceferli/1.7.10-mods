package dev.srpbackport.tests;
import java.io.*;import java.nio.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.security.*;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import java.awt.image.BufferedImage;import javax.imageio.ImageIO;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.client.particle.*;import net.minecraft.client.renderer.*;import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.init.Blocks;import net.minecraft.init.Items;import net.minecraft.item.Item;import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;import org.lwjgl.opengl.*;
import com.dhanantry.scapeandrunparasites.backport.particle.*;import com.dhanantry.scapeandrunparasites.backport.particle.client.*;import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;
public final class NativeParticleClientChecks {
    static final class Record {final EntityFX effect;final double x,y,z,vx,vy,vz;final Thread thread;Record(EntityFX e){effect=e;x=e.posX;y=e.posY;z=e.posZ;vx=e.motionX;vy=e.motionY;vz=e.motionZ;thread=Thread.currentThread();}}
    static final class Renderer extends EffectRenderer {
        final List<Record> records=new ArrayList<Record>();Renderer(World world,TextureManager textures){super(world,textures);}
        @Override public void addEffect(EntityFX effect){super.addEffect(effect);if(records!=null)records.add(new Record(effect));}
    }
    final List<String> report=new ArrayList<String>();final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    volatile Throwable error;Renderer recorder;EffectRenderer previous;Thread clientThread;int stage,wait=30,elapsed,stable;boolean done;
    public static void initialize(){FMLCommonHandler.instance().bus().register(new NativeParticleClientChecks());}
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    static boolean near(double a,double b){return Math.abs(a-b)<0.0000001;}
    void pass(String name){report.add("PASS: "+name);System.out.println("PARTICLE CLIENT PASS: "+name);}
    EntityPlayerMP player(){return (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);}
    @SubscribeEvent public void server(TickEvent.ServerTickEvent event){if(event.phase!=TickEvent.Phase.END)return;Runnable task;while((task=tasks.poll())!=null)try{task.run();}catch(Throwable problem){error=problem;problem.printStackTrace();}}
    String sha(byte[] bytes)throws Exception{StringBuilder s=new StringBuilder();for(byte b:MessageDigest.getInstance("SHA-256").digest(bytes))s.append(String.format("%02x",b&255));return s.toString();}
    void resources(Minecraft mc)throws Exception {
        Properties expected=new Properties();try(InputStream in=getClass().getResourceAsStream("/particle-assets.properties")){expected.load(in);}
        for(String name:expected.stringPropertyNames())try(InputStream in=mc.getResourceManager().getResource(new ResourceLocation("srparasites","textures/backport/minecraft112/"+name)).getInputStream()){
            ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] bytes=new byte[8192];int n;while((n=in.read(bytes))!=-1)out.write(bytes,0,n);check(sha(out.toByteArray()).equals(expected.getProperty(name)),"Incorrect/missing source texture "+name);
        }
    }
    void render(Minecraft mc)throws Exception {
        int fbo=EXTFramebufferObject.glGenFramebuffersEXT(),texture=GL11.glGenTextures();int old=GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPushMatrix();GL11.glLoadIdentity();GL11.glOrtho(-1,1,-1,1,-1,1);GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPushMatrix();GL11.glLoadIdentity();
        double ix=EntityFX.interpPosX,iy=EntityFX.interpPosY,iz=EntityFX.interpPosZ;EntityFX.interpPosX=EntityFX.interpPosY=EntityFX.interpPosZ=0;
        try {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,texture);GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL11.GL_RGBA8,64,64,0,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,(ByteBuffer)null);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_NEAREST);GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,fbo);EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,GL11.GL_TEXTURE_2D,texture,0);
            check(EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT)==EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT,"Particle FBO incomplete");
            GL11.glViewport(0,0,64,64);GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_FOG);GL11.glEnable(GL11.GL_TEXTURE_2D);
            ModernParticleFX[] effects={new ModernParticleFX.Barrier(mc.theWorld,0,0,0),new ModernParticleFX.EndRod(mc.theWorld,0,0,0,0,0,0),new ModernParticleFX.DragonBreath(mc.theWorld,0,0,0,0,0,0),new ModernParticleFX.DamageIndicator(mc.theWorld,0,0,0,0,0,0),new ModernParticleFX.Sweep(mc.theWorld,0,0,0,0),new ExtraParticleFX.Wake(mc.theWorld,0,0,0,0,0,0),new ExtraParticleFX.Totem(mc.theWorld,0,0,0,0,0,0),new ExtraParticleFX.Spit(mc.theWorld,0,0,0,0,0,0),new ExtraParticleFX.FallingDust(mc.theWorld,0,0,0,0xDDCCAA),new ExtraParticleFX.Appearance(mc.theWorld,0,0,0)};
            effects[1].onUpdate();effects[2].onUpdate();for(int i=5;i<effects.length;i++)effects[i].onUpdate();for(int i=0;i<8;i++)effects[8].onUpdate();for(int i=0;i<14;i++)effects[9].onUpdate();BufferedImage image=new BufferedImage(64*effects.length,64,BufferedImage.TYPE_INT_ARGB);
            for(int index=0;index<effects.length;index++){
                ModernParticleFX effect=effects[index];GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glLoadIdentity();double span=effect instanceof ExtraParticleFX.Appearance?1.5:1;GL11.glOrtho(-span,span,-span,span,-10,10);GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glLoadIdentity();if(effect instanceof ExtraParticleFX.Appearance)GL11.glTranslatef(0,-1.8F,0);effect.setPosition(0,0,0);effect.prevPosX=effect.prevPosY=effect.prevPosZ=0;
                GL11.glClearColor(0,0,0,0);GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);GL11.glEnable(GL11.GL_CULL_FACE);GL11.glDisable(GL11.GL_BLEND);GL11.glColor4f(0.2F,0.3F,0.4F,0.5F);int binding=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                effect.renderParticle(Tessellator.instance,0.5F,1,1,0,0,0);
                check(GL11.glIsEnabled(GL11.GL_CULL_FACE)&&!GL11.glIsEnabled(GL11.GL_BLEND)&&GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)==binding,"Particle render leaked GL state");
                ByteBuffer pixels=BufferUtils.createByteBuffer(64*64*4);GL11.glReadPixels(0,0,64,64,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,pixels);int opaque=0;
                for(int y=0;y<64;y++)for(int x=0;x<64;x++){int at=(y*64+x)*4,r=pixels.get(at)&255,g=pixels.get(at+1)&255,b=pixels.get(at+2)&255,a=pixels.get(at+3)&255;if(a>0)opaque++;image.setRGB(index*64+x,63-y,(a<<24)|(r<<16)|(g<<8)|b);}
                check(opaque>0,"New particle rendered blank: "+effect.getClass());
            }
            ImageIO.write(image,"png",new File(mc.mcDataDir,"particle-visual.png"));
        }finally{EntityFX.interpPosX=ix;EntityFX.interpPosY=iy;EntityFX.interpPosZ=iz;EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,old);EXTFramebufferObject.glDeleteFramebuffersEXT(fbo);GL11.glDeleteTextures(texture);GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPopMatrix();GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPopMatrix();GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPopAttrib();}
    }
    void direct(Minecraft mc)throws Exception {
        clientThread=Thread.currentThread();previous=mc.effectRenderer;recorder=new Renderer(mc.theWorld,mc.getTextureManager());mc.effectRenderer=recorder;resources(mc);
        String[] used="BARRIER,BLOCK_CRACK,BLOCK_DUST,CLOUD,CRIT,CRIT_MAGIC,DAMAGE_INDICATOR,DRAGON_BREATH,DRIP_LAVA,END_ROD,EXPLOSION_HUGE,EXPLOSION_LARGE,EXPLOSION_NORMAL,FIREWORKS_SPARK,FLAME,PORTAL,REDSTONE,SLIME,SMOKE_LARGE,SMOKE_NORMAL,SPELL_MOB,SPELL_MOB_AMBIENT,SPELL_WITCH,SWEEP_ATTACK,VILLAGER_HAPPY,WATER_BUBBLE".split(",");
        int state=BlockStates.stateId(BlockStates.fromMeta(Blocks.wool,14));for(String name:used){EnumParticleTypes type=EnumParticleTypes.valueOf(name);EntityFX fx=ClientParticles.create(mc.theWorld,type,false,20.5,82,20.5,0.02,0.03,0.04,state,0);check(fx!=null,"Referenced type has no native effect: "+name);}
        pass("All 26 vanilla particle types referenced by SRP create actual native or ported EntityFX objects");
        int factories=0;for(EnumParticleTypes type:EnumParticleTypes.values()){
            int[] arguments=type==EnumParticleTypes.ITEM_CRACK?new int[]{Item.getIdFromItem(Items.diamond_sword),0}:new int[]{state,0};
            EntityFX effect=ClientParticles.create(mc.theWorld,EnumParticleTypes.getByName(type.getParticleName()),true,20.5,82,20.5,0.02,0.03,0.04,arguments);
            if(type==EnumParticleTypes.ITEM_TAKE)check(effect==null,"Original unregistered item-take ID unexpectedly spawned an effect");
            else{check(effect!=null,"Saved-name particle factory missing: "+type);factories++;}
        }
        check(factories==48,"Original factory coverage");
        pass("Every saved particle name resolves to its real factory: all 48 registered original effects, with item-take intentionally unregistered as in 1.12");

        double x=mc.renderViewEntity.posX,y=mc.renderViewEntity.posY,z=mc.renderViewEntity.posZ;
        check(mc.renderGlobal.doSpawnParticle("portal",x+24,y,z,0,0,0)==null,"Native distance fixture");check(ClientParticles.create(mc.theWorld,EnumParticleTypes.PORTAL,false,x+24,y,z,0,0,0)!=null,"Modern 32-block distance lost");
        check(mc.renderGlobal.doSpawnParticle("portal",x+24,y,z,0,0,0)==null,"Modern range leaked into ordinary native call");
        mc.gameSettings.particleSetting=2;check(ClientParticles.create(mc.theWorld,EnumParticleTypes.PORTAL,false,x,y,z,0,0,0)==null,"Minimal setting ignored");check(ClientParticles.create(mc.theWorld,EnumParticleTypes.PORTAL,true,x+40,y,z,0,0,0)!=null,"Forced particle suppressed");check(ClientParticles.create(mc.theWorld,EnumParticleTypes.DAMAGE_INDICATOR,false,x+40,y,z,0,0,0)!=null,"Original ignore-range flag lost");check(mc.renderGlobal.doSpawnParticle("portal",x,y,z,0,0,0)==null,"Modern setting bypass leaked");mc.gameSettings.particleSetting=0;
        pass("Modern range, minimal settings and force flags work while ordinary native factory filters remain unchanged");
        mc.gameSettings.particleSetting=2;mc.theWorld.rand.setSeed(123);Random expectedRandom=new Random(123);int expectedCount=0,actualCount=0;
        for(int i=0;i<200;i++){int level=2;if(expectedRandom.nextInt(10)==0)level=1;if(level==1&&expectedRandom.nextInt(3)==0)level=2;if(level<=1)expectedCount++;
            if(ClientParticles.createAlwaysVisible(mc.theWorld,EnumParticleTypes.PORTAL,x,y,z,0,0,0)!=null)actualCount++;}
        check(expectedCount>0&&actualCount==expectedCount,"Always-visible minimal-setting RNG order changed");
        check(ClientParticles.createAlwaysVisible(mc.theWorld,EnumParticleTypes.DAMAGE_INDICATOR,x+40,y,z,0,0,0)==null,"Always-visible ID incorrectly inherited enum ignore-range flag");
        mc.gameSettings.particleSetting=0;pass("The toxic-cloud always-visible overload preserves original minimal-settings RNG and its separate range rule");
        render(mc);pass("All ten ported effects, including the elder-guardian model, use exact source textures, render nonblank pixels and restore GL state");
        ModernParticleFX.EndRod rod=new ModernParticleFX.EndRod(mc.theWorld,20.5,80.1,20.5,0,-1,0);rod.onUpdate();check(rod.posY<80&&rod.textureFrame()>=176&&rod.textureFrame()<=183,"End rod collision/animation");
        ModernParticleFX.Sweep sweep=new ModernParticleFX.Sweep(mc.theWorld,20.5,82,20.5,0);for(int n=0;n<4;n++)sweep.onUpdate();check(sweep.isDead,"Sweep lifetime");
        ModernParticleFX.Barrier barrier=new ModernParticleFX.Barrier(mc.theWorld,20.5,82,20.5);for(int n=0;n<81;n++)barrier.onUpdate();check(barrier.isDead&&near(barrier.posX,20.5)&&near(barrier.posY,82),"Barrier stationary lifetime");
        ModernParticleFX.DamageIndicator hit=new ModernParticleFX.DamageIndicator(mc.theWorld,20.5,82,20.5,0,0,0);check(hit.textureFrame()==67&&hit.maximumAge()==20&&hit.age()==1,"Damage-indicator factory sequence");for(int n=0;n<21;n++)hit.onUpdate();check(hit.isDead,"Damage indicator did not expire");
        pass("New particle animation, no-collision movement and finite lifetimes run in a real native client world");
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){if(done||event.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();
        try{
            check(++elapsed<1800,"Particle client timeout at stage "+stage);if(error!=null)throw new AssertionError("Server fixture failed",error);
            if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;mc.gameSettings.renderDistanceChunks=4;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;mc.gameSettings.particleSetting=0;WorldSettings settings=new WorldSettings(1710112,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("disposable-particle-client","Disposable particle QA",settings);stage=1;wait=60;return;}
            if(mc.theWorld==null||mc.thePlayer==null)return;
            if(stage==1){mc.displayGuiScreen(null);tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);for(int x=18;x<=24;x++)for(int z=18;z<=24;z++)w.setBlock(x,79,z,Blocks.stone,0,3);player().setPositionAndUpdate(20.5,80,20.5);});stage=2;wait=40;return;}
            if(stage==2){direct(mc);recorder.records.clear();tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);ParticleAccess.spawn(w,EnumParticleTypes.END_ROD,20.5,82,20.5,0,0,0.3,0,0.5);ParticleAccess.spawn(w,EnumParticleTypes.BARRIER,20.5,82,20.5,3,0.01,0.01,0.01,0.02);});stage=3;return;}
            if(stage==3){int rods=0,barriers=0;for(Record record:recorder.records){if(record.effect instanceof ModernParticleFX.EndRod){rods++;check(near(record.vy,(double)(0.5F*0.3F)),"Count-zero direction/speed changed");}if(record.effect instanceof ModernParticleFX.Barrier)barriers++;check(record.thread==clientThread,"Particle created on Netty/server thread");}if(rods<1||barriers<3)return;check(rods==1&&barriers==3,"Network particle count duplicated");pass("Actual server packets create one directed particle for count zero and three batch particles on the client thread");recorder.records.clear();mc.gameSettings.particleSetting=2;tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);ParticleAccess.spawn(w,EnumParticleTypes.PORTAL,false,60.5,82,20.5,0,0,0,0,1,new int[0]);ParticleAccess.spawn(w,EnumParticleTypes.PORTAL,true,60.5,82,20.5,0,0,0,0,1,new int[0]);});stage=4;return;}
            if(stage==4){int portals=0;for(Record record:recorder.records)if(record.effect instanceof EntityPortalFX&&near(record.x,60.5))portals++;if(portals==0)return;if(++stable<20)return;check(portals==1,"Normal recipient range or long-distance force flag lost");pass("Real long-distance server delivery bypasses client minimal/range suppression while ordinary out-of-range delivery is excluded");finish(mc,false);}
        }catch(Throwable problem){problem.printStackTrace();report.add("FAIL: stage "+stage+" :: "+problem);finish(mc,true);}
    }
    void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"particle-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException problem){throw new RuntimeException(problem);}if(previous!=null)mc.effectRenderer=previous;mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
