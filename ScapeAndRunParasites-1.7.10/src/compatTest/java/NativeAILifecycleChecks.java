package dev.srpbackport.tests;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import com.dhanantry.scapeandrunparasites.backport.entity.LegacyEntityMob;
import com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.*;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityNetwork;

@Mod(modid="srp_ai_lifecycle_checks", name="AI lifecycle compatibility checks", version="1")
public final class NativeAILifecycleChecks {
    final List<String> report = new ArrayList<String>();
    int ticks, failed;
    boolean done, restart;

    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        GravityNetwork.initialize();
        EntityRegistry.registerModEntity(Actor.class, "lifecycle_actor", 0, this, 64, 1, true);
        restart = Files.exists(Paths.get("lifecycle-fixture.dat"));
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) throws Exception {
        if (event.getSide().isClient()) Class.forName("dev.srpbackport.tests.NativeAILifecycleClientChecks").getMethod("initialize").invoke(null);
        else FMLCommonHandler.instance().bus().register(this);
    }

    public static class Actor extends LegacyEntityMob {
        public int callbacks, fieldInitializer = 73, observedInitializer, targetTicks, actionTicks, starts, aiCycles, legacyCycles, mobTicks;
        public boolean ready, targetBeforeAction = true, mobAfterSelectors = true, controlsAfterMob = true;
        public String trace;
        public Actor(World world) {
            super(world);
            trace = (trace == null ? "" : trace) + "/baseCtor";
            // Mimics the original parasite base adding shared goals after the native constructor.
            tasks.addTask(10, new EntityAIBase() { @Override public boolean shouldExecute() { return false; } });
            moveHelper = new net.minecraft.entity.ai.EntityMoveHelper(this) {
                @Override public void onUpdateMoveHelper() { controlsAfterMob &= mobTicks == actionTicks; super.onUpdateMoveHelper(); }
            };
            func_110163_bv();
        }
        @Override protected void initEntityAI() {
            super.initEntityAI();
            callbacks++;
            observedInitializer = fieldInitializer;
            ready = tasks != null && targetTasks != null && getNavigator() != null && getEntitySenses() != null
                && getMoveHelper() != null && getLookHelper() != null && getJumpHelper() != null
                && getEntityAttribute(SharedMonsterAttributes.maxHealth) != null
                && getEntityAttribute(SharedMonsterAttributes.followRange) != null
                && getEntityAttribute(ModernAttributes.ARMOR) != null;
            for (float chance : equipmentDropChances) ready &= chance == 0.085F;
            trace = "base";
            targetTasks.addTask(0, new EntityAIBase() {
                @Override public boolean shouldExecute() { return true; }
                @Override public boolean continueExecuting() { return true; }
                @Override public void updateTask() { targetTicks++; }
            });
            tasks.addTask(0, new EntityAIBase() {
                @Override public boolean shouldExecute() { return true; }
                @Override public boolean continueExecuting() { return true; }
                @Override public void startExecuting() { starts++; }
                @Override public void updateTask() { targetBeforeAction &= targetTicks == actionTicks + 1; actionTicks++; }
            });
        }
        @Override protected void updateAITasks() { aiCycles++; super.updateAITasks(); }
        @Override protected void updateEntityActionState() { legacyCycles++; }
        @Override protected void updateAITick() { super.updateAITick(); mobTicks++; mobAfterSelectors &= actionTicks == mobTicks && targetTicks == mobTicks; }
        public boolean usesTasks() { return isAIEnabled(); }
    }
    public static final class Derived extends Actor {
        int derivedInitializer = 91, observedDerived, derivedCallbacks;
        Derived(World world) { super(world); trace += "/derivedCtor"; }
        @Override protected void initEntityAI() {
            super.initEntityAI();
            derivedCallbacks++;
            observedDerived = derivedInitializer;
            trace += "/derived";
        }
    }
    static final class SuppressedParentTick extends Actor {
        SuppressedParentTick(World world) { super(world); }
        @Override protected void updateAITick() { mobTicks++; }
    }
    static final class Empty extends LegacyEntityMob {
        int callbacks;
        Empty(World world) { super(world); }
        @Override protected void initEntityAI() { callbacks++; }
    }
    static final class Plain extends EntityMob {
        int legacyCycles, aiCycles;
        Plain(World world) { super(world); }
        @Override protected void updateEntityActionState() { legacyCycles++; }
        @Override protected void updateAITasks() { aiCycles++; super.updateAITasks(); }
        boolean usesTasks() { return isAIEnabled(); }
    }
    static final class Rejected extends LegacyEntityMob {
        Rejected(World world) { super(world); }
        @Override protected void initEntityAI() { throw new IllegalStateException("lifecycle fixture rejection"); }
    }
    static final class Revenge extends LegacyEntityMob {
        int callbacks;
        Revenge(World world) { super(world); func_110163_bv(); }
        @Override protected void initEntityAI() { callbacks++; targetTasks.addTask(1, new EntityAIHurtByTarget(this, false)); }
    }
    interface Check { void run() throws Exception; }
    static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    void test(String name, Check body) {
        try { body.run(); report.add("PASS: " + name); }
        catch (Throwable t) { failed++; report.add("FAIL: " + name + " :: " + t); t.printStackTrace(); }
    }
    static void originalGate(EntityLiving actor, World world) throws Exception {
        Class.forName("dev.srpbackport.tests.reference.OriginalAILifecycleGate")
            .getMethod("constructed", EntityLiving.class, World.class).invoke(null, actor, world);
    }
    void initial(WorldServer world) {
        test("Server callback runs once with native helpers, schedulers, attributes and drop chances ready", () -> {
            Actor a = new Actor(world);
            check(a.callbacks == 1 && a.ready && a.usesTasks(), "Uninitialized or duplicate callback");
            check(a.tasks.taskEntries.size() == 2 && a.targetTasks.taskEntries.size() == 1, "Shared constructor goals lost");
        });
        test("Virtual initialization precedes subclass field initializers and preserves super/constructor order", () -> {
            Derived a = new Derived(world);
            check(a.observedInitializer == 0 && a.fieldInitializer == 73, "Base field initialized before callback");
            check(a.observedDerived == 0 && a.derivedInitializer == 91, "Derived field initialized before callback");
            check(a.callbacks == 1 && a.derivedCallbacks == 1, "Super callback duplicated");
            check("base/derived/baseCtor/derivedCtor".equals(a.trace), "Order: " + a.trace);
        });
        test("Empty overrides stay empty and native mobs retain their existing AI selection", () -> {
            Empty a = new Empty(world);
            check(a.callbacks == 1 && a.tasks.taskEntries.isEmpty() && a.targetTasks.taskEntries.isEmpty(), "Invented default goals");
            Plain plain = new Plain(world);
            plain.setPosition(0, 80, 0);
            plain.onLivingUpdate();
            check(!plain.usesTasks() && plain.legacyCycles == 1 && plain.aiCycles == 0, "Native fallback changed");
            check(!(((Object)plain) instanceof EntityAIInitializer) && !(((Object)new EntityZombie(world)) instanceof EntityAIInitializer), "Native entity opted in");
        });
        test("Living updates run both native task selectors in order without entering the old AI fallback", () -> {
            Actor a = new Actor(world);
            a.setPosition(0, 80, 0);
            for (int i = 0; i < 24; i++) { a.ticksExisted++; a.onLivingUpdate(); }
            check(a.aiCycles == 24 && a.legacyCycles == 0, "Wrong scheduling branch");
            check(a.targetTicks == 24 && a.actionTicks == 24 && a.starts == 1 && a.targetBeforeAction, "Goal execution/order");
        });
        test("Modern mob-tick overrides run after selectors and before controls, even without a super call", () -> {
            Actor a = new Actor(world); a.setPosition(0, 80, 0);
            for (int i = 0; i < 12; i++) { a.ticksExisted++; a.onLivingUpdate(); }
            check(a.mobTicks == 12 && a.mobAfterSelectors && a.controlsAfterMob, "Mob callback ordering");
            SuppressedParentTick b = new SuppressedParentTick(world); b.setPosition(0, 80, 0);
            for (int i = 0; i < 12; i++) { b.ticksExisted++; b.onLivingUpdate(); }
            check(b.mobTicks == 12 && b.actionTicks == 12 && b.targetTicks == 12 && b.legacyCycles == 0, "Override suppressed scheduler");
        });
        test("Constructor-registered modern retaliation executes through the real living update path", () -> {
            Revenge a = new Revenge(world), attacker = new Revenge(world);
            a.setPosition(0, 80, 0); attacker.setPosition(2, 80, 0);
            a.ticksExisted = 100; a.setRevengeTarget(attacker);
            for (int i = 0; i < 8; i++) { a.ticksExisted++; a.onLivingUpdate(); }
            check(a.callbacks == 1 && a.getAttackTarget() == attacker, "Revenge goal was never scheduled");
        });
        test("Spawn and NBT loading do not reinitialize or duplicate registered tasks", () -> {
            Actor a = new Actor(world); a.onSpawnWithEgg(null);
            NBTTagCompound tag = new NBTTagCompound(); a.writeToNBT(tag);
            Actor b = new Actor(world); b.readFromNBT(tag); b.readFromNBT(tag); b.onSpawnWithEgg(null);
            check(b.callbacks == 1 && b.tasks.taskEntries.size() == 2 && b.targetTasks.taskEntries.size() == 1, "Load duplicated goals");
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream("lifecycle-fixture.dat"))) { CompressedStreamTools.write(tag, out); }
        });
        test("Server and null-world gating agree with the retained original 1.12 constructor instruction slice", () -> {
            Actor port = new Actor(world), original = new Actor(world);
            port.callbacks = original.callbacks = 0;
            EntityAILifecycle.constructed(port, world); originalGate(original, world);
            check(port.callbacks == 1 && original.callbacks == 1, "Server gate mismatch");
            EntityAILifecycle.constructed(port, null); originalGate(original, null);
            check(port.callbacks == 1 && original.callbacks == 1, "Null-world gate mismatch");
            EntityAILifecycle.constructed(new Plain(world), world);
        });
        test("Exceptions from a mob initializer propagate instead of silently creating a partly initialized mob", () -> {
            boolean rejected = false;
            try { new Rejected(world); } catch (IllegalStateException expected) { rejected = "lifecycle fixture rejection".equals(expected.getMessage()); }
            check(rejected, "Initializer exception swallowed");
        });
    }
    void restarted(WorldServer world) {
        test("A new dedicated-server process reconstructs goals once and runs them after loading saved NBT", () -> {
            NBTTagCompound tag;
            try (DataInputStream in = new DataInputStream(new FileInputStream("lifecycle-fixture.dat"))) { tag = CompressedStreamTools.read(in); }
            Actor a = new Actor(world); a.readFromNBT(tag); a.setPosition(0, 80, 0);
            for (int i = 0; i < 8; i++) { a.ticksExisted++; a.onLivingUpdate(); }
            check(a.callbacks == 1 && a.actionTicks == 8 && a.targetTicks == 8 && a.legacyCycles == 0, "Reloaded AI lifecycle");
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event) {
        if (done || event.phase != TickEvent.Phase.END || ++ticks != 20) return;
        try { if (restart) restarted(MinecraftServer.getServer().worldServerForDimension(0)); else initial(MinecraftServer.getServer().worldServerForDimension(0)); }
        catch (Throwable t) { failed++; report.add("FAIL: setup :: " + t); t.printStackTrace(); }
        finally {
            done = true; report.add("Failed: " + failed);
            try { Files.write(Paths.get("lifecycle-" + (restart ? "restart" : "initial") + ".txt"), report, StandardCharsets.UTF_8); }
            catch (IOException e) { throw new RuntimeException(e); }
            MinecraftServer.getServer().initiateShutdown();
        }
    }
}
