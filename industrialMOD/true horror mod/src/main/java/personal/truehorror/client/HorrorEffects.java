package personal.truehorror.client;

import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import personal.truehorror.HorrorConfig;
import personal.truehorror.network.EffectMessage;

public final class HorrorEffects {
    private final ConcurrentLinkedQueue<EffectMessage> queue=new ConcurrentLinkedQueue<EffectMessage>();
    private static final ResourceLocation VIGNETTE=new ResourceLocation("truehorror","textures/gui/vignette.png");
    private WorldClient world;
    private int remaining,total,type;
    private float intensity;
    public void enqueue(EffectMessage effect){if(queue.size()<8)queue.offer(effect);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;
        Minecraft mc=Minecraft.getMinecraft();
        if(world!=mc.theWorld){world=mc.theWorld;queue.clear();remaining=total=0;}
        if(world==null){queue.clear();return;}
        if(remaining>0)remaining--;
        EffectMessage next;
        while((next=queue.poll())!=null){
            if(next.intensity>=intensity||remaining==0){type=next.type;intensity=next.intensity;}
            remaining=total=Math.min(160,Math.max(remaining,next.duration));
        }
    }
    private float strength(){
        if(remaining<=0||total<=0)return 0;
        double phase=(total-remaining)/(double)total;
        float strength=(float)(Math.sin(Math.PI*phase)*intensity);
        if(type==2)strength*=.45F+.25F*(float)Math.sin(phase*18);
        return Math.max(0,strength);
    }
    @SubscribeEvent public void fog(EntityViewRenderEvent.FogColors event){
        if(!HorrorConfig.screenEffects)return;
        float factor=1-strength()*.3F;event.red*=factor;event.green*=factor;event.blue*=factor;
    }
    @SubscribeEvent public void silence(net.minecraftforge.client.event.sound.PlaySoundEvent17 event){
        if(remaining>0&&type==3&&HorrorConfig.soundEffects
                &&(event.category==net.minecraft.client.audio.SoundCategory.AMBIENT||event.category==net.minecraft.client.audio.SoundCategory.MUSIC)
                &&event.sound.getPositionedSoundLocation().getResourceDomain().equals("minecraft"))event.result=null;
    }
    @SubscribeEvent public void overlay(RenderGameOverlayEvent.Post event){
        Minecraft mc=Minecraft.getMinecraft();
        if(!HorrorConfig.screenEffects||event.type!=RenderGameOverlayEvent.ElementType.ALL||world==null||mc.currentScreen!=null||remaining==0)return;
        float alpha=strength();if(alpha<=0)return;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_COLOR_BUFFER_BIT|GL11.GL_CURRENT_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        try{
            GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_LIGHTING);GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);GL11.glColor4f(1,1,1,alpha);
            mc.getTextureManager().bindTexture(VIGNETTE);
            int w=event.resolution.getScaledWidth(),h=event.resolution.getScaledHeight();
            Tessellator t=Tessellator.instance;t.startDrawingQuads();
            t.addVertexWithUV(0,h,-90,0,1);t.addVertexWithUV(w,h,-90,1,1);t.addVertexWithUV(w,0,-90,1,0);t.addVertexWithUV(0,0,-90,0,0);t.draw();
            if(HorrorConfig.flashing&&type==1){
                GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glColor4f(.7F,.66F,.61F,alpha*.035F);
                t.startDrawingQuads();t.addVertex(0,h,-89);t.addVertex(w,h,-89);t.addVertex(w,0,-89);t.addVertex(0,0,-89);t.draw();
            }
        }finally{GL11.glPopAttrib();}
    }
    public int remaining(){return remaining;}
}
