package dev.srpbackport.tests;

import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.*;import net.minecraft.pathfinding.PathEntity;import net.minecraft.pathfinding.PathPoint;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.init.Blocks;import net.minecraft.util.*;
import com.dhanantry.scapeandrunparasites.backport.entity.LegacyEntityMob;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.*;
import com.dhanantry.scapeandrunparasites.backport.navigation.*;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

@Mod(modid="srp_navigation_checks",name="Navigation compatibility checks",version="1")
public final class NativeNavigationChecks {
    final List<String> report=new ArrayList<String>();int ticks,failed,scene;boolean done;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        GravityNetwork.initialize();
        EntityRegistry.registerModEntity(GroundActor.class,"ground",0,this,64,1,true);
        EntityRegistry.registerModEntity(FlyingActor.class,"flying",1,this,64,1,true);
        EntityRegistry.registerModEntity(SwimmingActor.class,"swimming",2,this,64,1,true);
        EntityRegistry.registerModEntity(ClimbingActor.class,"climbing",3,this,64,1,true);
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event)throws Exception {
        if(event.getSide().isClient())Class.forName("dev.srpbackport.tests.NativeNavigationClientChecks").getMethod("initialize").invoke(null);
        else FMLCommonHandler.instance().bus().register(this);
    }
    public static class GroundActor extends LegacyEntityMob {
        public int factories,callbacks;public boolean ready;
        public GroundActor(World w){super(w);func_110163_bv();}
        @Override protected net.minecraft.pathfinding.PathNavigate createNavigator(World w){factories++;return new PathNavigateGround(this,w);}
        @Override protected void initEntityAI(){callbacks++;ready=getNavigator() instanceof com.dhanantry.scapeandrunparasites.backport.navigation.PathNavigate;}
        @Override protected void applyEntityAttributes(){super.applyEntityAttributes();getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40);getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);}
        public void resize(float width,float height){setSize(width,height);}
    }
    public static class FlyingActor extends GroundActor {
        public FlyingActor(World w){super(w);setSize(0.6F,0.6F);moveHelper=new Steering(this);GravityAccess.set(this,true);}
        @Override protected net.minecraft.pathfinding.PathNavigate createNavigator(World w){factories++;return new PathNavigateFlying(this,w);}
    }
    public static class SwimmingActor extends FlyingActor {
        public SwimmingActor(World w){super(w);setSize(0.9F,0.9F);}
        @Override protected net.minecraft.pathfinding.PathNavigate createNavigator(World w){factories++;return new PathNavigateSwimmer(this,w);}
    }
    public static class ClimbingActor extends GroundActor {
        public ClimbingActor(World w){super(w);}
        @Override protected net.minecraft.pathfinding.PathNavigate createNavigator(World w){factories++;return new PathNavigateClimber(this,w);}
        @Override public boolean isOnLadder(){return isCollidedHorizontally;}
    }
    public static final class Steering extends FlightMoveHelper {
        Steering(EntityLiving e){super(e);}
        @Override public void onUpdateMoveHelper(){
            if(action==Action.MOVE_TO){double x=posX-entity.posX,y=posY-entity.posY,z=posZ-entity.posZ,d=Math.sqrt(x*x+y*y+z*z);action=Action.WAIT;
                if(d>0.05){double rate=Math.min(d,0.16*speed)/d;entity.motionX=x*rate;entity.motionY=y*rate;entity.motionZ=z*rate;return;}}
            entity.motionX*=0.5;entity.motionY*=0.5;entity.motionZ*=0.5;
        }
    }
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    interface Check{void run()throws Exception;}
    void test(String name,Check body){try{body.run();report.add("PASS: "+name);}catch(Throwable e){failed++;report.add("FAIL: "+name+" :: "+e);e.printStackTrace();}}
    static void fill(World w,int x0,int y0,int z0,int x1,int y1,int z1,net.minecraft.block.Block b){for(int x=x0;x<=x1;x++)for(int z=z0;z<=z1;z++)for(int y=y0;y<=y1;y++)w.setBlock(x,y,z,b,0,2);}
    int room(World w){int x=scene++*48;fill(w,x-5,76,-6,x+18,95,8,Blocks.air);fill(w,x-5,79,-6,x+18,79,8,Blocks.stone);return x;}
    static <T extends EntityLiving>T place(T actor,double x,double y,double z){actor.setPosition(x,y,z);actor.onGround=true;return actor;}
    static boolean ends(PathEntity p,int x,int y,int z){if(p==null)return false;PathPoint e=p.getFinalPathPoint();return e!=null&&e.xCoord==x&&e.yCoord==y&&e.zCoord==z;}
    static String end(PathEntity p){return p==null?"null":String.valueOf(p.getFinalPathPoint());}
    static void safeEdges(World w,EntityLiving actor,PathEntity path,boolean water){
        check(path!=null,"No path");for(int i=1;i<path.getCurrentPathLength();i++)check(NavigationBlocks.clearSegment(w,actor,path.getVectorFromIndex(actor,i-1),path.getVectorFromIndex(actor,i),water),"Unsafe path segment "+i);
    }
    void tests(WorldServer w){
        NativeNavigationWorldChecks.run(this,w);
        test("Factory replaces the native private navigator before AI initialization, exactly once; vanilla entities retain native navigation",()->{
            GroundActor[] actors={new GroundActor(w),new FlyingActor(w),new SwimmingActor(w),new ClimbingActor(w)};
            Class<?>[] types={PathNavigateGround.class,PathNavigateFlying.class,PathNavigateSwimmer.class,PathNavigateClimber.class};
            for(int i=0;i<actors.length;i++)check(actors[i].factories==1&&actors[i].callbacks==1&&actors[i].ready&&actors[i].getNavigator().getClass()==types[i],"Factory order/type "+i);
            check(new net.minecraft.entity.monster.EntityZombie(w).getNavigator().getClass()==net.minecraft.pathfinding.PathNavigate.class,"Vanilla navigator replaced");
        });
        test("Ground search routes around a two-block wall, preserves native paths and stops when stalled",()->{
            int x=room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5);fill(w,x+3,80,-1,x+3,82,1,Blocks.stone);
            PathEntity p=a.getNavigator().getPathToXYZ(x+8,80,0);check(ends(p,x+8,80,0),"Ground endpoint "+end(p));
            for(int i=0;i<p.getCurrentPathLength();i++){PathPoint q=p.getPathPointFromIndex(i);check(!(q.xCoord==x+3&&Math.abs(q.zCoord)<=1),"Ground route crossed wall");}
            check(a.getNavigator().setPath(p,0.8)&&a.getNavigator().getPath()==p,"Native cursor/path lost");a.getNavigator().onUpdateNavigation();check(a.getMoveHelper().isUpdating(),"No movement command");
            for(int i=0;i<103;i++)a.getNavigator().onUpdateNavigation();check(a.getNavigator().noPath(),"Stalled route retained");
        });
        test("Closed/open doors use both halves of native metadata and respect enter/break flags",()->{
            int x=room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5);PathNavigateGround nav=(PathNavigateGround)a.getNavigator();
            w.setBlock(x+2,80,0,Blocks.wooden_door,0,2);w.setBlock(x+2,81,0,Blocks.wooden_door,8,2);
            check(NavigationBlocks.type(w,x+2,81,0)==PathNodeType.DOOR_WOOD_CLOSED,"Upper closed metadata");
            NodeProcessor proc=nav.getNodeProcessor();check(proc.getPathNodeType(w,x+2,80,0,a,1,2,1,false,true)==PathNodeType.DOOR_WOOD_CLOSED,"Closed wooden door accepted");
            nav.setBreakDoors(true);check(nav.getCanBreakDoors()&&proc.getCanOpenDoors()&&proc.getPathNodeType(w,x+2,80,0,a,1,2,1,true,true)==PathNodeType.WALKABLE,"Break flag not connected");
            w.setBlockMetadataWithNotify(x+2,80,0,4,2);check(NavigationBlocks.type(w,x+2,81,0)==PathNodeType.DOOR_OPEN,"Upper open metadata");
            nav.setEnterDoors(false);check(!nav.getEnterDoors()&&proc.getPathNodeType(w,x+2,80,0,a,1,2,1,true,false)==PathNodeType.BLOCKED,"Enter flag ignored");
            w.setBlock(x+2,80,0,Blocks.iron_door,0,2);w.setBlock(x+2,81,0,Blocks.iron_door,8,2);check(proc.getPathNodeType(w,x+2,80,0,a,1,2,1,true,true)==PathNodeType.DOOR_IRON_CLOSED,"Closed iron door opened by flag");
        });
        test("Per-mob water priorities are honored, restored after search and shared by native flag setters",()->{
            int x=room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5),b=new GroundActor(w);a.setPathPriority(PathNodeType.WATER,-1);check(b.getPathPriority(PathNodeType.WATER)==8,"Priority leaked");
            w.setBlock(x+2,80,0,Blocks.water,0,2);PathEntity p=a.getNavigator().getPathToXYZ(x+6,80,0);check(ends(p,x+6,80,0)&&a.getPathPriority(PathNodeType.WATER)==-1,"Water cost restore");
            for(int i=0;i<p.getCurrentPathLength();i++){PathPoint q=p.getPathPointFromIndex(i);check(!(q.xCoord==x+2&&q.zCoord==0),"Avoided water entered");}
            a.getNavigator().setAvoidsWater(false);check(a.getPathPriority(PathNodeType.WATER)==0&&!a.getNavigator().getAvoidsWater(),"Native setter split state");
        });
        test("Slabs, fences, trapdoors, fire and cactus retain native shape and hazard classification",()->{
            int x=room(w);BlockPos p=new BlockPos(x,80,0);w.setBlock(x,80,0,Blocks.stone_slab,0,2);check(NavigationBlocks.state(w,p).getBoundingBox(w,p).maxY==0.5,"Lower slab height");
            w.setBlockMetadataWithNotify(x,80,0,8,2);check(NavigationBlocks.state(w,p).getBoundingBox(w,p).minY==0.5,"Upper slab height");
            net.minecraft.block.Block[] blocks={Blocks.fence,Blocks.trapdoor,Blocks.fire,Blocks.cactus};PathNodeType[] types={PathNodeType.FENCE,PathNodeType.TRAPDOOR,PathNodeType.DAMAGE_FIRE,PathNodeType.DAMAGE_CACTUS};
            for(int i=0;i<blocks.length;i++){w.setBlock(x,80,0,blocks[i],0,2);check(NavigationBlocks.type(w,x,80,0)==types[i],"Hazard "+i);}
            w.setBlock(x+1,80,0,Blocks.fire,0,2);check(new WalkNodeProcessor().checkNeighborBlocks(w,x+2,80,0,PathNodeType.WALKABLE)==PathNodeType.DANGER_FIRE,"Neighbor fire cost");
        });
        test("Flying search moves vertically around walls and every selected edge clears the entity body",()->{
            int x=room(w);FlyingActor a=place(new FlyingActor(w),x+0.5,83,0.5);a.onGround=false;fill(w,x+3,80,-2,x+3,87,2,Blocks.stone);
            PathEntity p=a.getNavigator().getPathToXYZ(x+8,86,0);check(ends(p,x+8,86,0),"Flight endpoint "+end(p));safeEdges(w,a,p,false);
            check(a.getNavigator().setPath(p,1),"Flight rejected route");a.getNavigator().onUpdateNavigation();Steering h=(Steering)a.getMoveHelper();
            check(NavigationBlocks.clearSegment(w,a,Vec3.createVectorHelper(a.posX,a.posY,a.posZ),Vec3.createVectorHelper(h.getX(),h.getY(),h.getZ()),false),"Flight smoothing crossed wall");
        });
        test("Flying cannot cut blocked corners or pass a narrow gap that fits only its center",()->{
            int x=room(w);FlyingActor a=place(new FlyingActor(w),x+0.5,83,0.5);w.setBlock(x+1,83,0,Blocks.stone,0,2);w.setBlock(x,83,1,Blocks.stone,0,2);
            check(!NavigationBlocks.clearSegment(w,a,Vec3.createVectorHelper(x+0.5,83,0.5),Vec3.createVectorHelper(x+1.5,83,1.5),false),"Corner clipping");
            a.resize(1.8F,1.8F);fill(w,x+4,80,-2,x+4,88,3,Blocks.stone);fill(w,x+4,83,0,x+4,85,0,Blocks.air);
            check(!NavigationBlocks.clearSegment(w,a,Vec3.createVectorHelper(x+2.5,83,0.5),Vec3.createVectorHelper(x+6.5,83,0.5),false),"Center-only gap accepted");
        });
        test("Swimming performs three-dimensional water-only search, including upward routes around submerged obstacles",()->{
            int x=room(w);fill(w,x-1,80,-3,x+12,88,4,Blocks.water);SwimmingActor a=place(new SwimmingActor(w),x+0.5,82,0.5);a.handleWaterMovement();
            fill(w,x+3,80,-3,x+3,85,4,Blocks.stone);PathEntity p=a.getNavigator().getPathToXYZ(x+8,83,0);check(ends(p,x+8,84,0),"Swim endpoint "+end(p));safeEdges(w,a,p,true);
            check(a.getNavigator().setPath(p,1),"Swim rejected route");a.getNavigator().onUpdateNavigation();Steering h=(Steering)a.getMoveHelper();check(NavigationBlocks.clearSegment(w,a,Vec3.createVectorHelper(a.posX,a.posY,a.posZ),Vec3.createVectorHelper(h.getX(),h.getY(),h.getZ()),true),"Swim smoothing crossed obstacle");
        });
        test("Swimmers reject dry starts, lava nodes, dry shortcut gaps and passages smaller than their volume",()->{
            int x=room(w);SwimmingActor a=place(new SwimmingActor(w),x+0.5,82,0.5);a.handleWaterMovement();check(a.getNavigator().getPathToXYZ(x+6,83,0)==null,"Dry start accepted");
            fill(w,x-1,80,-1,x+8,86,1,Blocks.water);a.handleWaterMovement();fill(w,x+3,80,-1,x+3,86,1,Blocks.air);
            PathEntity p=a.getNavigator().getPathToXYZ(x+6,83,0);check(!ends(p,x+6,84,0),"Crossed dry gap");check(!NavigationBlocks.clearSegment(w,a,Vec3.createVectorHelper(x+0.5,82,0.5),Vec3.createVectorHelper(x+6.5,82,0.5),true),"Dry shortcut");
            fill(w,x+3,80,-1,x+3,86,1,Blocks.lava);check(!ends(a.getNavigator().getPathToXYZ(x+6,83,0),x+6,84,0),"Lava accepted");
            a.resize(3.8F,3.8F);check(!NavigationBlocks.clearSegment(w,a,Vec3.createVectorHelper(x+0.5,82,0.5),Vec3.createVectorHelper(x+1.5,82,0.5),true),"Partial body out of water");
        });
        test("Climbers preserve ground paths and continue steering at an unreachable target until the center threshold is reached",()->{
            int x=room(w);ClimbingActor a=place(new ClimbingActor(w),x+0.5,84,0.5);a.onGround=false;GroundActor target=place(new GroundActor(w),x+6.5,88,0.5);
            check(a.getNavigator().tryMoveToEntityLiving(target,0.65)&&a.getNavigator().getPath()==null,"No-path climb fallback rejected");a.getNavigator().onUpdateNavigation();
            check(a.getMoveHelper().isUpdating()&&a.getMoveHelper().posX==x+6&&a.getMoveHelper().posY==88&&a.getMoveHelper().getSpeed()==0.65,"Climber fallback command");
            a.setPosition(x+6.5,90.5,0.5);a.getMoveHelper().onUpdateMoveHelper();a.getNavigator().onUpdateNavigation();check(!a.getMoveHelper().isUpdating(),"Climber retained reached target");
        });
        test("Native climbing physics ascends a wall using the retained no-path fallback and ladder contact",()->{
            int x=room(w);ClimbingActor a=place(new ClimbingActor(w),x+0.5,80,0.5);fill(w,x+3,80,-3,x+3,84,3,Blocks.stone);GroundActor target=place(new GroundActor(w),x+3.5,85,0.5);
            check(a.getNavigator().tryMoveToEntityLiving(target,1),"Climb target rejected");double high=a.posY;
            for(int i=0;i<260;i++){a.onUpdate();high=Math.max(high,a.posY);}
            check(high>=84.9,"Climber never ascended: high="+high+" pos="+a.posX+","+a.posY+","+a.posZ);
        });
        test("Native clear/set/speed APIs share state and cauldron adjustment preserves path cursor",()->{
            int x=room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5);w.setBlock(x+2,80,0,Blocks.cauldron,0,2);
            PathEntity p=new PathEntity(new PathPoint[]{new PathPoint(x,80,0),new PathPoint(x+2,80,0),new PathPoint(x+3,80,0)});p.setCurrentPathIndex(1);
            a.getNavigator().setPath(p,1);PathEntity q=a.getNavigator().getPath();check(q.getCurrentPathIndex()==1&&q.getPathPointFromIndex(1).yCoord==81&&q.getFinalPathPoint().yCoord==81,"Cauldron step/cursor");
            a.getNavigator().setSpeed(0.7);a.getNavigator().onUpdateNavigation();check(a.getMoveHelper().getSpeed()==0.7,"Split speed");a.getNavigator().clearPathEntity();check(a.getNavigator().getPath()==null&&a.getNavigator().noPath(),"Split native clear");check(!a.getNavigator().setPath(null,1),"Null path accepted");
        });
        test("Native ground physics follows a planned route to its target without entering the wall",()->{
            int x=room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5);fill(w,x+3,80,-1,x+3,82,1,Blocks.stone);check(a.getNavigator().tryMoveToXYZ(x+8,80,0,1),"Route absent");
            for(int i=0;i<220;i++){a.onUpdate();check(!NavigationBlocks.collides(w,a.boundingBox.contract(1E-4,1E-4,1E-4)),"Ground body in wall");}
            check(a.getDistanceSq(x+8.5,80,0.5)<2,"Ground physics stopped at "+a.posX+","+a.posY+","+a.posZ);
        });
        test("Native flying physics follows a three-dimensional route around a wall with navigation commands",()->{
            int x=room(w);FlyingActor a=place(new FlyingActor(w),x+0.5,83,0.5);fill(w,x+3,80,-2,x+3,87,2,Blocks.stone);check(a.getNavigator().tryMoveToXYZ(x+8,86,0,1),"Flight route absent");
            for(int i=0;i<240;i++){a.onUpdate();check(!NavigationBlocks.collides(w,a.boundingBox.contract(1E-4,1E-4,1E-4)),"Flying body in wall");}
            check(a.getDistanceSq(x+8.5,86,0.5)<2,"Flight physics stopped at "+a.posX+","+a.posY+","+a.posZ);
        });
        test("Native swimming physics follows a submerged route without leaving water or intersecting the barrier",()->{
            int x=room(w);fill(w,x-1,80,-3,x+12,88,4,Blocks.water);SwimmingActor a=place(new SwimmingActor(w),x+0.5,82,0.5);a.handleWaterMovement();fill(w,x+3,80,-3,x+3,85,4,Blocks.stone);check(a.getNavigator().tryMoveToXYZ(x+8,83,0,1),"Swim route absent");
            for(int i=0;i<260;i++){a.onUpdate();check(a.isInWater()&&!NavigationBlocks.collides(w,a.boundingBox.contract(1E-4,1E-4,1E-4)),"Swim left water/collided at tick "+i+" pos="+a.posX+","+a.posY+","+a.posZ+" water="+a.isInWater());}
            check(a.getDistanceSq(x+8.5,84,0.5)<2,"Swim physics stopped at "+a.posX+","+a.posY+","+a.posZ);
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;try{tests(MinecraftServer.getServer().worldServerForDimension(0));}finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("navigation-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}}
}
