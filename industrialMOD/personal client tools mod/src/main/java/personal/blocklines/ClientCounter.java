package personal.blocklines;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public final class ClientCounter {
    public static ClientCounter instance;
    private final LineMeasure measure = new LineMeasure();
    private final KeyBinding toggle = new KeyBinding("key.blocklinecounter.toggle", Keyboard.KEY_G,
            "key.categories.blocklinecounter");
    private WorldClient world;
    private final LineMeasure.Blocks blocks = new LineMeasure.Blocks() {
        @Override public int at(int x, int y, int z) {
            // Do not request, generate or force-load chunks. Missing chunks are never counted as air.
            // ChunkProviderClient.chunkExists always returns true in 1.7.10.
            // Its provideChunk is a map lookup returning EmptyChunk for unavailable data.
            if (world == null || world.getChunkProvider().provideChunk(x >> 4, z >> 4)
                    instanceof net.minecraft.world.chunk.EmptyChunk) return -1;
            return world.isAirBlock(x, y, z) ? 0 : 1;
        }
    };

    public static void initialize() {
        instance = new ClientCounter();
        ClientRegistry.registerKeyBinding(instance.toggle);
        MinecraftForge.EVENT_BUS.register(instance);
        FMLCommonHandler.instance().bus().register(instance);
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void mouse(MouseEvent event) {
        if (event.button >= 0 && event.buttonstate && press(event.button - 100, false)) event.setCanceled(true);
    }
    @SubscribeEvent public void keyboard(InputEvent.KeyInputEvent event) {
        if (!Keyboard.getEventKeyState()) return;
        int code = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        if (press(code, Keyboard.isRepeatEvent())) {
            // KeyInputEvent runs before vanilla processes use-item presses. Consume a rebound keyboard Use key.
            KeyBinding use = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
            KeyBinding.setKeyBindState(use.getKeyCode(), false);
            while (use.isPressed()) { }
        }
    }
    boolean press(int code, boolean repeat) {
        Minecraft mc = Minecraft.getMinecraft();
        if (repeat || code == 0 || mc.theWorld == null || mc.thePlayer == null
                || mc.currentScreen != null || !mc.inGameHasFocus) return false;
        synchronizeWorld(mc);
        if (code == toggle.getKeyCode()) { measure.toggle(); return false; }
        if (!measure.enabled() || code != mc.gameSettings.keyBindUseItem.getKeyCode()) return false;
        MovingObjectPosition target = mc.objectMouseOver;
        if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                && !world.isAirBlock(target.blockX, target.blockY, target.blockZ)) {
            measure.select(new LineMeasure.Point(target.blockX, target.blockY, target.blockZ));
        }
        // While measuring, use-item clicks select blocks instead of placing/opening/using items.
        return true;
    }
    private void synchronizeWorld(Minecraft mc) {
        if (world != mc.theWorld) { world = mc.theWorld; measure.reset(); }
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        synchronizeWorld(Minecraft.getMinecraft());
        if (world != null) measure.tick(blocks, 256);
    }
    @SubscribeEvent public void unload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (event.world == world) { world = null; measure.reset(); }
    }
    @SubscribeEvent public void overlay(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !measure.enabled()
                || mc.theWorld == null || mc.thePlayer == null || mc.currentScreen != null
                || mc.gameSettings.hideGUI) return;
        String label = measure.label();
        mc.fontRenderer.drawStringWithShadow(label,
                event.resolution.getScaledWidth() - mc.fontRenderer.getStringWidth(label) - 8, 8, 0xffffff);
    }
    public LineMeasure state() { return measure; }
}
