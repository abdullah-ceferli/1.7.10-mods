package dev.srpbackport.tests;
import java.io.*;import java.util.*;import java.lang.reflect.*;import org.objectweb.asm.*;import sun.misc.Unsafe;
import net.minecraft.entity.Entity;import net.minecraft.world.World;import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
public final class EntityPositionParityChecks{
 public static class Actor extends Entity{Actor(World w){super(w);}protected void entityInit(){}protected void readEntityFromNBT(NBTTagCompound n){}protected void writeEntityToNBT(NBTTagCompound n){}}
 static class Loader extends ClassLoader{Loader(){super(EntityPositionParityChecks.class.getClassLoader());}Class<?> make(byte[] b){return defineClass(null,b,0,b.length);}}
 public static void main(String[] args)throws Exception{
  Properties p=new Properties();try(InputStream in=new FileInputStream(args[0])){p.load(in);}Field field=Unsafe.class.getDeclaredField("theUnsafe");field.setAccessible(true);Unsafe unsafe=(Unsafe)field.get(null);
  Class<?> original=Class.forName(p.getProperty("class.Entity"));ClassWriter writer=new ClassWriter(0);writer.visit(Opcodes.V1_8,Opcodes.ACC_PUBLIC,"dev/srpbackport/tests/OriginalPositionActor",null,original.getName(),null);writer.visitEnd();
  Object expectedEntity=unsafe.allocateInstance(new Loader().make(writer.toByteArray()));Actor actualEntity=(Actor)unsafe.allocateInstance(Actor.class);
  Field x=original.getField(p.getProperty("field.field_70165_t")),y=original.getField(p.getProperty("field.field_70163_u")),z=original.getField(p.getProperty("field.field_70161_v"));
  Method get=original.getMethod(p.getProperty("method.func_180425_c"));Class<?> pos=Class.forName(p.getProperty("class.BlockPos"));Method px=pos.getMethod(p.getProperty("method.func_177958_n")),py=pos.getMethod(p.getProperty("method.func_177956_o")),pz=pos.getMethod(p.getProperty("method.func_177952_p"));
  Random random=new Random(171012);int assertions=0;
  for(int n=0;n<5000;n++){double a=(random.nextDouble()-0.5)*60000000,b=n<12?new double[]{-2,-1.75,-1.5,-1.49,-0.5,-0.01,0,0.49,0.5,1.5,255.49,255.5}[n]:random.nextDouble()*1000-400,c=(random.nextDouble()-0.5)*60000000;
   x.setDouble(expectedEntity,a);y.setDouble(expectedEntity,b);z.setDouble(expectedEntity,c);actualEntity.posX=a;actualEntity.posY=b;actualEntity.posZ=c;actualEntity.yOffset=1.62f;
   Object expected=get.invoke(expectedEntity);BlockPos actual=EntityQueries.position(actualEntity);if(actual.getX()!=(Integer)px.invoke(expected)||actual.getY()!=(Integer)py.invoke(expected)||actual.getZ()!=(Integer)pz.invoke(expected))throw new AssertionError("Position/rounding case "+n);assertions++;
  }
  List<String> report=Arrays.asList("PASS: Original entity block position matches across 5000 signed/boundary cases, including the y+0.5 rule and ignored native render offset","Assertions: "+assertions,"Failed: 0");
  for(String line:report)System.out.println(line);try(PrintWriter out=new PrintWriter(args[1],"UTF-8")){for(String line:report)out.println(line);}
 }
}
