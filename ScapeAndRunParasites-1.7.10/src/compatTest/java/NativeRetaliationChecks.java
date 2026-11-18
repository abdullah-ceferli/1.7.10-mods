package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.lang.reflect.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.*;import net.minecraft.entity.*;import net.minecraft.entity.ai.*;import net.minecraft.entity.passive.EntityWolf;import net.minecraft.entity.player.*;import net.minecraft.pathfinding.*;import net.minecraft.scoreboard.*;import net.minecraft.server.MinecraftServer;import net.minecraftforge.common.util.FakePlayer;import com.mojang.authlib.GameProfile;
@Mod(modid="srp_retaliation_checks",name="Retaliation compatibility checks",version="1")
public final class NativeRetaliationChecks {
    final List<String> report=new ArrayList<String>();final List<Entity> spawned=new ArrayList<Entity>();int ticks,failed,scenario;boolean done;
    @Mod.EventHandler public void init(FMLInitializationEvent event){FMLCommonHandler.instance().bus().register(this);}
    static void check(boolean value,String why){if(!value)throw new AssertionError(why);}
    public static class Actor extends EntityCreature {
        boolean visible=true,allowed=true,home=true;Team team;Senses sense;Nav nav;int homeY,homeX,homeZ;
        public Actor(World w){super(w);sense=new Senses(this);nav=new Nav(this,w);}
        @Override public net.minecraft.entity.ai.EntitySenses getEntitySenses(){return sense==null?super.getEntitySenses():sense;}
        @Override public PathNavigate getNavigator(){return nav==null?super.getNavigator():nav;}
        @Override public boolean canAttackClass(Class type){return allowed;}
        @Override public Team getTeam(){return team;}
        @Override public boolean isWithinHomeDistance(int x,int y,int z){homeX=x;homeY=y;homeZ=z;return home;}
        void schedule(EntityAIBase goal){targetTasks.addTask(1,goal);targetTasks.onUpdateTasks();}
    }
    static class SubActor extends Actor {SubActor(World w){super(w);}}
    static class SubSubActor extends SubActor {SubSubActor(World w){super(w);}}
    static class OtherActor extends EntityCreature {OtherActor(World w){super(w);}}
    static class Senses extends net.minecraft.entity.ai.EntitySenses {
        final Actor actor;Senses(Actor a){super(a);actor=a;}@Override public boolean canSee(Entity target){return actor.visible;}
    }
    static class Nav extends PathNavigate {
        PathEntity path;int calls;Nav(EntityLiving owner,World world){super(owner,world);}
        @Override public PathEntity getPathToEntityLiving(Entity candidate){calls++;return path;}
    }
    static class TameActor extends EntityWolf {
        EntityLivingBase actualOwner;TameActor(World w){super(w);setTamed(true);}
        @Override public EntityLivingBase getOwner(){return actualOwner;}
    }
    public static class TargetProbe extends com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAITarget {
        public TargetProbe(EntityCreature owner,boolean sight,boolean nearby){super(owner,sight,nearby);}
        @Override public boolean shouldExecute(){return true;}
        public boolean suitable(EntityLivingBase candidate,boolean invulnerable){return isSuitableTarget(candidate,invulnerable);}
    }
    EntityAIBase probe(boolean original,Actor actor,boolean sight,boolean nearby)throws Exception{
        return original?(EntityAIBase)Class.forName("dev.srpbackport.tests.reference.TargetProbe").getConstructor(EntityCreature.class,boolean.class,boolean.class).newInstance(actor,sight,nearby):new TargetProbe(actor,sight,nearby);
    }
    boolean suitable(EntityAIBase ai,EntityLivingBase target)throws Exception{return (Boolean)ai.getClass().getMethod("suitable",EntityLivingBase.class,boolean.class).invoke(ai,target,false);}
    interface PairCheck{void run(boolean original)throws Exception;}
    void test(String name,PairCheck check,WorldServer world){
        try{for(boolean original:new boolean[]{true,false}){scenario++;try{check.run(original);}finally{for(Entity entity:spawned)world.removeEntity(entity);spawned.clear();}}report.add("PASS: Original bytecode and port: "+name);}
        catch(Throwable problem){failed++;report.add("FAIL: "+name+" :: "+problem);problem.printStackTrace();}
    }
    <T extends Entity>T place(T entity,double dx,double dy,double dz){
        entity.setPosition(scenario*64+dx,80+dy,dz);entity.forceSpawn=true;check(entity.worldObj.spawnEntityInWorld(entity),"Could not spawn native actor");spawned.add(entity);return entity;
    }
    Actor actor(WorldServer w,double x,double y,double z){return place(new Actor(w),x,y,z);}
    void revenge(EntityLivingBase owner,EntityLivingBase attacker){owner.ticksExisted=100;owner.setRevengeTarget(attacker);}
    EntityAIBase goal(boolean original,EntityCreature actor,boolean help,Class<?>... excluded)throws Exception{
        if(!original)return new com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget(actor,help,excluded);
        return (EntityAIBase)Class.forName("dev.srpbackport.tests.reference.EntityAIHurtByTarget").getConstructor(EntityCreature.class,boolean.class,Class[].class).newInstance(actor,help,excluded);
    }
    boolean eligible(boolean original,EntityLiving actor,EntityLivingBase target,boolean allow,boolean sight)throws Exception{
        if(!original)return com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAITarget.isSuitableTarget(actor,target,allow,sight);
        return (Boolean)Class.forName("dev.srpbackport.tests.reference.EntityAITarget").getMethod("isSuitableTarget",EntityLiving.class,EntityLivingBase.class,boolean.class,boolean.class).invoke(null,actor,target,allow,sight);
    }
    void tests(WorldServer w){
        test("revenge timer gates execution; start sets target and mutex bits; reset cannot replay old hurt",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0);EntityAIBase ai=goal(original,owner,false);check(!ai.shouldExecute(),"No hit started AI");revenge(owner,attacker);check(ai.shouldExecute(),"New hit ignored");check(ai.getMutexBits()==1,"Mutex bits");ai.startExecuting();check(owner.getAttackTarget()==attacker,"Wrong attacker");check(!ai.shouldExecute(),"Same hit restarted");ai.resetTask();check(owner.getAttackTarget()==null&&!ai.shouldExecute(),"Reset replayed hit");owner.ticksExisted++;owner.setRevengeTarget(attacker);check(ai.shouldExecute(),"New revenge timestamp ignored");
        },w);
        test("initial targeting requires sight, attack eligibility and home distance, with direct floor-Y coordinates",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,-0.25,0.75,-0.25);revenge(owner,attacker);EntityAIBase ai=goal(original,owner,false);owner.visible=false;check(!ai.shouldExecute(),"Invisible attacker accepted");owner.visible=true;owner.allowed=false;check(!ai.shouldExecute(),"Attack-class veto");owner.allowed=true;owner.home=false;check(!ai.shouldExecute(),"Home veto");owner.home=true;check(ai.shouldExecute()&&owner.homeY==80&&owner.homeX==(int)(scenario*64-1)&&owner.homeZ==-1,"Block position rounding changed");
        },w);
        test("unseen attacker persists exactly 300 ticks, and sight refreshes that memory",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0);revenge(owner,attacker);EntityAIBase ai=goal(original,owner,false);ai.startExecuting();owner.visible=false;for(int i=0;i<300;i++)check(ai.continueExecuting(),"Memory expired at "+i);check(!ai.continueExecuting(),"301st unseen tick accepted");owner.visible=true;check(ai.continueExecuting(),"Sight did not restore target");owner.visible=false;for(int i=0;i<300;i++)check(ai.continueExecuting(),"Refreshed memory expired early");
        },w);
        test("fallback target is restored, while dead or out-of-range targets terminate continuation",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0);revenge(owner,attacker);EntityAIBase ai=goal(original,owner,false);ai.startExecuting();owner.setAttackTarget(null);check(ai.continueExecuting()&&owner.getAttackTarget()==attacker,"Fallback lost");attacker.setPosition(owner.posX+17,80,0);check(!ai.continueExecuting(),"Range ignored");attacker.setPosition(owner.posX+2,80,0);attacker.setHealth(0);check(!ai.continueExecuting(),"Dead target retained");
        },w);
        test("teams reject initial targets, continuation and friendly helper recruitment",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0),helper=actor(w,3,0,0);Team team=new ScorePlayerTeam(new Scoreboard(),"allies");owner.team=attacker.team=team;revenge(owner,attacker);EntityAIBase ai=goal(original,owner,true);check(!ai.shouldExecute(),"Teammate selected");owner.team=null;check(ai.shouldExecute(),"Enemy rejected");helper.team=team;ai.startExecuting();check(helper.getAttackTarget()==null,"Friendly helper recruited");owner.team=team;check(!ai.continueExecuting(),"Friendly continuation");
        },w);
        test("help toggle preserves ordinary allies, busy allies and other-species selection",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0),idle=actor(w,3,0,0),busy=actor(w,4,0,0),otherEnemy=actor(w,5,0,0);OtherActor other=place(new OtherActor(w),3,0,2);busy.setAttackTarget(otherEnemy);revenge(owner,attacker);goal(original,owner,false).startExecuting();check(idle.getAttackTarget()==null,"Help disabled");goal(original,owner,true).startExecuting();check(idle.getAttackTarget()==attacker&&busy.getAttackTarget()==otherEnemy&&other.getAttackTarget()==null,"Recruitment scope");
        },w);
        test("helper exclusions match exact runtime classes and do not reject the attacker or subclasses",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0),excluded=actor(w,3,0,0);SubActor child=place(new SubActor(w),4,0,0);revenge(owner,attacker);EntityAIBase ai=goal(original,owner,true,Actor.class);check(ai.shouldExecute(),"Excluded helper type rejected attacker");ai.startExecuting();check(excluded.getAttackTarget()==null&&child.getAttackTarget()==attacker,"Exact-class rule became instanceof");
        },w);
        test("helper search retains full follow range, ten vertical blocks and the one-block origin box",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0);owner.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16);Actor inside=actor(w,16.9,0,0),above=actor(w,0,10.9,0),outside=actor(w,18,0,0),tooHigh=actor(w,0,11.1,0);revenge(owner,attacker);goal(original,owner,true).startExecuting();check(inside.getAttackTarget()==attacker&&above.getAttackTarget()==attacker,"Original expanded query box shrunk");check(outside.getAttackTarget()==null&&tooHigh.getAttackTarget()==null,"Search bounds expanded");
        },w);
        test("tamed helpers require the same resolved owner object, including unresolved owners",original->{
            EntityPlayer first=new FakePlayer(w,new GameProfile(UUID.fromString("74d7bf4b-baa3-4208-b444-c9db759274bc"),"OwnerA")),second=new FakePlayer(w,new GameProfile(UUID.fromString("74d7bf4b-baa3-4208-b444-c9db759274bd"),"OwnerB"));TameActor owner=place(new TameActor(w),0,0,0),same=place(new TameActor(w),3,0,0),different=place(new TameActor(w),4,0,0);Actor attacker=actor(w,2,0,0);owner.actualOwner=same.actualOwner=first;different.actualOwner=second;owner.func_152115_b(first.getUniqueID().toString());same.func_152115_b(first.getUniqueID().toString());different.func_152115_b(second.getUniqueID().toString());revenge(owner,attacker);goal(original,owner,true).startExecuting();check(same.getAttackTarget()==attacker&&different.getAttackTarget()==null,"Owner filter");same.setAttackTarget(null);owner.actualOwner=same.actualOwner=null;goal(original,owner,true).startExecuting();check(same.getAttackTarget()==attacker,"Unresolved-owner equality changed");
        },w);
        test("static suitability rejects own owner, shared owner UUID, self, null and dead candidates",original->{
            EntityPlayer player=new FakePlayer(w,new GameProfile(UUID.fromString("74d7bf4b-baa3-4208-b444-c9db759274bc"),"OwnerA"));TameActor owner=place(new TameActor(w),0,0,0),sibling=place(new TameActor(w),3,0,0);owner.actualOwner=player;owner.func_152115_b(player.getUniqueID().toString());sibling.func_152115_b(player.getUniqueID().toString().toUpperCase(Locale.ROOT));check(!eligible(original,owner,player,true,false)&&!eligible(original,owner,sibling,true,false),"Owner identity");check(!eligible(original,owner,owner,true,false)&&!eligible(original,owner,null,true,false),"Null/self");Actor dead=actor(w,2,0,0);dead.setHealth(0);check(!eligible(original,owner,dead,true,false),"Dead target");
        },w);
        test("player invulnerability is honored and continuation stops when a player becomes invulnerable",original->{
            Actor owner=actor(w,0,0,0);FakePlayer player=new FakePlayer(w,new GameProfile(UUID.randomUUID(),"TargetQA"));player.setPosition(owner.posX+2,80,0);player.capabilities.disableDamage=true;check(!eligible(original,owner,player,false,false)&&eligible(original,owner,player,true,false),"Allow-invulnerable flag");player.capabilities.disableDamage=false;revenge(owner,player);EntityAIBase ai=goal(original,owner,false);ai.startExecuting();check(ai.continueExecuting(),"Normal player rejected");player.capabilities.disableDamage=true;check(!ai.continueExecuting(),"Invulnerable player retained");
        },w);
        test("nearby path reachability rejects null paths/endpoints and retains its retry cache",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0);EntityAIBase ai=probe(original,owner,false,true);check(!suitable(ai,attacker)&&owner.nav.calls==1,"Null path");owner.nav.path=new PathEntity(new PathPoint[]{new PathPoint((int)attacker.posX,80,0)});check(!suitable(ai,attacker)&&owner.nav.calls==1,"Failed path retry cache ignored");boolean found=false;for(int n=0;n<15;n++)if(suitable(ai,attacker)){found=true;break;}check(found&&owner.nav.calls==2,"Path never retried");ai.startExecuting();owner.nav.path=new PathEntity(new PathPoint[0]);check(!suitable(ai,attacker),"Empty endpoint path");
        },w);
        test("nearby endpoint tolerance, custom unseen memory and reset match the original base goal",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0);EntityAIBase ai=probe(original,owner,true,true);owner.nav.path=new PathEntity(new PathPoint[]{new PathPoint((int)attacker.posX+1,80,1)});check(suitable(ai,attacker),"Endpoint within squared distance 2.25 rejected");ai.startExecuting();owner.nav.path=new PathEntity(new PathPoint[]{new PathPoint((int)attacker.posX+2,80,0)});check(!suitable(ai,attacker),"Distant endpoint accepted");ai.startExecuting();owner.setAttackTarget(attacker);ai.getClass().getMethod("setUnseenMemoryTicks",int.class).invoke(ai,2);owner.visible=false;check(ai.continueExecuting()&&ai.continueExecuting()&&!ai.continueExecuting(),"Configured memory");ai.resetTask();check(!ai.continueExecuting()&&owner.getAttackTarget()==null,"Reset retained target");
        },w);
        test("native task scheduler selects the revenge goal and assigns its target",original->{
            Actor owner=actor(w,0,0,0),attacker=actor(w,2,0,0);revenge(owner,attacker);EntityAIBase ai=goal(original,owner,false);owner.schedule(ai);check(owner.getAttackTarget()==attacker,"Task scheduler did not execute goal");
        },w);
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;
        try{tests(MinecraftServer.getServer().worldServerForDimension(0));}catch(Throwable problem){failed++;report.add("FAIL: setup :: "+problem);problem.printStackTrace();}
        finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("retaliation-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}
    }
}
