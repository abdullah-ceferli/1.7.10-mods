package personal.truehorror.test;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import personal.truehorror.client.ClientProxy;
import personal.truehorror.entity.DreadEntity;

public final class ClientProbe extends Probe {
    private int stage,ticks,frames;
    private long started=System.currentTimeMillis(),rejoinAt;
    private boolean two=Boolean.getBoolean("horror.test.two"),b="B".equals(System.getProperty("horror.test.role"));
    @Override public void init(){FMLCommonHandler.instance().bus().register(this);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;
        Minecraft mc=Minecraft.getMinecraft();
        try{
            if(has("FAILED")){stage=99;mc.shutdown();return;}
            if(System.currentTimeMillis()-started>200000)throw new AssertionError("Client timeout stage "+stage);
            if(stage==0&&mc.currentScreen instanceof GuiMainMenu&&!two){stage=1;mc.launchIntegratedServer("true-horror-test","True Horror Test",new WorldSettings(37L,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT).enableCommands());}
            if(two&&stage==0&&mc.thePlayer!=null)stage=1;
            if(stage==10&&System.currentTimeMillis()>=rejoinAt){
                stage=11;
                if(two)mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(),mc,"127.0.0.1",Integer.getInteger("horror.test.port")));
                else mc.launchIntegratedServer("true-horror-test","True Horror Test",new WorldSettings(37L,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT));
            }
            if(mc.theWorld==null||mc.thePlayer==null||++ticks%5!=0)return;
            if(stage==1&&has("server-checks")){
                List<DreadEntity> models=new ArrayList<DreadEntity>();
                for(Object value:mc.theWorld.loadedEntityList)if(value instanceof DreadEntity)models.add((DreadEntity)value);
                if(models.size()<7)return;
                for(DreadEntity model:models)if(model.ticksExisted<20)return;
                check(models.size()==7,"Seven different entities synchronized to client");
                mc.displayGuiScreen(new GalleryScreen(models));stage=2;frames=0;
            }else if(stage==3&&(!two||has("gallery")&&has("b-gallery"))){if(!b)mark("request-effect");stage=4;}
            else if(stage==4&&ClientProxy.effects.remaining()>0){mark("client-effect-active");stage=5;ticks=0;}
            else if(stage==5&&ticks>180){
                check(ClientProxy.effects.remaining()==0,"Effect expired without stuck overlay");mark("client-effect-expired");
                if(b){stage=20;}else{mc.thePlayer.sendChatMessage("/kill");stage=6;}
            }else if(stage==6&&mc.thePlayer.getHealth()<=0){mc.thePlayer.respawnPlayer();mc.displayGuiScreen(null);stage=7;}
            else if(stage==7&&has("respawn-persistence")&&mc.thePlayer.getHealth()>0){
                mark("world-left");mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);mc.displayGuiScreen(new GuiMainMenu());
                rejoinAt=System.currentTimeMillis()+2000;stage=10;
            }else if(stage==11&&has("reload-persistence")){
                mark("complete");mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);mc.displayGuiScreen(new GuiMainMenu());stage=12;mc.shutdown();
            }else if(stage==20&&has("complete")){mark("b-complete");mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);mc.displayGuiScreen(new GuiMainMenu());mc.shutdown();}
        }catch(Throwable ex){failed(ex);stage=99;mc.shutdown();}
    }
    @SubscribeEvent public void render(TickEvent.RenderTickEvent event){
        if(event.phase!=TickEvent.Phase.END||stage!=2||++frames<20)return;
        Minecraft mc=Minecraft.getMinecraft();
        try{
            ScreenShotHelper.saveScreenshot(root,b?"gallery-b.png":"gallery.png",mc.displayWidth,mc.displayHeight,mc.getFramebuffer());
            mark(b?"b-gallery":"gallery");mc.displayGuiScreen(null);mc.setIngameFocus();
            stage=3;
        }catch(Throwable ex){failed(ex);stage=99;mc.shutdown();}
    }
}
