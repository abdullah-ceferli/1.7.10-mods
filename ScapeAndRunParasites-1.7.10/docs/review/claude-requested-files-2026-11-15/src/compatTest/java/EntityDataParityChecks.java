package dev.srpbackport.tests;
import java.io.*;import java.lang.reflect.*;import java.util.*;import io.netty.buffer.*;
import net.minecraft.network.PacketBuffer;import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.datasync.*;import static dev.srpbackport.tests.FoundationParityChecks.*;
public final class EntityDataParityChecks {
 static Class<?> cls(String name)throws Exception{return Class.forName(mappings.getProperty("class."+name),false,EntityDataParityChecks.class.getClassLoader());}
 static Object serializer(String name)throws Exception{return cls("DataSerializers").getField(mappings.getProperty("serializer."+name)).get(null);}
 static Object key(int id,String name)throws Exception{return cls("DataParameter").getConstructor(int.class,cls("DataSerializer")).newInstance(id,serializer(name));}
 static Object originalManager()throws Exception{return cls("EntityDataManager").getConstructor(cls("Entity")).newInstance(new Object[]{null});}
 static Object originalBuffer(ByteBuf buffer)throws Exception{return cls("PacketBuffer").getConstructor(ByteBuf.class).newInstance(buffer);}
 static void register(Object manager,Object key,Object value)throws Exception{call(manager,"func_187214_a",new Class<?>[]{cls("DataParameter"),Object.class},key,value);}
 static byte[] originalBytes(Object manager)throws Exception{ByteBuf out=Unpooled.buffer();try{call(manager,"func_187216_a",new Class<?>[]{cls("PacketBuffer")},originalBuffer(out));byte[] bytes=new byte[out.readableBytes()];out.readBytes(bytes);return bytes;}finally{out.release();}}
 static byte[] ownBytes(EntityDataManager manager)throws Exception{ByteBuf out=Unpooled.buffer();try{manager.writeEntries(new PacketBuffer(out));byte[] bytes=new byte[out.readableBytes()];out.readBytes(bytes);return bytes;}finally{out.release();}}
 static void scalarWire()throws Exception{
  Random rng=new Random(1710112);for(int i=0;i<200;i++){
   Object original=originalManager();EntityDataManager own=new EntityDataManager(null);byte b=(byte)rng.nextInt();int n=i<5?new int[]{Integer.MIN_VALUE,-1,0,127,Integer.MAX_VALUE}[i]:rng.nextInt();float f=i<4?new float[]{-0.0f,Float.NaN,Float.POSITIVE_INFINITY,1.25f}[i]:rng.nextFloat()*2000-1000;boolean flag=rng.nextBoolean();BlockPos p=new BlockPos(rng.nextInt(60000000)-30000000,rng.nextInt(4096)-2048,rng.nextInt(60000000)-30000000);
   register(original,key(0,"BYTE"),b);register(original,key(1,"VARINT"),n);register(original,key(2,"FLOAT"),f);register(original,key(3,"BOOLEAN"),flag);register(original,key(4,"BLOCK_POS"),nativePos(p.getX(),p.getY(),p.getZ()));
   own.register(new DataParameter<Byte>(0,DataSerializers.BYTE),b);own.register(new DataParameter<Integer>(1,DataSerializers.VARINT),n);own.register(new DataParameter<Float>(2,DataSerializers.FLOAT),f);own.register(new DataParameter<Boolean>(3,DataSerializers.BOOLEAN),flag);own.register(new DataParameter<BlockPos>(4,DataSerializers.BLOCK_POS),p);
   byte[] bytes=originalBytes(original);check(Arrays.equals(bytes,ownBytes(own)),"Original metadata wire bytes case "+i);
   ByteBuf input=Unpooled.wrappedBuffer(bytes);List<EntityDataManager.DataEntry<?>> decoded;try{decoded=EntityDataManager.readEntries(new PacketBuffer(input));check(!input.isReadable(),"Entire metadata frame consumed");}finally{input.release();}
   check(decoded.size()==5&&decoded.get(0).getValue().equals(b)&&decoded.get(1).getValue().equals(n),"Integer/byte roundtrip");check(Float.floatToIntBits((Float)decoded.get(2).getValue())==Float.floatToIntBits(f)&&decoded.get(3).getValue().equals(flag)&&decoded.get(4).getValue().equals(p),"Float/bool/position roundtrip");
  }
  report.add("PASS: exact original 1.12 metadata wire bytes for all five used serializers across 200 signed/random edge cases");groups++;
 }
 static void dirtyAndCopies()throws Exception{
  Object original=originalManager();EntityDataManager own=new EntityDataManager(null);check((Boolean)no(original,"func_187228_d")==own.isEmpty(),"Empty manager");Object ok=key(3,"BLOCK_POS");DataParameter<BlockPos> k=new DataParameter<BlockPos>(3,DataSerializers.BLOCK_POS);
  Object mutable=cls("MutableBlockPos").getConstructor(int.class,int.class,int.class).newInstance(-8,70,13);BlockPos.MutableBlockPos p=new BlockPos.MutableBlockPos(-8,70,13);register(original,ok,mutable);own.register(k,p);
  check(!(Boolean)no(original,"func_187223_a")&&!own.isDirty(),"Registration does not set manager dirty flag");check(no(original,"func_187221_b")==null&&own.getDirty()==null,"No dirty frame before first change");
  List<?> originals=(List<?>)no(original,"func_187231_c");List<EntityDataManager.DataEntry<?>> copies=own.getAll();call(mutable,"func_181079_c",new Class<?>[]{int.class,int.class,int.class},4,5,6);p.setPos(4,5,6);
  Object saved=no(originals.get(0),"func_187206_b");check(coords(saved).equals("4,5,6")&&copies.get(0).getValue().equals(new BlockPos(4,5,6)),"Mutable BlockPos snapshot: original="+coords(saved)+", own="+copies.get(0).getValue());
  call(original,"func_187217_b",new Class<?>[]{cls("DataParameter")},ok);own.setDirty(k);check((Boolean)no(original,"func_187223_a")&&own.isDirty(),"Forced dirty flag");
  List<?> dirty=(List<?>)no(original,"func_187221_b");check(dirty.size()==own.getDirty().size()&&!(Boolean)no(original,"func_187223_a")&&!own.isDirty(),"Dirty drain clears global flag");
  call(original,"func_187217_b",new Class<?>[]{cls("DataParameter")},ok);own.setDirty(k);no(original,"func_187230_e");own.setClean();check(no(original,"func_187221_b")==null&&own.getDirty()==null,"Clean clears pending changes");
  report.add("PASS: original empty/register/dirty/clean semantics and original mutable-position value-copy behavior");groups++;
 }
 static int nativeId(Object key)throws Exception{return (Integer)no(key,"func_187155_a");}
 static Object allocate(Class<?> owner,String serializer)throws Exception{return call(cls("EntityDataManager"),"func_187226_a",new Class<?>[]{Class.class,cls("DataSerializer")},owner,serializer(serializer));}
 static void inheritance()throws Exception{
  Object root=allocate(cls("Entity"),"BYTE");DataParameter<Byte> a=EntityDataManager.createKey(net.minecraft.entity.Entity.class,DataSerializers.BYTE);int offset=nativeId(root)-a.getId();
  Object child=allocate(cls("EntityLivingBase"),"BOOLEAN");DataParameter<Boolean> b=EntityDataManager.createKey(net.minecraft.entity.EntityLivingBase.class,DataSerializers.BOOLEAN);check(nativeId(child)-b.getId()==offset&&b.getId()>a.getId(),"Inherited data IDs");
  Object descendant=allocate(cls("EntityMob"),"FLOAT");DataParameter<Float> c=EntityDataManager.createKey(net.minecraft.entity.monster.EntityMob.class,DataSerializers.FLOAT);check(nativeId(descendant)-c.getId()==offset&&c.getId()>b.getId(),"Further inherited IDs");
  int last=-1;boolean failed=false;for(int i=0;i<256;i++)try{last=EntityDataManager.createKey(net.minecraft.entity.monster.EntityMob.class,DataSerializers.VARINT).getId();}catch(IllegalArgumentException expected){failed=true;break;}check(failed&&last==254,"Original 254-ID ceiling");
  report.add("PASS: inherited tracked-data key allocation and the original 254-ID ceiling in a namespace separate from legacy DataWatcher");groups++;
 }
 public static void main(String[] args)throws Exception{try(InputStream in=new FileInputStream(args[0])){mappings.load(in);}test("scalar wire",()->scalarWire());test("dirty and copies",()->dirtyAndCopies());test("inherited IDs",()->inheritance());report.add("Assertions: "+assertions);report.add("Passed groups: "+groups);report.add("Failed: "+FoundationParityChecks.failures);for(String row:report)System.out.println(row);try(PrintWriter out=new PrintWriter(args[1],"UTF-8")){for(String row:report)out.println(row);}if(FoundationParityChecks.failures!=0)System.exit(1);}
}
