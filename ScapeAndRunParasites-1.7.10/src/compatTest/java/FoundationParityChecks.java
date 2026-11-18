package dev.srpbackport.tests;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import com.dhanantry.scapeandrunparasites.backport.math.*;
import com.dhanantry.scapeandrunparasites.backport.util.*;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/** Compare ported operations with the original Mojang 1.12.2 implementation in the same JVM. */
public final class FoundationParityChecks {
    static Properties mappings=new Properties();static int assertions,groups;static List<String> report=new ArrayList<String>();
    static Class<?> ref(String name)throws Exception{return Class.forName(mappings.getProperty("class."+name));}
    static Object call(Object target,String srg,Class<?>[] signature,Object... args)throws Exception{
        Class<?> type=target instanceof Class?(Class<?>)target:target.getClass();Class<?> owner=type;String mapped=null;while(owner!=null&&mapped==null){mapped=mappings.getProperty("method."+owner.getName()+"."+srg);owner=owner.getSuperclass();}if(mapped==null)throw new NoSuchMethodException(type.getName()+" "+srg);Method method=type.getMethod(mapped,signature);
        method.setAccessible(true);return method.invoke(target instanceof Class?null:target,args);
    }
    static Object no(Object target,String srg)throws Exception{return call(target,srg,new Class<?>[0]);}
    static void check(boolean value,String reason){assertions++;if(!value)throw new AssertionError(reason);}
    static void number(double expected,double actual,String reason){check(Double.doubleToLongBits(expected)==Double.doubleToLongBits(actual)||Math.abs(expected-actual)<=1e-12*Math.max(1,Math.abs(expected)),reason+": "+expected+" != "+actual);}
    static void coords(Object reference,Vec3i actual,String reason)throws Exception{check(((Integer)no(reference,"func_177958_n"))==actual.getX()&&((Integer)no(reference,"func_177956_o"))==actual.getY()&&((Integer)no(reference,"func_177952_p"))==actual.getZ(),reason);}
    static String coords(Object reference)throws Exception{return no(reference,"func_177958_n")+","+no(reference,"func_177956_o")+","+no(reference,"func_177952_p");}
    static Object nativePos(int x,int y,int z)throws Exception{return ref("BlockPos").getConstructor(int.class,int.class,int.class).newInstance(x,y,z);}
    static Object nativeVector(double x,double y,double z)throws Exception{return ref("Vec3d").getConstructor(double.class,double.class,double.class).newInstance(x,y,z);}
    static void vector(Object reference,Vec3d actual,String reason)throws Exception{
        number(reference.getClass().getField(mappings.getProperty("field.field_72450_a")).getDouble(reference),actual.xCoord,reason+" x");
        number(reference.getClass().getField(mappings.getProperty("field.field_72448_b")).getDouble(reference),actual.yCoord,reason+" y");
        number(reference.getClass().getField(mappings.getProperty("field.field_72449_c")).getDouble(reference),actual.zCoord,reason+" z");
    }
    static void positions()throws Exception{
        Random random=new Random(1121710L);Class<?> pos=ref("BlockPos");
        for(int i=0;i<2500;i++){
            int x=i<5?new int[]{-33554432,-1,0,1,33554431}[i]:random.nextInt(67108864)-33554432;
            int y=i<5?new int[]{-2048,-1,0,255,2047}[i]:random.nextInt(4096)-2048;
            int z=i<5?new int[]{33554431,-1,0,1,-33554432}[i]:random.nextInt(67108864)-33554432;
            BlockPos own=new BlockPos(x,y,z);Object original=nativePos(x,y,z);coords(original,own,"integer position");long packed=(Long)no(original,"func_177986_g");
            check(packed==own.toLong(),"saved-coordinate bit layout");coords(call(pos,"func_177969_a",new Class<?>[]{long.class},packed),BlockPos.fromLong(packed),"signed packed coordinates");
            coords(no(original,"func_177977_b"),own.down(),"down");coords(no(original,"func_177984_a"),own.up(),"up");
            coords(call(original,"func_177982_a",new Class<?>[]{int.class,int.class,int.class},-3,7,2),own.add(-3,7,2),"position addition");
            number((Double)call(original,"func_177954_c",new Class<?>[]{double.class,double.class,double.class},x+0.75,y-0.5,z+1.5),own.distanceSq(x+0.75,y-0.5,z+1.5),"position distance");
        }
        for(double value:new double[]{-30000000.25,-100.001,-1,-0.001,0,0.999,5.5,29999999.99})coords(pos.getConstructor(double.class,double.class,double.class).newInstance(value,value,value),new BlockPos(value,value,value),"negative coordinate flooring");
        check(new BlockPos(1,0,0).toLong()==(1L<<38)&&new BlockPos(0,1,0).toLong()==(1L<<26)&&new BlockPos(0,0,1).toLong()==1,"known serialized coordinate fixtures");
        report.add("PASS: original 1.12.2 coordinate flooring, offsets, distances and 2500 signed serialization cases");groups++;
    }
    static void mutablesAndIteration()throws Exception{
        Class<?> mutable=ref("MutableBlockPos"),pos=ref("BlockPos");Object original=mutable.getConstructor().newInstance();BlockPos.MutableBlockPos own=new BlockPos.MutableBlockPos();
        call(original,"func_181079_c",new Class<?>[]{int.class,int.class,int.class},-4,61,7);own.setPos(-4,61,7);Object saved=no(original,"func_185334_h");BlockPos snapshot=own.toImmutable();
        call(original,"func_181079_c",new Class<?>[]{int.class,int.class,int.class},9,12,-1);own.setPos(9,12,-1);coords(saved,snapshot,"immutable snapshots survive mutation");coords(original,own,"mutable coordinate updates");
        Object result=call(original,"func_177981_b",new Class<?>[]{int.class},0);BlockPos ownResult=own.up(0);check((result.getClass()==pos)==(ownResult.getClass()==BlockPos.class),"zero-offset mutable position is copied");
        Random random=new Random(431L);
        for(int n=0;n<200;n++){
            BlockPos a=new BlockPos(random.nextInt(7)-3,random.nextInt(7)-3,random.nextInt(7)-3),b=new BlockPos(random.nextInt(7)-3,random.nextInt(7)-3,random.nextInt(7)-3);
            Iterable<?> expected=(Iterable<?>)call(pos,"func_177980_a",new Class<?>[]{pos,pos},nativePos(a.getX(),a.getY(),a.getZ()),nativePos(b.getX(),b.getY(),b.getZ()));Iterator<?> expectedIterator=expected.iterator();
            int count=0;for(BlockPos p:BlockPos.getAllInBox(a,b)){check(expectedIterator.hasNext(),"extra box coordinate");check(coords(expectedIterator.next()).equals(p.getX()+","+p.getY()+","+p.getZ()),"box traversal order");count++;}
            check(!expectedIterator.hasNext(),"missing box coordinate");check(count==(Math.abs(a.getX()-b.getX())+1)*(Math.abs(a.getY()-b.getY())+1)*(Math.abs(a.getZ()-b.getZ())+1),"inclusive box volume");
        }
        report.add("PASS: original mutable snapshots and inclusive reversed-corner iteration order across 200 boxes");groups++;
    }
    static void directions()throws Exception{
        Class<?> facing=ref("EnumFacing"),rotation=ref("Rotation"),mirror=ref("Mirror");Object[] faces=facing.getEnumConstants(),rotations=rotation.getEnumConstants(),mirrors=mirror.getEnumConstants();
        for(int i=-1000;i<=1000;i++){
            check(((Enum<?>)call(facing,"func_82600_a",new Class<?>[]{int.class},i)).name().equals(EnumFacing.getFront(i).name()),"direction wrapping");
            check(((Enum<?>)call(facing,"func_176731_b",new Class<?>[]{int.class},i)).name().equals(EnumFacing.byHorizontalIndex(i).name()),"horizontal wrapping");
        }
        for(double angle=-720.5;angle<=720.5;angle+=0.25)check(((Enum<?>)call(facing,"func_176733_a",new Class<?>[]{double.class},angle)).name().equals(EnumFacing.fromAngle(angle).name()),"angle direction "+angle);
        for(EnumFacing f:EnumFacing.values()){
            Object original=faces[f.ordinal()];check(((Enum<?>)no(original,"func_176734_d")).name().equals(f.getOpposite().name()),"opposite");
            for(Rotation r:Rotation.values())check(((Enum<?>)call(rotations[r.ordinal()],"func_185831_a",new Class<?>[]{facing},original)).name().equals(r.rotate(f).name()),"facing rotation");
            for(Mirror m:Mirror.values())check(((Enum<?>)call(mirrors[m.ordinal()],"func_185800_a",new Class<?>[]{facing},original)).name().equals(m.toRotation(f).name()),"mirror rotation");
        }
        report.add("PASS: original direction indexes, negative wrapping, angle selection, rotations and mirrors");groups++;
    }
    static void vectors()throws Exception{
        Random random=new Random(500L);Class<?> vector=ref("Vec3d");
        for(int i=0;i<1200;i++){
            double x=i==0?0:i==1?0.00005:random.nextDouble()*2000-1000,y=i<2?0:random.nextDouble()*2000-1000,z=i<2?0:random.nextDouble()*2000-1000;
            Vec3d v=new Vec3d(x,y,z),w=new Vec3d(z,-x,y);Object original=nativeVector(x,y,z),other=nativeVector(z,-x,y);
            number((Double)no(original,"func_72433_c"),v.lengthVector(),"vector length");number((Double)no(original,"func_189985_c"),v.lengthSquared(),"squared length");
            vector(no(original,"func_72432_b"),v.normalize(),"normalization");vector(call(original,"func_178788_d",new Class<?>[]{vector},other),v.subtract(w),"subtraction");
            vector(call(original,"func_178787_e",new Class<?>[]{vector},other),v.add(w),"addition");vector(call(original,"func_72431_c",new Class<?>[]{vector},other),v.crossProduct(w),"cross product");
            number((Double)call(original,"func_72430_b",new Class<?>[]{vector},other),v.dotProduct(w),"dot product");number((Double)call(original,"func_72438_d",new Class<?>[]{vector},other),v.distanceTo(w),"vector distance");
            float angle=(float)(random.nextDouble()*Math.PI*2-Math.PI);vector(call(original,"func_178789_a",new Class<?>[]{float.class},angle),v.rotatePitch(angle),"pitch");vector(call(original,"func_178785_b",new Class<?>[]{float.class},angle),v.rotateYaw(angle),"yaw");
            vector(call(original,"func_186678_a",new Class<?>[]{double.class},1.234567),v.scale(1.234567),"scale");
        }
        report.add("PASS: original vector arithmetic, normalization threshold, float-root precision and trig rotations across 1200 vectors");groups++;
    }
    static void collectionsAndHits()throws Exception{
        Class<?> lists=ref("NonNullList");@SuppressWarnings("unchecked") List<String> original=(List<String>)call(lists,"func_191197_a",new Class<?>[]{int.class,Object.class},3,"air");NonNullList<String> own=NonNullList.withSize(3,"air");
        original.set(1,"stone");own.set(1,"stone");check(original.equals(own),"fixed inventory contents");original.clear();own.clear();check(original.equals(own)&&own.size()==3,"fixed inventory clear resets slots");
        boolean a=false,b=false;try{original.set(0,null);}catch(NullPointerException expected){a=true;}try{own.set(0,null);}catch(NullPointerException expected){b=true;}check(a&&b,"null inventory rejection");
        NonNullList<String> variable=NonNullList.create();variable.add("one");variable.add("two");variable.clear();check(variable.isEmpty(),"resizable list clear");
        MovingObjectPosition legacy=new MovingObjectPosition(-8,70,12,2,Vec3.createVectorHelper(-7.5,70.25,12.75));RayTraceResult hit=RayTraceResult.fromLegacy(legacy);check(hit.typeOfHit==RayTraceResult.Type.BLOCK&&hit.sideHit==EnumFacing.NORTH&&hit.getBlockPos().equals(new BlockPos(-8,70,12)),"real 1.7 ray-hit boundary");number(12.75,hit.hitVec.zCoord,"real 1.7 ray-hit vector");
        report.add("PASS: original fixed-list/null semantics and actual Minecraft 1.7.10 ray-hit conversion");groups++;
    }
    static String descriptor(Class<?> c){if(c.isArray())return c.getName().replace('.','/');if(!c.isPrimitive())return "L"+c.getName().replace('.','/')+";";if(c==void.class)return "V";if(c==boolean.class)return "Z";if(c==byte.class)return "B";if(c==char.class)return "C";if(c==short.class)return "S";if(c==int.class)return "I";if(c==long.class)return "J";if(c==float.class)return "F";return "D";}
    static String parameters(Class<?>[] types){StringBuilder b=new StringBuilder("(");for(Class<?> t:types)b.append(descriptor(t));return b.append(')').toString();}
    static void memberContract(String path)throws Exception{
        BufferedReader reader=new BufferedReader(new FileReader(path));String line;int count=0;
        while((line=reader.readLine())!=null){String[] row=line.split("\\t");Class<?> c=Class.forName(row[0]);boolean found=false;
            if(row[1].equals("F")){for(Field f:c.getFields())if(f.getName().equals(row[2])&&descriptor(f.getType()).equals(row[3]))found=true;}
            else if(row[2].equals("<init>")){for(Constructor<?> m:c.getConstructors())if((parameters(m.getParameterTypes())+"V").equals(row[3]))found=true;}
            else{for(Method m:c.getMethods())if(m.getName().equals(row[2])&&(parameters(m.getParameterTypes())+descriptor(m.getReturnType())).equals(row[3]))found=true;}
            check(found,"missing original member contract: "+line);count++;
        }
        reader.close();report.add("PASS: all "+count+" originally referenced foundation member descriptors link against the 1.7.10 compatibility classes");groups++;
    }
    interface Checked { void run()throws Exception; }
    static int failures;
    static void test(String name,Checked body){try{body.run();}catch(Throwable t){failures++;report.add("FAIL: "+name+" :: "+t);t.printStackTrace();}}
    public static void main(String[] args)throws Exception{
        try(InputStream input=new FileInputStream(args[0])){mappings.load(input);}
        test("positions",()->positions());test("mutables/boxes",()->mutablesAndIteration());test("directions",()->directions());test("vectors",()->vectors());test("collections/legacy hits",()->collectionsAndHits());test("original member descriptors",()->memberContract(args[1]));
        report.add("Assertions: "+assertions);report.add("Passed groups: "+groups);report.add("Failed: "+failures);for(String line:report)System.out.println(line);
        try(PrintWriter writer=new PrintWriter(args[2],"UTF-8")){for(String line:report)writer.println(line);}
        if(failures!=0)System.exit(1);
    }
}
