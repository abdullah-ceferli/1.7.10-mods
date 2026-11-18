package dev.srpbackport.tests;
import java.io.*;import java.lang.reflect.*;import java.util.*;import org.objectweb.asm.*;import sun.misc.Unsafe;
import net.minecraft.world.*;import net.minecraft.world.chunk.IChunkProvider;import net.minecraft.entity.Entity;import net.minecraft.util.*;import net.minecraft.profiler.Profiler;
import net.minecraft.world.storage.ISaveHandler;
import com.dhanantry.scapeandrunparasites.backport.math.*;import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;
public final class WorldPositionParityChecks{
 static Properties names=new Properties();static List<String> report=new ArrayList<String>(),chunkCalls=new ArrayList<String>(),rays=new ArrayList<String>();static int assertions,failed;static Unsafe unsafe;
 static Class<?> vec,box,pos,world;static Object reference;static NativeProbe own;static Constructor<?> vc,bc,pc;static String vx,vy,vz;
 public static boolean originalChunk(int x,int z,boolean allow){return loaded(x,z);}
 static boolean loaded(int x,int z){chunkCalls.add(x+","+z);return ((x*31+z*17)&7)!=3;}
 static boolean occluded(double x,double y,double z){rays.add(Long.toHexString(Double.doubleToLongBits(x))+","+Long.toHexString(Double.doubleToLongBits(y))+","+Long.toHexString(Double.doubleToLongBits(z)));return x+z*0.7>y*0.3;}
 public static Object originalRay(Object a,Object b)throws Exception{
  double x=vec.getField(vx).getDouble(a),y=vec.getField(vy).getDouble(a),z=vec.getField(vz).getDouble(a);
  if(!occluded(x,y,z))return null;
  Class<?> facing=Class.forName(names.getProperty("class.EnumFacing")),hit=Class.forName(names.getProperty("class.RayTraceResult"));
  return hit.getConstructor(vec,facing,pos).newInstance(a,((Object[])facing.getEnumConstants())[1],pc.newInstance(0,0,0));
 }
 public static class NativeProbe extends World{
  // This constructor is never invoked; Unsafe allocation keeps both reference worlds free of unrelated registries/save handlers.
  NativeProbe(){super((ISaveHandler)null,"qa",(WorldSettings)null,(WorldProvider)null,(Profiler)null);}
  protected IChunkProvider createChunkProvider(){throw new UnsupportedOperationException();}
  protected int func_152379_p(){throw new UnsupportedOperationException();}
  public Entity getEntityByID(int id){throw new UnsupportedOperationException();}
  public IChunkProvider getChunkProvider(){return (IChunkProvider)Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{IChunkProvider.class},(proxy,method,args)->{if(method.getName().equals("chunkExists"))return loaded((Integer)args[0],(Integer)args[1]);throw new AssertionError("Unexpected chunk operation: "+method);});}
  public MovingObjectPosition rayTraceBlocks(Vec3 a,Vec3 b){return occluded(a.xCoord,a.yCoord,a.zCoord)?new MovingObjectPosition(0,0,0,1,a):null;}
 }
 static class Loader extends ClassLoader{Loader(){super(WorldPositionParityChecks.class.getClassLoader());}Class<?> make(byte[] bytes){return defineClass(null,bytes,0,bytes.length);}}
 static String key(String owner,String srg){return names.getProperty("method."+owner+"."+srg);}
 static void setup()throws Exception{
  Field f=Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);unsafe=(Unsafe)f.get(null);
  vec=Class.forName(names.getProperty("class.Vec3d"));box=Class.forName(names.getProperty("class.AxisAlignedBB"));pos=Class.forName(names.getProperty("class.BlockPos"));world=Class.forName(names.getProperty("class.World"));
  vc=vec.getConstructor(double.class,double.class,double.class);bc=box.getConstructor(double.class,double.class,double.class,double.class,double.class,double.class);pc=pos.getConstructor(int.class,int.class,int.class);
  vx=names.getProperty("field.field_72450_a");vy=names.getProperty("field.field_72448_b");vz=names.getProperty("field.field_72449_c");
  ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);String parent=world.getName().replace('.','/'),owner=WorldPositionParityChecks.class.getName().replace('.','/'),child="dev/srpbackport/tests/GeneratedOriginalQueryWorld";
  cw.visit(Opcodes.V1_8,Opcodes.ACC_PUBLIC|Opcodes.ACC_SUPER,child,null,parent,null);
  MethodVisitor mv=cw.visitMethod(Opcodes.ACC_PROTECTED,key(parent,"func_175680_a"),"(IIZ)Z",null,null);mv.visitCode();mv.visitVarInsn(Opcodes.ILOAD,1);mv.visitVarInsn(Opcodes.ILOAD,2);mv.visitVarInsn(Opcodes.ILOAD,3);mv.visitMethodInsn(Opcodes.INVOKESTATIC,owner,"originalChunk","(IIZ)Z",false);mv.visitInsn(Opcodes.IRETURN);mv.visitMaxs(0,0);mv.visitEnd();
  String rayDesc="(L"+vec.getName()+";L"+vec.getName()+";)L"+names.getProperty("class.RayTraceResult")+";";
  mv=cw.visitMethod(Opcodes.ACC_PUBLIC,key(parent,"func_72933_a"),rayDesc,null,null);mv.visitCode();mv.visitVarInsn(Opcodes.ALOAD,1);mv.visitVarInsn(Opcodes.ALOAD,2);mv.visitMethodInsn(Opcodes.INVOKESTATIC,owner,"originalRay","(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",false);mv.visitTypeInsn(Opcodes.CHECKCAST,names.getProperty("class.RayTraceResult"));mv.visitInsn(Opcodes.ARETURN);mv.visitMaxs(0,0);mv.visitEnd();cw.visitEnd();
  reference=unsafe.allocateInstance(new Loader().make(cw.toByteArray()));own=(NativeProbe)unsafe.allocateInstance(NativeProbe.class);
 }
 static Object call(String srg,Class<?>[] types,Object...args)throws Exception{return world.getMethod(key(world.getName(),srg),types).invoke(reference,args);}
 static void check(boolean ok,String why){assertions++;if(!ok)throw new AssertionError(why);}
 static void area()throws Exception{
  Random random=new Random(19281);
  for(int n=0;n<1600;n++){
   int x=random.nextInt(1300)-650,y=random.nextInt(700)-200,z=random.nextInt(1300)-650,r=random.nextInt(40)-3;BlockPos p=new BlockPos(x,y,z);
   chunkCalls.clear();boolean expected=(Boolean)call("func_175648_a",new Class<?>[]{pos,int.class,boolean.class},pc.newInstance(x,y,z),r,false);List<String> before=new ArrayList<String>(chunkCalls);chunkCalls.clear();
   check(expected==WorldAccess.isAreaLoaded(own,p,r,false),"Area radius result");check(before.equals(chunkCalls),"Area radius chunk order/bounds");
   int x2=x+random.nextInt(50)-10,y2=y+random.nextInt(600)-150,z2=z+random.nextInt(50)-10;
   chunkCalls.clear();expected=(Boolean)call("func_175706_a",new Class<?>[]{pos,pos,boolean.class},pc.newInstance(x,y,z),pc.newInstance(x2,y2,z2),true);before=new ArrayList<String>(chunkCalls);chunkCalls.clear();
   check(expected==WorldAccess.isAreaLoaded(own,p,new BlockPos(x2,y2,z2),true),"Area corner result");check(before.equals(chunkCalls),"Area corner chunk order/bounds");
   chunkCalls.clear();expected=(Boolean)call("func_175668_a",new Class<?>[]{pos,boolean.class},pc.newInstance(x,y,z),true);before=new ArrayList<String>(chunkCalls);chunkCalls.clear();check(expected==WorldAccess.isBlockLoaded(own,p),"Block-loaded result");check(before.equals(chunkCalls),"Block query must ignore height");
  }
 }
 static void density()throws Exception{
  Random random=new Random(1121710);
  for(int n=0;n<350;n++){
   double x=random.nextDouble()*40-20,y=random.nextDouble()*30-15,z=random.nextDouble()*40-20;
   double sx=random.nextDouble()*2,sy=random.nextDouble()*3,sz=random.nextDouble()*2;
   Object b=bc.newInstance(x,y,z,x+sx,y+sy,z+sz),target=vc.newInstance(10,12,14);
   rays.clear();float expected=(Float)call("func_72842_a",new Class<?>[]{vec,box},target,b);List<String> points=new ArrayList<String>(rays);rays.clear();
   float actual=WorldAccess.getBlockDensity(own,new Vec3d(10,12,14),Boxes.create(x,y,z,x+sx,y+sy,z+sz));
   check(Float.floatToIntBits(expected)==Float.floatToIntBits(actual),"Density result differs from original");check(points.equals(rays),"Horizontal sample centering or float loop precision differs");
  }
  rays.clear();float expected=(Float)call("func_72842_a",new Class<?>[]{vec,box},vc.newInstance(0,0,0),bc.newInstance(Double.NaN,0,0,1,1,1));
  List<String> points=new ArrayList<String>(rays);rays.clear();float actual=WorldAccess.getBlockDensity(own,new Vec3d(0,0,0),Boxes.create(Double.NaN,0,0,1,1,1));check(expected==actual&&points.equals(rays),"Original NaN sampling result/coordinates");
 }
 interface Check{void run()throws Exception;}static void test(String name,Check fn){try{fn.run();report.add("PASS: "+name);}catch(Throwable e){failed++;report.add("FAIL: "+name+" :: "+e);e.printStackTrace();}}
 public static void main(String[] args)throws Exception{
  try(InputStream in=new FileInputStream(args[0])){names.load(in);}setup();
  test("Original world radius/corner/block-loaded results and exact chunk query sequences across 1600 signed/vertical/reversed cases",()->area());
  test("Original explosion density and exact sample coordinates across 350 random boxes, including horizontal centering and NaN",()->density());
  report.add("Assertions: "+assertions);report.add("Failed: "+failed);for(String s:report)System.out.println(s);try(PrintWriter writer=new PrintWriter(args[1],"UTF-8")){for(String s:report)writer.println(s);}if(failed!=0)System.exit(1);
 }
}
