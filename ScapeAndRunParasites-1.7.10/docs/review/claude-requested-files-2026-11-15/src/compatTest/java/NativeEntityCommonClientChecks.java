package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.lang.reflect.Modifier;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.*;import net.minecraft.client.renderer.Tessellator;
public final class NativeEntityCommonClientChecks{
 int ticks;boolean done;
 static final class Slot extends GuiSlot{Slot(Minecraft mc){super(mc,80,80,0,80,20);}protected int getSize(){return 0;}protected void elementClicked(int i,boolean b,int x,int y){}protected boolean isSelected(int i){return false;}protected void drawBackground(){}protected void drawSlot(int a,int b,int c,int d,Tessellator t,int x,int y){}}
 public static void initialize(){FMLCommonHandler.instance().bus().register(new NativeEntityCommonClientChecks());}
 @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END||++ticks<30)return;Minecraft mc=Minecraft.getMinecraft();if(!(mc.currentScreen instanceof GuiMainMenu))return;done=true;List<String> report=new ArrayList<String>();try{
  Slot slot=new Slot(mc);if(slot.mc!=mc)throw new AssertionError("Native GUI context field not accessible");int flags=cpw.mods.fml.relauncher.ReflectionHelper.findField(GuiSlot.class,"mc","field_148161_k").getModifiers();if(!Modifier.isPublic(flags)||!Modifier.isFinal(flags))throw new AssertionError("GUI context access/final flags");
  report.add("PASS: Actual native client loads the GUI access rule, exposes the real Minecraft context and preserves the final flag");report.add("Failed: 0");
 }catch(Throwable problem){problem.printStackTrace();report.add("FAIL: native client access :: "+problem);report.add("Failed: 1");}
 try{Files.write(new File(mc.mcDataDir,"entity-common-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException problem){throw new RuntimeException(problem);}mc.shutdown();}
}
