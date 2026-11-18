package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.item.ItemStack;import net.minecraft.world.*;
import com.dhanantry.scapeandrunparasites.backport.item.*;

public final class NativeItemPropertyClientChecks {
    final List<String> report=new ArrayList<String>();int stage,wait=30,elapsed;boolean done;ItemStack stack;
    public static void initialize(){FMLCommonHandler.instance().bus().register(new NativeItemPropertyClientChecks());}
    static void check(boolean value,String why){if(!value)throw new AssertionError(why);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(done||event.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();
        try{
            check(++elapsed<1000,"Property client timeout");
            if(stage==0){
                if(!(mc.currentScreen instanceof GuiMainMenu))return;
                IItemPropertyGetter getter=ItemProperties.get(NativeItemPropertyChecks.item,NativeItemPropertyChecks.ANGLE);stack=new ItemStack(NativeItemPropertyChecks.item,3);
                check(getter.apply(stack,null,null)==4&&getter.apply(stack,null,null)==5,"Client callback state or nullable contexts");
                check(NativeItemPropertyChecks.lastStack==stack&&NativeItemPropertyChecks.lastWorld==null&&NativeItemPropertyChecks.lastEntity==null,"Nullable argument identity");
                report.add("PASS: Real client retains client-only fields/methods and stateful callbacks accept nullable world and entity contexts");
                mc.gameSettings.renderDistanceChunks=3;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;
                WorldSettings settings=new WorldSettings(171012,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("property-client","Property compatibility",settings);stage=1;wait=60;return;
            }
            if(mc.theWorld==null||mc.thePlayer==null)return;mc.displayGuiScreen(null);
            IItemPropertyGetter getter=ItemProperties.get(NativeItemPropertyChecks.item,NativeItemPropertyChecks.ANGLE);
            float value=getter.apply(stack,mc.theWorld,mc.thePlayer),expected=(float)(3+3.0+100+mc.thePlayer.getEyeHeight());
            check(Float.floatToIntBits(value)==Float.floatToIntBits(expected),"Actual client context result");
            check(NativeItemPropertyChecks.lastStack==stack&&NativeItemPropertyChecks.lastWorld==mc.theWorld&&NativeItemPropertyChecks.lastEntity==mc.thePlayer,"Actual arguments replaced");
            stack.stackSize=7;value=getter.apply(stack,mc.theWorld,mc.thePlayer);expected=(float)(7+4.0+100+mc.thePlayer.getEyeHeight());check(value==expected,"Mutable stack state lost");
            report.add("PASS: Callback lookup evaluates the original stack with the actual client world/player and observes live stack changes");finish(mc,false);
        }catch(Throwable t){t.printStackTrace();report.add("FAIL: stage "+stage+" :: "+t);finish(mc,true);}
    }
    void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"item-properties-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
