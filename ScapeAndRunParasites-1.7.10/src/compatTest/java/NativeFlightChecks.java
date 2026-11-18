package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.lang.reflect.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.pathfinding.*;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper;import com.dhanantry.scapeandrunparasites.backport.math.ModernMath;
@Mod(modid="srp_flight_checks",name="Flight compatibility checks",version="1")
public final class NativeFlightChecks {
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done;Properties catalog=new Properties();
    @Mod.EventHandler public void init(FMLInitializationEvent event){FMLCommonHandler.instance().bus().register(this);}
    interface Check{void run()throws Exception;}
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    static void equal(double a,double b,String why){check(Double.doubleToLongBits(a)==Double.doubleToLongBits(b),why+": "+a+" != "+b);}
    void test(String name,Check body){try{body.run();report.add("PASS: "+name);}catch(Throwable e){failed++;report.add("FAIL: "+name+" :: "+e);e.printStackTrace();}}
    FlightActor actor(String name,WorldServer world)throws Exception{return (FlightActor)Class.forName(name.replace('/','.')).getConstructor(World.class).newInstance(world);}
    EntityMoveHelper reference(FlightActor actor)throws Exception{
        Class<?> type=Class.forName("dev.srpbackport.tests.flight.reference."+actor.getClass().getSimpleName()+"Controller");
        Constructor<?> constructor=type.getDeclaredConstructors()[0];constructor.setAccessible(true);return (EntityMoveHelper)constructor.newInstance(actor,actor);
    }
    String state(EntityMoveHelper helper)throws Exception{
        for(Class<?> type=helper.getClass();type!=null;type=type.getSuperclass())for(Field field:type.getDeclaredFields())if(field.getType().isEnum()){field.setAccessible(true);return ((Enum<?>)field.get(helper)).name();}
        throw new AssertionError("No action state");
    }
    void same(FlightActor a,FlightActor b,String why)throws Exception{
        equal(a.motionX,b.motionX,why+" motionX");equal(a.motionY,b.motionY,why+" motionY");equal(a.motionZ,b.motionZ,why+" motionZ");equal(a.rotationYaw,b.rotationYaw,why+" yaw");equal(a.renderYawOffset,b.renderYawOffset,why+" body yaw");
        check(a.getMoveHelper().isUpdating()==b.getMoveHelper().isUpdating()&&state(a.getMoveHelper()).equals(state(b.getMoveHelper())),why+" action");
    }
    void tests(WorldServer world)throws Exception{
        try(InputStream in=getClass().getResourceAsStream("/flight-controllers.properties")){catalog.load(in);}check(catalog.size()==25,"Controller catalog");
        final Method atan=Class.forName("dev.srpbackport.tests.flight.reference.Math").getMethod("atan2",double.class,double.class);
        test("Steering atan2 matches original bytecode across signed zeros, infinities, NaNs, extremes and 50,000 randomized inputs",()->{
            double[] edges={0.0,-0.0,1,-1,Double.NaN,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.MIN_VALUE,-Double.MIN_VALUE,Double.MAX_VALUE,-Double.MAX_VALUE,1E-150,1E150};int count=0;
            for(double y:edges)for(double x:edges){compareMath(atan,y,x);count++;}
            Random random=new Random(171012);for(int i=0;i<50000;i++){compareMath(atan,Math.scalb(random.nextDouble()*2-1,random.nextInt(1900)-950),Math.scalb(random.nextDouble()*2-1,random.nextInt(1900)-950));count++;}
            check(count==50169,"Math case count");
        });
        test("Native controller API preserves original zero initialization, command speed/position and WAIT/MOVE_TO state",()->{
            for(String name:catalog.stringPropertyNames()){
                FlightActor a=actor(catalog.getProperty(name),world),b=actor(catalog.getProperty(name),world);a.setPosition(-23.75,80.125,6.75);b.setPosition(-23.75,80.125,6.75);Constructor<?> ctor=a.getMoveHelper().getClass().getDeclaredConstructors()[0];ctor.setAccessible(true);a.controller((EntityMoveHelper)ctor.newInstance(a,a));b.controller(reference(b));
                check(!a.getMoveHelper().isUpdating()&&!b.getMoveHelper().isUpdating(),"Initial state");
                for(String getter:new String[]{"getX","getY","getZ"})equal((Double)a.getMoveHelper().getClass().getMethod(getter).invoke(a.getMoveHelper()),(Double)b.getMoveHelper().getClass().getMethod(getter).invoke(b.getMoveHelper()),"Initial "+getter);
                a.getMoveHelper().setMoveTo(-0.25,81.75,37.125,0.375);b.getMoveHelper().setMoveTo(-0.25,81.75,37.125,0.375);
                check(a.getMoveHelper().isUpdating()&&b.getMoveHelper().isUpdating(),"Command state");equal(a.getMoveHelper().getSpeed(),b.getMoveHelper().getSpeed(),"Command speed");
                for(String getter:new String[]{"getX","getY","getZ"})equal((Double)a.getMoveHelper().getClass().getMethod(getter).invoke(a.getMoveHelper()),(Double)b.getMoveHelper().getClass().getMethod(getter).invoke(b.getMoveHelper()),"Command "+getter);
            }
        });
        test("All 25 actual converted SRP flight controllers match original class bytecode over 24,000 update steps",()->{
            Random random=new Random(1121710);int steps=0;
            for(String name:new TreeSet<String>(catalog.stringPropertyNames()))for(int sample=0;sample<120;sample++){
                FlightActor a=actor(catalog.getProperty(name),world),b=actor(catalog.getProperty(name),world);b.controller(reference(b));
                double x=(random.nextDouble()-0.5)*1000000,y=20+random.nextDouble()*100,z=(random.nextDouble()-0.5)*1000000;
                a.setPosition(x,y,z);b.setPosition(x,y,z);float width=0.3F+random.nextFloat()*5,height=0.3F+random.nextFloat()*5;a.resize(width,height);b.resize(width,height);
                double vx=random.nextDouble()-0.5,vy=random.nextDouble()-0.5,vz=random.nextDouble()-0.5;a.motionX=b.motionX=vx;a.motionY=b.motionY=vy;a.motionZ=b.motionZ=vz;a.rotationYaw=b.rotationYaw=random.nextFloat()*720-360;a.renderYawOffset=b.renderYawOffset=random.nextFloat()*720-360;
                FlightActor target=new FlightActor(world);target.setPosition(x+7,y,z-9);if(sample%3==0){a.setAttackTarget(target);b.setAttackTarget(target);a.target=b.target=target;}a.moveM=b.moveM=sample%11==0;
                double dx=sample%4==0?0.00001:random.nextDouble()*40-20,dy=sample%4==0?0.00001:random.nextDouble()*40-20,dz=sample%4==0?0.00001:random.nextDouble()*40-20,speed=random.nextDouble()*3;
                if(sample%5!=0){a.getMoveHelper().setMoveTo(x+dx,y+dy,z+dz,speed);b.getMoveHelper().setMoveTo(x+dx,y+dy,z+dz,speed);}
                for(int tick=0;tick<8;tick++){
                    a.getMoveHelper().onUpdateMoveHelper();b.getMoveHelper().onUpdateMoveHelper();same(a,b,name+" sample "+sample+" tick "+tick);
                    a.setPosition(a.posX+a.motionX,a.posY+a.motionY,a.posZ+a.motionZ);b.setPosition(b.posX+b.motionX,b.posY+b.motionY,b.posZ+b.motionZ);steps++;
                }
            }
            check(steps==24000,"Controller step count");
        });
        test("Arrival clears updating state, halves momentum and accepts a new native movement command",()->{
            for(String name:catalog.stringPropertyNames()){
                FlightActor a=actor(catalog.getProperty(name),world);a.setPosition(0,80,0);a.motionX=0.8;a.motionY=-0.4;a.motionZ=0.2;a.getMoveHelper().setMoveTo(0,80,0,1);a.getMoveHelper().onUpdateMoveHelper();
                check(!a.getMoveHelper().isUpdating()&&state(a.getMoveHelper()).equals("WAIT"),"Arrival state "+name);equal(a.motionX,0.4,"Arrival X");equal(a.motionY,-0.2,"Arrival Y");equal(a.motionZ,0.1,"Arrival Z");
                a.getMoveHelper().setMoveTo(10,80,0,1);check(a.getMoveHelper().isUpdating(),"New command "+name);
            }
        });
        test("Flam's movement lock preserves its pending command and releases it when unlocked",()->{
            FlightActor a=actor("dev/srpbackport/tests/flight/samples/EntityFlam",world);a.setPosition(0,80,0);a.moveM=true;a.getMoveHelper().setMoveTo(10,80,0,1);a.getMoveHelper().onUpdateMoveHelper();check(a.motionX==0&&a.getMoveHelper().isUpdating(),"Lock consumed command");a.moveM=false;a.getMoveHelper().onUpdateMoveHelper();check(a.motionX>0&&a.getMoveHelper().isUpdating(),"Unlock did not move");
        });
        test("Native PathNavigate delivers real path commands through the shared controller interface",()->{
            FlightActor a=actor("dev/srpbackport/tests/flight/samples/EntityEmana",world);a.setPosition(0.5,1,0.5);a.onGround=true;
            for(int x=-1;x<=4;x++)for(int z=-1;z<=1;z++)world.setBlock(x,0,z,Blocks.stone,0,3);
            PathEntity path=new PathEntity(new PathPoint[]{new PathPoint(3,1,0)});check(a.getNavigator().setPath(path,0.75),"Native path rejected");
            a.getNavigator().onUpdateNavigation();check(a.getMoveHelper().isUpdating(),"Navigator failed to set modern action");equal(a.getMoveHelper().getSpeed(),0.75,"Navigator speed");a.getMoveHelper().onUpdateMoveHelper();check(a.motionX>0,"Navigator command did not accelerate flight");
        });
    }
    static void compareMath(Method original,double y,double x)throws Exception{
        Double expected=null,actual=null;Throwable e=null,a=null;
        try{expected=(Double)original.invoke(null,y,x);}catch(InvocationTargetException t){e=t.getCause();}
        try{actual=ModernMath.atan2(y,x);}catch(Throwable t){a=t;}
        if(e!=null||a!=null){check(e!=null&&a!=null&&e.getClass()==a.getClass(),"Math exceptional behavior");return;}
        equal(actual,expected,"atan2("+y+","+x+")");
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;
        try{tests(MinecraftServer.getServer().worldServerForDimension(0));}catch(Throwable e){failed++;report.add("FAIL: setup :: "+e);e.printStackTrace();}
        finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("flight-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}
    }
}
