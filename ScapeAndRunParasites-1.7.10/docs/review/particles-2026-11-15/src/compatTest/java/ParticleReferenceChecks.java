package dev.srpbackport.tests;
import java.io.*;import java.lang.reflect.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.jar.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
import com.dhanantry.scapeandrunparasites.backport.particle.*;
/** Compare packet expansion against the actual original handler bytecode with only its world sink/thread gate replaced. */
public final class ParticleReferenceChecks {
    public static final org.apache.logging.log4j.Logger LOGGER=org.apache.logging.log4j.LogManager.getLogger("ParticleReference");
    static final List<double[]> CAPTURE=new ArrayList<double[]>();
    public static void capture(Object world,Object type,boolean force,double x,double y,double z,double vx,double vy,double vz,int[] args){
        double[] values=new double[9+args.length];values[0]=((Enum<?>)type).ordinal();values[1]=force?1:0;values[2]=x;values[3]=y;values[4]=z;values[5]=vx;values[6]=vy;values[7]=vz;values[8]=args.length;for(int i=0;i<args.length;i++)values[9+i]=args[i];CAPTURE.add(values);
    }
    static class Loader extends ClassLoader {Loader(){super(ParticleReferenceChecks.class.getClassLoader());}Class<?> define(byte[] bytes){return defineClass(null,bytes,0,bytes.length);}}
    static byte[] entry(String jar,String name)throws Exception{try(JarFile file=new JarFile(jar);InputStream in=file.getInputStream(file.getJarEntry(name+".class"))){ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)bytes.write(buf,0,n);return bytes.toByteArray();}}
    static Method harness(Properties p,String jar)throws Exception {
        ClassNode original=new ClassNode();new ClassReader(entry(jar,p.getProperty("handler"))).accept(original,ClassReader.SKIP_DEBUG);MethodNode source=null;
        String packet=p.getProperty("packet"),enumName=p.getProperty("enum"),handler=p.getProperty("handler");
        for(Object o:original.methods){MethodNode m=(MethodNode)o;if(m.name.equals(p.getProperty("handle"))&&m.desc.equals("(L"+packet+";)V"))source=m;}
        if(source==null)throw new AssertionError("Original particle handler missing");
        source.access=Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC;source.name="run";source.desc="(Ljava/lang/Object;L"+packet+";Ljava/util/Random;)V";
        // Strip only the original main-thread scheduling preamble; retain packet getters, branches, float math, RNG order and catch blocks.
        AbstractInsnNode threadCheck=null;for(AbstractInsnNode i=source.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode&&i.getOpcode()==Opcodes.INVOKESTATIC){threadCheck=i;break;}
        if(threadCheck==null)throw new AssertionError("Original thread gate missing");AbstractInsnNode after=threadCheck.getNext();while(source.instructions.getFirst()!=after)source.instructions.remove(source.instructions.getFirst());
        for(AbstractInsnNode i=source.instructions.getFirst();i!=null;){AbstractInsnNode next=i.getNext();
            if(i instanceof FrameNode)source.instructions.remove(i);
            else if(i instanceof VarInsnNode){VarInsnNode v=(VarInsnNode)i;if(v.var>=2)v.var++;}
            else if(i instanceof IincInsnNode){IincInsnNode v=(IincInsnNode)i;if(v.var>=2)v.var++;}
            else if(i instanceof FieldInsnNode){FieldInsnNode f=(FieldInsnNode)i;
                if(f.owner.equals(handler)&&f.getOpcode()==Opcodes.GETFIELD){InsnList replacement=new InsnList();replacement.add(new InsnNode(Opcodes.POP));
                    if(f.desc.equals("Ljava/util/Random;"))replacement.add(new VarInsnNode(Opcodes.ALOAD,2));else replacement.add(new InsnNode(Opcodes.ACONST_NULL));
                    source.instructions.insertBefore(i,replacement);source.instructions.remove(i);
                }else if(f.owner.equals(handler)&&f.getOpcode()==Opcodes.GETSTATIC&&f.desc.equals("Lorg/apache/logging/log4j/Logger;"))f.owner="dev/srpbackport/tests/ParticleReferenceChecks";
                if(f.owner.equals("dev/srpbackport/tests/ParticleReferenceChecks"))f.name="LOGGER";
            }else if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;
                if(call.desc.equals("(L"+enumName+";ZDDDDDD[I)V")){call.setOpcode(Opcodes.INVOKESTATIC);call.owner="dev/srpbackport/tests/ParticleReferenceChecks";call.name="capture";call.desc="(Ljava/lang/Object;Ljava/lang/Object;ZDDDDDD[I)V";call.itf=false;}
            }
            i=next;
        }
        source.maxLocals++;source.localVariables=null;
        ClassNode result=new ClassNode();result.version=Opcodes.V1_8;result.access=Opcodes.ACC_PUBLIC;result.name="dev/srpbackport/tests/OriginalParticleExpansion";result.superName="java/lang/Object";result.methods.add(source);
        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);result.accept(writer);
        return new Loader().define(writer.toByteArray()).getMethod("run",Object.class,Class.forName(packet.replace('/','.')),Random.class);
    }
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    public static void main(String[] args)throws Exception {
        Properties p=new Properties();try(InputStream in=new FileInputStream(args[0])){p.load(in);}Class<?> enumClass=Class.forName(p.getProperty("enum").replace('/','.'));
        Object[] values=enumClass.getEnumConstants();check(values.length==EnumParticleTypes.values().length,"Catalog size");
        Method name=enumClass.getMethod(p.getProperty("name")),id=enumClass.getMethod(p.getProperty("id")),ignore=enumClass.getMethod(p.getProperty("ignore")),arguments=enumClass.getMethod(p.getProperty("arguments"));
        for(int i=0;i<values.length;i++){EnumParticleTypes type=EnumParticleTypes.values()[i];Object original=values[i];check(type.name().equals(((Enum<?>)original).name())&&type.getParticleName().equals(name.invoke(original))&&type.getParticleID()==(Integer)id.invoke(original)&&type.getShouldIgnoreRange()==(Boolean)ignore.invoke(original)&&type.getArgumentCount()==(Integer)arguments.invoke(original),"Catalog entry "+i);}
        List<String> report=new ArrayList<String>();report.add("PASS: All 49 particle names, IDs, range flags and argument counts match original 1.12.2 bytecode");
        Method original=harness(p,args[1]);Class<?> packet=Class.forName(p.getProperty("packet").replace('/','.'));Constructor<?> constructor=packet.getConstructor(enumClass,boolean.class,float.class,float.class,float.class,float.class,float.class,float.class,float.class,int.class,int[].class);
        Random inputs=new Random(171012);int cases=0;
        for(EnumParticleTypes type:EnumParticleTypes.values())for(int count:new int[]{-1,0,1,3,17})for(boolean force:new boolean[]{false,true})for(int repeat=0;repeat<5;repeat++) {
            float x=inputs.nextFloat()*200-100,y=inputs.nextFloat()*256,z=inputs.nextFloat()*200-100,dx=inputs.nextFloat(),dy=inputs.nextFloat(),dz=inputs.nextFloat(),speed=inputs.nextFloat();int[] extra={0xE123,41};int[] required=Arrays.copyOf(extra,type.getArgumentCount());
            Object nativePacket=constructor.newInstance(values[type.ordinal()],force,x,y,z,dx,dy,dz,speed,count,required);long seed=inputs.nextLong();CAPTURE.clear();original.invoke(null,null,nativePacket,new Random(seed));List<double[]> expected=new ArrayList<double[]>(CAPTURE);CAPTURE.clear();
            ParticleBatch batch=new ParticleBatch(0,type,force,x,y,z,count,dx,dy,dz,speed,extra);batch.expand(new Random(seed),(t,f,a,b,c,d,e,g,ar)->capture(null,t,f,a,b,c,d,e,g,ar));
            check(CAPTURE.size()==expected.size(),"Expansion count "+count);
            for(int i=0;i<expected.size();i++)for(int j=0;j<expected.get(i).length;j++)check(Double.doubleToLongBits(CAPTURE.get(i)[j])==Double.doubleToLongBits(expected.get(i)[j]),"Expansion bits case "+cases+" entry "+i+" component "+j);
            cases++;
        }
        report.add("PASS: Batch positions, count-zero float velocity products and six-Gaussian RNG order match original handler bytecode in "+cases+" cases");report.add("Failed: 0");Files.write(Paths.get(args[2]),report,StandardCharsets.UTF_8);for(String line:report)System.out.println(line);
    }
}
