package dev.zahin.mobileperformance.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.zahin.mobileperformance.RenderPolicy;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.logging.log4j.Logger;

public final class ClientRuntime {
    public final PerformanceConfig config;public final FrameStats frames=new FrameStats();public final GraphicsPreset preset;
    public long entitySkipped,tileSkipped,particlesDropped,animationsSkipped,tick;
    private World cameraWorld;private double cx,cy,cz;private int cosmetics;private boolean greeted;
    private final ClassValue<Integer> categories=new ClassValue<Integer>(){protected Integer computeValue(Class<?> type){String name=type.getName();return RenderPolicy.ordinaryMob(name)?1:RenderPolicy.decorativeTile(name)?2:RenderPolicy.cosmeticParticle(name)?3:0;}};
    private ClientRuntime(File dir){config=new PerformanceConfig(new File(dir,"mobileperformance.cfg"));preset=new GraphicsPreset(new File(dir,"mobileperformance-before-preset.json"));}
    public static void install(File dir,Logger log){ClientRuntime r=new ClientRuntime(dir);Hooks.runtime=r;FMLCommonHandler.instance().bus().register(r);ClientCommandHandler.instance.registerCommand(new PerformanceCommand(r));log.info("Mobile Performance enabled: client rendering budgets; graphics preset is opt-in");}
    @SubscribeEvent public void clientTick(TickEvent.ClientTickEvent e){if(e.phase!=TickEvent.Phase.START)return;tick++;cosmetics=0;Minecraft mc=Minecraft.getMinecraft();if(mc.theWorld==null){cameraWorld=null;greeted=false;resetCounters();return;}if(!greeted&&mc.thePlayer!=null){greeted=true;mc.thePlayer.addChatMessage(new ChatComponentText("Mobile Performance: /mobileperf preset for low graphics; /mobileperf help for controls."));}}
    @SubscribeEvent public void render(TickEvent.RenderTickEvent e){if(e.phase!=TickEvent.Phase.START)return;Minecraft mc=Minecraft.getMinecraft();Entity camera=mc.renderViewEntity;if(mc.theWorld==null||camera==null){cameraWorld=null;return;}cameraWorld=mc.theWorld;cx=camera.lastTickPosX+(camera.posX-camera.lastTickPosX)*e.renderTickTime;cy=camera.lastTickPosY+(camera.posY-camera.lastTickPosY)*e.renderTickTime;cz=camera.lastTickPosZ+(camera.posZ-camera.lastTickPosZ)*e.renderTickTime;if(mc.currentScreen==null)frames.frame(System.nanoTime());else frames.pause();}
    private boolean active(World world){return config.enabled&&world!=null&&world==cameraWorld&&MinecraftForgeClient.getRenderPass()>=0;}
    public boolean entity(Object raw){
        if(!(raw instanceof Entity))return true;Entity e=(Entity)raw;if(!active(e.worldObj)||e instanceof EntityPlayer||e instanceof IBossDisplayData)return true;
        int range=e instanceof EntityItem?config.itemDistance:categories.get(e.getClass())==1?config.mobDistance:0;
        boolean keep=range==0||RenderPolicy.inRange(RenderPolicy.distanceSq(e.posX,e.posY,e.posZ,cx,cy,cz),range);if(!keep)entitySkipped++;return keep;
    }
    public boolean tile(Object raw){if(!(raw instanceof TileEntity))return true;TileEntity t=(TileEntity)raw;if(!active(t.getWorldObj())||categories.get(t.getClass())!=2)return true;boolean keep=RenderPolicy.inRange(RenderPolicy.distanceSq(t.xCoord+.5,t.yCoord+.5,t.zCoord+.5,cx,cy,cz),config.tileDistance);if(!keep)tileSkipped++;return keep;}
    public boolean particle(Object raw){if(!(raw instanceof EntityFX)||!config.enabled)return true;EntityFX e=(EntityFX)raw;Minecraft mc=Minecraft.getMinecraft();Entity camera=mc.renderViewEntity;if(camera==null||mc.theWorld==null||e.worldObj!=mc.theWorld||categories.get(e.getClass())!=3)return true;double distance=RenderPolicy.distanceSq(e.posX,e.posY,e.posZ,camera.posX,camera.posY,camera.posZ);if(RenderPolicy.inRange(distance,8))return true;if(!RenderPolicy.inRange(distance,config.particleDistance)||cosmetics++>=config.particlesPerTick){particlesDropped++;return false;}return true;}
    public boolean animation(){Minecraft mc=Minecraft.getMinecraft();if(!config.enabled||mc.theWorld==null||mc.currentScreen!=null||RenderPolicy.animationTick(tick,config.animationInterval))return true;animationsSkipped++;return false;}
    public void resetCounters(){entitySkipped=tileSkipped=particlesDropped=animationsSkipped=0;frames.reset();}
}
