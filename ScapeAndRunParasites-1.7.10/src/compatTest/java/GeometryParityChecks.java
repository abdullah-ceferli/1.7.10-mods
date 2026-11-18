package dev.srpbackport.tests;
import java.io.*;import java.util.*;import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.*;import static dev.srpbackport.tests.FoundationParityChecks.*;
public final class GeometryParityChecks {
 static Class<?> box()throws Exception{return Class.forName("bhb");}
 static Object original(double[] v)throws Exception{return box().getConstructor(double.class,double.class,double.class,double.class,double.class,double.class).newInstance(v[0],v[1],v[2],v[3],v[4],v[5]);}
 static AxisAlignedBB own(double[] v){return Boxes.create(v[0],v[1],v[2],v[3],v[4],v[5]);}
 static double[] values(AxisAlignedBB b){return new double[]{b.minX,b.minY,b.minZ,b.maxX,b.maxY,b.maxZ};}
 static void compare(Object expected,AxisAlignedBB actual,String why)throws Exception{double[] found=values(actual);for(int i=0;i<6;i++)number(box().getField(String.valueOf((char)('a'+i))).getDouble(expected),found[i],why+" coordinate "+i);}
 static Object axes(Object b,String method,double x,double y,double z)throws Exception{return call(b,method,new Class<?>[]{double.class,double.class,double.class},x,y,z);}
 static void boxes()throws Exception{
  Random random=new Random(171012);for(int n=0;n<1500;n++){
   double[] values=new double[6];for(int i=0;i<6;i++)values[i]=(random.nextDouble()-0.5)*200;Object b=original(values);AxisAlignedBB a=own(values);compare(b,a,"constructor normalization");double[] before=values(a);
   double x=(random.nextDouble()-0.5)*20,y=(random.nextDouble()-0.5)*20,z=(random.nextDouble()-0.5)*20;
   compare(axes(b,"func_72314_b",x,y,z),Boxes.grow(a,x,y,z),"symmetric growth");compare(axes(b,"func_72321_a",x,y,z),Boxes.expand(a,x,y,z),"directional expansion");compare(axes(b,"func_191195_a",x,y,z),Boxes.contract(a,x,y,z),"directional contraction");
   compare(axes(b,"func_72317_d",x,y,z),Boxes.offset(a,x,y,z),"immutable offset");compare(call(b,"func_186664_h",new Class<?>[]{double.class},x),Boxes.shrink(a,x),"scalar shrink");
   Object v=no(b,"func_189972_c");vector(v,Boxes.center(a),"box center");check(Arrays.equals(before,values(a)),"Geometry operation mutated its source");
   double[] other={x,y,z,x+2,y+3,z+4};Object otherRef=original(other);AxisAlignedBB otherOwn=own(other);
   compare(call(b,"func_111270_a",new Class<?>[]{box()},otherRef),Boxes.union(a,otherOwn),"union");compare(call(b,"func_191500_a",new Class<?>[]{box()},otherRef),Boxes.intersect(a,otherOwn),"intersection");
   check(((Boolean)call(b,"func_72326_a",new Class<?>[]{box()},otherRef))==Boxes.intersects(a,otherOwn),"Strict overlap");
  }
  for(double size:new double[]{0,0.5,1,2,10})compare(call(original(new double[]{0,0,0,1,1,1}),"func_186664_h",new Class<?>[]{double.class},size),Boxes.shrink(Boxes.create(0,0,0,1,1,1),size),"over-shrink constructor normalization");
  report.add("PASS: original normalized constructors, immutable grow/shrink/offset, directional bounds, centers, union and intersection across 1500 boxes");groups++;
 }
 static void rays()throws Exception{
  Random rng=new Random(8912);Class<?> vec=ref("Vec3d");for(int n=0;n<1500;n++){double[] v={-1,-2,-3,2,3,4};Object b=original(v);AxisAlignedBB own=own(v);double[] s={rng.nextDouble()*12-6,rng.nextDouble()*12-6,rng.nextDouble()*12-6},e={rng.nextDouble()*12-6,rng.nextDouble()*12-6,rng.nextDouble()*12-6};Object a=nativeVector(s[0],s[1],s[2]),z=nativeVector(e[0],e[1],e[2]);Object hit=call(b,"func_72327_a",new Class<?>[]{vec,vec},a,z);RayTraceResult actual=Boxes.calculateIntercept(own,new Vec3d(s[0],s[1],s[2]),new Vec3d(e[0],e[1],e[2]));check((hit==null)==(actual==null),"Segment hit presence");if(hit!=null){Object hv=hit.getClass().getField(mappings.getProperty("field.field_72307_f")).get(hit);vector(hv,actual.hitVec,"Segment hit coordinate");Object facing=hit.getClass().getField(mappings.getProperty("field.field_178784_b")).get(hit);check(((Enum<?>)facing).name().equals(actual.sideHit.name()),"Segment face");}
   check(((Boolean)call(b,"func_72318_a",new Class<?>[]{vec},a))==Boxes.contains(own,new Vec3d(s[0],s[1],s[2])),"Strict point containment");
  }
  report.add("PASS: original ray-intersection coordinates/faces and strict containment across 1500 segments");groups++;
 }
 public static void main(String[] args)throws Exception{try(InputStream in=new FileInputStream(args[0])){mappings.load(in);}test("box operations",()->boxes());test("ray segments",()->rays());report.add("Assertions: "+assertions);report.add("Passed groups: "+groups);report.add("Failed: "+FoundationParityChecks.failures);for(String line:report)System.out.println(line);try(PrintWriter out=new PrintWriter(args[1],"UTF-8")){for(String line:report)out.println(line);}if(FoundationParityChecks.failures!=0)System.exit(1);}
}
