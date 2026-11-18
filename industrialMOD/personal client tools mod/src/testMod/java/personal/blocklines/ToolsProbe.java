package personal.blocklines;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

@Mod(modid="personaltoolstest", name="Personal Tools Test Harness", version="1",
        acceptableRemoteVersions="*", dependencies="required-after:personalclienttools;after:offlineskins")
public class ToolsProbe {
    private final File root = new File(System.getProperty("tools.test.root", "."));
    private int stage, ticks, frames;
    private long started = System.currentTimeMillis();
    private int[] originalCodes;
    private int originalUse;
    private int testForward;
    private GuiScreen presets;
    private int pointX = 1, pointZ = 1;
    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        try {
            if (System.currentTimeMillis() - started > 160000) throw new AssertionError("Timeout at stage " + stage);
            if (stage == 0 && mc.currentScreen instanceof GuiMainMenu) {
                testPresets(mc);
                mc.displayGuiScreen(new GuiControls(new GuiMainMenu(), mc.gameSettings));
                stage = 1; frames = 0;
            }
            if (mc.theWorld == null || mc.thePlayer == null || ++ticks % 5 != 0) return;
            ClientCounter counter = ClientCounter.instance;
            if (stage == 4) {
                // Fixture is confined to its newly generated disposable test world.
                pointX = (int)Math.floor(mc.thePlayer.posX);
                pointZ = (int)Math.floor(mc.thePlayer.posZ);
                if (mc.theWorld.getChunkFromBlockCoords(pointX + 7, pointZ) instanceof net.minecraft.world.chunk.EmptyChunk) return;
                for (int i=0; i<8; i++) mc.theWorld.setBlock(pointX+i, 80, pointZ,
                        i == 2 || i == 5 ? Blocks.air : (i % 2 == 0 ? Blocks.stone : Blocks.glass));
                mc.displayGuiScreen(null); mc.setIngameFocus();
                check(!counter.state().enabled(), "Counter starts disabled");
                int use = mc.gameSettings.keyBindUseItem.getKeyCode();
                target(pointX, 80, pointZ);
                check(!counter.press(use, false), "Use passes through when disabled");
                counter.press(Keyboard.KEY_G, false);
                check(counter.state().stage() == LineMeasure.Stage.FIRST, "G enters first-point selection");
                mouse(use);
                check(counter.state().stage() == LineMeasure.Stage.SECOND, "First right click selects");
                target(pointX+7, 81, pointZ); mc.theWorld.setBlock(pointX+7,81,pointZ,Blocks.stone);
                mouse(use);
                check(counter.state().stage() == LineMeasure.Stage.SECOND, "Different height rejected");
                target(pointX+7,80,pointZ);
                mouse(use);
                check(counter.state().stage() == LineMeasure.Stage.COUNTING, "Second click starts scan");
                stage = 5;
            } else if (stage == 5 && counter.state().stage() == LineMeasure.Stage.DONE) {
                check(counter.state().count() == 6, "Actual world gaps excluded and endpoints counted");
                mark("gap-count-6-of-8"); stage = 6; frames = 0;
            } else if (stage == 7) {
                counter.press(Keyboard.KEY_G, false);
                check(!counter.state().enabled() && counter.state().count() == 0, "G clears and hides");
                counter.press(Keyboard.KEY_G, true);
                check(!counter.state().enabled(), "Key repeat ignored");
                counter.press(Keyboard.KEY_G, false);
                check(counter.state().stage() == LineMeasure.Stage.FIRST, "G starts fresh");
                mc.displayGuiScreen(new GuiMainMenu());
                counter.press(Keyboard.KEY_G, false);
                check(counter.state().stage() == LineMeasure.Stage.FIRST, "Menu input ignored");
                mc.displayGuiScreen(null); mc.setIngameFocus();
                // Keyboard-rebound use follows exactly the same selection path.
                mc.gameSettings.keyBindUseItem.setKeyCode(Keyboard.KEY_U);
                target(pointX,80,pointZ); check(counter.press(Keyboard.KEY_U,false), "Keyboard Use consumed");
                target(pointX+7,80,pointZ); counter.press(Keyboard.KEY_U,false);
                mc.gameSettings.keyBindUseItem.setKeyCode(originalUse);
                stage = 8;
            } else if (stage == 8 && counter.state().stage() == LineMeasure.Stage.DONE) {
                check(counter.state().count() == 6, "Rebound Use counts correctly");
                // Scan deliberately crosses an unloaded client region. No server chunk loading is requested.
                counter.state().select(new LineMeasure.Point(pointX,80,pointZ));
                counter.state().select(new LineMeasure.Point(pointX+3000,80,pointZ)); stage = 9;
            } else if (stage == 9 && counter.state().stage() == LineMeasure.Stage.ERROR) {
                check(counter.state().count() == 0, "Unloaded region rejected without partial count");
                mark("selection-reset-rebind-unloaded");
                mc.theWorld.sendQuittingDisconnectingPacket(); mc.loadWorld(null);
                mc.displayGuiScreen(new GuiMainMenu()); stage = 10;
            }
        } catch (Throwable ex) { fail(ex); }
    }

    private void testPresets(Minecraft mc) throws Exception {
        originalCodes = new int[mc.gameSettings.keyBindings.length];
        for (int i=0;i<originalCodes.length;i++) originalCodes[i] = mc.gameSettings.keyBindings[i].getKeyCode();
        originalUse = mc.gameSettings.keyBindUseItem.getKeyCode();
        GuiControls gui = new GuiControls(new GuiMainMenu(), mc.gameSettings); mc.displayGuiScreen(gui);
        List<GuiButton> buttons = buttons(gui);
        GuiButton saved = null;
        for (GuiButton button : buttons) if (button.displayString.equals("Saved Controls")) saved = button;
        check(saved != null, "Controls button installed");
        for (GuiButton button : buttons) if (button.id == 200 || button.id == 201) {
            check(button.xPosition + button.width <= saved.xPosition, "Controls buttons do not overlap");
        }
        click(gui, saved.id); presets = mc.currentScreen;
        check(presets.getClass().getName().equals("personal.controlpresets.PresetsScreen"), "Controls button opens presets");
        check(new File(root,"config/controlpresets/presets.json").isFile(), "Initial controls saved");
        // Pick an otherwise unused key: vanilla 1.7.10 hashes one binding per code,
        // and HBM already uses Up. Preserving such conflicts is expected behavior.
        testForward = 100;
        while (used(mc, testForward)) testForward++;
        mc.gameSettings.keyBindForward.setKeyCode(testForward);
        mc.gameSettings.keyBindAttack.setKeyCode(-99);
        mc.gameSettings.keyBindUseItem.setKeyCode(-100);
        mc.gameSettings.keyBindPickBlock.setKeyCode(0);
        KeyBinding modKey = findKey(mc,"key.blocklinecounter.toggle"); modKey.setKeyCode(Keyboard.KEY_H);
        mc.gameSettings.mouseSensitivity = .81F; mc.gameSettings.invertMouse = true;
        setName(presets,"Factory layout"); click(presets,1);
        mc.gameSettings.keyBindForward.setKeyCode(Keyboard.KEY_F);
        modKey.setKeyCode(Keyboard.KEY_J); mc.gameSettings.keyBindAttack.setKeyCode(-100);
        mc.gameSettings.keyBindUseItem.setKeyCode(-99); mc.gameSettings.keyBindPickBlock.setKeyCode(5);
        mc.gameSettings.mouseSensitivity=.1F; mc.gameSettings.invertMouse=false;
        click(presets,2);
        check(mc.gameSettings.keyBindForward.getKeyCode()==testForward, "Vanilla keyboard restored");
        check(modKey.getKeyCode()==Keyboard.KEY_H, "Mod binding restored");
        check(mc.gameSettings.keyBindAttack.getKeyCode()==-99 && mc.gameSettings.keyBindUseItem.getKeyCode()==-100, "Mouse bindings restored");
        check(mc.gameSettings.keyBindPickBlock.getKeyCode()==0, "Unbound key restored");
        check(Math.abs(mc.gameSettings.mouseSensitivity-.81F)<.00001 && mc.gameSettings.invertMouse, "Mouse settings restored");
        KeyBinding.onTick(testForward); check(mc.gameSettings.keyBindForward.isPressed(), "Key lookup refreshed");
        String disk = new String(Files.readAllBytes(new File(root,"options.txt").toPath()),StandardCharsets.UTF_8);
        check(disk.contains("key_key.forward:"+testForward), "Options persisted");
        // Exercise backup/undo through the same menu.
        select(presets,"Before last load"); click(presets,2);
        check(mc.gameSettings.keyBindForward.getKeyCode()==Keyboard.KEY_F && modKey.getKeyCode()==Keyboard.KEY_J, "Backup restores previous layout");
        // Reordered live array + added mod key: identity matching must be independent of order.
        KeyBinding[] reordered = mc.gameSettings.keyBindings.clone();
        for(int i=0;i<reordered.length/2;i++){KeyBinding a=reordered[i]; reordered[i]=reordered[reordered.length-1-i]; reordered[reordered.length-1-i]=a;}
        mc.gameSettings.keyBindings = reordered;
        select(presets,"Factory layout"); click(presets,2);
        check(mc.gameSettings.keyBindForward.getKeyCode()==testForward && modKey.getKeyCode()==Keyboard.KEY_H,"Reordered mod/vanilla keys match");
        mark("presets-save-load-mouse-mod-undo-reorder");
        // Return to the original layout so the line-counter test uses G and normal right-click.
        select(presets,"Initial controls"); click(presets,2);
        check(findKey(mc,"key.blocklinecounter.toggle").getKeyCode()==Keyboard.KEY_G,"G restored from baseline");
    }
    @SubscribeEvent public void render(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ++frames < 12) return;
        Minecraft mc=Minecraft.getMinecraft();
        try {
            if(stage==1){ screenshot("controls.png"); click(mc.currentScreen,23174); stage=2;frames=0; }
            else if(stage==2){ screenshot("presets.png"); mc.displayGuiScreen(null);stage=3;
                mc.launchIntegratedServer("personal-tools-test","Personal Tools Test",new WorldSettings(17L,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT)); stage=4; }
            else if(stage==6){ screenshot("line-result.png");stage=7; }
            else if(stage==10){
                check(!ClientCounter.instance.state().enabled(),"Leaving world clears measurement");
                mark("complete"); stage=11; mc.shutdown();
            }
        } catch(Throwable ex){fail(ex);}
    }
    private void target(int x,int y,int z) {
        Minecraft.getMinecraft().objectMouseOver=new MovingObjectPosition(x,y,z,1,Vec3.createVectorHelper(x+.5,y+.5,z+.5));
    }
    private void mouse(int code) throws Exception {
        MouseEvent event=new MouseEvent();
        Field button=MouseEvent.class.getDeclaredField("button");button.setAccessible(true);button.setInt(event,code+100);
        Field state=MouseEvent.class.getDeclaredField("buttonstate");state.setAccessible(true);state.setBoolean(event,true);
        check(MinecraftForge.EVENT_BUS.post(event),"Measurement consumes Forge mouse event");
    }
    private static KeyBinding findKey(Minecraft mc,String name){for(KeyBinding k:mc.gameSettings.keyBindings)if(k.getKeyDescription().equals(name))return k;throw new AssertionError("Missing key "+name);}
    private static boolean used(Minecraft mc,int code){for(KeyBinding k:mc.gameSettings.keyBindings)if(k.getKeyCode()==code)return true;return false;}
    @SuppressWarnings("unchecked") private List<GuiButton> buttons(GuiScreen screen){return ReflectionHelper.getPrivateValue(GuiScreen.class,screen,"buttonList","field_146292_n");}
    private void click(GuiScreen screen,int id)throws Exception{
        GuiButton found=null;for(GuiButton b:buttons(screen))if(b.id==id)found=b;
        check(found!=null && found.enabled,"Clickable button "+id);
        GuiScreenEvent.ActionPerformedEvent.Pre event=new GuiScreenEvent.ActionPerformedEvent.Pre(screen,found,buttons(screen));
        if(!MinecraftForge.EVENT_BUS.post(event)){
            Method action=ReflectionHelper.findMethod(GuiScreen.class,screen,new String[]{"actionPerformed","func_146284_a"},GuiButton.class);
            action.invoke(screen,found);
        }
    }
    private void setName(GuiScreen screen,String value)throws Exception{
        Field field=screen.getClass().getDeclaredField("name");field.setAccessible(true);((GuiTextField)field.get(screen)).setText(value);
    }
    private void select(GuiScreen screen,String name)throws Exception{
        Field field=screen.getClass().getDeclaredField("names");field.setAccessible(true);
        List<?> names=(List<?>)field.get(screen);
        Field rowsField=screen.getClass().getDeclaredField("rows");rowsField.setAccessible(true);int rows=rowsField.getInt(screen);
        int index=names.indexOf(name);check(index>=0,"Preset found: "+name);
        Field page=screen.getClass().getDeclaredField("page");page.setAccessible(true);page.setInt(screen,index/rows);
        screen.initGui();click(screen,100+index%rows);
    }
    private void screenshot(String name){Minecraft mc=Minecraft.getMinecraft();ScreenShotHelper.saveScreenshot(root,name,mc.displayWidth,mc.displayHeight,mc.getFramebuffer());}
    private void check(boolean passed,String message){if(!passed)throw new AssertionError(message);System.out.println("TOOLS_TEST PASS "+message);}
    private void mark(String name)throws Exception{Files.write(new File(root,name+".txt").toPath(),("PASS "+name).getBytes(StandardCharsets.UTF_8));System.out.println("TOOLS_TEST MARK "+name);}
    private void fail(Throwable ex){try{Files.write(new File(root,"FAILED.txt").toPath(),ex.toString().getBytes(StandardCharsets.UTF_8));}catch(Exception ignored){}ex.printStackTrace();stage=99;Minecraft.getMinecraft().shutdown();}
}
