package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.client.registry.RenderingRegistry;import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.client.model.ModelBiped;import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.*;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.init.Blocks;import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAILifecycle;

public final class NativeAILifecycleClientChecks {
    final List<String> report = new ArrayList<String>();
    final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();
    volatile NativeAILifecycleChecks.Actor serverActor;
    volatile Throwable error;
    NativeAILifecycleChecks.Actor previous;
    int stage, wait = 30, elapsed;
    boolean done;
    public static void initialize() {
        RenderingRegistry.registerEntityRenderingHandler(NativeAILifecycleChecks.Actor.class, new RenderLiving(new ModelBiped(), 0.5F) {
            @Override protected ResourceLocation getEntityTexture(Entity e) { return new ResourceLocation("textures/entity/steve.png"); }
        });
        FMLCommonHandler.instance().bus().register(new NativeAILifecycleClientChecks());
    }
    static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
    void pass(String message) { report.add("PASS: " + message); System.out.println("LIFECYCLE CLIENT PASS: " + message); }
    EntityPlayerMP player() { return (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0); }
    NativeAILifecycleChecks.Actor actor(Minecraft mc) {
        if (serverActor == null) return null;
        Entity e = mc.theWorld.getEntityByID(serverActor.getEntityId());
        return e instanceof NativeAILifecycleChecks.Actor ? (NativeAILifecycleChecks.Actor)e : null;
    }
    @SubscribeEvent public void server(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Runnable next; while ((next = tasks.poll()) != null) try { next.run(); } catch (Throwable t) { error = t; t.printStackTrace(); }
    }
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event) {
        if (done || event.phase != TickEvent.Phase.END || --wait > 0) return;
        Minecraft mc = Minecraft.getMinecraft();
        try {
            check(++elapsed < 1600, "Lifecycle client timeout at " + stage);
            if (error != null) throw new AssertionError("Server fixture failed", error);
            if (stage == 0) {
                if (!(mc.currentScreen instanceof GuiMainMenu)) return;
                mc.gameSettings.renderDistanceChunks = 8; mc.gameSettings.pauseOnLostFocus = false;
                mc.gameSettings.showInventoryAchievementHint = false; mc.gameSettings.limitFramerate = 60;
                WorldSettings settings = new WorldSettings(1710112, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT); settings.func_82750_a("2;7;1;");
                mc.launchIntegratedServer("lifecycle-client", "AI lifecycle compatibility", settings);
                stage = 1; wait = 60; return;
            }
            if (mc.theWorld == null || mc.thePlayer == null) return;
            if (stage == 1) {
                mc.displayGuiScreen(null);
                tasks.add(() -> {
                    WorldServer world = MinecraftServer.getServer().worldServerForDimension(0);
                    for (int x = 17; x <= 25; x++) for (int z = 17; z <= 25; z++) world.setBlock(x, 79, z, Blocks.stone, 0, 3);
                    player().setPositionAndUpdate(20.5, 80, 20.5);
                    NativeAILifecycleChecks.Actor a = new NativeAILifecycleChecks.Actor(world); a.setPosition(22, 80, 22);
                    check(world.spawnEntityInWorld(a), "Spawn failed"); serverActor = a;
                });
                stage = 2; return;
            }
            NativeAILifecycleChecks.Actor a = actor(mc);
            if (stage == 2) {
                if (a == null || serverActor.actionTicks < 12 || a.ticksExisted < 8) return;
                check(a != serverActor && a.worldObj.isRemote, "Server entity read as client");
                check(a.callbacks == 0 && a.actionTicks == 0 && a.targetTicks == 0 && a.aiCycles == 0, "Client initialized or scheduled server goals");
                check(serverActor.callbacks == 1 && serverActor.ready && serverActor.targetBeforeAction && serverActor.legacyCycles == 0, "Server AI lifecycle");
                pass("Real tracked client mobs skip server goal initialization while the integrated server schedules both selectors");
                EntityAILifecycle.constructed(a, mc.theWorld); NativeAILifecycleChecks.originalGate(a, mc.theWorld);
                check(a.callbacks == 0, "Client gate disagrees with original constructor slice");
                pass("Client-world gating agrees with the retained original 1.12 constructor instruction slice");
                previous = a;
                tasks.add(() -> player().setPositionAndUpdate(140.5, 80, 20.5)); stage = 3; return;
            }
            if (stage == 3) {
                if (a != null) return;
                tasks.add(() -> player().setPositionAndUpdate(20.5, 80, 20.5)); stage = 4; return;
            }
            if (stage == 4) {
                if (a == null || a.ticksExisted < 5) return;
                check(a != previous && a.callbacks == 0 && a.actionTicks == 0 && serverActor.callbacks == 1, "Tracking re-entry duplicated goals");
                pass("Tracking re-entry builds a fresh client entity without duplicating the server's registered goals");
                finish(mc, false);
            }
        } catch (Throwable t) { t.printStackTrace(); report.add("FAIL: stage " + stage + " :: " + t); finish(mc, true); }
    }
    void finish(Minecraft mc, boolean failed) {
        done = true; report.add("Failed: " + (failed ? 1 : 0));
        try { Files.write(new File(mc.mcDataDir, "lifecycle-client.txt").toPath(), report, StandardCharsets.UTF_8); }
        catch (IOException e) { throw new RuntimeException(e); }
        mc.displayGuiScreen(new GuiMainMenu());
        if (mc.theWorld != null) { mc.theWorld.sendQuittingDisconnectingPacket(); mc.loadWorld(null); }
        mc.shutdown();
    }
}
