package dev.srpbackport.tests;

import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.WorldServer;
import com.dhanantry.scapeandrunparasites.backport.navigation.PathNavigate;
import static dev.srpbackport.tests.NativeNavigationChecks.*;

/** World notifications and active navigation, tested in a disposable native world. */
public final class NativeNavigationWorldChecks {
    private static PathNavigate navigator(EntityLiving e) { return (PathNavigate)e.getNavigator(); }
    private static void advance(WorldServer w) { w.getWorldInfo().incrementTotalWorldTime(w.getTotalWorldTime()+21); }
    private static void spawn(WorldServer w, EntityLiving e) { check(w.spawnEntityInWorld(e),"Fixture spawn rejected"); }
    private static void avoids(PathEntity path, int x, int minY, int maxY, int minZ, int maxZ) {
        check(path!=null,"Replacement route absent");
        for(int i=path.getCurrentPathIndex();i<path.getCurrentPathLength();i++) {
            PathPoint p=path.getPathPointFromIndex(i);
            check(p.xCoord!=x || p.yCoord<minY || p.yCoord>maxY || p.zCoord<minZ || p.zCoord>maxZ,"Replacement route crosses changed block");
        }
    }
    public static void run(NativeNavigationChecks suite,WorldServer w) {
        suite.test("World block notification replaces an active ground route and native movement reaches the destination",()->{
            int x=suite.room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5);spawn(w,a);
            try {
                check(a.getNavigator().tryMoveToXYZ(x+8,80,0,1),"Original route absent");PathEntity before=a.getNavigator().getPath();
                fill(w,x+3,80,-1,x+3,82,1,Blocks.stone);w.markBlockForUpdate(x+3,80,0);
                check(navigator(a).canUpdatePathOnTimeout(),"Block update was not queued");advance(w);a.getNavigator().onUpdateNavigation();
                check(a.getNavigator().getPath()!=before,"Obsolete ground route retained");avoids(a.getNavigator().getPath(),x+3,80,82,-1,1);
                for(int i=0;i<220;i++)a.onUpdate();check(a.getDistanceSq(x+8.5,80,0.5)<2,"Ground reroute failed to arrive");
            } finally { w.removeEntity(a); }
        });
        suite.test("World block notification replaces an active flying route around a newly built wall",()->{
            int x=suite.room(w);FlyingActor a=place(new FlyingActor(w),x+0.5,83,0.5);spawn(w,a);
            try {
                check(a.getNavigator().tryMoveToXYZ(x+8,86,0,1),"Original flight route absent");PathEntity before=a.getNavigator().getPath();
                fill(w,x+3,80,-2,x+3,87,2,Blocks.stone);w.markBlockForUpdate(x+3,84,0);advance(w);a.getNavigator().onUpdateNavigation();
                check(a.getNavigator().getPath()!=before,"Obsolete flying route retained");avoids(a.getNavigator().getPath(),x+3,80,87,-2,2);
                for(int i=0;i<240;i++)a.onUpdate();check(a.getDistanceSq(x+8.5,86,0.5)<2,"Flying reroute failed to arrive");
            } finally { w.removeEntity(a); }
        });
        suite.test("Water removal invalidates a swimming route even though neither old nor new block has a collision box",()->{
            int x=suite.room(w);fill(w,x-1,80,-3,x+12,88,4,Blocks.water);SwimmingActor a=place(new SwimmingActor(w),x+0.5,82,0.5);a.handleWaterMovement();spawn(w,a);
            try {
                check(a.getNavigator().tryMoveToXYZ(x+8,83,0,1),"Original water route absent");PathEntity before=a.getNavigator().getPath();
                fill(w,x+3,80,-1,x+3,85,1,Blocks.air);w.markBlockForUpdate(x+3,83,0);advance(w);a.getNavigator().onUpdateNavigation();
                check(a.getNavigator().getPath()!=before,"Obsolete water route retained");avoids(a.getNavigator().getPath(),x+3,80,85,-1,1);
                for(int i=0;i<260;i++){a.onUpdate();check(a.isInWater(),"Rerouted swimmer left water");}
                check(a.getDistanceSq(x+8.5,84,0.5)<2,"Swimming reroute failed to arrive");
            } finally { w.removeEntity(a); }
        });
        suite.test("Closing a real native door queues a route refresh through its world notifications",()->{
            int x=suite.room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5);
            w.setBlock(x+3,80,0,Blocks.wooden_door,4,2);w.setBlock(x+3,81,0,Blocks.wooden_door,8,2);spawn(w,a);
            try {
                check(a.getNavigator().tryMoveToXYZ(x+8,80,0,1),"Open-door route absent");
                ((BlockDoor)Blocks.wooden_door).func_150014_a(w,x+3,80,0,false);
                check(navigator(a).canUpdatePathOnTimeout(),"Door close not observed");advance(w);a.getNavigator().onUpdateNavigation();
                avoids(a.getNavigator().getPath(),x+3,80,81,0,0);
            } finally { w.removeEntity(a); }
        });
        suite.test("Nearby update bursts coalesce until the navigation cooldown; distant and lighting updates do not queue",()->{
            int x=suite.room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5);spawn(w,a);
            try {
                check(a.getNavigator().tryMoveToXYZ(x+8,80,0,1),"Route absent");advance(w);navigator(a).updatePath();PathEntity before=a.getNavigator().getPath();
                w.markBlockForUpdate(x+500,80,0);w.setLightValue(net.minecraft.world.EnumSkyBlock.Block,x+3,80,0,0);
                check(!navigator(a).canUpdatePathOnTimeout(),"Unrelated update queued");
                for(int i=0;i<50;i++)w.markBlockForUpdate(x+3,80,0);
                a.getNavigator().onUpdateNavigation();check(navigator(a).canUpdatePathOnTimeout()&&a.getNavigator().getPath()==before,"Cooldown did not defer search");
                w.getWorldInfo().incrementTotalWorldTime(w.getTotalWorldTime()+20);a.getNavigator().onUpdateNavigation();check(a.getNavigator().getPath()==before,"Search ran before 21 ticks");
                w.getWorldInfo().incrementTotalWorldTime(w.getTotalWorldTime()+1);a.getNavigator().onUpdateNavigation();check(!navigator(a).canUpdatePathOnTimeout()&&a.getNavigator().getPath()!=before,"Pending search did not run once");
                PathEntity after=a.getNavigator().getPath();a.getNavigator().onUpdateNavigation();check(a.getNavigator().getPath()==after,"Search repeated without an update");
            } finally { w.removeEntity(a); }
        });
        suite.test("Clearing a route cancels queued world updates instead of resurrecting a cancelled destination",()->{
            int x=suite.room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5);spawn(w,a);
            try {
                for(int mode=0;mode<2;mode++){
                    check(a.getNavigator().tryMoveToXYZ(x+8,80,0,1),"Route absent");w.markBlockForUpdate(x+3,80,0);check(navigator(a).canUpdatePathOnTimeout(),"Update not queued");
                    if(mode==0)a.getNavigator().clearPathEntity();else a.getNavigator().setPath(null,1);
                    advance(w);a.getNavigator().onUpdateNavigation();check(a.getNavigator().noPath()&&!navigator(a).canUpdatePathOnTimeout(),"Cancelled path resurrected");
                }
            } finally { w.removeEntity(a); }
        });
        suite.test("Native entity removal detaches navigation and re-addition resumes tracking; unspawned entities are ignored",()->{
            int x=suite.room(w);GroundActor a=place(new GroundActor(w),x+0.5,80,0.5),unspawned=place(new GroundActor(w),x+0.5,80,0.5);spawn(w,a);
            try {
                check(a.getNavigator().tryMoveToXYZ(x+8,80,0,1)&&unspawned.getNavigator().tryMoveToXYZ(x+8,80,0,1),"Route absent");
                w.onEntityRemoved(a);w.markBlockForUpdate(x+3,80,0);check(!navigator(a).canUpdatePathOnTimeout()&&!navigator(unspawned).canUpdatePathOnTimeout(),"Inactive entity retained");
                w.onEntityAdded(a);w.markBlockForUpdate(x+3,80,0);check(navigator(a).canUpdatePathOnTimeout(),"Re-added entity ignored");
                advance(w);a.getNavigator().onUpdateNavigation();check(!navigator(a).canUpdatePathOnTimeout(),"Duplicate pending update");
            } finally { w.removeEntity(a); }
        });
    }
}
