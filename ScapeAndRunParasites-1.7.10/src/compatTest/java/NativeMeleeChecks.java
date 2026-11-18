package dev.srpbackport.tests;

import java.io.*;import java.lang.reflect.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.*;import net.minecraft.entity.ai.*;import net.minecraft.entity.player.*;import net.minecraft.pathfinding.*;import net.minecraft.server.MinecraftServer;import net.minecraft.world.*;import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.FakePlayer;import com.mojang.authlib.GameProfile;
import com.dhanantry.scapeandrunparasites.backport.entity.LegacyEntityMob;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityNetwork;

@Mod(modid="srp_melee_checks",name="Melee compatibility checks",version="1")
public final class NativeMeleeChecks {
    final List<String> report=new ArrayList<String>();int ticks,failed,pairedScenarios;boolean done;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event){
        GravityNetwork.initialize();
        EntityRegistry.registerModEntity(Combatant.class,"melee_combatant",0,this,64,1,true);
        EntityRegistry.registerModEntity(Victim.class,"melee_victim",1,this,64,1,true);
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event)throws Exception{
        if(event.getSide().isClient())Class.forName("dev.srpbackport.tests.NativeMeleeClientChecks").getMethod("initialize").invoke(null);
        else FMLCommonHandler.instance().bus().register(this);
    }
    static void check(boolean value,String why){if(!value)throw new AssertionError(why);}
    public static class Actor extends EntityCreature {
        boolean visible=true,home=true,attackResult=true;int homeX,homeY,homeZ,hits,swings,clock;final List<Integer> hitTicks=new ArrayList<Integer>();
        Nav navigation;Senses senses;
        public Actor(World world){super(world);navigation=new Nav(this,world);senses=new Senses(this);setPosition(0,80,0);getRNG().setSeed(171012);}
        @Override public PathNavigate getNavigator(){return navigation==null?super.getNavigator():navigation;}
        @Override public EntitySenses getEntitySenses(){return senses==null?super.getEntitySenses():senses;}
        @Override public boolean isWithinHomeDistance(int x,int y,int z){homeX=x;homeY=y;homeZ=z;return home;}
        @Override public void swingItem(){swings++;}
        @Override public boolean attackEntityAsMob(Entity target){hits++;hitTicks.add(clock);return attackResult;}
    }
    static final class Senses extends EntitySenses {
        final Actor actor;Senses(Actor owner){super(owner);actor=owner;}
        @Override public boolean canSee(Entity target){return actor.visible;}
    }
    static final class Nav extends PathNavigate {
        final Actor actor;PathEntity candidate,current;boolean moving,moveSuccess=true;int queries,moves,clears,sets;double lastSpeed;
        Nav(Actor owner,World world){super(owner,world);actor=owner;candidate=new PathEntity(new PathPoint[]{new PathPoint(0,80,0),new PathPoint(1,80,0)});}
        @Override public PathEntity getPathToEntityLiving(Entity target){queries++;return candidate;}
        @Override public boolean setPath(PathEntity path,double speed){sets++;lastSpeed=speed;current=path;moving=path!=null;return moving;}
        @Override public boolean tryMoveToEntityLiving(Entity target,double speed){moves++;lastSpeed=speed;if(moveSuccess){moving=true;current=candidate;}return moveSuccess;}
        @Override public boolean noPath(){return !moving;}
        @Override public PathEntity getPath(){return current;}
        @Override public void clearPathEntity(){clears++;current=null;moving=false;}
    }
    public static class Combatant extends LegacyEntityMob {
        public int callbacks,hits,swings;
        public Combatant(World world){super(world);func_110163_bv();}
        @Override protected void initEntityAI(){callbacks++;tasks.addTask(1,new com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIAttackMelee(this,1.0,true));}
        @Override protected void applyEntityAttributes(){super.applyEntityAttributes();getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(80);getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2);getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);}
        @Override public void swingItem(){swings++;super.swingItem();}
        @Override public boolean attackEntityAsMob(Entity target){hits++;return super.attackEntityAsMob(target);}
    }
    public static class Victim extends LegacyEntityMob {
        public Victim(World world){super(world);func_110163_bv();}
        @Override protected void applyEntityAttributes(){super.applyEntityAttributes();getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(80);getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0);getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);}
    }
    interface PairCheck{String run(boolean original)throws Exception;}
    void test(String name,PairCheck body){
        try{String original=body.run(true),port=body.run(false);pairedScenarios+=2;check(original.equals(port),"Original/port trace mismatch\noriginal="+original+"\nport="+port);report.add("PASS: Original bytecode and port: "+name);}
        catch(Throwable error){failed++;report.add("FAIL: "+name+" :: "+error);error.printStackTrace();}
    }
    EntityAIBase goal(boolean original,EntityCreature actor,double speed,boolean memory)throws Exception{
        if(!original)return new com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIAttackMelee(actor,speed,memory);
        return(EntityAIBase)Class.forName("dev.srpbackport.tests.reference.EntityAIAttackMelee").getConstructor(EntityCreature.class,double.class,boolean.class).newInstance(actor,speed,memory);
    }
    static Field field(Object instance,String name)throws Exception{Class<?> type=instance.getClass();while(type!=null){try{Field f=type.getDeclaredField(name);f.setAccessible(true);return f;}catch(NoSuchFieldException e){type=type.getSuperclass();}}throw new NoSuchFieldException(name);}
    static int integer(Object goal,String name)throws Exception{return field(goal,name).getInt(goal);}
    static double reach(Object goal,EntityLivingBase target)throws Exception{Method m=goal.getClass().getDeclaredMethod("getAttackReachSqr",EntityLivingBase.class);m.setAccessible(true);return(Double)m.invoke(goal,target);}
    static String state(Object goal,Actor actor)throws Exception{return actor.navigation.queries+","+actor.navigation.moves+","+actor.hits+","+actor.swings+","+integer(goal,"attackTick")+","+integer(goal,"delayCounter")+","+field(goal,"targetX").getDouble(goal)+","+field(goal,"targetY").getDouble(goal)+","+field(goal,"targetZ").getDouble(goal);}
    Actor target(WorldServer world,Actor actor,double x,double y,double z){Actor t=new Actor(world);t.setPosition(x,y,z);actor.setAttackTarget(t);return t;}
    void tests(WorldServer world){
        test("Null/dead targets reject activation; a valid path is queried on every activation attempt",original->{
            Actor a=new Actor(world);EntityAIBase ai=goal(original,a,1.3,false);check(ai.getMutexBits()==3&&!ai.shouldExecute(),"Null target/mutex");Actor t=target(world,a,8,80,0);t.setHealth(0);check(!ai.shouldExecute()&&a.navigation.queries==0,"Dead target queried");t.setHealth(20);check(ai.shouldExecute()&&ai.shouldExecute()&&a.navigation.queries==2,"Activation throttled");return state(ai,a);
        });
        test("Targets already within reach remain attackable without a path",original->{
            Actor a=new Actor(world);Actor t=target(world,a,0.5,80,0);a.navigation.candidate=null;EntityAIBase ai=goal(original,a,1,false);check(ai.shouldExecute(),"Close target rejected");t.setPosition(12,80,0);check(!ai.shouldExecute(),"Unreachable distant target accepted");t.setPosition(0.5,80,0);ai.shouldExecute();ai.startExecuting();ai.updateTask();check(a.hits==1&&a.swings==1,"Close no-path attack");return state(ai,a);
        });
        test("Short memory follows path state; long memory follows home bounds with direct floor-Y positions",original->{
            Actor a=new Actor(world);Actor t=target(world,a,-0.25,80.75,-0.25);EntityAIBase shortGoal=goal(original,a,1,false),longGoal=goal(original,a,1,true);check(!shortGoal.continueExecuting(),"Absent path continued");a.navigation.moving=true;a.home=false;check(shortGoal.continueExecuting()&&!longGoal.continueExecuting(),"Memory/home policy");a.home=true;check(longGoal.continueExecuting()&&a.homeX==-1&&a.homeY==80&&a.homeZ==-1,"Home coordinates");t.setHealth(0);check(!shortGoal.continueExecuting()&&!longGoal.continueExecuting(),"Dead continuation");a.setAttackTarget(null);check(!longGoal.continueExecuting(),"Null continuation");return a.homeX+","+a.homeY+","+a.homeZ;
        });
        test("Native creative or damage-immune players stop long-memory pursuit and clear on reset",original->{
            Actor a=new Actor(world);EntityAIBase ai=goal(original,a,1,true);FakePlayer p=new FakePlayer(world,new GameProfile(UUID.randomUUID(),"MeleeQA"));p.setPosition(1,80,0);StringBuilder out=new StringBuilder();
            for(boolean creative:new boolean[]{false,true})for(boolean immune:new boolean[]{false,true}){p.capabilities.isCreativeMode=creative;p.capabilities.disableDamage=immune;a.setAttackTarget(p);boolean active=ai.continueExecuting();check(active==!(creative||immune),"Native player guard");ai.resetTask();check((a.getAttackTarget()==null)==(creative||immune),"Reset target retention");out.append(active).append(a.getAttackTarget()==null);}
            a.setAttackTarget(new Actor(world));ai.resetTask();check(a.getAttackTarget()!=null,"Ordinary reset target lost");return out.toString();
        });
        test("Start uses the selected path and configured speed, and reset clears native navigation",original->{
            Actor a=new Actor(world);target(world,a,6,80,0);EntityAIBase ai=goal(original,a,1.75,true);check(ai.shouldExecute(),"Path missing");field(ai,"delayCounter").setInt(ai,90);ai.startExecuting();check(a.navigation.current==a.navigation.candidate&&a.navigation.lastSpeed==1.75&&integer(ai,"delayCounter")==0,"Start contract");ai.resetTask();check(a.navigation.clears==1&&!a.navigation.moving&&a.getAttackTarget()!=null,"Reset path");return state(ai,a);
        });
        test("Attacks and unarmed swings repeat at exact twenty-update intervals",original->{
            Actor a=new Actor(world);target(world,a,0.5,80,0);EntityAIBase ai=goal(original,a,1,true);ai.shouldExecute();ai.startExecuting();for(int i=0;i<61;i++){a.clock=i;ai.updateTask();}check(a.hitTicks.equals(Arrays.asList(0,20,40,60))&&a.swings==4,"Cadence "+a.hitTicks);return state(ai,a)+a.hitTicks;
        });
        test("Rejected damage still consumes the attack cooldown, and out-of-reach updates clamp it at zero",original->{
            Actor a=new Actor(world);Actor t=target(world,a,0.5,80,0);a.attackResult=false;EntityAIBase ai=goal(original,a,1,true);ai.updateTask();check(a.hits==1&&integer(ai,"attackTick")==20,"Rejected damage cooldown");t.setPosition(100,80,0);for(int i=0;i<50;i++)ai.updateTask();check(a.hits==1&&integer(ai,"attackTick")==0,"Cooldown clamp");return state(ai,a);
        });
        test("Sight gates short-memory repathing but does not add a new close-range attack veto",original->{
            Actor a=new Actor(world);target(world,a,0.5,80,0);a.visible=false;EntityAIBase ai=goal(original,a,1,false);ai.updateTask();check(a.navigation.moves==0&&a.hits==1,"Sight policy changed");a.visible=true;ai.updateTask();check(a.navigation.moves==1,"Visible repath missing");Actor b=new Actor(world);target(world,b,8,80,0);b.visible=false;EntityAIBase longGoal=goal(original,b,1,true);longGoal.updateTask();check(b.navigation.moves==1,"Long memory lost target");return state(ai,a)+"/"+state(longGoal,b);
        });
        test("Reach distance and recorded target Y use bounding-box minY rather than the entity origin",original->{
            Actor a=new Actor(world);Actor t=target(world,a,0.5,100,0);t.boundingBox.minY=80;a.navigation.candidate=null;EntityAIBase ai=goal(original,a,1,true);check(ai.shouldExecute(),"Bounding box activation");ai.updateTask();check(a.hits==1&&field(ai,"targetY").getDouble(ai)==80,"Bounding box update");return state(ai,a);
        });
        test("Ten thousand randomized reach calculations preserve the original float evaluation order",original->{
            Actor a=new Actor(world);Actor t=new Actor(world);EntityAIBase ai=goal(original,a,1,true);Random random=new Random(171012);long digest=0;for(int i=0;i<10000;i++){a.width=Float.intBitsToFloat(random.nextInt(0x50000000));t.width=random.nextFloat()*32;digest=digest*31+Double.doubleToLongBits(reach(ai,t));}return Long.toHexString(digest);
        });
        test("Forty thousand original/port updates agree on retry timing, movement thresholds, sight and failed paths",original->{
            StringBuilder out=new StringBuilder();Random input=new Random(1221710);
            for(int scenario=0;scenario<100;scenario++){
                Actor a=new Actor(world);Actor t=target(world,a,8,80,1);a.getRNG().setSeed(scenario);EntityAIBase ai=goal(original,a,0.5+scenario*0.01,scenario%2==0);ai.shouldExecute();ai.startExecuting();
                for(int i=0;i<200;i++){a.clock=i;a.visible=input.nextBoolean();a.navigation.moveSuccess=input.nextBoolean();if(i%3==0)t.setPosition(new double[]{1,8,16,17,32,33,70}[input.nextInt(7)],80+input.nextInt(3),input.nextDouble()*3);ai.updateTask();out.append(state(ai,a)).append(';');}
            }return out.toString();
        });
        try{
            for(int x=-3;x<=3;x++)for(int z=-3;z<=3;z++)world.setBlock(x,79,z,Blocks.stone,0,3);
            Combatant a=new Combatant(world);Victim target=new Victim(world);a.setPosition(0,80,0);target.setPosition(0.8,80,0);a.setAttackTarget(target);float before=target.getHealth();for(int i=0;i<41;i++){target.onUpdate();a.ticksExisted++;a.onLivingUpdate();}check(a.callbacks==1&&a.hits==3&&a.swings==3&&target.getHealth()==before-6,"Native scheduler/damage "+a.hits+" health="+target.getHealth());report.add("PASS: Constructor-registered goal executes through native living updates and inflicts real native damage");
        }catch(Throwable t){failed++;report.add("FAIL: Native living update combat :: "+t);t.printStackTrace();}
        try{
            for(int x=96;x<=114;x++)for(int z=-3;z<=3;z++)world.setBlock(x,79,z,Blocks.stone,0,3);
            Combatant a=new Combatant(world);Victim target=new Victim(world);a.setPosition(100.5,80,0.5);target.setPosition(110.5,80,0.5);a.onGround=true;a.setAttackTarget(target);double initial=a.getDistanceSqToEntity(target);for(int i=0;i<160&&a.hits==0;i++){a.ticksExisted++;a.onUpdate();}check(a.hits>0&&a.getDistanceSqToEntity(target)<initial,"Native path did not reach target: x="+a.posX+" hit="+a.hits);report.add("PASS: Real native pathfinding, movement helpers and collision travel reach a distant target and attack it");
        }catch(Throwable t){failed++;report.add("FAIL: Native navigation combat :: "+t);t.printStackTrace();}
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){
        if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;
        try{tests(MinecraftServer.getServer().worldServerForDimension(0));}catch(Throwable t){failed++;report.add("FAIL: setup :: "+t);t.printStackTrace();}
        finally{done=true;report.add("Paired scenario executions: "+pairedScenarios);report.add("Failed: "+failed);try{Files.write(Paths.get("melee-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}
    }
}
